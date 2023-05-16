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

import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase;
import io.github.palexdev.mfxcomponents.skins.MFXFabSkin;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.Optional;

/**
 * This is the default behavior used by all {@link MFXFabBase} components.
 * <p>
 * This behavior is an extension of {@link MFXButtonBehaviorBase} and adds some methods to animate
 * the FAB when needed, as described by the M3 guidelines.
 * <p></p>
 * Side note. Notice that if you want to animate the FAB through mouse events it is recommended to
 * avoid triggering the ripple generator. If you don't want to disable it, then the proper way to do this
 * is to add and event filter to the FAB and then consume the event before it can trigger the ripple generation.
 */
public class MFXFabBehavior extends MFXButtonBehaviorBase<MFXFabBase> {
    //================================================================================
    // Properties
    //================================================================================
    private final Scale scale = new Scale(1, 1);

    private Animation extendAnimation;
    private Animation changeIconAnimation;
    private boolean changingIcon = false;

    private Label labelNode;
    private DisplacementStrategy ds;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFabBehavior(MFXFabBase node) {
        super(node);
        node.getTransforms().add(scale);

        updateDisplacementStrategy();
        register(node.alignmentProperty(), i -> updateDisplacementStrategy());
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
    @SuppressWarnings("OptionalGetWithoutIsPresent")
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

        double resizeDuration = M3Motion.LONG2.toMillis();
        double opacityDuration = (extended ? M3Motion.LONG4 : M3Motion.SHORT4).toMillis();
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
    @SuppressWarnings({"ConstantValue", "OptionalGetWithoutIsPresent"})
    public void changeIcon(MFXFontIcon newIcon, Pos pivot) {
        MFXFabBase fab = getFab();
        MFXFontIcon currentIcon = fab.getIcon();
        if (Animations.isPlaying(changeIconAnimation)) {
            changeIconAnimation.stop();
        }

        Interpolator curve = M3Motion.EMPHASIZED;
        if (fab.isExtended()) {
            Duration inMillis = M3Motion.SHORT2;
            Duration outMillis = M3Motion.LONG4;
            Duration oOutMills = M3Motion.MEDIUM1;
            Interpolator oCurve = M3Motion.EMPHASIZED_ACCELERATE;
            Optional<Label> label = getLabelNode();

            Timeline openAnimation = TimelineBuilder.build()
                .add(KeyFrames.of(0, e -> {
                    newIcon.setOpacity(0.0);
                    changingIcon = false;
                }))
                .addConditional(() -> newIcon != null, KeyFrames.of(oOutMills, newIcon.opacityProperty(), 1.0, oCurve))
                .addConditional(label::isPresent, KeyFrames.of(oOutMills, label.get().opacityProperty(), 1.0, oCurve))
                .addConditional(label::isPresent, KeyFrames.of(outMillis, label.get().translateXProperty(), computeLabelDisplacement(), curve))
                .getAnimation();

            Animation closeAnimation = TimelineBuilder.build()
                .add(KeyFrames.of(0, e -> changingIcon = true))
                .addConditional(() -> currentIcon != null, KeyFrames.of(inMillis, currentIcon.opacityProperty(), 0.0, curve))
                .addConditional(label::isPresent, KeyFrames.of(inMillis, label.get().opacityProperty(), 0.0, curve))
                .add(KeyFrames.of(inMillis, fab.prefWidthProperty(), 0.0, curve))
                .setOnFinished(e -> {
                    changingIcon = false;
                    fab.setIcon(newIcon);
                    newIcon.applyCss();

                    Label lNode = getLabelNode().get();
                    double w = computeWidth();
                    double displacement = computeLabelDisplacement();

                    KeyFrame expandKF = KeyFrames.of(outMillis, fab.prefWidthProperty(), w, curve);
                    KeyFrame displacementKF = KeyFrames.of(outMillis, lNode.translateXProperty(), displacement, curve);
                    openAnimation.getKeyFrames().addAll(expandKF, displacementKF);
                })
                .getAnimation();
            changeIconAnimation = SequentialBuilder.build()
                .add(closeAnimation)
                .add(openAnimation)
                .getAnimation();
        } else {
            setScalePivot(pivot);

            Duration inMillis = M3Motion.SHORT4;
            Duration outMillis = M3Motion.LONG4;
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
            changeIconAnimation = SequentialBuilder.build()
                .add(scaleDown)
                .add(scaleUp)
                .getAnimation();
        }
        changeIconAnimation.play();
    }

    /**
     * Responsible for computing the target width when collapsing/extending the FAB,
     * a simple delegate for {@link MFXLabeled#computePrefWidth(double)}.
     */
    protected double computeWidth() {
        MFXFabBase fab = getFab();
        return fab.computePrefWidth(fab.getHeight());
    }

    /**
     * Calls {@link #computeLabelDisplacement(double, double, double)} feeding the following values as parameters:
     * <p> - The width of the fab computed through {@link LayoutUtils#boundWidth(Node)}
     * <p> - The icon width retrieved from its bounds, 0.0 if the icon is null or the content display is set to
     * {@link ContentDisplay#TEXT_ONLY}
     * <p> - The text width computed through {@link TextUtils#computeTextWidth(Font, String)}, and also pixel snapped with
     * {@link MFXFabBase#snapSizeX(double)}.
     * <p></p>
     * Little side node, if the FAB is using a skin of type {@link MFXLabeledSkin} and the text measurement cache has been
     * initialized, the text width is retrieved with {@link MFXLabeledSkin#getCachedTextWidth()}.
     */
    public double computeLabelDisplacement() {
        MFXFabBase fab = getFab();
        ContentDisplay cd = fab.getContentDisplay();
        MFXFontIcon icon = fab.getIcon();
        double tW = ds.getTextWidth(fab);
        return computeLabelDisplacement(
            LayoutUtils.boundWidth(fab),
            (icon == null || cd == ContentDisplay.TEXT_ONLY) ? 0.0 : icon.getLayoutBounds().getWidth(),
            tW
        );
    }

    /**
     * By default, the text node is placed at {@link Pos#CENTER_LEFT} by {@link MFXFabSkin}.
     * This method serves two purposes:
     * <p> 1) Supporting {@link HPos#CENTER} and {@link HPos#RIGHT} other than just {@link HPos#LEFT}
     * <p> 2) Making sure that the label is always well positioned when expanding/collapsing the FAB. By always positioning
     * it at {@link Pos#CENTER_LEFT} we make sure that it's {@code layoutX} doesn't change, and we can animate the
     * {@link Node#translateXProperty()} to make the transition look smooth.
     * <p></p>
     * For a less error-prone and efficient computation the algorithm is split in three internal classes implementing
     * {@link DisplacementStrategy}. See also {@link #updateDisplacementStrategy()}.
     */
    public double computeLabelDisplacement(double w, double iconW, double textW) {
        if (ds == null) updateDisplacementStrategy();
        return ds.computeDisplacement(w, iconW, textW);
    }

    /**
     * Responsible for changing the class used by {@link #computeLabelDisplacement(double, double, double)} which is
     * responsible for computing the x translation to apply to the label node.
     */
    protected void updateDisplacementStrategy() {
        MFXFabBase fab = getFab();
        switch (fab.getAlignment().getHpos()) {
            case LEFT: {
                ds = new LeftDisplacement();
                break;
            }
            case CENTER: {
                ds = new CenterDisplacement();
                break;
            }
            case RIGHT: {
                ds = new RightDisplacement();
                break;
            }
        }
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
        extendAnimation = null;
        changeIconAnimation = null;
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
        return getNode();
    }

    /**
     * Indicated whether the extended FAB is changing its icon through {@link #changeIcon(MFXFontIcon)}.
     * <p></p>
     * This is used by {@link MFXFabSkin} to allow the FAB's width to be set to 0.0 initially and then to
     * correctly compute the new width when expanding again.
     */
    public boolean isChangingIcon() {
        return changingIcon;
    }

    // Internal Classes
    protected interface DisplacementStrategy {
        double computeDisplacement(double w, double iconW, double textW);

        default double getTextWidth(MFXFabBase fab) {
            Skin<?> skin = fab.getSkin();
            if (skin instanceof MFXLabeledSkin) {
                double cached = ((MFXLabeledSkin<?, ?>) skin).getCachedTextWidth();
                if (cached >= 0) return cached;
            }
            return fab.snapSizeX(TextUtils.computeTextWidth(fab.getFont(), fab.getText()));
        }
    }

    protected class LeftDisplacement implements DisplacementStrategy {
        @Override
        public double computeDisplacement(double w, double iconW, double textW) {
            MFXFabBase fab = getFab();
            if (!fab.isExtended()) return new CenterDisplacement().computeDisplacement(w, iconW, textW);
            return 0;
        }
    }

    protected class CenterDisplacement implements DisplacementStrategy {
        @Override
        public double computeDisplacement(double w, double iconW, double textW) {
            MFXFabBase fab = getFab();
            Optional<Label> nodeOpt = getLabelNode();
            if (fab.getSkin() == null || nodeOpt.isEmpty()) return 0.0;

            Label node = nodeOpt.get();
            double nodeX = node.getLayoutX();

            if (!fab.isExtended()) {
                if (iconW <= 0) return 0.0;
                double target = (w - iconW) / 2;
                return target - nodeX;
            }

            double gap = (iconW > 0) ? fab.getGraphicTextGap() : 0.0;
            double totalLW = iconW + gap + getTextWidth(fab);
            double target = (w - totalLW) / 2;
            return target - nodeX;
        }
    }

    protected class RightDisplacement implements DisplacementStrategy {
        @Override
        public double computeDisplacement(double w, double iconW, double textW) {
            MFXFabBase fab = getFab();
            if (!fab.isExtended()) return new CenterDisplacement().computeDisplacement(w, iconW, textW);

            Optional<Label> nodeOpt = getLabelNode();
            if (fab.getSkin() == null || nodeOpt.isEmpty()) return 0.0;

            Label node = nodeOpt.get();
            double nodeX = node.getLayoutX();
            double right = fab.snappedRightInset();

            double gap = (iconW > 0) ? fab.getGraphicTextGap() : 0.0;
            double totalLW = iconW + gap + getTextWidth(fab);
            double target = (w - totalLW - right);
            return target - nodeX;
        }
    }
}
