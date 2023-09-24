package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXIconButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcore.builders.bindings.DoubleBindingBuilder;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/**
 * Default skin implementation for {@link MFXIconButton}s. Doesn't extend {@link MFXButtonSkin} as one may expect since
 * we don't need the label node.
 * <p>
 * This skin uses behaviors of type {@link MFXIconButtonBehavior}.
 * <p></p>
 * The layout is simple, there are just the button's icon specified by {@link MFXIconButton#iconProperty()} and the
 * {@link MaterialSurface} responsible for showing the various interaction states (applying an overlay background)
 * and generating ripple effects.
 */
public class MFXIconButtonSkin extends MFXSkinBase<MFXIconButton, MFXIconButtonBehavior> {
	//================================================================================
	// Properties
	//================================================================================
	private final MaterialSurface surface;
	private final MFXIconWrapper icon;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXIconButtonSkin(MFXIconButton button) {
		super(button);

		// Init icon wrapper
		icon = new MFXIconWrapper();
		icon.animatedProperty().bind(button.animatedProperty());
		icon.sizeProperty().bind(DoubleBindingBuilder.build()
			.setMapper(() -> Math.max(button.getWidth(), button.getHeight()))
			.addSources(button.widthProperty(), button.heightProperty())
			.get());

		// Init surface
		surface = new MaterialSurface(button)
			.initRipple(rg -> rg.setRippleColor(Color.web("#d7d1e7")));

		// Finalize init
		getChildren().addAll(surface, icon);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners:
	 * <p> - A listener on the {@link MFXIconButton#iconProperty()} to update the children list when it changes
	 */
	private void addListeners() {
		MFXIconButton button = getSkinnable();
		listeners(
			onInvalidated(button.iconProperty())
				.then(icon::setIcon)
				.executeNow()
		);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Initializes the given {@link MFXIconButtonBehavior} to handle events such as: {@link MouseEvent#MOUSE_PRESSED},
	 * {@link MouseEvent#MOUSE_RELEASED}, {@link MouseEvent#MOUSE_CLICKED}, {@link MouseEvent#MOUSE_EXITED} and
	 * {@link KeyEvent#KEY_PRESSED}.
	 */
	@Override
	protected void initBehavior(MFXIconButtonBehavior behavior) {
		MFXIconButton button = getSkinnable();
		MFXRippleGenerator rg = surface.getRippleGenerator();
		behavior.init();
		events(
			intercept(button, MouseEvent.MOUSE_PRESSED)
				.process(e -> behavior.mousePressed(e, c -> rg.generate(e))),

			intercept(button, MouseEvent.MOUSE_RELEASED)
				.process(e -> behavior.mouseReleased(e, c -> rg.release())),

			intercept(button, MouseEvent.MOUSE_CLICKED)
				.process(behavior::mouseClicked),

			intercept(button, MouseEvent.MOUSE_EXITED)
				.process(e -> behavior.mouseExited(e, c -> rg.release())),

			intercept(button, KeyEvent.KEY_PRESSED)
				.process(e -> behavior.keyPressed(e, c -> {
					Bounds b = button.getLayoutBounds();
					rg.generate(b.getCenterX(), b.getCenterY());
				}))
		);
	}

	@Override
	public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return leftInset + getSkinnable().getSize() + rightInset;
	}

	@Override
	public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return topInset + getSkinnable().getSize() + bottomInset;
	}

	@Override
	public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXIconButton button = getSkinnable();
		surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
		icon.autosize();
		positionInArea(icon, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	public void dispose() {
		surface.dispose();
		super.dispose();
	}
}
