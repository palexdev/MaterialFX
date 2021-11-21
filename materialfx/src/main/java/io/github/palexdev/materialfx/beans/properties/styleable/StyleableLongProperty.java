package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableLongProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableLongProperty extends SimpleStyleableLongProperty {

	public StyleableLongProperty(CssMetaData<? extends Styleable, Number> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableLongProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Long initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableLongProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableLongProperty(CssMetaData<? extends Styleable, Number> cssMetaData, Object bean, String name, Long initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
