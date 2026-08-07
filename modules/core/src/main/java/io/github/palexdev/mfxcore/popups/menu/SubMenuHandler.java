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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.Disposable;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyEvent;

import static io.github.palexdev.mfxcore.observables.When.observe;

/// Utility class to make the submenu's handling easier and cleaner.
public class SubMenuHandler {
    private MFXMenuItem item;
    private MFXMenu subMenu;
    private final List<Disposable> disposables = new ArrayList<>();

    public SubMenuHandler(MFXMenuItem item) {
        this.item = item;
        subMenu = item.getMenu().createSubMenu(item);
        Collections.addAll(disposables,
            observe(this::hide, item.getMenu().hoveredItemProperty()).listen(),
            observe(() -> {
                item.getMenu().setHoveredItem(null);
                subMenu.hide();
            }, item.getMenu().positionProperty()).listen()
        );
    }

    /// Shows the submenu by calling [MFXMenu#showSub(Node)] with the placement specified by the submenu's config.
    ///
    /// Also resets the submenu's hovered item to `null`.
    public void show() {
        subMenu.setHoveredItem(null);
        subMenu.showSub(item);
    }

    /// Hides the submenu only if its parent's [MFXMenu#hoveredItemProperty()] is not this entry.
    public void hide() {
        Node hc = subMenu.getParentMenu().getHoveredItem();
        if (hc != item) {
            subMenu.hide();
        }
    }

    /// When a submenu is shown by a [KeyEvent], transfer focus from its content to its first item.
    public void focus() {
        Node content = subMenu.getContent();
        content.requestFocusTraversal(TraversalDirection.NEXT);
    }

    /// Delegate to [MFXMenu#isShowing()].
    public boolean isShowing() {
        return subMenu.isShowing();
    }

    /// Sets the submenu's content to the product of the given function, which accepts the submenu itself as the input.
    ///
    /// This is to allow custom implementations of [MFXMenuContent] to be propagated to the submenus, because by default
    /// every [MFXMenu]'s content is set to [MFXMenuContent].
    // TODO this is not reachable by the users for now
    public void setContent(Function<MFXMenu, ? extends MFXMenuContent> contentSupplier) {
        subMenu.setContent(contentSupplier.apply(subMenu));
    }

    /// @return the managed submenu
    public MFXMenu getSubMenu() {
        return subMenu;
    }

    public void dispose() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        subMenu.uninstall();
        subMenu = null;
        item = null;
    }
}
