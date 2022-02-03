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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.BaseToggleNodeBuilder;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.enums.TextPosition;

public class CircleToggleNodeBuilder extends BaseToggleNodeBuilder<MFXCircleToggleNode> {

	//================================================================================
	// Constructors
	//================================================================================
	public CircleToggleNodeBuilder() {
		this(new MFXCircleToggleNode());
	}

	public CircleToggleNodeBuilder(MFXCircleToggleNode toggleNode) {
		super(toggleNode);
	}

	public static CircleToggleNodeBuilder circleToggleNode() {
		return new CircleToggleNodeBuilder();
	}

	public static CircleToggleNodeBuilder circleToggleNode(MFXCircleToggleNode circleToggleNode) {
		return new CircleToggleNodeBuilder(circleToggleNode);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public CircleToggleNodeBuilder setGap(double gap) {
		node.setGap(gap);
		return this;
	}

	public CircleToggleNodeBuilder setSize(double size) {
		node.setSize(size);
		return this;
	}

	public CircleToggleNodeBuilder setTextPosition(TextPosition textPosition) {
		node.setTextPosition(textPosition);
		return this;
	}
}
