/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx.resize;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.enums.Zone;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.utils.fx.resize.targets.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static io.github.palexdev.mfxcore.utils.CollectionUtils.set;
import static io.github.palexdev.mfxcore.utils.fx.LayoutUtils.boundedSize;
import static java.util.Optional.ofNullable;

/// Makes something resizable by dragging its edges and corners. What that something is comes from the [ResizeTarget]
/// strategy: a [Region], a [Stage], a [Rectangle], a [Circle], or your own.
///
/// ### Usage
///
/// ```java
/// // a free-floating card. Install on the parent, or the outer half of the band is unreachable
/// Resizer.resizer(card)
///     .hitSource(card.getParent())
///     .band(new ResizeBand(8, 8, 24))
///     .install();
///
/// // a table column: right edge only
/// Resizer.resizer(column)
///     .allowedZones(Zone.CENTER_RIGHT)
///     .resizeHandler((c, _, _, w, _) -> c.resize(w))
///     .install();
/// ```
///
/// ### The gesture
///
/// The zone and the geometry to resize from are decided at press and held for the rest of the drag.
/// Holding [KeyCode#SHIFT] at press locks the aspect ratio.
///
/// [KeyCode#ESCAPE] cancels an in-flight gesture and restores the original geometry.
/// It's a scene-wide filter, but it only consumes the key when it actually canceled something,
/// so it won't swallow the [KeyCode#ESCAPE] that closes your dialog.
///
/// [#threshold(double)] holds the gesture until the pointer has traveled far enough to mean it, which suppresses
/// accidental one-pixel resizes. The press is still consumed while waiting, so it never falls through to a child.
/// By default, this is off, `threshold = 0.0`
///
/// ### Target, hit node, hit source
///
/// Three roles that are easy to conflate:
///
/// | | What it is | `resizer(region)` | `resizer(stage)` |
/// |---|---|---|---|
/// | target | what gets resized | the region | the stage |
/// | hit node | what gets hit-tested | the region | the scene root |
/// | hit source | where the handlers live | the region | the scene root |
///
/// Only the last one is yours to choose, through [#hitSource(Node)], and it decides nothing but which events arrive.
/// Detection goes through scene coordinates, so the zone comes out the same wherever the handlers sit. Pointing it at
/// the parent is what makes the part of the band *outside* the node reachable. On a self-install there is nothing out
/// there to receive the event. Resizing a [Stage] is a target concern and never a hit source one.
public class Resizer<T> implements Disposable {

    //================================================================================
    // Properties
    //================================================================================

    private ResizeTarget<T> target;
    private Node hitSource;
    private CursorOwner cursorOwner;

    private ResizeBand band = ResizeBand.DEFAULT;
    private final Set<Zone> allowedZones = set(() -> EnumSet.noneOf(Zone.class), Zone.all());
    private BiPredicate<MouseEvent, Zone> condition = (_, _) -> true;
    private ResizeHandler<T> resizeHandler;
    private double threshold = 0.0;

    private Zone detectedZone = Zone.NONE;
    private boolean resizing = false;
    private boolean armed = false;
    private boolean ratioLocked = false;
    private Point2D origin;
    private Bounds bSnapshot;

    private WhenEvent<?> escFilter;
    private final List<Disposable> disposables = new ArrayList<>();

    // Callbacks
    private BiConsumer<MouseEvent, Zone> onPressed = (me, z) -> {
        if (z != Zone.NONE) me.consume();
    };
    private BiConsumer<Zone, Bounds> onResizing;
    private BiConsumer<Bounds, Bounds> onResized;
    private Runnable onCancelled;

    //================================================================================
    // Constructors
    //================================================================================

    protected Resizer(ResizeTarget<T> target) {
        this.target = target;
    }

    /// @return a resizer working on a [RegionTarget]
    @SuppressWarnings("unchecked")
    public static <R extends Region> Resizer<R> resizer(R region) {
        return new Resizer<>((ResizeTarget<R>) new RegionTarget(region));
    }

    /// @return a resizer working on a [StageTarget]. The stage must have a scene by the time [#install()] runs
    public static Resizer<Stage> resizer(Stage stage) {
        return new Resizer<>(new StageTarget(stage));
    }

    /// @return a resizer working on a [RectangleTarget]
    public static Resizer<Rectangle> resizer(Rectangle rectangle) {
        return new Resizer<>(new RectangleTarget(rectangle));
    }

    /// Delegates to [#resizer(Circle, double)] with `minRadius = 0.0`.
    public static Resizer<Circle> resizer(Circle circle) {
        return resizer(circle, 0.0);
    }

    /// @return a resizer working on a [CircleTarget] with the specified minimum radius constraint
    public static Resizer<Circle> resizer(Circle circle, double minRadius) {
        return new Resizer<>(new CircleTarget(circle, minRadius));
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Registers the handlers on the hit source, falling back to [ResizeTarget#hitNode()] when none was given. Nothing
    /// happens until this is called.
    ///
    /// @throws IllegalStateException if already installed
    public Resizer<T> install() {
        if (isInstalled()) throw new IllegalStateException("Resizer is already installed");
        hitSource = hitSource == null ? target.hitNode() : hitSource;
        cursorOwner = CursorOwner.forNode(hitSource);
        Collections.addAll(disposables,
            intercept(hitSource, MouseEvent.MOUSE_PRESSED).handle(this::onMousePressed).asFilter().register(),
            intercept(hitSource, MouseEvent.MOUSE_DRAGGED).handle(this::onMouseDragged).asFilter().register(),
            intercept(hitSource, MouseEvent.MOUSE_RELEASED).handle(this::onMouseReleased).asFilter().register(),
            intercept(hitSource, MouseEvent.MOUSE_MOVED).handle(this::onMouseMoved).asFilter().register(),
            intercept(hitSource, MouseEvent.MOUSE_EXITED).handle(this::onMouseExited).asFilter().register(),
            onInvalidated(hitSource.sceneProperty())
                .condition(Objects::nonNull)
                .then(s -> disposables.add(escFilter(s).register()))
                .executeNow(() -> hitSource.getScene() != null)
                .listen()
        );
        return this;
    }

    /// Disposes the handlers and restores the cursor. A gesture in flight is left as it is, use [#cancel()] before
    /// to revert.
    public void uninstall() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        if (cursorOwner != null) {
            resetState();
            cursorOwner = null;
        }
    }

    /// Restores the geometry the gesture started from and fires [#onCancelled(Runnable)]. Bound to [KeyCode#ESCAPE].
    ///
    /// @return whether there was anything to cancel
    public boolean cancel() {
        if (!resizing) return false;
        if (resizeHandler != null) {
            resizeHandler.resize(target.target(),
                bSnapshot.getMinX(), bSnapshot.getMinY(),
                bSnapshot.getWidth(), bSnapshot.getHeight());
        } else {
            target.apply(
                bSnapshot.getMinX(), bSnapshot.getMinY(),
                bSnapshot.getWidth(), bSnapshot.getHeight());
        }

        if (onCancelled != null) onCancelled.run();
        resetState();
        return true;
    }

    /// Core method of the resizer. There is a lot of math going on here.
    ///
    /// Fires [#onResizing(BiConsumer)].
    protected void resize(MouseEvent me) {
        Point2D now = target.pointer(me);
        double dx = now.getX() - origin.getX();
        double dy = now.getY() - origin.getY();
        if (armed) {
            if (!crossedThreshold(dx, dy)) return;
            armed = false;
        }

        double newW = bSnapshot.getWidth();
        if (detectedZone.isRight()) newW += dx;
        else if (detectedZone.isLeft()) newW -= dx;

        double newH = bSnapshot.getHeight();
        if (detectedZone.isBottom()) newH += dy;
        else if (detectedZone.isTop()) newH -= dy;

        double ratio = ratioLocked ? ratio() : 0.0;
        if (ratio > 0.0) {
            if (drivenByWidth(newW, newH)) newH = newW / ratio;
            else newW = newH * ratio;
        }

        double cW = boundedSize(target.minW(), newW, target.maxW());
        double cH = boundedSize(target.minH(), newH, target.maxH());
        if (ratio > 0.0) {
            if (cW != newW) cH = boundedSize(target.minH(), cW / ratio, target.maxH());
            else if (cH != newH) cW = boundedSize(target.minW(), cH * ratio, target.maxW());
        }
        newW = cW;
        newH = cH;

        double nX = detectedZone.isLeft() ? bSnapshot.getMaxX() - newW : bSnapshot.getMinX();
        double nY = detectedZone.isTop() ? bSnapshot.getMaxY() - newH : bSnapshot.getMinY();
        if (resizeHandler != null) {
            resizeHandler.resize(target.target(), nX, nY, newW, newH);
        } else {
            target.apply(nX, nY, newW, newH);
        }

        if (onResizing != null)
            onResizing.accept(detectedZone, new BoundingBox(nX, nY, newW, newH));
    }

    /// Starts the gesture. Ignores anything but [MouseButton#PRIMARY], as well as presses that aren't over a zone.
    ///
    /// The zone is recomputed here rather than read from [#detectedZone()], because a press can arrive with no
    /// preceding `MOUSE_MOVED`. Snapshots the target's bounds and the pointer, arms the [#threshold(double)] and
    /// latches the aspect ratio lock.
    ///
    /// The event is consumed only when a zone was hit, otherwise children inside the target would never receive clicks.
    protected void onMousePressed(MouseEvent me) {
        if (me.getButton() != MouseButton.PRIMARY) return;

        // make sure zone and cursor are correct on press
        Zone zone = detectZone(me);
        if (zone == Zone.NONE) {
            if (onPressed != null) onPressed.accept(me, Zone.NONE);
            return;
        }
        cursorOwner.claim(this, zone.cursor());

        origin = target.pointer(me);
        bSnapshot = target.bounds();
        resizing = true;
        armed = threshold > 0.0;
        ratioLocked = me.isShiftDown();
        if (onPressed != null) onPressed.accept(me, zone);
    }

    /// Delegates to [#resize(MouseEvent)] and consumes the event, but only while a gesture is in flight.
    protected void onMouseDragged(MouseEvent me) {
        if (!resizing) return;
        resize(me);
        me.consume();
    }

    /// Ends the gesture and fires [#onResized(BiConsumer)].
    ///
    /// The zone and the cursor are recomputed for the pointer's current position, since dragging past a min or max
    /// clamp leaves the pointer somewhere the target no longer reaches.
    protected void onMouseReleased(MouseEvent me) {
        if (!resizing) return;
        Bounds tmp = bSnapshot;
        resetState();
        Zone zone = detectZone(me);
        cursorOwner.claim(this, zone.cursor());
        me.consume();

        if (onResized != null) onResized.accept(tmp, target.bounds());
    }

    /// Updates [#detectedZone()] and the cursor. Doesn't run during a drag, since JavaFX doesn't send `MOUSE_MOVED`
    /// while a button is held, which is what leaves the zone latched for the whole gesture.
    protected void onMouseMoved(MouseEvent me) {
        Zone zone = detectZone(me);
        cursorOwner.claim(this, zone.cursor());
    }

    /// Clears the state when the pointer leaves the hit source, unless a gesture is in flight. Mid-drag the pointer
    /// leaves the bounds routinely, and resetting there would end the gesture, not just the cursor.
    protected void onMouseExited(MouseEvent me) {
        if (resizing) return;
        resetState();
    }

    /// Ends the gesture and releases the cursor claim. Doesn't touch the target's geometry, see [#cancel()] for that.
    protected void resetState() {
        resizing = false;
        armed = false;
        ratioLocked = false;
        detectedZone = Zone.NONE;
        cursorOwner.release(this);
        origin = null;
        bSnapshot = null;
    }

    /// @return the aspect ratio to preserve, taken from the press-time snapshot rather than the live geometry so that
    /// it can't drift as the gesture goes on. `0.0` for a degenerate snapshot, which the caller's `ratio > 0.0` guard
    /// reads as "no lock", NaN included
    private double ratio() {
        double w = bSnapshot.getWidth();
        double h = bSnapshot.getHeight();
        return w > 0.0 && h > 0.0 ? w / h : 0.0;
    }

    /// Decides which axis leads when the aspect ratio is locked, the other one being derived from it.
    ///
    /// On an edge that's simply the axis being dragged. On a corner both axes moved, so the one that moved *more*
    /// wins, comparing against the snapshot. Same rule as [CircleTarget]'s collapse, so the two agree on what a
    /// diagonal drag means.
    private boolean drivenByWidth(double w, double h) {
        if (detectedZone.isCorner())
            return Math.abs(w - bSnapshot.getWidth()) >= Math.abs(h - bSnapshot.getHeight());
        return detectedZone.isLeft() || detectedZone.isRight();
    }

    /// @return whether the pointer has traveled at least [#threshold(double)] px on an axis the zone actually resizes.
    /// Not `hypot(dx, dy)`: sliding along a right edge moves `dy` without changing the width, and arming the gesture on
    /// travel that can't resize anything would defeat the point
    private boolean crossedThreshold(double dx, double dy) {
        double travel = 0.0;
        if (detectedZone.isLeft() || detectedZone.isRight()) travel = Math.abs(dx);
        if (detectedZone.isTop() || detectedZone.isBottom()) travel = Math.max(travel, Math.abs(dy));
        return travel >= threshold;
    }

    /// Resolves the zone under the pointer and stores it in [#detectedZone()].
    ///
    /// Ordered cheapest first: capability, then geometry through [ResizeTarget#detectZone(MouseEvent, ResizeBand)],
    /// then [#allowedZones(Zone...)] with a [#degrade(Zone)] pass, then [#condition(BiPredicate)]. The condition is
    /// guarded by `zone != NONE` so no user predicate ever has to handle being asked about [Zone#NONE].
    private Zone detectZone(MouseEvent me) {
        if (!target.isResizable()) {
            detectedZone = Zone.NONE;
            return Zone.NONE;
        }

        Zone zone = target.detectZone(me, band);
        if (!allowedZones.contains(zone)) zone = degrade(zone);
        if (zone != Zone.NONE && !condition.test(me, zone)) {
            detectedZone = Zone.NONE;
            return Zone.NONE;
        }

        detectedZone = zone;
        return zone;
    }

    /// Falls back from a corner that [#allowedZones(Zone...)] rejected to one of the two edges it's made of, horizontal
    /// first, or [Zone#NONE] if neither is allowed.
    ///
    /// Safe because a corner *is* the conjunction of its two edges, so this can never hand back something the caller
    /// didn't allow. Only corners degrade, and only on the miss path.
    private Zone degrade(Zone zone) {
        if (!zone.isCorner()) return Zone.NONE;
        Zone horizontal = zone.isLeft() ? Zone.CENTER_LEFT : Zone.CENTER_RIGHT;
        if (allowedZones.contains(horizontal)) return horizontal;
        Zone vertical = zone.isTop() ? Zone.TOP_CENTER : Zone.BOTTOM_CENTER;
        return allowedZones.contains(vertical) ? vertical : Zone.NONE;
    }

    private WhenEvent<?> escFilter(Scene scene) {
        if (escFilter != null) {
            escFilter.dispose();
            disposables.remove(escFilter);
        }
        escFilter = intercept(scene, KeyEvent.KEY_PRESSED)
            .condition(k -> k.getCode() == KeyCode.ESCAPE)
            .handle(k -> {if (cancel()) k.consume();})
            .asFilter();
        return escFilter;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Calls [#cancel()], then [#uninstall()], then drops the references to the target and the hit source.
    @Override
    public void dispose() {
        cancel();
        uninstall();
        target = null;
        hitSource = null;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public Node hitSource() {
        return hitSource;
    }

    /// Sets where the handlers are registered. Defaults to [ResizeTarget#hitNode()]; pass the parent to make the outer
    /// half of the band reachable. Has no effect after [#install()].
    public Resizer<T> hitSource(Node node) {
        hitSource = node;
        return this;
    }

    public ResizeBand band() {
        return band;
    }

    /// Sets how thick the grabbable border is, see [ResizeBand]. Defaults to [ResizeBand#DEFAULT].
    public Resizer<T> band(ResizeBand band) {
        this.band = band;
        return this;
    }

    public Set<Zone> allowedZones() {
        return allowedZones;
    }

    /// Restricts which zones can be grabbed. All of them by default.
    ///
    /// A corner that isn't allowed degrades to one of the two edges it's made of, horizontal first, or to [Zone#NONE]
    /// if neither is allowed. This restricts what can be grabbed, not which axes may move: an edge with a locked aspect
    /// ratio still changes both.
    public Resizer<T> allowedZones(Zone... zones) {
        allowedZones.clear();
        allowedZones.addAll(Arrays.asList(zones));
        return this;
    }

    public BiPredicate<MouseEvent, Zone> condition() {
        return condition;
    }

    /// Vetoes a detected zone, for constraints the geometry can't express. Never called with [Zone#NONE], and a `null`
    /// condition allows everything.
    ///
    /// Checked during detection rather than at resize time, so a vetoed zone shows no cursor and doesn't consume the
    /// press. Has no effect once a gesture is under way, the zone being latched at press.
    public Resizer<T> condition(BiPredicate<MouseEvent, Zone> condition) {
        this.condition = ofNullable(condition).orElse((_, _) -> true);
        return this;
    }

    public ResizeHandler<T> resizeHandler() {
        return resizeHandler;
    }

    /// Routes the computed geometry somewhere else instead of writing it through the target, see [ResizeHandler].
    public Resizer<T> resizeHandler(ResizeHandler<T> resizeHandler) {
        this.resizeHandler = resizeHandler;
        return this;
    }

    public double threshold() {
        return threshold;
    }

    /// How far the pointer must travel before the gesture starts applying anything, `0` by default. Measured only on
    /// the axes the zone resizes, so sliding along an edge doesn't count.
    ///
    /// Once crossed, the edge moves to the pointer rather than trailing it by `threshold` px.
    public Resizer<T> threshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public BiConsumer<MouseEvent, Zone> onPressed() {
        return onPressed;
    }

    public Resizer<T> onPressed(BiConsumer<MouseEvent, Zone> onPressed) {
        this.onPressed = onPressed;
        return this;
    }

    public BiConsumer<Zone, Bounds> onResizing() {
        return onResizing;
    }

    /// Fired on every applied drag event with the zone and the new geometry.
    public Resizer<T> onResizing(BiConsumer<Zone, Bounds> onResizing) {
        this.onResizing = onResizing;
        return this;
    }

    public BiConsumer<Bounds, Bounds> onResized() {
        return onResized;
    }

    /// Fired on release with the target's old bounds (pre-resize) and new bounds (post-resize).
    public Resizer<T> onResized(BiConsumer<Bounds, Bounds> onResized) {
        this.onResized = onResized;
        return this;
    }

    public Runnable onCancelled() {
        return onCancelled;
    }

    /// Fired when a gesture is cancelled, after the geometry has been restored.
    public Resizer<T> onCancelled(Runnable onCancelled) {
        this.onCancelled = onCancelled;
        return this;
    }

    /// @return the zone this resizer currently owns: the one under the pointer while idle, the one grabbed at press
    /// while resizing, [Zone#NONE] otherwise
    public Zone detectedZone() {
        return detectedZone;
    }

    /// @return whether a gesture is in flight. True from the press, including while a [#threshold(double)] is still
    /// holding it back
    public boolean isResizing() {
        return resizing;
    }

    /// @return whether the resizer is currently installed and working onto something
    public boolean isInstalled() {
        return !disposables.isEmpty();
    }
}
