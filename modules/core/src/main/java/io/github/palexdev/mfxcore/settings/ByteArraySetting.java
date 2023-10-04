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

package io.github.palexdev.mfxcore.settings;

public class ByteArraySetting extends Setting<Byte[]> {

	//================================================================================
	// Constructors
	//================================================================================
	public ByteArraySetting(String name, String description, Byte[] defaultValue, Settings container) {
		super(name, description, defaultValue, container);
	}

	public static ByteArraySetting of(String name, String description, Byte[] defaultValue, Settings container) {
		return new ByteArraySetting(name, description, defaultValue, container);
	}

	//================================================================================
	// Methods
	//================================================================================
	protected byte[] toPrimitive(Byte[] boxed) {
		if (boxed == null) return null;
		if (boxed.length == 0) return new byte[0];

		byte[] result = new byte[boxed.length];
		for (int i = 0; i < boxed.length; i++) {
			result[i] = boxed[i];
		}
		return result;
	}

	protected Byte[] toBoxed(byte[] boxed) {
		if (boxed == null) return null;
		if (boxed.length == 0) return new Byte[0];

		Byte[] result = new Byte[boxed.length];
		for (int i = 0; i < boxed.length; i++) {
			result[i] = boxed[i];
		}
		return result;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Byte[] get() {
		byte[] arr = container.prefs().getByteArray(name, toPrimitive(defaultValue));
		return toBoxed(arr);
	}

	@Override
	public void set(Byte[] val) {
		container.prefs.putByteArray(name, toPrimitive(val));
	}
}
