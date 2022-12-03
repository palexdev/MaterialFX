/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.builders.nodes;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class PaneBuilder<P extends Pane> extends RegionBuilder<P> {

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("unchecked")
	public PaneBuilder() {
		this((P) new Pane());
	}

	public PaneBuilder(P pane) {
		super(pane);
	}

	public static PaneBuilder<Pane> pane() {
		return new PaneBuilder<>();
	}

	public static PaneBuilder<Pane> pane(Pane pane) {
		return new PaneBuilder<>(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public PaneBuilder<P> addChildren(Node... children) {
		node.getChildren().addAll(children);
		return this;
	}

	public PaneBuilder<P> setChildren(Node... children) {
		node.getChildren().setAll(children);
		return this;
	}

	public PaneBuilder<P> removeChildren(Node... children) {
		node.getChildren().removeAll(children);
		return this;
	}
}
