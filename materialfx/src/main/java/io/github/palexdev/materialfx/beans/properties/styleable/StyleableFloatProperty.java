package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableFloatProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableFloatProperty extends SimpleStyleableFloatProperty {

	public StyleableFloatProperty(CssMetaData<? extends Styleable, Number> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableFloatProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Float initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableFloatProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableFloatProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name, Float initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
