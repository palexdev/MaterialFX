/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.theming.enums;

import io.github.palexdev.mfxcomponents.controls.fab.MFXExtendedFab;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.base.Variant;

/**
 * Enumerator implementing {@link Variant} to define the variants of {@link MFXFab} and {@link MFXExtendedFab}.
 * <p></p>
 * Note that {@link MFXExtendedFab} doesn't have 'small' and 'large' variants, applying those will likely result
 * in un-styled components.
 */
public enum FABVariants implements Variant {
	SMALL("small"),
	LARGE("large"),
	LOWERED("lowered"),
	SURFACE("surface"),
	SECONDARY("secondary"),
	TERTIARY("tertiary"),
	;

	private final String styleClass;

	FABVariants(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String variantStyleClass() {
		return styleClass;
	}
}
