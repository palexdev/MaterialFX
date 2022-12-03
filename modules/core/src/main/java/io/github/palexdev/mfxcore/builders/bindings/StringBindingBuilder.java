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

package io.github.palexdev.mfxcore.builders.bindings;

import io.github.palexdev.mfxcore.builders.base.BindingBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

public class StringBindingBuilder extends BindingBuilder<String, StringBinding> {

	public static StringBindingBuilder build() {
		return new StringBindingBuilder();
	}

	@Override
	protected StringBinding create() {
		if (mapper == null) {
			throw new IllegalStateException("Mapper has not been set!");
		}
		return Bindings.createStringBinding(mapper, getSourcesArray());
	}
}
