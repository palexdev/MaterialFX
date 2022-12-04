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

package io.github.palexdev.mfxcore.enums;

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;

/**
 * Enumeration to identify the various states of a {@link IBinding}
 */
public enum BindingState {

	/**
	 * This special state describes bindings that have just been created and have yet to
	 * be activated.
	 */
	NULL,

	/**
	 * This state describes bindings that have been disposed.
	 */
	DISPOSED,

	/**
	 * This state describes bindings that are currently active.
	 */
	BOUND,

	/**
	 * This state describes bindings that have been deactivated.
	 * <p>
	 * This is different from {@link #DISPOSED}!
	 */
	UNBOUND
}
