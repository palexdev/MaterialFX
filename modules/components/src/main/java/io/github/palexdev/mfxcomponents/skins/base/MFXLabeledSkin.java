package io.github.palexdev.mfxcomponents.skins.base;

import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.utils.fx.TextMeasurementCache;

import java.util.function.Consumer;

/**
 * Base skin for all components based on {@link MFXLabeled}, extension of {@link MFXSkinBase} for integration
 * with the new behavior API.
 * <p></p>
 * Allows implementations to easily change the way the text node is created by simply overriding {@link #createLabel(MFXLabeled)}.
 * <p></p>
 * <b>Note</b> that the text node is not added to the children list here, implementations are responsible for it, this
 * is to simplify things since (most probably) subclasses may have more than one node to add.
 *
 * @see BoundLabel
 */
public abstract class MFXLabeledSkin<L extends MFXLabeled<B>, B extends BehaviorBase<L>> extends MFXSkinBase<L, B> {
    //================================================================================
    // Properties
    //================================================================================
    protected final BoundLabel label;
    protected TextMeasurementCache tmCache;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabeledSkin(L labeled) {
        super(labeled);
        label = createLabel(labeled);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Initializes the {@link TextMeasurementCache} instance of this skin.
     * Implementations that heavily rely on such computations should call this and use {@link #getCachedTextWidth()}
     * and {@link #getCachedTextHeight()} to retrieve the text sizes when needed.
     */
    protected void initTextMeasurementCache() {
        if (tmCache == null) tmCache = new TextMeasurementCache(getSkinnable());
    }

    /**
     * Creates the {@link BoundLabel} which will display the component's text.
     * <p></p>
     * By default, also sets the {@link BoundLabel#onSetTextNode(Consumer)} action to bind the text node opacity property
     * to {@link MFXLabeled#textOpacityProperty()}.
     */
    protected BoundLabel createLabel(L labeled) {
        BoundLabel bl = new BoundLabel(labeled);
        bl.onSetTextNode(n -> n.opacityProperty().bind(labeled.textOpacityProperty()));
        return bl;
    }

    //================================================================================
    // Getters
    //================================================================================

    /**
     * Delegate for {@link TextMeasurementCache#getSnappedWidth()}. If the cache was not initialized before, returns -1.
     */
    public double getCachedTextWidth() {
        return (tmCache != null) ? tmCache.getSnappedWidth() : -1.0;
    }

    /**
     * Delegate for {@link TextMeasurementCache#getSnappedHeight()}. If the cache was not initialized before, returns -1.
     */
    public double getCachedTextHeight() {
        return (tmCache != null) ? tmCache.getSnappedHeight() : -1.0;
    }
}
