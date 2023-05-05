package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class MFXPopupSkin<P extends IMFXPopup & Skinnable> implements Skin<P> {
    //================================================================================
    // Properties
    //================================================================================
    protected P popup;
    protected MFXPopupRoot root;

    private Scale scale;
    protected double inScaleX = 0.7;
    protected double outScaleX = 1.0;
    protected double inScaleY = 1.0;
    protected double outScaleY = 1.0;

    private Animation inAnimation;
    private Animation outAnimation;
    protected Duration inDuration = M3Motion.LONG2;
    protected Duration outDuration = M3Motion.LONG2;
    protected Interpolator curve = M3Motion.EMPHASIZED;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopupSkin(P popup) {
        this.popup = popup;
        root = new MFXPopupRoot(popup);
        root.setOpacity(0.0);
        root.setCache(true);
        root.setCacheHint(CacheHint.SCALE);
        scale = initScale();
        animateIn();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void animateIn() {
        if (!popup.isAnimated()) return;
        if (inAnimation == null) {
            inAnimation = TimelineBuilder.build()
                .add(KeyFrames.of(inDuration, root.opacityProperty(), 1.0, curve))
                .add(KeyFrames.of(inDuration, scale.xProperty(), 1.0, curve))
                .add(KeyFrames.of(inDuration, scale.yProperty(), 1.0, curve))
                .getAnimation();
        }
        if (Animations.isPlaying(outAnimation)) outAnimation.stop();
        scale.setX(inScaleX);
        scale.setY(inScaleY);
        inAnimation.play();
    }

    protected void animateOut() {
        if (!popup.isAnimated()) {
            popup.close();
            return;
        }
        if (outAnimation == null) {
            outAnimation = TimelineBuilder.build()
                .add(KeyFrames.of(outDuration, root.opacityProperty(), 0.0, curve))
                .add(KeyFrames.of(outDuration, scale.xProperty(), outScaleX, curve))
                .add(KeyFrames.of(outDuration, scale.yProperty(), outScaleY, curve))
                .setOnFinished(e -> popup.close())
                .getAnimation();
        }
        if (Animations.isPlaying(inAnimation)) inAnimation.stop();
        outAnimation.play();
    }

    protected Scale initScale() {
        if (scale == null) {
            scale = new Scale(inScaleX, inScaleY);
            scale.pivotXProperty().bind(root.layoutBoundsProperty().map(Bounds::getCenterX));
            scale.pivotYProperty().bind(root.layoutBoundsProperty().map(Bounds::getCenterY));
            root.getTransforms().add(scale);
        }
        return scale;
    }

    protected void rebuildAnimations() {
        inAnimation = null;
        outAnimation = null;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public P getSkinnable() {
        return popup;
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public void dispose() {
        root.dispose();
        root = null;
        popup = null;
    }
}
