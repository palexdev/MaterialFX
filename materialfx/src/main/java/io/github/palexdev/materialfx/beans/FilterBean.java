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

package io.github.palexdev.materialfx.beans;

import io.github.palexdev.materialfx.enums.ChainMode;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A simple bean that has all the necessary information to produce a {@link Predicate}
 * for a given T object type.
 * <p></p>
 * It wraps the following data:
 * <p> - A String which is the query
 * <p> - An object of type {@link AbstractFilter}, which is effectively responsible for producing the {@link Predicate}
 * <p> - A {@link BiPredicateBean}, which is used by {@link AbstractFilter}, see {@link AbstractFilter#predicateFor(String)} or {@link AbstractFilter#predicateFor(String, BiPredicate)}
 * <p> - A {@link ChainMode} enumeration to specify how this filter should be combined with other filters
 *
 * @param <T> the type of objects to filter
 * @param <U> the type of objects on which the {@link BiPredicate} operates
 */
public class FilterBean<T, U> {
	//================================================================================
	// Properties
	//================================================================================
	private final String query;
	private final AbstractFilter<T, U> filter;
	private final BiPredicateBean<U, U> predicateBean;
	private ChainMode mode;

	//================================================================================
	// Constructors
	//================================================================================
	public FilterBean(String query, AbstractFilter<T, U> filter, BiPredicateBean<U, U> predicateBean) {
		this(query, filter, predicateBean, ChainMode.OR);
	}

	public FilterBean(String query, AbstractFilter<T, U> filter, BiPredicateBean<U, U> predicateBean, ChainMode mode) {
		this.query = query;
		this.filter = filter;
		this.predicateBean = predicateBean;
		this.mode = mode;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Calls {@link AbstractFilter#predicateFor(String)} with the query specified by this bean.
	 */
	public Predicate<T> predicate() {
		return filter.predicateFor(query);
	}

	/**
	 * @return the query, see {@link AbstractFilter} documentation for more info about the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @return the {@link AbstractFilter} specified by this bean
	 */
	public AbstractFilter<T, U> getFilter() {
		return filter;
	}

	/**
	 * Delegate for {@link AbstractFilter#name()}.
	 */
	public String getFilterName() {
		return filter.name();
	}

	/**
	 * @return the {@link BiPredicateBean} specified by this bean
	 */
	public BiPredicateBean<U, U> getPredicateBean() {
		return predicateBean;
	}

	/**
	 * Delegate for {@link BiPredicateBean#name()}.
	 */
	public String getPredicateName() {
		return predicateBean.name();
	}

	/**
	 * @return the {@link ChainMode} enumeration that specifies how this filter should be chained with other filters.
	 */
	public ChainMode getMode() {
		return mode;
	}

	/**
	 * Sets the chain mode to the specified one.
	 */
	public void setMode(ChainMode mode) {
		this.mode = mode;
	}
}
