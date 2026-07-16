/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.popups;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.popups.MFXTooltip.TooltipConfig.Builder;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/// Custom implementation of tooltips based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to '.root' and '.mfx-tooltip.'. This mimics JavaFX popups which also have the '.root' style class
/// applied. It's recommended to keep it so that if your theme defines some lookup color on the '.root' selector, it gets
/// propagated to the tooltips too.
///
/// ### Definition, Usage and Features
///
/// Tooltips are a type of popup appearing close to a specific UI element, usually triggered by hovering over an element
/// or tapping on it. They display contextual information, for example, to explain the purpose of a button.
///
/// Tooltips are non-modal, so they don’t disrupt the user’s flow.
/// They don’t use a backdrop and generally disappear when you move the cursor away from the trigger, therefore, we can
/// say they have _light dismiss_.
///
/// They fall in the popovers group.
///
/// ### Implementation Details
///
/// Because tooltips are just popovers with a different activation, and a slightly different purpose, [MFXTooltip] does
/// not use any JavaFX class as the peer, rather it uses [MFXPopover] with composition over inheritance.
///
/// Surprisingly, if we think about tooltips in such terms, their implementation becomes stupidly easy.
/// Here are the key details:
/// 1) As a good practice, there should be at max one tooltip visible at any time. We use a static property to keep track
/// of the currently open tooltip. See [TooltipTracker]
/// 2) By design tooltips are tightly coupled with a specific UI element. Therefore, [#show(Node, double, double)] and
/// [#show(Node, Placement)] methods are overridden to throw an [UnsupportedOperationException].
/// Typically, tooltips are _installed_ onto a node. So, to use a tooltip, you can call [#install(Node)] or [#uninstall()]
/// to disable it.
/// 3) Show and hide mechanisms remain the same. The only additional mechanic is that they are delayed by a certain amount
/// of time. [MFXTooltip] uses a single [PauseTransition] to delay the calls to show and hide methods inside the [MFXPopover]
/// peer. The animation's duration and action are configured every time according to the needs.
///
/// @see TooltipConfig
public class MFXTooltip implements MFXPopup<Node>, MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================

    private static final TooltipTracker tracker = new TooltipTracker();
    public static final String PROP_KEY = MFXTooltip.class.getName();

    //================================================================================
    // Properties
    //================================================================================

    private final MFXPopover peer = new MFXPopover() {
        @Override
        protected void doShow(Node owner, double x, double y) {
            MFXTooltip.tracker.set(MFXTooltip.this);
            super.doShow(owner, x, y);
        }

        @Override
        public void hide() {
            tracker.set((WeakReference<MFXTooltip>) null);
            super.hide();
        }

        @Override
        public void show(Node owner, Placement placement) {
            if (timer.getStatus() == Animation.Status.RUNNING)
                timer.stop();

            timer.setDuration(inDelay);
            timer.setOnFinished(_ -> super.show(owner, placement));
            timer.playFromStart();
        }

        @Override
        public List<String> defaultStyleClasses() {
            return MFXStyleable.styleClasses("root", "mfx-tooltip");
        }
    };

    private Node owner;
    private Placement placement;

    private final PauseTransition timer = new PauseTransition();
    private final List<Disposable> handlers = new ArrayList<>();

    private Duration inDelay;
    private Duration outDelay;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTooltip() {
        TooltipConfig.DEFAULT.apply(this);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void install(Node owner) {
        if (isInstalled()) throw new IllegalStateException("Tooltip is already installed on a node!");
        this.owner = owner;
        owner.getProperties().put(PROP_KEY, this);
        initTooltip(owner);
    }

    public boolean isInstalled() {
        return owner != null;
    }

    protected void initTooltip(Node owner) {
        Collections.addAll(handlers,
            WhenEvent.intercept(owner, MouseEvent.MOUSE_ENTERED)
                .handle(_ -> peer.show(owner, placement))
                .asFilter()
                .register(),
            WhenEvent.intercept(owner, MouseEvent.MOUSE_EXITED)
                .handle(_ -> hideDelayed())
                .asFilter()
                .register(),
            When.onInvalidated(owner.hoverProperty())
                .condition(h -> isShowing() && (!h && !owner.isFocused()))
                .then(_ -> hideDelayed())
                .invalidating(owner.focusedProperty())
                .listen(),
            When.onInvalidated(contentProperty().flatMap(Node::hoverProperty))
                .condition(h -> isShowing() && !h)
                .then(_ -> {
                    if (timer.getStatus() == Animation.Status.RUNNING) return;
                    hide();
                })
                .listen());
    }

    public void uninstall() {
        hide();
        handlers.forEach(Disposable::dispose);
        handlers.clear();
        if (owner != null) {
            owner.getProperties().remove(PROP_KEY);
            this.owner = null;
        }
    }

    protected void hideDelayed() {
        if (timer.getStatus() == Animation.Status.RUNNING)
            timer.stop();

        if (!isShowing()) return;
        timer.setDuration(outDelay);
        timer.setOnFinished(_ -> {
            if (getContent() != null && !getContent().isHover())
                hide();
        });
        timer.playFromStart();
    }

    /// Convenience method to change the configuration of this tooltip. The provided builder starts with the values from
    /// the current config.
    public MFXTooltip configure(Consumer<Builder> cfg) {
        Builder builder = TooltipConfig.builder(getConfig());
        cfg.accept(builder);
        builder.build().apply(this);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void show(Node owner, double x, double y) {
        throw new UnsupportedOperationException("Tooltips cannot be shown directly, but need to be installed on a 'owner' Node");
    }

    @Override
    public void show(Node owner, Placement placement) {
        throw new UnsupportedOperationException("Tooltips cannot be shown directly, but need to be installed on a 'owner' Node");
    }

    @Override
    public void hide() {
        if (timer.getStatus() == Animation.Status.RUNNING)
            timer.stop();
        peer.hide();
    }

    @Override
    public void reposition() {
        peer.reposition();
    }

    @Override
    public Node getOwner() {
        return owner;
    }

    @Override
    public Parent getRoot() {
        return peer.getRoot();
    }

    @Override
    public NodeProperty contentProperty() {
        return peer.contentProperty();
    }

    @Override
    public PositionProperty positionProperty() {
        return peer.positionProperty();
    }

    @Override
    public Position getOffset() {
        return peer.getOffset();
    }

    @Override
    public void setOffset(Position offset) {
        peer.setOffset(offset);
    }

    @Override
    public ReadOnlyObjectProperty<PopupState> stateProperty() {
        return peer.stateProperty();
    }

    @Override
    public Position getPeerPosition() {
        return peer.getPeerPosition();
    }

    @Override
    public void setStyleClass(String... styleClass) {
        peer.setStyleClass(styleClass);
    }

    @Override
    public PopupAnimation getAnimation() {
        return peer.getAnimation();
    }

    @Override
    public void setAnimation(PopupAnimation animation) {
        peer.setAnimation(animation);
    }

    @Override
    public TooltipConfig getConfig() {
        return (TooltipConfig) peer.config;
    }

    @Override
    public List<String> defaultStyleClasses() {
        return peer.defaultStyleClasses();
    }

    @Override
    public String getTypeSelector() {
        return peer.getTypeSelector();
    }

    @Override
    public String getId() {
        return peer.getId();
    }

    @Override
    public ObservableList<String> getStyleClass() {
        return peer.getStyleClass();
    }

    @Override
    public String getStyle() {
        return peer.getStyle();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return peer.getCssMetaData();
    }

    @Override
    public Styleable getStyleableParent() {
        return peer.getStyleableParent();
    }

    @Override
    public ObservableSet<PseudoClass> getPseudoClassStates() {
        return peer.getPseudoClassStates();
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Custom extension of a [SimpleObjectProperty] to keep track of the currently open [MFXTooltip] wrapped in a
    /// [WeakReference]. When the value changes, if both the new and the old values are not `null`, it calls
    /// [MFXTooltip#hide()] (hides immediately without delay) on the old value.
    static class TooltipTracker extends SimpleObjectProperty<WeakReference<MFXTooltip>> {
        public void set(MFXTooltip tooltip) {
            set(new WeakReference<>(tooltip));
        }

        @Override
        public void set(WeakReference<MFXTooltip> newValue) {
            if (newValue != null) {
                Optional.ofNullable(get())
                    .map(Reference::get)
                    .filter(old -> old != newValue.get() && old.isShowing())
                    .ifPresent(MFXTooltip::hide);
            }
            super.set(newValue);
        }
    }

    // Config

    public record TooltipConfig(
        Placement placement,
        Position offset,
        Duration inDelay,
        Duration outDelay,
        Node styleableParent
    ) implements Config<MFXTooltip> {
        public static final TooltipConfig DEFAULT = builder().build();

        @Override
        public void apply(MFXTooltip tooltip) {
            tooltip.placement = placement;
            tooltip.setOffset(offset);
            tooltip.inDelay = inDelay;
            tooltip.outDelay = outDelay;
            tooltip.peer.setStyleableParent(styleableParent);
            tooltip.peer.config = this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(TooltipConfig config) {
            return new Builder()
                .placement(config.placement)
                .offset(config.offset)
                .inDelay(config.inDelay)
                .outDelay(config.outDelay);
        }

        public static final class Builder {
            private Placement placement = Placement.Outside.BOTTOM_CENTER;
            private Position offset = Position.origin();
            private Duration inDelay = Duration.millis(500);
            private Duration outDelay = Duration.millis(500);
            private Node styleableParent;

            public Builder placement(Placement placement) {
                this.placement = placement;
                return this;
            }

            public Builder offset(Position offset) {
                this.offset = offset;
                return this;
            }

            public Builder inDelay(Duration inDelay) {
                this.inDelay = inDelay;
                return this;
            }

            public Builder inDelay(double millis) {
                return inDelay(Duration.millis(millis));
            }

            public Builder outDelay(Duration outDelay) {
                this.outDelay = outDelay;
                return this;
            }

            public Builder outDelay(double millis) {
                return outDelay(Duration.millis(millis));
            }

            public Builder styleableParent(Node styleableParent) {
                this.styleableParent = styleableParent;
                return this;
            }

            public TooltipConfig build() {
                return new TooltipConfig(
                    placement,
                    offset,
                    inDelay,
                    outDelay,
                    styleableParent
                );
            }
        }
    }
}
