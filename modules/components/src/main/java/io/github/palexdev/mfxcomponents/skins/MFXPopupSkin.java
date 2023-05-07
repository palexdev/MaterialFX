package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.skins.base.IMFXPopupSkin;
import io.github.palexdev.mfxcomponents.window.popups.IMFXPopup;
import io.github.palexdev.mfxcomponents.window.popups.MFXPopup;
import io.github.palexdev.mfxcomponents.window.popups.MFXPopupRoot;
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

/**
 * Default skin implementation for popups implementing {@link IMFXPopup} and {@link Skinnable}.
 * <p>
 * Supports animation as this also implements {@link IMFXPopupSkin}. As shown by the M3 guidelines,
 * popups and tooltips use animations that alter both the opacity and the scale of the content.
 * The {@link Scale} transform sets its pivot at the center of the content. For tooltips the scale will only change
 * for the x, for generic popups it will also affect the y.
 * <p>
 * It's worth mentioning that all the needed properties regarding the animations are {@code protected}, meaning
 * that they can be easily changed inline. There's also a method that forces the animations to be rebuilt, {@link #rebuildAnimations()},
 * to ensure that the new parameters are used.
 */
public class MFXPopupSkin<P extends IMFXPopup & Skinnable> implements Skin<P>, IMFXPopupSkin {
    //================================================================================
    // Properties
    //================================================================================
    protected P popup;
    protected MFXPopupRoot root;

    private Scale scale;
    protected double inScaleX = 0.5;
    protected double outScaleX = 1.0;
    protected double inScaleY = 1.0;
    protected double outScaleY = 1.0;

    private Animation inAnimation;
    private Animation outAnimation;
    protected Duration inDuration = M3Motion.MEDIUM2;
    protected Duration outDuration = M3Motion.LONG2;
    protected Interpolator curve = M3Motion.EMPHASIZED;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopupSkin(P popup) {
        this.popup = popup;

        // Init animations parameters
        if (popup instanceof MFXPopup) {
            inScaleY = 0.5;
        }

        // Init root
        root = new MFXPopupRoot(popup);
        root.setOpacity(0.0);
        root.setCache(true);
        root.setCacheHint(CacheHint.SCALE);

        // Init scale and make sure inAnimation is played
        scale = initScale();
        animateIn();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p></p>
     * Increments the opacity to 1.0, and both the xScale and yScale to 1.0.
     * Before it is played, the {@link Scale} transform is initialized with the values specified by
     * {@link #inScaleX} and {@link #inScaleY}.
     */
    @Override
    public void animateIn() {
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

    /**
     * {@inheritDoc}
     * <p></p>
     * Decrements the opacity to 0.0, and both the xScale and yScale to {@link #outScaleX} and {@link #outScaleY}
     * respectively.
     * <p>
     * At the end of the animation {@link IMFXPopup#close()} is called, thus ensuring that the framework really
     * closes the window.
     */
    @Override
    public void animateOut() {
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

    /**
     * Initializes, adds and  returns the {@link Scale} transform.
     * <p>
     * The initial xScale and yScale are set to {@link #outScaleX} and {@link #outScaleY} respectively.
     * The pivot is set at the center of the content.
     */
    protected Scale initScale() {
        if (scale == null) {
            scale = new Scale(inScaleX, inScaleY);
            scale.pivotXProperty().bind(root.layoutBoundsProperty().map(Bounds::getCenterX));
            scale.pivotYProperty().bind(root.layoutBoundsProperty().map(Bounds::getCenterY));
            root.getTransforms().add(scale);
        }
        return scale;
    }

    /**
     * Sets both the in animation and the out animation to null.
     * Thus ensuring that the next time {@link #animateIn()} and {@link #animateOut()} are called they
     * will be rebuilt.
     */
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
