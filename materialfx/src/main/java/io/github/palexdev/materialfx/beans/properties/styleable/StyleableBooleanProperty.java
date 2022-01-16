package io.github.palexdev.materialfx.beans.properties.styleable;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

public class StyleableBooleanProperty extends SimpleStyleableBooleanProperty {

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, boolean initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableBooleanProperty(CssMetaData<? extends Styleable, Boolean> cssMetaData, Object bean, String name, boolean initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	@Override
	public StyleOrigin getStyleOrigin() {
		return StyleOrigin.USER_AGENT;
	}
}
