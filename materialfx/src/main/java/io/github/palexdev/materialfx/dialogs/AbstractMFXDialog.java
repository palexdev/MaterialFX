package io.github.palexdev.materialfx.dialogs;

import javafx.scene.layout.BorderPane;

/**
 * Base class every {@code MFXDialog} should extend.
 */
public abstract class AbstractMFXDialog extends BorderPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-dialog";

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXDialog() {
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setMinSize(400, 200);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}
}
