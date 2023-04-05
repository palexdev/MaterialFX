package io.github.palexdev.mfxcomponents.skins.base;

import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.BoundLabel;

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
}
