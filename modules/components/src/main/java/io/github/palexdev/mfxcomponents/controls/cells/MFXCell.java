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

package io.github.palexdev.mfxcomponents.controls.cells;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.SelectionEventHandler;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.MomentumTransition;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.virtualizedfx.base.VFXContainer;
import io.github.palexdev.virtualizedfx.base.VFXContext;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXCellBase;
import io.github.palexdev.virtualizedfx.cells.VFXLabeledCellSkin;
import io.github.palexdev.virtualizedfx.cells.VFXSimpleCell;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.list.VFXList;
import io.github.palexdev.virtualizedfx.list.VFXListHelper;
import io.github.palexdev.virtualizedfx.list.VFXListState;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;

/// Simple extension of [VFXSimpleCell] and base cell for all MaterialFX components.
///
/// ### Main features:
/// - Implements selection: for this to work properly, any virtualized control should register an instance of [ISelectionModel]
/// in its [VFXContainer#context()]! If the service is not preset, the cell will simply not be selectable.
/// - Adds a [MFXRippleGenerator] and a [MFXSurface] to express user interaction with the cell, [MFXCellSkin]
/// - Implements focus handling and scrolling, see [MFXCellBehavior]
public class MFXCell<T> extends VFXSimpleCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final BooleanProperty selected = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            PseudoClasses.SELECTED.setOn(MFXCell.this, get());
        }
    };
    protected ISelectionModel<T> selectionModel;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXCell(T item) {
        super(item);
    }

    public MFXCell(T item, StringConverter<T> converter) {
        super(item, converter);
    }

    {
        setFocusTraversable(true);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @SuppressWarnings("unchecked")
    @Override
    public void onCreated(VFXContext<T> context) {
        super.onCreated(context);
        if ((selectionModel = context.get(ISelectionModel.class)) != null) {
            selected.bind(BooleanBindingBuilder.build()
                .setMapper(() -> {
                    int index = getIndex();
                    return selectionModel.contains(index);
                })
                .addSources(indexProperty(), selectionModel.selection())
                .get()
            );
        }
    }

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXCellBehavior<>(this);
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXCellSkin<>(this);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    protected ISelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selected;
    }

    protected void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Default skin implementation for [MFXCell] and extension of [VFXLabeledCellSkin].
    ///
    /// Adds a [MFXSurface] and a [MFXRippleGenerator] to express user interaction with the cell.
    public static class MFXCellSkin<T> extends VFXLabeledCellSkin<T> {
        private final MFXSurface surface;
        private final MFXRippleGenerator rg;

        public MFXCellSkin(MFXCell<T> cell) {
            super(cell);

            surface = new MFXSurface(cell);
            surface.getStates().add(new MFXSurface.State(
                1,
                _ -> cell.isSelected(),
                MFXSurface::getPressedOpacity
            ));

            rg = new MFXRippleGenerator(cell);
            rg.getStyleClass().add("surface-ripple");
            rg.setMeToPosConverter(me ->
                (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
            );
            getChildren().addAll(0, List.of(surface, rg));
        }

        @Override
        protected void update() {
            MFXCell<T> cell = getControl();
            T item = cell.getItem();
            label.setText(cell.getConverter().toString(item));
        }

        @Override
        protected void registerBehavior() {
            super.registerBehavior();
            MFXCell<T> cell = getControl();
            MFXBehavior<? extends Node> behavior = getBehavior();
            events(
                intercept(cell, MouseEvent.MOUSE_PRESSED).handle(rg::generate),
                intercept(cell, MouseEvent.MOUSE_RELEASED).handle(_ -> rg.release()),
                intercept(cell, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked),
                intercept(cell, MouseEvent.MOUSE_EXITED).handle(_ -> rg.release()),
                intercept(cell, KeyEvent.KEY_PRESSED).handle(e -> behavior.keyPressed(e, () -> {
                    if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                        Bounds b = cell.getLayoutBounds();
                        rg.generate(b.getCenterX(), b.getCenterY());
                        rg.release();
                    }
                }))

            );
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            VFXCellBase<T> cell = getSkinnable();
            super.layoutChildren(x, y, w, h);
            surface.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
            rg.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
        }

        @Override
        public void dispose() {
            getChildren().clear();
            super.dispose();
        }

        @Override
        protected MFXCell<T> getControl() {
            return (MFXCell<T>) getSkinnable();
        }
    }

    /// Default behavior implementation for [MFXCell].
    ///
    /// Implements the necessary logic to: handle selection (via mouse and keyboard), handle focus traversal and scroll.
    ///
    /// @see SelectionEventHandler
    public static class MFXCellBehavior<T> extends CellBaseBehavior<T> {
        private Animation focusScrollAnimation;

        public MFXCellBehavior(MFXCell<T> cell) {
            super(cell);
        }

        protected void traverseAndScroll(TraversalDirection direction) {
            MFXCell<T> cell = getNode();
            cell.requestFocusTraversal(direction);

            VFXContainer<T> container = cell.context().getContainer();
            if (!(container instanceof VFXList<?, ?> list)) {
                // TODO cover other containers? For now I think this makes more sense for lists only
                return;
            }

            VFXListState<?, ?> state = list.getState();
            Node focused = state.getCellsByIndexUnmodifiable().values().stream()
                .map(VFXCell::toNode)
                .filter(Node::isFocusVisible)
                .findFirst()
                .orElse(null);
            if (focused == null) return;

            VFXListHelper<?, ?> helper = list.getHelper();
            Bounds focusedBounds = focused.getBoundsInParent();

            double vMin, vMax, bMin, bMax;
            if (list.getOrientation() == Orientation.VERTICAL) {
                vMin = Math.abs(helper.getViewportPosition().y());
                vMax = vMin + list.getHeight();
                bMin = focusedBounds.getMinY();
                bMax = focusedBounds.getMaxY();
            } else {
                vMin = Math.abs(helper.getViewportPosition().x());
                vMax = vMin + list.getWidth();
                bMin = focusedBounds.getMinX();
                bMax = focusedBounds.getMaxX();
            }

            double scrollDelta;
            if (bMin < vMin) {
                scrollDelta = bMin - vMin;
            } else if (bMax > vMax) {
                scrollDelta = bMax - vMax;
            } else {
                return;
            }

            if (Animations.isPlaying(focusScrollAnimation))
                focusScrollAnimation.stop();
            focusScrollAnimation = MomentumTransition.fromTime(
                scrollDelta,
                M3Motion.SHORT4.toMillis()
            ).setOnUpdate(list::scrollBy);
            focusScrollAnimation.play();
        }

        @Override
        public void mouseClicked(MouseEvent me, Runnable callback) {
            if (me.getButton() != MouseButton.PRIMARY) return;
            MFXCell<T> cell = getNode();
            ISelectionModel<T> sm = cell.getSelectionModel();
            if (sm != null) sm.eventHandler().handle(me, cell.getIndex());
            callback.run();
        }

        @Override
        public void keyPressed(KeyEvent ke, Runnable callback) {
            MFXCell<T> cell = getNode();
            switch (ke.getCode()) {
                case ENTER, SPACE -> {
                    ISelectionModel<T> sm = cell.getSelectionModel();
                    if (sm != null) sm.eventHandler().handle(ke, cell.getIndex());
                }
                case UP, RIGHT, DOWN, LEFT -> {
                    traverseAndScroll(TraversalDirection.valueOf(ke.getCode().name()));
                    ke.consume();
                }
            }
            callback.run();
        }

        @Override
        public MFXCell<T> getNode() {
            return (MFXCell<T>) super.getNode();
        }
    }
}
