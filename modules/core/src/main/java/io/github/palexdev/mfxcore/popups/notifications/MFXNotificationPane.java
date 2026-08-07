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

package io.github.palexdev.mfxcore.popups.notifications;

import java.util.*;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.popups.notifications.Notification.NotificationEvent;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import io.github.palexdev.mfxcore.utils.PositionUtils;
import io.github.palexdev.mfxcore.utils.fx.CubicCurve;
import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.animation.*;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/// A simple notification system that can show up to three [Notifications][Notification] at a time (see [#maxVisibleProperty()]).
///
/// For once, the design focuses on simplicity and ease of use rather than flexibility. The user's main interactions with
/// this pane are send and dismiss notifications. The actual show and hide operations are done by protected methods, those
/// include:
/// - Adding and removing notifications from the queue and the children list
/// - React to dismiss events, [NotificationEvent#DISMISS]
/// - Layout management
/// - In and out animations
///
/// ### Queue System
///
/// This notification system uses two queues to manage the notifications:
/// - the 'pending' queue, which holds notifications that are waiting to be shown
/// - the 'visible' queue, which holds the notifications that are currently visible
///
/// Both are managed by the [#processQueue()] method.
///
/// For the pending queue it uses a [SequencedSet], which guarantees both order and uniqueness.<br >
/// For the visible queue it uses a [SequencedMap], which offers the same guarantees, but for the uniqueness we rely on
/// the notifications' node. This queue can contain at most the number specified by the [#maxVisibleProperty()] property.
///
/// ### Layout
///
/// All notifications are auto-sized ([Node#autosize()]) and positioned according to the [#alignmentProperty()].
/// The vertical position is computed for each notification at layout time, and it's set by the [#position(Node, double)]
/// method. In the initial design, the vertical position was adjusted with an animation, later the animation changed,
/// see [#animate(Notification, boolean)].
///
/// If you want to change the layout, you want to override [#position(Node, double)] or [#layoutChildren()] directly.
///
/// ### Animations
///
/// By default, when a notification is shown or hidden, a short animation is built by [#animate(Notification, boolean)]
/// and played by the caller. When the notification is shown, it is played immediately. When the notification is hidden,
/// the animation is also responsible for removing the node from the pane and issuing a [#processQueue()]. In other words,
/// The next notification is shown only after the out animation has finished.
///
/// If you want to change the animation, you want to override [#animate(Notification, boolean)] or even [#position(Node, double)]
/// if you want to animate the translation.
public class MFXNotificationPane extends Region implements MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final Interpolator SPACE_CURVE = new CubicCurve(0.42, 1.67, 0.21, 0.90);
    private static final Interpolator OPACITY_CURVE = new CubicCurve(0.2, 0.0, 0.0, 1.0);

    //================================================================================
    // Properties
    //================================================================================
    private final SequencedSet<Notification> pending = new LinkedHashSet<>();
    private final SequencedMap<Node, Notification> visible = new LinkedHashMap<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXNotificationPane() {
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setDefaultStyleClasses();
        addEventHandler(NotificationEvent.DISMISS, this::onDismissRequest);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the given notification to the 'pending' queue and issues a [#processQueue()].
    public void show(Notification notification) {
        pending.addLast(notification);
        processQueue();
    }

    /// Removes the given notification from the 'visible' queue (**by its node!**) and if present, calls [#doHide(Notification)].
    public void hide(Notification notification) {
        if (visible.remove(notification.toNode()) != null)
            doHide(notification);
    }

    /// Reacts to events of type [NotificationEvent#DISMISS] and calls [#hide(Notification)].
    protected void onDismissRequest(NotificationEvent event) {
        Notification notification = event.getNotification();
        hide(notification);
        event.consume();
    }

    /// Clears all pending and visible notifications. If `closeAll` is `true`, all visible notifications are also hidden immediately.
    public void clear(boolean closeAll) {
        pending.clear();
        if (closeAll) visible.values().forEach(this::doHide);
        visible.clear();
    }

    /// This is responsible for actually showing notifications.
    /// - First, it's added to the children list
    /// - Then requests and plays a show animation with [#animate(Notification, boolean)]
    /// - Finally calls the notification's hook: [Notification#onShow()]
    protected void doShow(Notification notification) {
        Node node = notification.toNode();
        getChildren().add(node);
        Optional.ofNullable(animate(notification, true)).ifPresent(Animation::play);
        notification.onShow();
    }

    /// This is responsible for actually hiding a notification.
    /// - First it calls the notification's hook: [Notification#onHide()]
    /// - Then requests a hide animation with [#animate(Notification, boolean)] and sets its [Animation#onFinishedProperty()]
    /// action to:
    ///     1) Remove the notification from the children list
    ///     2) Issuing a [#processQueue()]
    protected void doHide(Notification notification) {
        notification.onHide();
        Animation animation = animate(notification, false);
        if (animation != null) {
            animation.setOnFinished(_ -> {
                getChildren().remove(notification.toNode());
                processQueue();
            });
            animation.play();
        } else {
            getChildren().remove(notification.toNode());
            processQueue();
        }
    }

    /// This core method is responsible for processing the notification queues.
    ///
    /// While there are pending notifications, and the number of visibles is lesser than [#getMaxVisible()]:
    /// 1) Moves a notification from the 'pending' queue to the 'visible' queue
    /// 2) Calls [#doShow(Notification)]
    private void processQueue() {
        while (!pending.isEmpty() && visible.size() < getMaxVisible()) {
            Notification notification = pending.removeFirst();
            visible.putLast(notification.toNode(), notification);
            doShow(notification);
        }
    }

    /// This is responsible for building the animations to show and hide the given notification.
    ///
    /// By default, the animation consists in a short fade and horizontal slide. Notes:
    /// 1) According to the [#alignmentProperty()] the slide can be from right to left or left to right.
    /// 2) The initial or target value for the horizontal translation is double the value of the [#spacingProperty()]
    ///
    /// If you want to disable animations entirely, override this method to return `null`.
    protected Animation animate(Notification notification, boolean show) {
        Node node = notification.toNode();
        Timeline t = new Timeline();

        boolean right = PositionUtils.isRight(getAlignment());
        if (show) {
            node.setOpacity(0.0);
            node.setTranslateX(getSpacing() * (right ? 2 : -2));
            t.getKeyFrames().add(new KeyFrame(
                Duration.millis(350.0),
                new KeyValue(node.translateXProperty(), 0.0, SPACE_CURVE)
            ));
        } else {
            t.getKeyFrames().add(new KeyFrame(
                Duration.millis(350.0),
                new KeyValue(node.translateXProperty(), getSpacing() * (right ? 2 : -2), SPACE_CURVE)
            ));
        }

        KeyFrame okf = new KeyFrame(
            Duration.millis(350.0),
            new KeyValue(node.opacityProperty(), show ? 1.0 : 0.0, OPACITY_CURVE)
        );
        t.getKeyFrames().add(okf);
        return t;
    }

    /// By default, the layout strategy positions all notifications in the same area. Typically, in any notification system,
    /// multiple notifications are shown vertically. This method is responsible for positioning the given notification
    /// to the right position by setting its [Node#translateYProperty()]. The given `targetY` value is computed at layout
    /// time, so it's guaranteed to be correct, which implies that this method is automatically called by the pane's layout
    /// strategy.
    ///
    /// In the initial design, the translation to the given target was animated, but then I opted for something else
    /// (see [#animate(Notification, boolean)]). I kept this for flexibility.
    protected void position(Node node, double targetY) {
        boolean top = PositionUtils.isTop(getAlignment());
        node.setTranslateY(top ? -targetY : targetY);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-notification-pane");
    }

    @Override
    protected void layoutChildren() {
        Pos alignment = getAlignment();
        double y = 0;
        for (Notification n : visible.values()) {
            Node node = n.toNode();
            node.autosize();
            positionInArea(
                node, 0, 0, getWidth(), getHeight(),
                0, getPadding(),
                alignment.getHpos(), alignment.getVpos(), true
            );

            position(node, -y);
            y += node.getLayoutBounds().getHeight() + getSpacing();
        }
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Pos> alignment = new StyleableObjectProperty<>(
        StyleableProperties.ALIGNMENT,
        this,
        "alignment",
        Pos.CENTER
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    private final StyleableIntegerProperty maxVisible = PropUtils.intProperty()
        .bean(this)
        .name("maxVisible")
        .initialValue(1)
        .mapper(i -> NumberUtils.clamp(i, 1, 5))
        .asStyleable(StyleableProperties.MAX_VISIBLE, null);

    private final StyleableDoubleProperty spacing = new StyleableDoubleProperty(
        StyleableProperties.SPACING,
        this,
        "spacing",
        10.0
    );

    public Pos getAlignment() {
        return alignment.get();
    }

    /// Specifies the anchor for the notifications inside this pane.<br >
    /// By default, it's set to [Pos#CENTER].
    ///
    /// Can be set from CSS via the property: '-mfx-alignment'.
    public StyleableObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    public int getMaxVisible() {
        return maxVisible.get();
    }

    /// Specifies the maximum number of visible notifications. This property is clamped between 1 and 5, because I consider
    /// these to be sensible values. By default, it's set to 1.
    ///
    /// Can be set from CSS via the property: '-mfx-max-visible'.
    public StyleableIntegerProperty maxVisibleProperty() {
        return maxVisible;
    }

    public void setMaxVisible(int maxVisible) {
        this.maxVisible.set(maxVisible);
    }

    public double getSpacing() {
        return spacing.get();
    }

    /// Specifies the gap between each notification (when maxVisible > 1).<br >
    /// By default, it's set to 10.0.
    ///
    /// Can be set from CSS via the property: '-mfx-spacing'.
    public StyleableDoubleProperty spacingProperty() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing.set(spacing);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXNotificationPane> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXNotificationPane, Pos> ALIGNMENT =
            FACTORY.createEnumCssMetaData(
                Pos.class,
                "-mfx-alignment",
                MFXNotificationPane::alignmentProperty,
                Pos.CENTER
            );

        private static final CssMetaData<MFXNotificationPane, Number> MAX_VISIBLE =
            FACTORY.createSizeCssMetaData(
                "-mfx-max-visible",
                MFXNotificationPane::maxVisibleProperty,
                1
            );

        private static final CssMetaData<MFXNotificationPane, Number> SPACING =
            FACTORY.createSizeCssMetaData(
                "-mfx-spacing",
                MFXNotificationPane::spacingProperty,
                10.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Region.getClassCssMetaData(),
                ALIGNMENT, MAX_VISIBLE, SPACING
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters
    //================================================================================

    /// @return the set of pending notifications as an unmodifiable [SequencedSet].
    public SequencedSet<Notification> getPending() {
        return Collections.unmodifiableSequencedSet(pending);
    }

    /// @return the map of visible notifications as an unmodifiable [SequencedMap].
    public SequencedMap<Node, Notification> getVisible() {
        return Collections.unmodifiableSequencedMap(visible);
    }
}
