package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableObjectProperty<T> extends SimpleStyleableObjectProperty<T> {

	public StyleableObjectProperty(CssMetaData<? extends Styleable, T> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableObjectProperty(CssMetaData<? extends Styleable, T> cssMetaData, T initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableObjectProperty(CssMetaData<? extends Styleable, T> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableObjectProperty(CssMetaData<? extends Styleable, T> cssMetaData, Object bean, String name, T initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
