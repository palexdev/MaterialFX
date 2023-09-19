/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase.PropsWrapper;
import io.github.palexdev.mfxcore.builders.bindings.DoubleBindingBuilder;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.Optional;

import static io.github.palexdev.mfxcore.observables.When.onChanged;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/**
 * Base skin implementation for all components of type {@link MFXFabBase}, extends {@link MFXButtonSkin}.
 * <p>
 * This skin uses behaviors of type {@link MFXButtonBehaviorBase} as the FAB is just a simple button
 * with a different look and purpose.
 * <p></p>
 * The layout is the same described in {@link MFXButtonSkin} (since it extends it), but is more complex due to the fact
 * that FABs have animations, and are not meant to be resized as one pleases.
 * In fact, for the animations, the skin sets the {@link MFXFabBase#prefWidthProperty()}. The min and max width computations
 * are set to follow the desired pref width, so that animations can play without any issue.
 * More info on how animations work: {@link #extendCollapse(boolean)}, {@link #attributesChanged(PropsWrapper, PropsWrapper)}.
 */
public class MFXFabSkin extends MFXButtonSkin<MFXFabBase, MFXButtonBehaviorBase<MFXFabBase>> {
	//================================================================================
	// Properties
	//================================================================================
	private Animation attributesAnimation;
	private Animation extendAnimation;
	private final Scale scale = new Scale(1, 1);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabSkin(MFXFabBase fab) {
		super(fab);
		updateScalePivot();
		fab.getTransforms().add(scale);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This is a core method responsible for updating the label when the {@link MFXFabBase#attributesProperty()} changes.
	 * This transition in animated, but the animation won't play if one of these conditions is true:
	 * <p> - The {@link MFXFabBase#animatedProperty()} is false
	 * <p> - The {@code oldValue} is null. Technically this should never happen (see {@link MFXFabBase#attributesProperty()}),
	 * but in reality it happens at most one time, at initialization time, from one of the listeners registered in
	 * {@link #addListeners()}
	 * <p> - The current icon, given by the old attributes, is null
	 * <p></p>
	 * After those first checks, the animations are built and played. First we check if the FAB is extended or not, as
	 * the resulting animation will be different.
	 * <p>
	 * In case the FAB is not extended, the animation could still not be played if the new icon, given by the {@code newValue},
	 * is null.
	 * <p></p>
	 * <b>The two animations</b>
	 * <p> - Extended: the FAB's pref width and the text opacity are animated. The width starts from the current width
	 * multiplied by 0.3, to the width computed by {@link #computePrefWidth(double, double, double, double, double)}.
	 * It's important to note that, in order to get the new width, first we set the new attributes on the label, then
	 * we force a CSS pass to update the layout too ({@link MFXFabBase#applyCss()}).
	 * <p> - Collapsed: the FAB has a {@link Scale} transform applied to it. In this state, the Scale's x and y values
	 * are animated first from 1.0 to 0.3 and then back to 1.0. The current icon fades out, and then during the scale up
	 * phase, the new icon fades in. Additionally, during the scale up phase, we also ensure that the prefWidth is correct.
	 */
	protected void attributesChanged(PropsWrapper oldValue, PropsWrapper newValue) {
		MFXFabBase fab = getSkinnable();
		boolean animated = fab.isAnimated();
		MFXFontIcon currentIcon = Optional.ofNullable(oldValue)
			.map(PropsWrapper::getIcon)
			.orElse(null);
		if (!animated || oldValue == null || currentIcon == null) {
			label.setGraphic(newValue.getIcon());
			label.setText(newValue.getText());
			return;
		}

		boolean extended = fab.isExtended();
		if (extended) {
			double startWidth = fab.getWidth() * 0.3;
			fab.setTextOpacity(0.0);
			label.setText(newValue.getText());
			label.setGraphic(newValue.getIcon());
			fab.setPrefWidth(startWidth);
			fab.applyCss();
			double endWidth = fab.computePrefWidth(-1);

			attributesAnimation = TimelineBuilder.build()
				.add(KeyFrames.of(M3Motion.LONG4, fab.prefWidthProperty(), endWidth, M3Motion.EMPHASIZED_DECELERATE))
				.add(KeyFrames.of(M3Motion.EXTRA_LONG4, fab.textOpacityProperty(), 1.0, M3Motion.EMPHASIZED))
				.getAnimation();
		} else {
			MFXFontIcon newIcon = newValue.getIcon();
			label.setText(newValue.getText());
			if (newIcon == null) {
				label.setGraphic(null);
				return;
			}

			Duration downMillis = M3Motion.MEDIUM1;
			Interpolator downCurve = M3Motion.EMPHASIZED_ACCELERATE;
			Duration upMillis = M3Motion.LONG1;
			Interpolator upCurve = M3Motion.EMPHASIZED_DECELERATE;

			Timeline scaleUp = TimelineBuilder.build()
				.add(KeyFrames.of(upMillis, scale.xProperty(), 1.0, upCurve))
				.add(KeyFrames.of(upMillis, scale.yProperty(), 1.0, upCurve))
				.add(KeyFrames.of(upMillis, newIcon.opacityProperty(), 1.0, upCurve))
				.getAnimation();
			Timeline scaleDown = TimelineBuilder.build()
				.add(KeyFrames.of(downMillis, currentIcon.opacityProperty(), 0.0, downCurve))
				.add(KeyFrames.of(downMillis, scale.xProperty(), 0.3, downCurve))
				.add(KeyFrames.of(downMillis, scale.yProperty(), 0.3, downCurve))
				.setOnFinished(e -> {
					newIcon.setOpacity(0.0);
					label.setGraphic(newIcon);
					fab.applyCss();
					fab.layout();
					scaleUp.getKeyFrames().add(
						KeyFrames.of(upMillis, fab.prefWidthProperty(), fab.computePrefWidth(-1), upCurve)
					);
				})
				.getAnimation();
			attributesAnimation = SequentialBuilder.build()
				.add(scaleDown)
				.add(scaleUp)
				.getAnimation();
		}
		attributesAnimation.play();
	}

	/**
	 * This is responsible for expanding/collapsing the FAB. The very first thing we have to do is force a CSS pass with
	 * {@link MFXFabBase#applyCss()}. This is very important as after the ":extended" PseudoClass is activated on the node,
	 * some values are likely to be outdated ({@link MFXFabBase#initWidthProperty()} and {@link MFXFabBase#initHeightProperty()}
	 * for example). This is not good when computing layout, since the resulting calculations may be also wrong.
	 * So, only after that we can compute the new prefWidth with {@link #computePrefWidth(double, double, double, double, double)}.
	 * <p>
	 * If the {@link MFXFabBase#animatedProperty()} is false then the values are set immediately, otherwise the animation
	 * is built and run.
	 * <p></p>
	 * There are a couple of important things to note though.
	 * <p> 1) In the previous implementation of this skin, the label was shifted left/right with a translation to make it
	 * appear always centered (this is the way FAB are intended to work anyway). It was not perfect, and complicated to manage.
	 * This new implementation simply resizes the label to always be as big as the FAB, and its alignment is set to
	 * {@link Pos#CENTER}, this way the content is always centered, even when animating (more info on the label here
	 * {@link #createLabel(MFXFabBase)}). That said, I found out that this translation may still be necessary in some cases,
	 * so the animation does also that.
	 * <p> 2) Since there could be the other animation running (from {@link #attributesChanged(PropsWrapper, PropsWrapper)}),
	 * before starting this we ensure first that the other is stopped, which means that: the scale transform must be reset,
	 * the text and icon must be updated, then we force a CSS pass again, and finally we can re-compute what is the
	 * desired prefWidth for the FAB
	 */
	protected void extendCollapse(boolean animated) {
		MFXFabBase fab = getSkinnable();
		boolean extended = fab.isExtended();

		// The initWidth property causes the width computation to fail because it still uses the old values
		// Forcing the CSS to be re-processed makes so that the LayoutStrategy can compute the right width
		fab.applyCss();

		double targetSize = fab.computePrefWidth(-1);
		double targetTextOpacity = extended ? 1.0 : 0.0;

		// Why do we still need the displacement? Simple, when the FAB is not extended the label will take into account
		// the graphicTextGap for its size, this leads to it being slightly shifted to the left
		// Translate by half the value to correct
		double labelDisplacement = extended ? 0.0 : fab.getGraphicTextGap() / 2.0;

		if (!animated) {
			fab.setPrefWidth(targetSize);
			fab.setTextOpacity(targetTextOpacity);
			label.setTranslateX(labelDisplacement);
			return;
		}

		Duration resizeDuration = M3Motion.LONG2;
		Duration opacityDuration = (extended ? M3Motion.EXTRA_LONG4 : M3Motion.SHORT2);
		Interpolator curve = M3Motion.EMPHASIZED;

		if (!extended && fab.getPrefWidth() == Region.USE_COMPUTED_SIZE) fab.setPrefWidth(fab.getWidth());
		if (extendAnimation != null) extendAnimation.stop();
		if (Animations.isPlaying(attributesAnimation)) {
			attributesAnimation.stop();
			scale.setX(1);
			scale.setY(1);
			PropsWrapper attributes = fab.getAttributes();
			label.setGraphic(attributes.getIcon());
			label.setText(attributes.getText());
			fab.applyCss();
			targetSize = fab.computePrefWidth(-1);
		}
		extendAnimation = TimelineBuilder.build()
			.add(KeyFrames.of(resizeDuration, fab.prefWidthProperty(), targetSize, curve))
			.add(KeyFrames.of(resizeDuration, label.translateXProperty(), labelDisplacement, curve))
			.add(KeyFrames.of(opacityDuration, fab.textOpacityProperty(), targetTextOpacity, curve))
			.getAnimation();
		extendAnimation.play();
	}

	/**
	 * Responsible for setting the {@link Scale} transform pivot according to the {@link MFXFabBase#scalePivotProperty()}.
	 * <p>
	 * Supported positions: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT(default), CENTER_LEFT, CENTER_RIGHT.
	 */
	protected void updateScalePivot() {
		MFXFabBase fab = getSkinnable();
		Pos pos = fab.getScalePivot();
		switch (pos) {
			case TOP_LEFT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMinX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMinY));
				break;
			}
			case TOP_RIGHT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMaxX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMinY));
				break;
			}
			case BOTTOM_LEFT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMinX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMaxY));
				break;
			}
			case CENTER_LEFT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMinX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(b -> b.getMaxY() / 2.0));
				break;
			}
			case CENTER_RIGHT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMaxX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(b -> b.getMaxY() / 2.0));
				break;
			}
			default:
			case BOTTOM_RIGHT: {
				scale.pivotXProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMaxX));
				scale.pivotYProperty().bind(fab.layoutBoundsProperty().map(Bounds::getMaxY));
			}
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * The label created by this skin is quite unique compared to others.
	 * <p>
	 * These properties have been unbound: text and icon (updated "manually"), content display (set to LEFT), alignment
	 * (set to CENTER).
	 * <p>
	 * Makes use of the {@link Label#setForceDisableTextEllipsis(boolean)} feature for better animations.
	 * <p>
	 * The width and height are bound to the ones of the FAB, which menus that animations will indirectly involve the
	 * label too.
	 */
	@Override
	protected BoundLabel createLabel(MFXFabBase labeled) {
		MFXFabBase fab = getSkinnable();
		BoundLabel bl = super.createLabel(labeled);
		bl.setManaged(false);

		// Properties
		bl.graphicProperty().unbind();
		bl.setGraphic(null);
		bl.textProperty().unbind();
		bl.setText(null);
		bl.contentDisplayProperty().unbind();
		bl.setContentDisplay(ContentDisplay.LEFT);
		bl.alignmentProperty().unbind();
		bl.setAlignment(Pos.CENTER);
		bl.setForceDisableTextEllipsis(true);

		// Layout
		bl.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		bl.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		bl.prefWidthProperty().bind(DoubleBindingBuilder.build()
			.setMapper(() -> fab.getWidth() - snappedLeftInset() - snappedRightInset())
			.addSources(fab.widthProperty(), fab.insetsProperty())
			.get()
		);
		bl.prefHeightProperty().bind(fab.heightProperty());
		return bl;
	}

	/**
	 * Adds the following listeners:
	 * <p> - A listener on the {@link MFXFabBase#extendedProperty()} to trigger {@link #extendCollapse(boolean)}
	 * <p> - A listener on the {@link MFXFabBase#scalePivotProperty()} to trigger {@link #updateScalePivot()}
	 * <p> - A listener on the {@link MFXFabBase#attributesProperty()} to trigger {@link #attributesChanged(PropsWrapper, PropsWrapper)},
	 * this is also "force called" at init
	 * <p> - A listener on the {@link MFXFabBase#layoutStrategyProperty()} to trigger {@link #extendCollapse(boolean)},
	 * this forcing is to ensure the FAB has correct sizes after the strategy changes, the method is called without animating
	 * <p> - A listener on the {@link MFXFabBase#graphicTextGapProperty()} to update the label's translate x as described by
	 * {@link #extendCollapse(boolean)}. This is FAB is not extended.
	 * <p> - A listener on the {@link MFXFabBase#sceneProperty()}, this is important to update the label's text and icon
	 * when the FAB is placed in a new Scene, as well as re-setting prefWidth and setting the text opacity to
	 * 0.0 (if collapsed) or 1.0 (if extended)
	 */
	@Override
	protected void addListeners() {
		MFXFabBase fab = getSkinnable();
		listeners(
			onChanged(fab.extendedProperty())
				.condition((o, n) -> fab.getScene() != null)
				.then((o, n) -> extendCollapse(fab.isAnimated())),

			onInvalidated(fab.scalePivotProperty())
				.then(v -> updateScalePivot()),

			onChanged(fab.attributesProperty())
				.then(this::attributesChanged)
				.executeNow(),

			onInvalidated(fab.layoutStrategyProperty())
				.then(s -> extendCollapse(false)),

			onInvalidated(fab.graphicTextGapProperty())
				.condition(v -> !fab.isExtended())
				.then(v -> label.setTranslateX(v.doubleValue() / 2.0)),

			onChanged(fab.sceneProperty())
				.condition((o, n) -> n != null)
				.then((o, n) -> {
					label.setText(fab.getFabText());
					label.setGraphic(fab.getIcon());
					fab.setPrefWidth(Region.USE_COMPUTED_SIZE);
					fab.setTextOpacity(fab.isExtended() ? 1.0 : 0.0);
				})
				.executeNow(() -> fab.getScene() != null)
		);
	}

	@Override
	public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Region.USE_COMPUTED_SIZE;
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getSkinnable();
		MFXFontIcon icon = fab.getIcon();
		double iW = (icon != null) ? icon.getLayoutBounds().getWidth() : 0.0;
		double gap = (icon != null) ? fab.getGraphicTextGap() : 0.0;
		return snapSizeX(fab.isExtended() ?
			leftInset + iW + gap + getCachedTextWidth() + rightInset :
			leftInset + iW + rightInset);
	}

	@Override
	public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getSkinnable();
		MFXFontIcon icon = fab.getIcon();
		double iH = (icon != null) ? icon.getLayoutBounds().getHeight() : 0.0;
		return snapSizeY(topInset + Math.max(iH, getCachedTextHeight()) + bottomInset);
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXFabBase fab = getSkinnable();
		surface.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
		label.relocate(x, 0);
		label.autosize();
	}

	@Override
	public void dispose() {
		attributesAnimation = null;
		extendAnimation = null;
		super.dispose();
	}
}
