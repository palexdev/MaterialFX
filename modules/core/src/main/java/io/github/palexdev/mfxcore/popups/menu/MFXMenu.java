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

package io.github.palexdev.mfxcore.popups.menu;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.input.ShortcutManager;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.popups.MFXPopover;
import io.github.palexdev.mfxcore.popups.MFXPopup;
import io.github.palexdev.mfxcore.popups.PopupState;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu.MenuConfig.Builder;
import io.github.palexdev.mfxcore.utils.CollectionUtils;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Direction;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Placement;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.x;
import static io.github.palexdev.mfxcore.base.beans.Position.position;
import static java.util.Optional.ofNullable;

/// Custom implementation of menus based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to '.root' and '.mfx-menu.'. This mimics JavaFX popups which also have the '.root' style class
/// applied. It's recommended to keep it so that if your theme defines some lookup color on the '.root' selector, it gets
/// propagated to the menus too.
///
/// ### Definition, Usage and Features
///
/// Menus, like tooltips, fall in the popovers group. They appear close to a specific UI element, usually triggered by
/// tapping/clicking on it. They present a set of options in a compact form.
///
/// Menus are non-modal, do not use a backdrop and have _light dismiss_
///
/// ### Implementation Details
///
/// Because menus are just popovers with a specific purpose, and therefore a bunch of extra features, [MFXMenu] does not
/// use any JavaFX class as the peer, rather it uses [MFXPopover] with composition over inheritance.
///
/// Because of this, the implementation becomes much easier, as all the basic stuff such as show/hide, positioning,
/// etc., is handled by the popover. Also, menus share some similarities with tooltips. Here are the key details:
/// 1) Just like tooltips, as a good practice, there should be at max one menu visible at a time. We use a static property
/// to keep track of the currently open menu, see [MenuTracker]. However, the difference with tooltips is that menus can
/// "contain" submenus! So, when we say at "max one menu open", we mean a `root menu`.
/// 2) By design, menus are tightly coupled with a specific UI element. Therefore, [#show(Node, double, double)] and
/// [#show(Node, Placement)] methods are overridden to throw an [UnsupportedOperationException].
/// Like tooltips, menus must be _installed_ onto a node, use [#install(Node)] or [#uninstall()] to disable the menu.
/// However, there's a difference here too. If you think about the context menu of a text field, it is shown at the
/// screen coordinates where the right mouse click occurs. So, unlike tooltips, menus can have both anchor-base positioning
/// and absolute positioning. For simplicity and design consistency, we unified the behavior in the _install mechanism_.
/// When the menu is installed, a [EventHandler] is added on the node to listen for [MouseEvent#MOUSE_CLICKED] events.
/// The [MenuConfig] allows you to specify which mouse button to use (Context menus usually use the right mouse button,
/// whereas menus in a toolbar usually use the primary) and the positioning strategy to use.
/// 3) The [#contentProperty()] is locked, bound to the peer's content, and even unbinding it won't change the menu's content.
/// This was a straightforward design decision. The content for this kind of popover is already known and preset, see [MFXMenuContent].
/// 4) To add entries to the menu, use the [#getItems()] list.
/// 5) Animations are disabled by default. The design convention for menus is that they should open and close instantly.
///
/// ### SubMenus
///
/// Submenus are conceptually easy but very hard to implement. I tried to replicate the native menu's behavior.
/// [MFXMenu] has three extra properties for this specific functionality:
/// 1) The `parent` property: this allows distinguishing between `root` menus and submenus. This is used, for example,
/// by the [MenuTracker] property to track only the first type.
/// 2) The `hoveredItem` property: this specifies the currently hovered entry in the menu. This piece of information is
/// crucial to the close behavior. When the property's value becomes `null`, any open submenu is closed, and this causes
/// all other submenus in the cascade (if any) to close too.
/// 3) When it comes to show/hide, submenus are an exception to the rule. They do not need to be installed on an owner,
/// as they typically appear on hover. So, a protected extra method [#showSub(Node)] allows this behavior
/// internally. (this may be changed in the future, or at least made configurable)
///
/// @see MenuConfig
/// @see #textColumnWidthProperty()
/// @see #iconColumnWidthProperty()
public class MFXMenu implements MFXPopup<Node>, MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final MenuTracker tracker = new MenuTracker();

    //================================================================================
    // Properties
    //================================================================================
    private final MFXPopover peer = new MFXPopover() {
        @Override
        protected void doShow(Node owner, double x, double y) {
            MFXMenu.tracker.set(MFXMenu.this);
            super.doShow(owner, x, y);
            getContent().requestFocusTraversal(TraversalDirection.NEXT); // move focus onto menu content
        }

        @Override
        public void hide() {
            tracker.set((WeakReference<MFXMenu>) null);
            // Also reset the hovered item!
            hoveredItem.set(null);
            super.hide();
        }

        @Override
        protected void onContentChanged() {
            Node content = getContent();
            if (content == null || content instanceof MFXMenuContent) {
                super.onContentChanged();
            } else {
                throw new IllegalStateException("Content must be of type MFXMenuContent! Got: " +
                                                ofNullable(content).map(Object::getClass).orElse(null)
                );
            }
        }

        @Override
        public List<String> defaultStyleClasses() {
            return MFXStyleable.extend(super.defaultStyleClasses(), "mfx-menu");
        }
    };

    private Node owner;
    private boolean anchorBasedPositioning;
    private Placement placement;
    private MouseButton triggerButton;
    private boolean filterMouseEvents;
    private boolean enableKeyTrigger;
    private MenuConfig config;
    private final ObservableList<MFXMenuItem> items;

    private WhenEvent<?> mTrigger;
    private WhenEvent<?> kTrigger;

    private final ReadOnlyDoubleWrapper textColumnWidth = new ReadOnlyDoubleWrapper(Region.USE_COMPUTED_SIZE) {
        @Override
        protected void invalidated() {
            getRoot().requestLayout();
        }
    };
    private final ReadOnlyDoubleWrapper iconColumnWidth = new ReadOnlyDoubleWrapper(Region.USE_COMPUTED_SIZE) {
        @Override
        protected void invalidated() {
            getRoot().requestLayout();
        }
    };

    // SubMenus Specific
    private Function<ObservableList<MFXMenuItem>, MFXMenu> subMenuFactory = items ->
        new MFXMenu(items).configure(cfg -> cfg
            .placement(Placement.placement(Pos.TOP_RIGHT, Direction.AFTER, Direction.AFTER))
            .offset(x(4.0))
        );
    private final ReadOnlyObjectWrapper<MFXMenu> parent = new ReadOnlyObjectWrapper<>(null);
    private final ObjectProperty<Node> hoveredItem = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenu() {
        this(FXCollections.observableArrayList());
    }

    public MFXMenu(MFXMenuItem... items) {
        this(FXCollections.observableArrayList(items));
    }

    public MFXMenu(ObservableList<MFXMenuItem> items) {
        MenuConfig.DEFAULT.apply(this);
        this.items = items;
        setContent(new MFXMenuContent(this));
    }

    //================================================================================
    // Methods
    //================================================================================
    public void install(Node owner) {
        if (isInstalled())
            throw new IllegalStateException("Menu is already installed on a node!");
        this.owner = owner;
        initMenu(owner);
    }

    public boolean isInstalled() {
        return owner != null;
    }

    protected void initMenu(Node owner) {
        mTrigger = WhenEvent.intercept(owner, MouseEvent.MOUSE_CLICKED)
            .condition(e -> e.getButton() == triggerButton)
            .handle(e -> {
                if (anchorBasedPositioning) {
                    if (!isShowing()) {
                        peer.show(owner, placement);
                    } else {
                        hide();
                    }
                    return;
                }

                // TODO do we need to do this for anchor-based positioning too? probably not
                if (isShowing()) {
                    setPosition(position(e.getScreenX(), e.getScreenY()));
                } else {
                    peer.show(owner, e.getScreenX(), e.getScreenY());
                }
            })
            .otherwise((_, _) -> hide())
            .asFilter(filterMouseEvents)
            .register();

        if (enableKeyTrigger) {
            kTrigger = WhenEvent.intercept(owner, KeyEvent.KEY_PRESSED)
                .condition(e -> e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE)
                .handle(e -> {
                    if (!isShowing()) {
                        peer.show(owner, placement);
                    } else {
                        hide();
                    }
                })
                .register();
        }

        // This effectively works for root menus only
        // Submenus are handled by the cells
        if (getContent() == null)
            peer.setContent(new MFXMenuContent(this));

        // install key shortcuts
        ShortcutManager.installFor(this);
    }

    public void uninstall() {
        getRootMenu().hide();
        if (mTrigger != null) {
            mTrigger.dispose();
            mTrigger = null;
        }
        if (kTrigger != null) {
            kTrigger.dispose();
            kTrigger = null;
        }
        setContent(null);
        ShortcutManager.uninstallFor(this);
        owner = null;
    }

    protected MFXMenu createSubMenu(MFXMenuItem item) {
        MFXMenu subMenu = ofNullable(subMenuFactory)
            .map(f -> f.apply(item.getSubItems()))
            .orElse(null);
        if (subMenu == null) return null;

        subMenu.owner = item;
        subMenu.setParentMenu(this);
        subMenu.setSubMenuFactory(subMenuFactory);
        subMenu.configure(cfg -> cfg
            .styleableParent(cfg.styleableParent == null ? getRoot() : cfg.styleableParent)
        );
        return subMenu;
    }

    /// This is used by [SubMenuHandler] to show a sub-menu on the screen, next to the element that owns it.
    /// The owner typically is one of the entries of the parent menu.
    void showSub(Node owner) {
        peer.show(owner, placement);
    }

    /// Convenience method to change the configuration of this menu. The provided builder starts with the values from
    /// the current config.
    public MFXMenu configure(Consumer<Builder> cfg) {
        Builder builder = MenuConfig.builder(getConfig());
        cfg.accept(builder);
        builder.build().apply(this);
        return this;
    }

    public void updateStylesheets() {
        peer.updateStylesheets();
    }

    protected MFXPopover getPeer() {
        return peer;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void show(Node owner, double x, double y) {
        throw new UnsupportedOperationException("Menus cannot be shown directly, but need to be installed on a 'owner' Node");
    }

    @Override
    public void show(Node owner, Placement placement) {
        throw new UnsupportedOperationException("Menus cannot be shown directly, but need to be installed on a 'owner' Node");
    }

    @Override
    public void hide() {
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
    public MenuConfig getConfig() {
        return config;
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
    // Getters/Setters
    //================================================================================

    /// @return the list containing the menu's entries.
    public ObservableList<MFXMenuItem> getItems() {
        return items;
    }

    /// @return a stream consisting of all the items and subitems from this menu
    /// @see CollectionUtils#flatten(Object, Function)
    public Stream<MFXMenuItem> getAllItems() {
        return getItems().stream()
            .flatMap(c -> CollectionUtils.flatten(c, MFXMenuItem::getSubItems));
    }

    /// @return whether the [#parentMenuProperty()]'s value is `null`.
    public boolean isRootMenu() {
        return getParentMenu() == null;
    }

    /// @return the `root` menu by climbing up the [#getParentMenu()] chain.
    public MFXMenu getRootMenu() {
        MFXMenu root = this;
        while (root.getParentMenu() != null)
            root = root.getParentMenu();
        return root;
    }

    public double getTextColumnWidth() {
        return textColumnWidth.get();
    }

    /// Specifies the minimum width for every item's leading text.
    ///
    /// Usually, by design, menus use a grid layout, in the sense that:
    /// - All icons are aligned on the first column
    /// - All items' labels are aligned on the second column
    /// - All the shortcuts are aligned to the right in the third column
    ///
    /// Now, in reality, using a grid would not be possible, because you would have to split the three nodes, and
    /// consequently it would not be possible to highlight items as a whole when hovering with the mouse for example.
    ///
    /// So, how do we deal with this? Through convention and a bit of trickery.
    /// 1. All icons must have the same size. If that is not possible, wrap the icon in a container and size it to be
    /// big enough to contain the biggest icon. (See `MFXIconWrapper` from _NFXResources_)
    /// 2. At layout time, the text width of each item is computed ([MFXMenuItem#textWidth()]) and the maximum set as
    /// the value of this property. Then, the default item's skin retrieves this value and adjusts the layout.
    ///
    /// Yes, it's complicated, I don't like it, but there's no other way and JavaFX does something similar internally too.
    public ReadOnlyDoubleProperty textColumnWidthProperty() {
        return textColumnWidth.getReadOnlyProperty();
    }

    protected void setTextColumnWidth(double width) {
        textColumnWidth.set(width);
    }

    public double getIconColumnWidth() {
        return iconColumnWidth.get();
    }

    /// Specifies the minimum width for every item's leading icon.
    ///
    /// Same story as [#textColumnWidthProperty()] but for the icons.
    public ReadOnlyDoubleProperty iconColumnWidthProperty() {
        return iconColumnWidth.getReadOnlyProperty();
    }

    protected void setIconColumnWidth(double width) {
        iconColumnWidth.set(width);
    }

    public Function<ObservableList<MFXMenuItem>, MFXMenu> getSubMenuFactory() {
        return subMenuFactory;
    }

    public void setSubMenuFactory(Function<ObservableList<MFXMenuItem>, MFXMenu> subMenuFactory) {
        this.subMenuFactory = subMenuFactory;
    }

    public MFXMenu getParentMenu() {
        return parent.get();
    }

    /// Specifies the parent of this menu. If it is `null`, then this is a `root` menu.
    public ReadOnlyObjectProperty<MFXMenu> parentMenuProperty() {
        return parent.getReadOnlyProperty();
    }

    protected void setParentMenu(MFXMenu parent) {
        this.parent.set(parent);
    }

    public Node getHoveredItem() {
        return hoveredItem.get();
    }

    /// Specifies the currently hovered entry in the menu. When this changes, submenus react appropriately
    /// (by either hiding the cascade or showing themselves).
    public ReadOnlyObjectProperty<Node> hoveredItemProperty() {
        return hoveredItem;
    }

    protected void setHoveredItem(Node item) {
        hoveredItem.set(item);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Custom extension of a [SimpleObjectProperty] to keep track of the currently open [MFXMenu] wrapped in a
    /// [WeakReference]. The [#set(WeakReference)] method is overridden to accept only `root` menus ([MFXMenu#isRootMenu()]
    /// returns `true`). When the value effectively changes (so a new root menu is about to be shown), if both the
    /// new and the old values are not `null`, it calls [MFXMenu#hide()] on the old value.
    static class MenuTracker extends SimpleObjectProperty<WeakReference<MFXMenu>> {
        public void set(MFXMenu menu) {
            set(new WeakReference<>(menu));
        }

        @Override
        public void set(WeakReference<MFXMenu> newValue) {
            // Keep track ONLY of root menus
            boolean isRoot = ofNullable(newValue)
                .map(Reference::get)
                .map(MFXMenu::isRootMenu)
                .orElse(false);
            if (!isRoot) return;

            ofNullable(get())
                .map(Reference::get)
                .filter(old -> old != newValue.get() && old.isShowing())
                .ifPresent(MFXMenu::hide);
            super.set(newValue);
        }
    }

    // Config

    /// A few notes on some peculiar configs:
    /// - The `anchorBasedPositioning` determines how the menu is going to be positioned on the screen. If `true`, the
    /// position is computed by [MFXPopupBase#computePosition(Object, Placement)]. Otherwise, it uses the mouse coordinates
    /// from the event that triggered the menu. Example: context menus (like the ones for text fields) use the mouse
    /// coordinates, while menus from toolbars are anchored to a button.
    /// - The `animationProvider` parameter allows you to set the animation for both `root` menus and all the submenus
    /// in the cascade. This is needed because submenus are built internally by [MFXMenuItems][MFXMenuItem] (check default skin).
    public record MenuConfig(
        boolean anchorBasedPositioning,
        Placement placement,
        Position offset,
        MouseButton triggerButton,
        boolean filterMouseEvents,
        boolean enableKeyTrigger,
        Node styleableParent
    ) implements Config<MFXMenu> {
        public static final MenuConfig DEFAULT = builder().build();

        @Override
        public void apply(MFXMenu menu) {
            menu.anchorBasedPositioning = anchorBasedPositioning;
            menu.placement = placement;
            menu.setOffset(offset);
            menu.triggerButton = triggerButton;
            menu.filterMouseEvents = filterMouseEvents;
            menu.enableKeyTrigger = enableKeyTrigger;
            menu.peer.setStyleableParent(styleableParent);
            menu.config = this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(MenuConfig config) {
            return new Builder()
                .anchorBasedPositioning(config.anchorBasedPositioning)
                .placement(config.placement)
                .offset(config.offset)
                .triggerButton(config.triggerButton)
                .filterMouseEvents(config.filterMouseEvents)
                .enableKeyTrigger(config.enableKeyTrigger)
                .styleableParent(config.styleableParent);
        }

        public static final class Builder {
            private boolean anchorBasedPositioning = true;
            private Placement placement = Placement.placement(Pos.BOTTOM_LEFT, Direction.AFTER, Direction.AFTER);
            private Position offset = Position.origin();
            private MouseButton triggerButton = MouseButton.SECONDARY;
            private boolean filterMouseEvents = false;
            private boolean enableKeyTrigger = false;
            private Node styleableParent;

            public Builder anchorBasedPositioning(boolean anchorBasedPositioning) {
                this.anchorBasedPositioning = anchorBasedPositioning;
                return this;
            }

            public Builder placement(Placement placement) {
                this.placement = placement;
                return this;
            }

            public Builder offset(Position offset) {
                this.offset = offset;
                return this;
            }

            public Builder triggerButton(MouseButton triggerButton) {
                this.triggerButton = triggerButton;
                return this;
            }

            public Builder filterMouseEvents(boolean filterMouseEvents) {
                this.filterMouseEvents = filterMouseEvents;
                return this;
            }

            public Builder enableKeyTrigger(boolean enableKeyTrigger) {
                this.enableKeyTrigger = enableKeyTrigger;
                return this;
            }

            public Builder styleableParent(Node styleableParent) {
                this.styleableParent = styleableParent;
                return this;
            }

            public MenuConfig build() {
                return new MenuConfig(
                    anchorBasedPositioning,
                    placement,
                    offset,
                    triggerButton,
                    filterMouseEvents,
                    enableKeyTrigger,
                    styleableParent
                );
            }
        }
    }
}
