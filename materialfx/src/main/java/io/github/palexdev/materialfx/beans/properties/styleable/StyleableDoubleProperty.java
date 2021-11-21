package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableDoubleProperty extends SimpleStyleableDoubleProperty {

	public StyleableDoubleProperty(CssMetaData<? extends Styleable, Number> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableDoubleProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Double initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableDoubleProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableDoubleProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name, Double initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
