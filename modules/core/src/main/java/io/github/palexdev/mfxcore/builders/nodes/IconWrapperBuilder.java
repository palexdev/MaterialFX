/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.builders.nodes;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.MFXIconWrapper;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public class IconWrapperBuilder extends NodeBuilder<MFXIconWrapper> {

	//================================================================================
	// Constructors
	//================================================================================
	public IconWrapperBuilder() {
		this(new MFXIconWrapper());
	}

	public IconWrapperBuilder(MFXIconWrapper node) {
		super(node);
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static IconWrapperBuilder build() {
		return new IconWrapperBuilder();
	}

	public static IconWrapperBuilder build(MFXIconWrapper icon) {
		return new IconWrapperBuilder(icon);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public IconWrapperBuilder addRippleGenerator() {
		node.addRippleGenerator();
		return this;
	}

	public IconWrapperBuilder defaultRippleGeneratorBehavior() {
		node.defaultRippleGeneratorBehavior();
		return this;
	}

	public IconWrapperBuilder rippleGeneratorBehavior(Function<MouseEvent, Position> positionFunction) {
		node.rippleGeneratorBehavior(positionFunction);
		return this;
	}

	public IconWrapperBuilder setSize(double size) {
		node.setSize(size);
		return this;
	}

	public IconWrapperBuilder setIcon(Node icon) {
		node.setIcon(icon);
		return this;
	}

	public IconWrapperBuilder setIcon(Supplier<Node> iconSupplier) {
		node.setIcon(iconSupplier.get());
		return this;
	}

	public IconWrapperBuilder removeIcon() {
		node.removeIcon();
		return this;
	}
}
