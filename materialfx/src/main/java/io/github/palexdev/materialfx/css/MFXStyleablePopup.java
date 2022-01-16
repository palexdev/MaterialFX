/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.css;

import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.materialfx.skins.MFXContextMenuSkin;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.Region;

/**
 * JavaFX offers a special type of Popup, the {@link PopupControl}, to allow styling
 * its content with CSS. It would be a great control....if only it worked properly, but just like most of JavaFX's
 * controls it is borked too because those code monkeys at JavaFX are dumb.
 * <p></p>
 * Since I could not make it work no matter what I tried I was tired of this bullshit and designed my
 * own solution.
 * <p>
 * {@link MFXPopup} now implements this interface allowing CSS to really fucking work as it should!
 * <p></p>
 * The system is quite simple at the moment:
 * <p>
 * First when creating a {@code MFXStyleablePopup} you must set which is the parent that has the stylesheet,
 * use {@link #setPopupStyleableParent(Parent)}. Then the interface, thanks to a helper class (the {@link MFXCSSBridge},
 * will parse the parent's user agent stylesheet and all its stylesheets automatically, even if changed at runtime (the list
 * will always be rebuilt for simplicity). Then it's up to the popup implementation to connect the bridge to the
 * node on which apply the stylesheets.
 */
public interface MFXStyleablePopup {

	/**
	 * @return the node that has the necessary stylesheets to customize the popup
	 */
	Parent getPopupStyleableParent();

	/**
	 * Sets the node that has the necessary stylesheets to customize the popup.
	 */
	void setPopupStyleableParent(Parent parent);

	/**
	 * @return the parsed stylesheets
	 */
	ObservableList<String> getStyleSheets();

	/**
	 * This method works just like {@link Region#getUserAgentStylesheet()}.
	 * <p></p>
	 * Any {@link MFXStyleablePopup} should offer the possibility of setting a base stylesheet.
	 * It's up to the skin on how to use it though (an example can be found here {@link MFXContextMenuSkin}).
	 */
	String getUserAgentStylesheet();
}
