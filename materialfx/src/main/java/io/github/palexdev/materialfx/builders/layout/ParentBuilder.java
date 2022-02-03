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

package io.github.palexdev.materialfx.builders.layout;

import javafx.scene.Parent;

public class ParentBuilder<P extends Parent> extends NodeBuilder<P> {

	//================================================================================
	// Constructors
	//================================================================================
	public ParentBuilder(P parent) {
		super(parent);
	}

	public static ParentBuilder<Parent> parent(Parent parent) {
		return new ParentBuilder<>(parent);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public ParentBuilder<P> requestLayout() {
		node.requestLayout();
		return this;
	}

	public ParentBuilder<P> addStylesheets(String... stylesheets) {
		node.getStylesheets().addAll(stylesheets);
		return this;
	}

	public ParentBuilder<P> setStylesheets(String... stylesheets) {
		node.getStylesheets().setAll(stylesheets);
		return this;
	}
}
