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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.skins.MFXProgressBarSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

import java.util.List;

import static io.github.palexdev.materialfx.utils.NodeUtils.isPseudoClassActive;

/**
 * This is the implementation of a progress bar following Google's material design guidelines.
 * <p>
 * Extends {@code ProgressBar} and redefines the style class to "mfx-progress-bar" for usage in CSS.
 * <p></p>
 * MFXProgressBar introduces three new css pseudo classes:
 * <p> - ":range1", activated when the bar value is contained in any of the ranges specified in here {@link #getRanges1()}
 * <p> - ":range2", activated when the bar value is contained in any of the ranges specified in here {@link #getRanges2()}
 * <p> - ":range3", activated when the bar value is contained in any of the ranges specified in here {@link #getRanges3()}
 * <p>
 * I know this may seem a strange approach, but it is much more flexible and allows for a lot more customization.
 */
public class MFXProgressBar extends ProgressBar {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXProgressBar> FACTORY = new StyleablePropertyFactory<>(ProgressBar.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-progress-bar";
	private final String STYLESHEETS = MFXResourcesLoader.load("css/MFXProgressBar.css");

	private final ObservableList<NumberRange<Double>> ranges1 = FXCollections.observableArrayList();
	private final ObservableList<NumberRange<Double>> ranges2 = FXCollections.observableArrayList();
	private final ObservableList<NumberRange<Double>> ranges3 = FXCollections.observableArrayList();
	protected final PseudoClass RANGE1_PSEUDO_CLASS = PseudoClass.getPseudoClass("range1");
	protected final PseudoClass RANGE2_PSEUDO_CLASS = PseudoClass.getPseudoClass("range2");
	protected final PseudoClass RANGE3_PSEUDO_CLASS = PseudoClass.getPseudoClass("range3");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXProgressBar() {
		initialize();
	}

	public MFXProgressBar(double progress) {
		super(progress);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setPrefWidth(200);

		addListeners();
	}

	private void addListeners() {
		ranges1.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		ranges2.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		ranges3.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		progressProperty().addListener((observable, oldValue, newValue) -> handlePseudoClasses());
	}

	/**
	 * Handles the ":range1", ":range2" and ":range3" css pseudo classes when these properties change:
	 * {@link #progressProperty()}, {@link #getRanges1()}, {@link #getRanges2()}, {@link #getRanges3()}.
	 */
	private void handlePseudoClasses() {
		double val = getProgress();
		if (!isPseudoClassActive(this, RANGE1_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges1)) {
			pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, true);
			pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, false);
			pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, false);
		} else if (!isPseudoClassActive(this, RANGE2_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges2)) {
			pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, true);
			pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, false);
			pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, false);
		} else if (!isPseudoClassActive(this, RANGE3_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges3)) {
			pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, true);
			pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, false);
			pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, false);
		}
	}

	/**
	 * Returns the first list of ranges.
	 */
	public ObservableList<NumberRange<Double>> getRanges1() {
		return ranges1;
	}

	/**
	 * Returns the second list of ranges.
	 */
	public ObservableList<NumberRange<Double>> getRanges2() {
		return ranges2;
	}

	/**
	 * Returns the third list of ranges.
	 */
	public ObservableList<NumberRange<Double>> getRanges3() {
		return ranges3;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty animationSpeed = new SimpleStyleableDoubleProperty(
			StyleableProperties.ANIMATION_SPEED,
			this,
			"animationSpeed",
			1.0
	);

	public double getAnimationSpeed() {
		return animationSpeed.get();
	}

	/**
	 * Specifies the indeterminate animation speed.
	 */
	public StyleableDoubleProperty animationSpeedProperty() {
		return animationSpeed;
	}

	public void setAnimationSpeed(double animationSpeed) {
		this.animationSpeed.set(animationSpeed);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXProgressBar, Number> ANIMATION_SPEED =
				FACTORY.createSizeCssMetaData(
						"-mfx-animation-speed",
						MFXProgressBar::animationSpeedProperty,
						1.0
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					ProgressBar.getClassCssMetaData(),
					ANIMATION_SPEED
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXProgressBarSkin(this);
	}

	@Override
	protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return getClassCssMetaData();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEETS;
	}
}
