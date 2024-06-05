/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
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
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.ConsumerTransition;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/**
 * Base skin implementation for all components of type {@link MFXFabBase}, extends {@link MFXButtonSkin}.
 * <p>
 * This skin uses behaviors of type {@link MFXButtonBehaviorBase} as the FAB is just a simple button
 * with a different look and purpose.
 * <p></p>
 * The layout is the same described in {@link MFXButtonSkin} (since it extends it), but it's more complex because of
 * animations. According to Material Design 3 guidelines, FABs and are not meant to be resized as one pleases.
 * In fact, for the animations, the skin sets the {@link MFXFabBase#prefWidthProperty()}. The min and max width computations
 * are set to follow the desired pref width, so that animations can play without any issue.
 * More info on how animations work: {@link WidthAnimation}, {@link ScaleAnimation}.
 * <p></p>
 * Animations' parameters can be changed easily as they are {@code protected} members of the class.
 */
public class MFXFabSkin extends MFXButtonSkin<MFXFabBase, MFXButtonBehaviorBase<MFXFabBase>> {
	//================================================================================
	// Properties
	//================================================================================
	// TODO define min sizes in Skins
	private Animation ecAnimation;
	private Animation sAnimation;
	private final Scale scale = new Scale(1, 1);

	// Width animation parameters
	protected Duration WIDTH_DURATION = M3Motion.LONG2;
	protected Duration WIDTH_TEXT_OPACITY_DURATION = M3Motion.MEDIUM2;
	protected Interpolator WIDTH_CURVE = M3Motion.STANDARD;

	// Scale animation parameters
	protected Duration SCALE_DOWN_DURATION = M3Motion.MEDIUM1;
	protected Duration SCALE_UP_DURATION = M3Motion.LONG3;
	protected Interpolator SCALE_CURVE = M3Motion.EMPHASIZED;

	// Extend/collapse animation parameters
	protected Duration RESIZE_DURATION = M3Motion.MEDIUM4;
	protected Duration OPACITY_DURATION = M3Motion.SHORT2;
	protected Duration OPACITY_DURATION_EXTENDED = M3Motion.EXTRA_LONG2;
	protected Interpolator RESIZE_CURVE = M3Motion.EMPHASIZED;

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
	 * This is responsible for creating and playing a {@link WidthAnimation} or {@link ScaleAnimation}
	 * depending on the {@link MFXFabBase#extendedProperty()}.
	 * <p>
	 * If another scale animation is already playing, it is stopped and overwritten.
	 */
	protected void scale() {
		MFXFabBase fab = getSkinnable();
		boolean extended = fab.isExtended();

		if (Animations.isPlaying(sAnimation)) sAnimation.stop();

		if (extended) {
			sAnimation = new WidthAnimation();
		} else {
			sAnimation = new ScaleAnimation().getAnimation();
		}
		sAnimation.play();
	}

	/**
	 * This is responsible for properly sizing the component when the {@link MFXFabBase#extendedProperty()} changes.
	 * <p>
	 * Depending on the {@link MFXFabBase#animatedProperty()}, the layout is requested using {@link MFXFabBase#requestLayout()}
	 * ({@code false}) or adjusted by an animation ({@code true}).
	 */
	protected void extendCollapse() {
		MFXFabBase fab = getSkinnable();
		boolean extended = fab.isExtended();
		boolean animated = fab.isAnimated();

		double targetWidth = computeTargetWidth();
		double targetOpacity = extended ? 1.0 : 0.0;
		double labelTargetX = extended ? 0.0 : computeLabelDisplacement(targetWidth);

		if (!animated) {
			fab.setTextOpacity(targetOpacity);
			label.setTranslateX(labelTargetX);
			fab.requestLayout();
			return;
		}

		if (Animations.isPlaying(ecAnimation)) ecAnimation.stop();
		if (Animations.isPlaying(sAnimation)) {
			sAnimation.stop();
			scale.setX(1.0);
			scale.setY(1.0);
		}
		ecAnimation = TimelineBuilder.build()
			.add(KeyFrames.of(RESIZE_DURATION, fab.prefWidthProperty(), targetWidth, RESIZE_CURVE))
			.add(KeyFrames.of(RESIZE_DURATION, label.translateXProperty(), labelTargetX, RESIZE_CURVE))
			.add(KeyFrames.of(extended ? OPACITY_DURATION_EXTENDED : OPACITY_DURATION, fab.textOpacityProperty(), targetOpacity, RESIZE_CURVE))
			.getAnimation();
		ecAnimation.play();
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

	/**
	 * Computes the ideal width to fully display the content. Ignores the max width constraint, given by
	 * {@code Math.max(minW, prefW)}.
	 */
	protected double computeTargetWidth() {
		double min = computeMinWidth(-1);
		double pref = computePrefWidth(-1);
		return Math.max(min, pref);
	}

	/**
	 * As shown in the Material Design 3 guidelines, the FAB's content is always centered. Since this skin uses a label
	 * to both show the text and the icon, the content is mispositioned when the component is not extended.
	 * <p>
	 * This method computes the number of pixels needed by only the icon node to be exactly at the center.
	 * Given by {@code (w - iW) / 2.0}, where {@code w} is the target width ({@link #computeTargetWidth()}) and
	 * {@code iW} is the icon's width.
	 */
	protected double computeLabelDisplacement(double w) {
		MFXFabBase fab = getSkinnable();
		MFXFontIcon icon = fab.getIcon();
		if (icon == null) return 0.0;
		double iW = LayoutUtils.getWidth(icon);
		return snapPositionX(((w - iW) / 2.0) - label.getLayoutX());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The FAB's label has its {@link BoundLabel#contentDisplayProperty()} always set to {@link ContentDisplay#LEFT}, and
	 * the text is never truncated.
	 *
	 * @see BoundLabel#setForceDisableTextEllipsis(boolean)
	 */
	@Override
	protected BoundLabel createLabel(MFXFabBase labeled) {
		BoundLabel bl = new BoundLabel(labeled);
		bl.onSetTextNode(n -> n.opacityProperty().bind(labeled.textOpacityProperty()));
		bl.contentDisplayProperty().unbind();
		bl.setContentDisplay(ContentDisplay.LEFT);
		bl.setForceDisableTextEllipsis(true);
		return bl;
	}

	/**
	 * Adds the following listeners:
	 * <p> - A listener on the {@link MFXFabBase#iconProperty()} to animate the change if {@link MFXFabBase#animatedProperty()}
	 * is {@code true}, {@link MFXFabBase#extendedProperty()} is false and the new icon is not null
	 * <p> - A listener on the {@link MFXFabBase#textProperty()} to animate the change if both {@link MFXFabBase#animatedProperty()}
	 * and {@link MFXFabBase#extendedProperty()} are {@code true}
	 * <p> - A listener on the {@link MFXFabBase#extendedProperty()} to call {@link #extendCollapse()}
	 * <p> - A listener on the {@link MFXFabBase#scalePivotProperty()} to trigger {@link #updateScalePivot()}
	 */
	@Override
	protected void addListeners() {
		MFXFabBase fab = getSkinnable();
		listeners(
			onInvalidated(fab.iconProperty())
				.condition(i -> fab.isAnimated() && !fab.isExtended() && i != null)
				.then(i -> {
					i.setOpacity(0.0);
					scale();
				}),
			onInvalidated(fab.textProperty())
				.condition(i -> fab.isAnimated() && fab.isExtended())
				.then(t -> scale()),
			onInvalidated(fab.extendedProperty())
				.then(e -> extendCollapse()),
			onInvalidated(fab.scalePivotProperty())
				.then(p -> updateScalePivot())
		);
	}

	@Override
	public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getSkinnable();
		if (!fab.isExtended()) {
			MFXFontIcon icon = fab.getIcon();
			double iW = (icon != null) ? LayoutUtils.getWidth(icon) + leftInset + rightInset : 0.0;
			double iH = (icon != null) ? LayoutUtils.getHeight(icon) + topInset + bottomInset : 0.0;
			return Math.max(Math.max(iW, iH), 56.0);
		}
		return 80.0;
	}

	@Override
	public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getSkinnable();
		return !fab.isExtended() ? computeMinWidth(-1) : 56.0;
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getSkinnable();
		MFXFontIcon icon = fab.getIcon();
		boolean extended = fab.isExtended();
		double iW = (icon != null) ? LayoutUtils.boundWidth(icon) : 0.0;
		double tW = getCachedTextWidth();
		double insets = leftInset + rightInset;
		return extended ? insets + iW + fab.getGraphicTextGap() + tW : insets + iW;
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXFabBase fab = getSkinnable();
		surface.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
		layoutInArea(label, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);
		if (!Animations.isPlaying(ecAnimation) && !Animations.isPlaying(sAnimation)) {
			fab.setTextOpacity(fab.isExtended() ? 1.0 : 0.0);
			label.setTranslateX(fab.isExtended() ? 0.0 : computeLabelDisplacement(computeTargetWidth()));
		}
	}

	//================================================================================
	// Internal Classes
	//================================================================================

	/**
	 * Custom animation which operates on the component's width.
	 * <p>
	 * It starts with the {@link #init()} method setting both the icon and text opacity to 0.0, and the {@link MFXFabBase#minWidthProperty()}
	 * to {@link Region#USE_PREF_SIZE}.
	 * <p>
	 * The main animation is defined in {@link #animate(double)}, it sets the icon's opacity to 1.0 (if the icon was not changed
	 * then we must revert the value 0.0 set by init()). The target width is given by {@link #computeTargetWidth()} and its
	 * set as the {@link MFXFabBase#prefWidthProperty()}.
	 * <p>
	 * When the main animation reaches 80% ot its duration, a secondary animation is played by {@link #animateText()}.
	 * This will bring back the text's opacity to 1.0. Why this, why at 80%? This is to ensure the text does not overflow
	 * the container.
	 */
	protected class WidthAnimation extends ConsumerTransition {
		protected final MFXFabBase fab = getSkinnable();
		protected final Node icon = fab.getIcon();
		protected Animation tAnimation; // Text animation

		public WidthAnimation() {
			setInterpolateConsumer(this::animate);
			setOnFinishedFluent(e -> end());
			setDuration(WIDTH_DURATION);
			setInterpolator(WIDTH_CURVE);
		}

		protected void init() {
			MFXFontIcon icon = fab.getIcon();
			if (icon != null) icon.setOpacity(0.0);
			fab.setTextOpacity(0.0);
			fab.setMinWidth(Region.USE_PREF_SIZE);
		}

		protected void end() {
			fab.setMinWidth(Region.USE_COMPUTED_SIZE);
		}

		protected void animate(double frac) {
			if (fab.getIcon() == icon && icon != null) icon.setOpacity(1.0);
			double w = computeTargetWidth() * frac;
			fab.setPrefWidth(w);
			if (frac >= 0.8 && tAnimation == null) animateText();
		}

		protected void animateText() {
			tAnimation = ConsumerTransition.of(f -> fab.setTextOpacity(1.0 * f))
				.setInterpolatorFluent(WIDTH_CURVE)
				.setDuration(WIDTH_TEXT_OPACITY_DURATION);
			tAnimation.play();

		}

		@Override
		public void play() {
			init();
			super.play();
		}

		@Override
		public void stop() {
			if (tAnimation != null) tAnimation.stop();
			super.stop();
		}
	}

	/**
	 * Custom animation which is the sequence of the animations:
	 * the first one scales down the component, and it's defined by {@link #scaleDown()}, the other scales it up
	 * and it's defined by {@link #scaleUp()}.
	 * <p>
	 * Since this extends a builder class, {@link SequentialBuilder}, the actual animation is given by {@link #getAnimation()}.
	 * The two sub-animations are added by {@link #add(Animation)} in the constructor.
	 */
	protected class ScaleAnimation extends SequentialBuilder {
		private final MFXFabBase fab = getSkinnable();

		public ScaleAnimation() {
			add(scaleDown());
			add(scaleUp());
		}

		protected Animation scaleDown() {
			return TimelineBuilder.build()
				.add(KeyFrames.of(SCALE_DOWN_DURATION, scale.xProperty(), 0.05, SCALE_CURVE))
				.add(KeyFrames.of(SCALE_DOWN_DURATION, scale.yProperty(), 0.05, SCALE_CURVE))
				.getAnimation();
		}

		protected Animation scaleUp() {
			MFXFontIcon icon = fab.getIcon();
			return TimelineBuilder.build()
				.add(KeyFrames.of(SCALE_UP_DURATION, scale.xProperty(), 1.0, SCALE_CURVE))
				.add(KeyFrames.of(SCALE_UP_DURATION, scale.yProperty(), 1.0, SCALE_CURVE))
				.addIf(() -> icon != null, KeyFrames.of(SCALE_UP_DURATION, icon.opacityProperty(), 1.0, SCALE_CURVE))
				.getAnimation();
		}
	}
}
