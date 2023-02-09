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

package io.github.palexdev.mfxresources.base.properties;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.input.MouseEvent;

import java.util.function.Function;

/**
 * Simple extension of {@link ReadOnlyObjectWrapper} to be used for {@link MFXIconWrapper} objects,
 * also offers a series of convenient methods to manipulate the icon with fluent API.
 */
public class WrappedIconProperty extends ReadOnlyObjectWrapper<MFXIconWrapper> {

	//================================================================================
	// Constructors
	//================================================================================
	public WrappedIconProperty() {
	}

	public WrappedIconProperty(MFXIconWrapper initialValue) {
		super(initialValue);
	}

	public WrappedIconProperty(Object bean, String name) {
		super(bean, name);
	}

	public WrappedIconProperty(Object bean, String name, MFXIconWrapper initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Setters
	//================================================================================

	/**
	 * Sets the {@link MFXIconWrapper#iconProperty()} to the given {@link MFXFontIcon}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty setIcon(MFXFontIcon icon) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper(icon));
		} else {
			val.setIcon(icon);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXIconWrapper#enableRippleGenerator(boolean)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty enableRippleGenerator(boolean enable) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper().enableRippleGenerator(enable));
		} else {
			val.enableRippleGenerator(enable);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXIconWrapper#enableRippleGenerator(boolean, Function)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty enableRippleGenerator(boolean enable, Function<MouseEvent, Position> positionFunction) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper().enableRippleGenerator(enable, positionFunction));
		} else {
			val.enableRippleGenerator(enable, positionFunction);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXIconWrapper#makeRound(boolean)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty makeRound(boolean state) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper().makeRound(state));
		} else {
			val.makeRound(state);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXIconWrapper#makeRound(boolean, double)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty makeRound(boolean state, double radius) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper().makeRound(state, radius));
		} else {
			val.makeRound(state, radius);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXIconWrapper#setSize(double)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXIconWrapper} will
	 * be created and no exception will be raised.
	 */
	public WrappedIconProperty setSize(double size) {
		MFXIconWrapper val = get();
		if (val == null) {
			set(new MFXIconWrapper(null, size));
		} else {
			val.setSize(size);
		}
		return this;
	}
}