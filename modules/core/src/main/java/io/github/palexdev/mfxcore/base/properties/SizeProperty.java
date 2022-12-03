package io.github.palexdev.mfxcore.base.properties;

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.Optional;

/**
 * Simple extension of {@link ReadOnlyObjectWrapper} for {@link Size} objects.
 */
public class SizeProperty extends ReadOnlyObjectWrapper<Size> {

	//================================================================================
	// Constructors
	//================================================================================
	public SizeProperty() {
	}

	public SizeProperty(Size initialValue) {
		super(initialValue);
	}

	public SizeProperty(Object bean, String name) {
		super(bean, name);
	}

	public SizeProperty(Object bean, String name, Size initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Convenience method to create a new {@link Size} object with the given parameters and set it
	 * as the new value of this property.
	 */
	public void setSize(double w, double h) {
		set(Size.of(w, h));
	}

	/**
	 * Convenience method to set only the width of the current {@link Size} of this property.
	 * Note that if the value is null a new {@link Size} object is created with a height of 0.0.
	 * Also, if the value was not null, {@link #invalidated()} and {@link #fireValueChangedEvent()} are invoked programmatically
	 * only if the width was not the same as the given one, this is needed as the object will remain the same.
	 */
	public void setWidth(double w) {
		Optional.ofNullable(get())
				.ifPresentOrElse(
						s -> {
							boolean changed = s.getWidth() != w;
							s.setWidth(w);
							if (changed) {
								invalidated();
								fireValueChangedEvent();
							}
						},
						() -> setSize(w, 0.0)
				);
	}

	/**
	 * Convenience method to set only the height of the current {@link Size} of this property.
	 * Note that if the value is null a new {@link Size} object is created with a width of 0.0.
	 * Also, if the value was not null, {@link #invalidated()}, {@link #fireValueChangedEvent()} are invoked programmatically
	 * only if the height was not the same as the given one, this is needed as the object will remain the same.
	 */
	public void setHeight(double h) {
		Optional.ofNullable(get())
				.ifPresentOrElse(
						s -> {
							boolean changed = s.getHeight() != h;
							s.setHeight(h);
							if (changed) {
								invalidated();
								fireValueChangedEvent();
							}
						},
						() -> setSize(0.0, h)
				);
	}

	/**
	 * Null-safe alternative to {@code get().getWidth()}, if the value is null returns an invalid width of -1.0.
	 */
	public double getWidth() {
		return Optional.ofNullable(get())
				.map(Size::getWidth)
				.orElse(-1.0);
	}

	/**
	 * Null-safe alternative to {@code get().getHeight()}, if the value is null returns an invalid height of -1.0.
	 */
	public double getHeight() {
		return Optional.ofNullable(get())
				.map(Size::getHeight)
				.orElse(-1.0);
	}
}
