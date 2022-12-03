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

package io.github.palexdev.mfxcore.builders.base;

import javafx.beans.Observable;
import javafx.beans.binding.Binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class BindingBuilder<T, B extends Binding<? super T>> {
	protected List<Observable> sources = new ArrayList<>();
	protected Callable<T> mapper;

	protected abstract B create();

	public BindingBuilder<T, B> addSources(Observable... sources) {
		Collections.addAll(this.sources, sources);
		return this;
	}

	public BindingBuilder<T, B> setSources(Observable... sources) {
		this.sources.clear();
		addSources(sources);
		return this;
	}

	public BindingBuilder<T, B> setMapper(Callable<T> mapper) {
		this.mapper = mapper;
		return this;
	}

	public List<Observable> getSources() {
		return sources;
	}

	public Observable[] getSourcesArray() {
		return sources.toArray(Observable[]::new);
	}

	public Callable<T> getMapper() {
		return mapper;
	}

	public B get() {
		return create();
	}
}
