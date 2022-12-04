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

package io.github.palexdev.mfxcore.base.bindings;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Specialization of {@link Function} to also add the support for "orElse".
 *
 * @param <T> – the type of the input to the function
 * @param <R> – the type of the result of the function, as well as the type of the orElse supplier
 */
public class Mapper<T, R> implements Function<T, R> {
	//================================================================================
	// Properties
	//================================================================================
	protected Function<T, R> fn;
	private Supplier<R> orElse = () -> null;

	//================================================================================
	// Constructors
	//================================================================================
	protected Mapper() {
	}

	public Mapper(Function<T, R> fn) {
		this.fn = fn;
	}

	public static <T, R> Mapper<T, R> of(Function<T, R> fn) {
		return new Mapper<>(fn);
	}

	//================================================================================
	// Methods
	//================================================================================
	@Override
	public R apply(T t) {
		return (t != null) ? fn.apply(t) : orElse.get();
	}

	@Override
	public <V> Mapper<T, V> andThen(Function<? super R, ? extends V> after) {
		return new Mapper<>(fn).andThen(after);
	}

	@Override
	public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
		return new Mapper<>(fn).compose(before);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the {@link Function} of this mapper
	 */
	public Function<T, R> getFn() {
		return fn;
	}

	/**
	 * @return the "orElse" {@link Supplier} of this mapper
	 */
	public Supplier<R> getOrElse() {
		return orElse;
	}

	/**
	 * Sets the "orElse" {@link Supplier} of this mapper.
	 */
	public Mapper<T, R> orElse(Supplier<R> orElse) {
		this.orElse = orElse;
		return this;
	}
}
