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

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

/**
 * Helper class which is responsible for parsing the stylesheets for a given {@link Parent}.
 * <p>
 * The list is automatically rebuilt if the parent's stylesheets change.
 * <p>
 * This approach is very simple yet effective (ffs even a baby could make it), but it has a limitation at the moment.
 * The stylesheets parsing is limited to the specified parent, it won't scan the entire scenegraph, I could also implement something like
 * that, it's simple, it's just needed to mimic the behavior of the JavaFX' StyleManager, but at the moment I don't think
 * it's really useful, so... we'll see in future if needed.
 */
public class MFXCSSBridge {
	//================================================================================
	// Properties
	//================================================================================
	private Parent parent;
	private final ObservableList<String> stylesheets = FXCollections.observableArrayList();
	private final InvalidationListener stylesheetsChanged = invalidated -> initializeStylesheets();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCSSBridge(Parent parent) {
		this.parent = parent;
		initializeStylesheets();
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Called by the constructor the first time.
	 * <p>
	 * Responsible for parsing and building the stylesheets list.
	 */
	public void initializeStylesheets() {
		stylesheets.clear();
		if (parent == null) return;

		stylesheets.addAll(parent.getStylesheets());
	}

	/**
	 * Adds the listener responsible for updating the stylesheets list
	 * to the parent's stylesheets observable list.
	 */
	public void addListeners() {
		if (parent == null) return;
		parent.getStylesheets().addListener(stylesheetsChanged);
	}

	/**
	 * Disposes the MFXCSSBridge by removing the stylesheetsChanged listener.
	 */
	public void dispose() {
		if (parent != null) {
			parent.getStylesheets().removeListener(stylesheetsChanged);
		}
	}

	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	/**
	 * @return the parsed stylesheets list
	 */
	public ObservableList<String> getStylesheets() {
		return stylesheets;
	}
}
