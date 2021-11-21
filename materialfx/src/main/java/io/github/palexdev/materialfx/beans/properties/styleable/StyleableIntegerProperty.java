package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableIntegerProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableIntegerProperty extends SimpleStyleableIntegerProperty {

	public StyleableIntegerProperty(CssMetaData<? extends Styleable, Number> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableIntegerProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Integer initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableIntegerProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableIntegerProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name, Integer initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
