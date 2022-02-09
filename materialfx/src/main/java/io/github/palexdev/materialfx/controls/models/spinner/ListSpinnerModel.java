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

package io.github.palexdev.materialfx.controls.models.spinner;

import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import io.github.palexdev.virtualizedfx.beans.NumberRange;
import io.github.palexdev.virtualizedfx.utils.ListChangeHelper;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Concrete implementation of {@link AbstractSpinnerModel} to work with lists of any type.
 * <p></p>
 * {@code ListSpinnerModel} adds the {@link #converterProperty()} (since we know the kind of data the model will deal with),
 * and three new properties, {@link #itemsProperty()}, {@link #getCurrentIndex()}, {@link #incrementProperty()}.
 * <p></p>
 * The model works by keeping the current value's index in the list and updating that index, even
 * when the list is modified, see {@link #updateCurrentIndex(ListChangeListener.Change)}.
 * <p></p>
 * The constructor initializes the model with these values:
 * <p> - The converter uses {@code toString(T)} on the value or empty string if the value is null
 * <p> - The default value is an empty {@link ObservableList}
 * <p> - The increment is 1
 * <p> - The initial value is the first element of the list (if not empty)
 */
public class ListSpinnerModel<T> extends AbstractSpinnerModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ListProperty<T> items = new SimpleListProperty<>();
	private int currentIndex = -1;
	private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
	private final IntegerProperty increment = new SimpleIntegerProperty();

	//================================================================================
	// Constructors
	//================================================================================
	public ListSpinnerModel() {
		this(FXCollections.observableArrayList());
	}

	public ListSpinnerModel(ObservableList<T> items) {
		setConverter(FunctionalStringConverter.to(t -> t != null ? t.toString() : ""));
		setItems(items);
		setIncrement(1);

		this.items.addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue) reset();
		});
		this.items.addListener((ListChangeListener<? super T>) this::updateCurrentIndex);

		if (!items.isEmpty()) {
			currentIndex = 0;
			setValue(items.get(currentIndex));
		}
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Responsible for updating the current value's index when the list changes.
	 * <p></p>
	 * The value won't change as the index will always be updated according to the value,
	 * unless the current value is removed from the list.
	 */
	private void updateCurrentIndex(ListChangeListener.Change<? extends T> change) {
		if (currentIndex == -1 && !change.getList().isEmpty()) {
			currentIndex = 0;
			setValue(change.getList().get(currentIndex));
			return;
		}

		if (change.getList().isEmpty()) {
			reset();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.processChange(change, NumberRange.of(0, Integer.MAX_VALUE));
		ListChangeProcessor updater = new ListChangeProcessor(Set.of(currentIndex));
		c.processReplacement((replaced, removed) -> {
			T value = items.get(currentIndex);
			setValue(value);
		});
		c.processAddition((from, to, added) -> {
			updater.computeAddition(added.size(), from);
			List<Integer> indexes = new ArrayList<>(updater.getIndexes());
			if (!indexes.isEmpty()) {
				currentIndex = indexes.get(0);
				T value = items.get(currentIndex);
				setValue(value);
			}
		});
		c.processRemoval((from, to, removed) -> {
			updater.computeRemoval(removed, from);
			List<Integer> indexes = new ArrayList<>(updater.getIndexes());
			if (!indexes.isEmpty()) {
				currentIndex = indexes.get(0);
				T value = items.get(currentIndex);
				setValue(value);
			}
		});
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * If the items list is empty exits immediately.
	 * <p></p>
	 * Increments the current index by {@link #incrementProperty()}, if the new index
	 * is greater than the list's last index and {@link #isWrapAround()} is true, the new index
	 * will be set to 0, otherwise to {@code items.size() - 1}
	 * <p></p>
	 * At the end the value is updated with the new index.
	 */
	@Override
	public void next() {
		if (items.isEmpty()) return;
		int newIndex = currentIndex + getIncrement();
		if (newIndex > items.size() - 1) {
			newIndex = isWrapAround() ? 0 : items.size() - 1;
		}
		currentIndex = newIndex;
		setValue(items.get(newIndex));
	}

	/**
	 * If the items list is empty exits immediately.
	 * <p></p>
	 * Decrements the current index by {@link #incrementProperty()}, if the new index
	 * is lesser than 0 and {@link #isWrapAround()} is true, the new index
	 * will be set to {@code items.size() - 1}, otherwise to 0
	 * <p></p>
	 * At the end the value is updated with the new index.
	 */
	@Override
	public void previous() {
		if (items.isEmpty()) return;
		int newIndex = currentIndex - getIncrement();
		if (newIndex < 0) {
			newIndex = isWrapAround() ? items.size() - 1 : 0;
		}
		currentIndex = newIndex;
		setValue(items.get(newIndex));
	}

	/**
	 * Resets the spinner's value to the value specified by {@link #defaultValueProperty()},
	 * and the current index to -1;
	 */
	@Override
	public void reset() {
		super.reset();
		currentIndex = -1;
	}

	@Override
	public StringConverter<T> getConverter() {
		return converter.get();
	}

	@Override
	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	@Override
	public void setConverter(StringConverter<T> converter) {
		this.converter.set(converter);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the current value's index in the items list
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	public ObservableList<T> getItems() {
		return items.get();
	}

	/**
	 * Specifies the items list.
	 */
	public ListProperty<T> itemsProperty() {
		return items;
	}

	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}

	public int getIncrement() {
		return increment.get();
	}

	/**
	 * Specifies the increment/decrement value to add/subtract from
	 * the current index when calling {@link #next()} or {@link #previous()}.
	 */
	public IntegerProperty incrementProperty() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment.set(increment);
	}
}
