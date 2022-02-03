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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.enums.ChainMode;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.skins.MFXFilterPaneSkin;
import io.github.palexdev.materialfx.utils.PredicateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

import java.util.function.Predicate;

/**
 * This control allows to produce a {@link Predicate} for a given object type
 * interactively, meaning that the filter is assembled from the user choices.
 * To produce a filter the user must choose the object's field, input/choose a query
 * and a way to evaluate the object's field against the query.
 * <p></p>
 * From now on all code examples to better understand the functionalities of this control
 * will use these POJO classes:
 * <pre>
 * {@code
 *      public enum Gender {
 *          MALE, FEMALE
 *      }
 *
 *      public class Person {
 *          private final String name;
 *          private final int age;
 *          private final Gender gender;
 *          private final City city;
 *
 *          public Person(String name, int age, Gender gender, City city) {
 *              this.name = name;
 *              this.age = age;
 *              this.gender = gender;
 *              this.city = city;
 *          }
 *
 *          public String name() {
 *              return name;
 *          }
 *
 *          public int age() {
 *              return age;
 *          }
 *
 *          public Gender gender() {
 *              return gender;
 *          }
 *
 *          public City city() {
 *              return city;
 *          }
 *      }
 *
 *      public class City {
 *          private final String name;
 *          private final long population;
 *
 *          public City(String name, long population) {
 *              this.name = name;
 *              this.population = population;
 *          }
 *
 *          public String name() {
 *              return name;
 *          }
 *
 *          public long population() {
 *              return population;
 *          }
 *      }
 * }
 * </pre>
 * <p></p>
 * To specify on which fields to operate the filters must be added like this:
 * <pre>
 * {@code
 *      MFXFilterPane<Person> fp = new MFXFilterPane<>();
 *      AbstractFilter<Person, String> nameFilter = new StringFilter<>("Name", Person::name)
 *      AbstractFilter<Person, Integer> ageFilter = new IntegerFilter<>("Age", Person:.age);
 *
 *      // MFXFilterPane is so powerful and versatile that you can also filter by nested objects, something like this for example...
 *      AbstractFilter<Person, Long> populationFilter = new LongFilter<>("City Population", person -> person.city().population());
 *
 *      // It even works for enumerators...
 *      AbstractFilter<Person, Enum<Gender>> genderFilter = new EnumFilter<>("Gender", Person::gender, Gender.class); // Note that the type is necessary
 *
 *      // Finally...
 *      fp.getFilters().addAll(nameFilter, ageFilter, populationFilter, genderFilter);
 * }
 * </pre>
 *
 * <p></p>
 * When a filter is created through the add button, a {@link FilterBean} is created and added to a list
 * which holds the "active filters", {@link #getActiveFilters()}.
 * <p>
 * Note that the list is not unmodifiable, potentially, you could even add your own custom filters, the UI will be updated anyway.
 * <p>
 * As you can read in the {@link FilterBean} documentation, they can be chained according to the specified {@link FilterBean#getMode()},
 * this is also interactive, meaning that when you build more than one filter, a node will appear between them, that node specifies
 * the {@link ChainMode}, by clicking on it, you can switch between modes.
 * <p></p>
 * Once filters you have finished you can produce a filter by calling {@link #filter()}.
 * <p>
 * The control also offers to icons that are intended to produce a filter or reset the control,
 * to set their behavior use {@link #setOnFilter(EventHandler)} and {@link #setOnReset(EventHandler)}.
 *
 * @param <T>
 */
public class MFXFilterPane<T> extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-filter-pane";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFilterPane.css");
	private final StringProperty headerText = new SimpleStringProperty(I18N.getOrDefault("filterPane.headerText"));
	private final ObservableList<AbstractFilter<T, ?>> filters = FXCollections.observableArrayList();
	private final ObservableList<FilterBean<T, ?>> activeFilters = FXCollections.observableArrayList();

	private EventHandler<MouseEvent> onFilter = event -> {};
	private EventHandler<MouseEvent> onReset = event -> {};

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterPane() {
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	/**
	 * Builds a predicate from the list of built filters (active filters).
	 * <p></p>
	 * The {@link FilterBean} are chained by using {@link PredicateUtils#chain(Predicate, Predicate, ChainMode)}.
	 * <p></p>
	 * If the list is empty by default a predicate that always returns true is built.
	 */
	public Predicate<T> filter() {
		Predicate<T> filter = null;
		ChainMode mode = null;

		for (FilterBean<T, ?> activeFilter : activeFilters) {
			if (filter == null) {
				filter = activeFilter.predicate();
				mode = activeFilter.getMode();
				continue;
			}

			filter = PredicateUtils.chain(filter, activeFilter.predicate(), mode);
			mode = activeFilter.getMode();
		}

		return filter != null ? filter : t -> true;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String getHeaderText() {
		return headerText.get();
	}

	/**
	 * Specifies the text of the header.
	 */
	public StringProperty headerTextProperty() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText.set(headerText);
	}

	/**
	 * @return the list of {@link AbstractFilter}s. Each of them
	 * represents an object's field on which the filter operates
	 */
	public ObservableList<AbstractFilter<T, ?>> getFilters() {
		return filters;
	}

	/**
	 * @return the list of built filters
	 */
	public ObservableList<FilterBean<T, ?>> getActiveFilters() {
		return activeFilters;
	}

	/**
	 * @return the action invoked when clicking on the filter icon
	 */
	public EventHandler<MouseEvent> getOnFilter() {
		return onFilter;
	}

	/**
	 * Sets the action to perform when the filter icon is clicked.
	 */
	public void setOnFilter(EventHandler<MouseEvent> onFilter) {
		this.onFilter = onFilter;
	}

	/**
	 * @return the action invoked when clicking on the reset icon
	 */
	public EventHandler<MouseEvent> getOnReset() {
		return onReset;
	}

	/**
	 * Sets the action to perform when the reset icon is clicked.
	 */
	public void setOnReset(EventHandler<MouseEvent> onReset) {
		this.onReset = onReset;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXFilterPaneSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
