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

import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TreeItemStream {
	public static <T> Stream<AbstractMFXTreeItem<T>> stream(AbstractMFXTreeItem<T> item) {
		return asStream(new TreeItemIterator<>(item));
	}

	private static <T> Stream<AbstractMFXTreeItem<T>> asStream(TreeItemIterator<T> iterator) {
		Iterable<AbstractMFXTreeItem<T>> iterable = () -> iterator;

		return StreamSupport.stream(
				iterable.spliterator(),
				false
		);
	}

	public static <T> Stream<AbstractMFXTreeItem<T>> flattenTree(final AbstractMFXTreeItem<T> item) {
		return Stream.concat(
				Stream.of(item),
				item.getItems().stream().flatMap(TreeItemStream::flattenTree)
		);
	}
}
