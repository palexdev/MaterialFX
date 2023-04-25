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

package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase;
import io.github.palexdev.mfxcomponents.skins.MFXFabSkin;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.Optional;

/**
 * This is the default behavior used by all {@link MFXFabBase} components.
 * <p>
 * This behavior is an extension of {@link MFXButtonBehavior} and adds some methods to animate
 * the FAB when needed, as described by the M3 guidelines.
 * <p></p>
 * Side note. Notice that if you want to animate the FAB through mouse events it is recommended to
 * avoid triggering the ripple generator. If you don't want to disable it, then the proper way to do this
 * is to add and event filter to the FAB and then consume the event before it can trigger the ripple generation.
 */
public class MFXFabBehavior extends MFXButtonBehavior {
	//================================================================================
	// Properties
	//================================================================================
	private final Scale scale = new Scale(1, 1);

	private Animation extendAnimation;
	private boolean inhibitAnimations = false;

	private Label labelNode;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabBehavior(MFXFabBase node) {
		super(node);
		node.getTransforms().add(scale);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This method is responsible for animating the transition between a standard FAB and
	 * an Extended FAB as shown on the M3 guidelines website.
	 * <p></p>
	 * This is automatically called by the {@link MFXFabBase#extendedProperty()} when it changes.
	 */
	public void extend(boolean animate) {
		MFXFabBase fab = getFab();
		boolean extended = fab.isExtended();
		double targetSize = computeWidth();
		double targetTextOpacity = extended ? 1.0 : 0.0;

		Optional<Label> labelNode = getLabelNode();
		double labelDisplacement = computeLabelDisplacement();

		if (!animate) {
			fab.setPrefWidth(targetSize);
			fab.setTextOpacity(targetTextOpacity);
			labelNode.ifPresent(n -> n.setTranslateX(labelDisplacement));
			return;
		}

		double resizeDuration = inhibitAnimations ? 1 : M3Motion.LONG2.toMillis();
		double opacityDuration = inhibitAnimations ? 1 : (extended ? M3Motion.LONG4 : M3Motion.SHORT4).toMillis();
		Interpolator curve = M3Motion.EMPHASIZED;

		if (!extended && fab.getPrefWidth() == Region.USE_COMPUTED_SIZE) fab.setPrefWidth(fab.getWidth());
		if (extendAnimation != null) extendAnimation.stop();
		extendAnimation = TimelineBuilder.build()
				.add(KeyFrames.of(resizeDuration, fab.prefWidthProperty(), targetSize, curve))
				.addConditional(labelNode::isPresent, KeyFrames.of(resizeDuration, labelNode.get().translateXProperty(), labelDisplacement, curve))
				.add(KeyFrames.of(opacityDuration, fab.textOpacityProperty(), targetTextOpacity, curve))
				.getAnimation();
		extendAnimation.play();
	}

	/**
	 * Calls {@link #changeIcon(MFXFontIcon, Pos)} with {@link Pos#BOTTOM_RIGHT} as the pivot.
	 */
	public void changeIcon(MFXFontIcon newIcon) {
		changeIcon(newIcon, Pos.BOTTOM_RIGHT);
	}

	/**
	 * This is responsible for animating the FAB when the icon changes.
	 * For a smooth transition, instead of using {@link MFXFabBase#setIcon(MFXFontIcon)}, one should
	 * use this instead, thus triggering the animation.
	 * <p></p>
	 * The M3 guidelines show a scale transition for regular FABs and a quick collapse/extend transition for
	 * Extended variants.
	 * <p></p>
	 * The 'pivot' argument is used to set the {@link Scale}'s pivotX and pivotY, the code is factored out in the
	 * {@link #setScalePivot(Pos)} method.
	 */
	public void changeIcon(MFXFontIcon newIcon, Pos pivot) {
		MFXFabBase fab = getFab();
		MFXFontIcon currentIcon = fab.getIcon();

		Duration outMillis = M3Motion.LONG4;
		if (fab.isExtended()) {
			newIcon.setOpacity(0.0);
			fab.setIcon(newIcon);
			TimelineBuilder.build()
					.add(KeyFrames.of(0, e -> {
						inhibitAnimations = true;
						fab.setExtended(false);
					}))
					.add(KeyFrames.of(M3Motion.SHORT2, e -> {
						inhibitAnimations = false;
						fab.setExtended(true);
					}))
					.add(KeyFrames.of(outMillis, newIcon.opacityProperty(), 1.0, M3Motion.EMPHASIZED))
					.getAnimation()
					.play();
		} else {
			setScalePivot(pivot);

			Duration inMillis = M3Motion.SHORT4;
			Interpolator inCurve = M3Motion.EMPHASIZED_ACCELERATE;
			Interpolator outCurve = M3Motion.EMPHASIZED_DECELERATE;

			Timeline scaleDown = TimelineBuilder.build()
					.addConditional(() -> currentIcon != null, KeyFrames.of(inMillis, currentIcon.opacityProperty(), 0.0, inCurve))
					.add(KeyFrames.of(inMillis, scale.xProperty(), 0.0, inCurve))
					.add(KeyFrames.of(inMillis, scale.yProperty(), 0.0, inCurve))
					.add(KeyFrames.of(inMillis, e -> fab.setIcon(newIcon)))
					.getAnimation();
			Timeline scaleUp = TimelineBuilder.build()
					.addConditional(() -> newIcon != null, KeyFrames.of(0, e -> newIcon.setOpacity(0.0)))
					.add(KeyFrames.of(outMillis, scale.xProperty(), 1.0, outCurve))
					.add(KeyFrames.of(outMillis, scale.yProperty(), 1.0, outCurve))
					.addConditional(() -> newIcon != null, KeyFrames.of(outMillis, newIcon.opacityProperty(), 1.0, outCurve))
					.getAnimation();
			SequentialBuilder.build()
					.add(scaleDown)
					.add(scaleUp)
					.getAnimation()
					.play();
		}
	}

	/**
	 * Responsible for computing the target width when collapsing/extending the FAB,
	 * a simple delegate for {@link MFXLabeled#computePrefWidth(double)}.
	 */
	@SuppressWarnings("JavadocReference")
	protected double computeWidth() {
		MFXFabBase fab = getFab();
		return fab.computePrefWidth(fab.getHeight());
	}

	/**
	 * By default, the text node is placed at {@link Pos#CENTER_LEFT} by {@link MFXFabSkin}.
	 * This method serves two purposes:
	 * <p> 1) Supporting {@link HPos#CENTER} and {@link HPos#RIGHT} other than just {@link HPos#LEFT}
	 * <p> 2) Making sure that the label is always well positioned when expanding/collapsing the FAB. By always positioning
	 * it at {@link Pos#CENTER_LEFT} we make sure that it's {@code layoutX} doesn't change, and we can animate the
	 * {@link Node#translateXProperty()} to make the transition look smooth.
	 */
	public double computeLabelDisplacement() {
		MFXFabBase fab = getFab();
		HPos hpos = fab.getAlignment().getHpos();
		Optional<Label> labelNodeOpt = getLabelNode();
		if (fab.getSkin() == null || labelNodeOpt.isEmpty()) return 0.0;

		boolean extended = fab.isExtended();
		double x = fab.snappedLeftInset();
		double right = fab.snappedRightInset();
		double w = LayoutUtils.boundWidth(fab);
		Label labelNode = labelNodeOpt.get();
		MFXFontIcon icon = fab.getIcon();

		double leftX = labelNode.getLayoutX();
		if (!extended) {
			if (icon == null) return 0.0;
			double centerX = LayoutUtils.computeXPosition(
					fab, icon,
					x, w, Insets.EMPTY, true, HPos.CENTER,
					true, true
			);
			return centerX - leftX - x;
		}

		double targetX = LayoutUtils.computeXPosition(
				fab, labelNode,
				x, w, Insets.EMPTY, true, hpos,
				true, true
		);
		switch (hpos) {
			case RIGHT:
				x += right;
				break;
			case LEFT:
				x = 0;
				break;
			case CENTER:
				x += Math.abs(x - right) / 2;
				break;
		}
		return targetX - leftX - x;
	}

	/**
	 * Retrieves the text node from the FAB' skin. Must be overridden if changing the skin/layout since this
	 * uses {@code fab.getChildrenUnmodifiable().get(1)}.
	 * <p>
	 * Returns an {@link Optional} for null-safety.
	 */
	protected Optional<Label> getLabelNode() {
		MFXFabBase fab = getFab();
		if (fab.getSkin() == null) return Optional.empty();
		if (labelNode == null) {
			labelNode = (Label) fab.getChildrenUnmodifiable().get(1);
		}
		return Optional.of(labelNode);
	}

	/**
	 * This is responsible for setting the {@link Scale}'s pivotX and pivotY for animating the
	 * FAB when changing icon through {@link #changeIcon(MFXFontIcon)}.
	 * <p></p>
	 * Supported values are: {@code TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER_LEFT, CENTER_RIGHT}.
	 */
	protected void setScalePivot(Pos pos) {
		MFXFabBase fab = getFab();
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
	@Override
	public void dispose() {
		labelNode = null;
		getNode().getTransforms().remove(scale);
		super.dispose();
	}

	//================================================================================
	// Getters
	//================================================================================

	/**
	 * @return {@link #getNode()} cast to {@link MFXFabBase}
	 */
	public MFXFabBase getFab() {
		return (MFXFabBase) getNode();
	}
}