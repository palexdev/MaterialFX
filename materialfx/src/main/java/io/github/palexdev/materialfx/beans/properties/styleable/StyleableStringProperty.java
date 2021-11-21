package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableStringProperty extends SimpleStyleableStringProperty {

	public StyleableStringProperty(CssMetaData<? extends Styleable, String> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableStringProperty(CssMetaData<? extends Styleable, String> cssMetaData, String initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableStringProperty(CssMetaData<? extends Styleable, String> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableStringProperty(CssMetaData<? extends Styleable, String> cssMetaData, Object bean, String name, String initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
