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

package io.github.palexdev.mfxcore.popups;

import java.util.*;

import io.github.palexdev.mfxcore.controls.ThemeEngine;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;

/// This is the pane used as the root of every [MFXPopup], wrapping the content.
///
/// ### Styling
///
/// Every popup (dialogs, popovers, tooltips, etc.) is a separate window from the main stage. JavaFX uses CSS to style
/// the application, applying the rules by traversing the scenegraph three. Because a popup is a separate window, it
/// is detached from the scenegraph on which you added your stylesheets, therefore, it won't get styled.<br >
/// The global useragent stylesheet is an exception as it is applied to every window.
///
/// This is clearly an issue, and the workaround would be to add your stylesheets on every popup too.
///
/// With [PopupControl], JavaFX offers another way to style popups. By specifying a `styleable parent` ([Node#getStyleableParent()]),
/// you automatically gain two functionalities:
/// 1) You can style the dialog by selecting the owner (styleable parent), e.g.:
/// ```css
/// .my-button .my-popup {...}
/// ```
/// 2) The dialog catches all the stylesheets applied along the path to its `styleable parent`, so you don't have to add
/// them yourself
/// ```java
/// Button myButton = new Button("My Button");
/// myButton.getStylesheets().add("my-button.css");
/// // Install a popup on the button with getStyleableParent() overridden to `myButton`
/// // The stylesheet will be applied to the popup too and the above example CSS will work
/// ```
/// Such functionality, which I deem crucial, is backported for my [popups][MFXPopup] hierarchy too, but:
/// 1) It's more dynamic/versatile. The `styleable parent` can be changed at any time through a property which will also
/// automatically update the style when needed.
/// 2) The API is only partly private and can be easily overridden
///
/// #### Additional Notes:
/// - On every popup there is a small default "embedded" stylesheet to reset some uglies from JavaFX, see [#RESET_CSS].
public class PopupRoot extends StackPane {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final String RESET_CSS = new CSSFragment("""
        PopupRoot {
          -fx-background-color: transparent;
          -fx-padding: 0px;
        }
        """).toDataUri();

    private static final ThemeEngine themeEngine = ServiceLoader.load(ThemeEngine.class)
        .findFirst()
        .orElse(null);

    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<Node> styleableParent = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            updateStylesheets();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================

    public PopupRoot() {
        if (themeEngine != null) {
            themeEngine.themeModeProperty().subscribe(_ -> {
                ThemeEngine.ThemeMode mode = themeEngine.resolveThemeMode();
                PseudoClasses.setOn(this, "dark", mode == ThemeEngine.ThemeMode.DARK);
            });
        }
        updateStylesheets();
    }

    //================================================================================
    // Methods
    //================================================================================

    protected void setContent(Node content) {
        if (content == null) {
            getChildren().clear();
        } else {
            getChildren().setAll(content);
        }
    }

    /// This is responsible for applying all the stylesheets collected by [#fetchStylesheets()] on this node.
    public void updateStylesheets() {
        Collection<String> stylesheets = fetchStylesheets();
        getStylesheets().setAll(stylesheets);
    }

    /// Core method responsible for fetching all the user-applied stylesheets from the 'parent' specified by the
    /// [#styleableParentProperty()].
    ///
    /// The algorithm is fairly simple:
    /// 1) If the `styleable parent` is `null` or not a [Parent], it returns only the [#RESET_CSS] stylesheet.
    /// 2) Traverses the scenegraph up from the set `styleable parent` to the root, adding each stylesheet to a `Set`
    /// (no duplicates!)
    /// 3) We also add all the stylesheets applied on the `styleable parent`'s scene, if any. This is an important difference
    /// over the JavaFX counterpart. In my apps, I usually add the stylesheets to the main scene, not to the root node.
    /// Since effectively it's the same thing, I thought we could include them too in the hierarchy.
    /// 4) Adds the [#RESET_CSS] stylesheet to the end.
    /// 5) Returns all the collected stylesheets in the `Set` but in [reverse][SequencedSet#reversed()] order
    /// (because we go up the scenegraph).
    private Collection<String> fetchStylesheets() {
        Styleable styleable = getStyleableParent();
        if (!(styleable instanceof Parent parent))
            return Collections.singleton(RESET_CSS);

        SequencedSet<String> set = new LinkedHashSet<>();
        while (parent != null) {
            set.addAll(parent.getStylesheets());
            parent = parent.getParent();
        }

        Optional.ofNullable(((Parent) styleable).getScene())
            .map(Scene::getStylesheets)
            .ifPresent(set::addAll);

        set.add(RESET_CSS);
        return set.reversed();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    @Override
    public Node getStyleableParent() {
        return styleableParent.get();
    }

    /// Specifies the `styleable parent` node of this pane (and transitively of the popup using this root).
    public ObjectProperty<Node> styleableParentProperty() {
        return styleableParent;
    }

    public void setStyleableParent(Node styleableParent) {
        this.styleableParent.set(styleableParent);
    }
}
