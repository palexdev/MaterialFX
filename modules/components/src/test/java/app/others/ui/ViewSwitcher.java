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

package app.others.ui;

import io.github.palexdev.mfxcore.utils.Memoizer;
import javafx.scene.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

// TODO implement animated switching for fun?
public class ViewSwitcher<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final Map<T, Function<T, Node>> views = new LinkedHashMap<>();

	//================================================================================
	// Methods
	//================================================================================
	public ViewSwitcher<T> register(T id, Function<T, Node> sceneSupplier) {
		views.put(id, Memoizer.memoize(sceneSupplier));
		return this;
	}

	public ViewSwitcher<T> unregister(T id) {
		views.remove(id);
		return this;
	}

	public Node load(T id) {
		return views.get(id).apply(id);
	}

	public Map<T, Function<T, Node>> views() {
		return views;
	}
}
