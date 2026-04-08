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

package io.github.palexdev.mfxcore.popups.menu;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.input.KeyStroke;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.Memoizer;
import io.github.palexdev.mfxcore.utils.fx.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;

/// Base implementation of menu entries to be used in [MFXMenu]. Extends [MFXLabeled] as the most basic entry has at least
/// three things: an icon, the text, and the shortcut.
///
/// The default skin and behavior implementations are [MFXMenuItemSkin] and [MFXMenuItemBehavior] respectively.
/// The default style class is set to `.mfx-menu-item` to not clash with JavaFX items.
///
/// Menu entries can also have:
/// - An action to run on trigger (click, enter, etc... Depends on the behavior)
/// - A list of [MFXMenuItems][MFXMenuItem], denoting, if not empty, that the entry can show a submenu. This is handled
/// by the default skin.
///
/// Other than that, there are just a few more things to note:
/// - When an item has sub-items (can show a submenu), the style class `.sub` is added onto the item, allowing for
/// specific CSS customization.
/// - If the item has sub-items (can show a submenu), the shortcut text will never be displayed. It will be replaced by
/// a region with style class `.svg-icon` which should be used as the arrow for the submenu
/// - Comes with a default stylesheet applied to give the aforementioned submenu icon a shape and a color, [#DEFAULT_CSS]
///
/// **NOTE:** the menu instance returned by [#getMenu()] is **not** available until the item's skin is created!
/// See [MFXMenuContentSkin#updateChildren()].
///
/// @see MFXMenu#textColumnWidthProperty()
/// @see MFXMenu#iconColumnWidthProperty()
public class MFXMenuItem extends MFXLabeled {

    //================================================================================
    // Static Properties
    //================================================================================

    private static final String DEFAULT_CSS = new CSSFragment("""
        .mfx-menu-item.sub .svg-icon {
          -fx-background-color: rgba(0, 0, 0, 0.87);
          -fx-pref-width: 8px;
          -fx-max-height: 12px;
          -fx-shape: "M439.1 297.4C451.6 309.9 451.6 330.2 439.1 342.7L279.1 502.7C266.6 515.2 246.3 515.2 233.8 502.7C221.3 490.2 221.3 469.9 233.8 457.4L371.2 320L233.9 182.6C221.4 170.1 221.4 149.8 233.9 137.3C246.4 124.8 266.7 124.8 279.2 137.3L439.2 297.3z";
        }
        
        .mfx-menu-item > .icon {
          -fx-padding: 0px 8px 0px 0px;
        }
        
        .menu-separator {
          -fx-background-color: rgba(0, 0, 0, 0.1);
          -fx-background-insets: 1px 2px 2px 2px;
          -fx-padding: 2px 0px;
        }
        """).toDataUri();

    //================================================================================
    // Properties
    //================================================================================

    private MFXMenu menu;
    private final ObjectProperty<KeyStroke> shortcut = new SimpleObjectProperty<>();
    private final ObjectProperty<Runnable> action = new SimpleObjectProperty<>();
    private final ObservableList<MFXMenuItem> subItems = FXCollections.observableArrayList();

    private final TextMeasurementCache tmc;
    private Supplier<Node> icRetriever = Memoizer.memoize(() -> lookup(".icon"));

    //================================================================================
    // Constructors
    //================================================================================

    public MFXMenuItem() {
        this("");
    }

    public MFXMenuItem(String text) {
        this(text, null);
    }

    public MFXMenuItem(String text, Node graphic) {
        super(text, graphic);
        tmc = new TextMeasurementCache(this);
        getStylesheets().add(DEFAULT_CSS);
    }

    public static MenuBuilder menuItem(String text) {
        return new MenuBuilder().text(text);
    }

    public static MenuBuilder menuItem(String text, Node graphic) {
        return new MenuBuilder().text(text).graphic(graphic);
    }

    public static MenuBuilder subMenu(String text, MenuBuilder... builders) {
        return menuItem(text).subItems(builders);
    }

    public static MenuBuilder subMenu(String text, Node graphic, MenuBuilder... builders) {
        return menuItem(text, graphic).subItems(builders);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// @return the width of the text specified by the [#textProperty()] with the current font, [#fontProperty()].
    /// Does not take into account the graphic and padding! The computation is cached by using [TextMeasurementCache]
    public double textWidth() {
        return tmc.getSnappedWidth();
    }

    public double iconWidth() {
        // FIXME ugly! is there another way?
        Node iconContainer = icRetriever.get();
        return iconContainer != null ? LayoutUtils.snappedBoundWidth(iconContainer) : USE_COMPUTED_SIZE;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXMenuItemBehavior(this);
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXMenuItemSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-menu-item");
    }

    @Override
    protected MFXSkinBase<?> buildSkin() {
        // reset cache!
        icRetriever = Memoizer.memoize(() -> lookup(".icon"));
        return super.buildSkin();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public MFXMenu getMenu() {
        return menu;
    }

    protected void setMenu(MFXMenu menu) {
        this.menu = menu;
    }

    public KeyStroke getShortcut() {
        return shortcut.get();
    }

    public ObjectProperty<KeyStroke> shortcutProperty() {
        return shortcut;
    }

    public void setShortcut(KeyStroke shortcut) {
        this.shortcut.set(shortcut);
    }

    public Runnable getAction() {
        return action.get();
    }

    public ObjectProperty<Runnable> actionProperty() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action.set(action);
    }

    public ObservableList<MFXMenuItem> getSubItems() {
        return subItems;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// This handles the following interactions:
    /// - On mouse click runs the action specified by the entry's item and closes the menu from the root if the action was not
    /// `null`!
    ///  On various other mouse interactions it enables/disables the `armed` pseudo class on the entry
    /// - On key presses runs actions such as key navigation (including showing and closing submenus) and trigger
    /// (run action or open submenu)
    public static class MFXMenuItemBehavior extends MFXBehavior<MFXMenuItem> {

        private Supplier<SubMenuHandler> subMenuHandlerAccessor;

        public MFXMenuItemBehavior(MFXMenuItem item) {
            super(item);
        }

        @Override
        public void init() {
            MFXMenuItem item = getNode();
            register(
                intercept(item, MouseEvent.MOUSE_PRESSED).handle(this::mousePressed),
                intercept(item, MouseEvent.MOUSE_RELEASED).handle(this::mouseReleased),
                intercept(item, MouseEvent.MOUSE_EXITED).handle(this::mouseExited),
                intercept(item, MouseEvent.MOUSE_CLICKED).handle(this::mouseClicked),
                intercept(item, KeyEvent.KEY_PRESSED).handle(this::keyPressed).asFilter()
            );
        }

        @Override
        public void mousePressed(MouseEvent e, Runnable callback) {
            PseudoClasses.setOn(getNode(), "armed", true);
        }

        @Override
        public void mouseReleased(MouseEvent e, Runnable callback) {
            PseudoClasses.setOn(getNode(), "armed", false);
        }

        @Override
        public void mouseExited(MouseEvent e, Runnable callback) {
            PseudoClasses.setOn(getNode(), "armed", false);
        }

        @Override
        public void mouseClicked(MouseEvent e, Runnable callback) {
            if (e.getButton() == MouseButton.PRIMARY) {
                runAction();
            }
        }

        @Override
        public void keyPressed(KeyEvent e, Runnable callback) {
            MFXMenuItem item = getNodeAs(MFXMenuItem.class);
            SubMenuHandler subMenuHandler = getSubMenuHandler();
            switch (e.getCode()) {
                case SPACE, ENTER -> {
                    if (subMenuHandler != null) {
                        subMenuHandler.show();
                        subMenuHandler.focus();
                    } else {
                        runAction();
                    }
                }
                case UP -> item.requestFocusTraversal(TraversalDirection.UP);
                case DOWN -> item.requestFocusTraversal(TraversalDirection.DOWN);
                case TAB -> {
                    if (e.isShiftDown()) {
                        item.requestFocusTraversal(TraversalDirection.UP);
                    } else {
                        item.requestFocusTraversal(TraversalDirection.DOWN);
                    }
                }
                case RIGHT -> {
                    if (subMenuHandler != null) {
                        subMenuHandler.show();
                        subMenuHandler.focus();
                    }
                }
                case LEFT -> {
                    if (!item.getMenu().isRootMenu()) {
                        item.getMenu().hide();
                    }
                }
                case ESCAPE -> item.getMenu().hide();
            }
            e.consume();
        }

        public void runAction() {
            MFXMenuItem item = getNode();
            if (item.getAction() != null) {
                item.getAction().run();
                item.getMenu().getRootMenu().hide();
            }
        }

        protected SubMenuHandler getSubMenuHandler() {
            return subMenuHandlerAccessor != null ? subMenuHandlerAccessor.get() : null;
        }

        protected void setSubMenuHandlerAccessor(Supplier<SubMenuHandler> subMenuHandlerAccessor) {
            this.subMenuHandlerAccessor = subMenuHandlerAccessor;
        }
    }

    public static class MFXMenuItemSkin extends MFXSkinBase<MFXMenuItem> {

        protected final BoundLabel leading;
        protected final StackPane iconContainer;
        private final Label trailing;
        private final Region tIcon;
        private final Region surface;

        private SubMenuHandler subMenuHandler;

        public MFXMenuItemSkin(MFXMenuItem item) {
            super(item);

            // Init
            leading = createLeadingLabel();
            iconContainer = new StackPane() {
                @Override
                protected void layoutChildren() {
                    for (Node child : getChildren()) {
                        child.autosize();
                        positionInArea(child, 0, 0, getWidth(), getHeight(), 0, getPadding(), HPos.CENTER, VPos.CENTER, true);
                    }
                }
            };
            iconContainer.getStyleClass().add("icon");
            trailing = new Label();
            trailing.getStyleClass().add("trailing");
            tIcon = new Region();
            tIcon.getStyleClass().add("svg-icon");
            trailing.setGraphic(tIcon);

            surface = new Region();
            surface.getStyleClass().add("pseudo-surface");
            surface.setManaged(false);
            StyleUtils.initProperty(surface.visibleProperty(), false);

            // Finalize
            addListeners();
            getChildren().addAll(surface, iconContainer, leading, trailing);
        }

        /// Adds the following listeners:
        /// - A listener on the entry's [#hoverProperty()] to show/hide the submenu if present. This also sets the parent menu's
        /// [MFXMenu#hoveredItemProperty()] to this entry.
        /// - A listener on the [MFXMenuItem#getSubItems()] list to build/dispose the submenu as needed.
        protected void addListeners() {
            MFXMenuItem item = getSkinnable();
            listeners(
                When.onInvalidated(item.hoverProperty())
                    .then(h -> {
                        setMenuHoveredItem();
                        item.requestFocus(); // Reset focus acquired by key navigation
                        if (subMenuHandler == null) return;
                        if (h) {
                            subMenuHandler.show();
                        } else {
                            subMenuHandler.hide();
                        }
                    })
                    .executeNow(item::isHover),
                When.onInvalidated(item.graphicProperty()).then(_ -> updateIcon()).executeNow(),
                When.observe(this::handleSubMenu, item.getSubItems()).executeNow()
            );

            trailing.textProperty().bind(item.shortcutProperty().map(KeyStroke::toDisplayString));
        }

        protected void updateIcon() {
            Node icon = getSkinnable().getGraphic();
            if (icon == null) {
                iconContainer.getChildren().clear();
            } else {
                iconContainer.getChildren().setAll(icon);
            }
        }

        /// This method is mainly responsible for creating or disposing the submenu depending on [MFXMenuItem#getSubItems()].
        ///
        /// It also updates the trailing label to only show the text or submenu icon accordingly ([ContentDisplay]),
        /// and adds/removes the `.sub` style class to the item
        protected void handleSubMenu() {
            MFXMenuItem item = getSkinnable();
            if (item.getSubItems().isEmpty()) {
                item.getStyleClass().remove("sub");
                trailing.setContentDisplay(ContentDisplay.TEXT_ONLY);
                if (subMenuHandler != null) {
                    subMenuHandler.dispose();
                    subMenuHandler = null;
                }
            } else {
                trailing.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                item.getStyleClass().add("sub");
                subMenuHandler = new SubMenuHandler(item);
            }
        }

        protected void setMenuHoveredItem() {
            MFXMenuItem item = getSkinnable();
            MFXMenu menu = item.getMenu();
            menu.setHoveredItem(item);
        }


        protected double minIconWidth() {
            MFXMenuItem item = getSkinnable();
            MFXMenu menu = item.getMenu();
            if (menu == null) return Region.USE_COMPUTED_SIZE;
            return Math.max(menu.getIconColumnWidth(), LayoutUtils.snappedBoundWidth(iconContainer));
        }

        /// Computes the minimum width required for the leading element of this item, including the value specified by the
        /// [MFXMenu#textColumnWidthProperty()] which is crucial for having all the menu's items aligned.
        protected double minLeadingWidth() {
            MFXMenuItem item = getSkinnable();
            MFXMenu menu = item.getMenu();
            if (menu == null) return Region.USE_COMPUTED_SIZE;
            return Math.max(menu.getTextColumnWidth(), LayoutUtils.snappedBoundWidth(leading));
        }

        protected BoundLabel createLeadingLabel() {
            BoundLabel label = new BoundLabel(getSkinnable());
            label.graphicProperty().unbind();
            label.setGraphic(null);
            label.contentDisplayProperty().unbind();
            label.setContentDisplay(ContentDisplay.TEXT_ONLY);
            label.getStyleClass().add("leading");
            return label;
        }

        @Override
        protected void registerBehavior() {
            super.registerBehavior();
            if (getBehavior() instanceof MFXMenuItemBehavior mib) {
                mib.setSubMenuHandlerAccessor(() -> this.subMenuHandler);
            }
        }

        // Ensures that the sub icon is not too close to the leading text
        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return 120.0;
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return leftInset + minIconWidth() + minLeadingWidth() + LayoutUtils.snappedBoundWidth(trailing) + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return topInset + Math.max(
                LayoutUtils.snappedBoundHeight(leading),
                LayoutUtils.snappedBoundHeight(trailing)
            ) + bottomInset;
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            MFXMenuItem item = getSkinnable();
            surface.resizeRelocate(0, 0, item.getWidth(), item.getHeight());

            double remainingWidth = w;
            layoutInArea(trailing, x, y, w, h, 0, HPos.RIGHT, VPos.CENTER);
            remainingWidth -= trailing.getWidth();

            iconContainer.resize(minIconWidth(), h);
            positionInArea(iconContainer, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);
            remainingWidth -= iconContainer.getWidth();

            leading.resize(remainingWidth, LayoutUtils.snappedBoundHeight(leading));
            positionInArea(leading, x + iconContainer.getWidth(), y, w, h, 0, HPos.LEFT, VPos.CENTER);
        }

        @Override
        public void dispose() {
            if (subMenuHandler != null) {
                subMenuHandler.dispose();
                subMenuHandler = null;
            }
            super.dispose();
        }
    }
}
