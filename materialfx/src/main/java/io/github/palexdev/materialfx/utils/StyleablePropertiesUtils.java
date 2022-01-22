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

package io.github.palexdev.materialfx.utils;

import javafx.css.CssMetaData;
import javafx.css.Styleable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StyleablePropertiesUtils {

	private StyleablePropertiesUtils() {}

	@SafeVarargs
	public static List<CssMetaData<? extends Styleable, ?>> cssMetaDataList(List<CssMetaData<? extends Styleable, ?>> styleable, CssMetaData<? extends Styleable, ?>... cssMetaData) {
		CssMetaDataList styleableMetaData = new CssMetaDataList(styleable);
		styleableMetaData.addAll(cssMetaData);
		return styleableMetaData.toUnmodifiable();
	}

	public static class CssMetaDataList extends ArrayList<CssMetaData<? extends Styleable, ?>> {
		public CssMetaDataList() {
		}

		public CssMetaDataList(Collection<? extends CssMetaData<? extends Styleable, ?>> c) {
			super(c);
		}

		@SafeVarargs
		public final boolean addAll(CssMetaData<? extends Styleable, ?>... cssMetaData) {
			return Collections.addAll(this, cssMetaData);
		}

		public List<CssMetaData<? extends Styleable, ?>> toUnmodifiable() {
			return Collections.unmodifiableList(this);
		}
	}
}
