package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.Optional;

/**
 * Generic base behavior for all buttons extending from {@link MFXButtonBase}.
 * <p>
 * Offers basic and common capabilities such as:
 * <p> - Handling the button's {@link MFXRippleGenerator}. The retrieval of the generator is done by {@link #getRippleGenerator()}
 * <p> - Handle mouse events such as {@link MouseEvent#MOUSE_PRESSED}, {@link MouseEvent#MOUSE_RELEASED},
 * {@link MouseEvent#MOUSE_CLICKED} and {@link MouseEvent#MOUSE_EXITED}
 * <p> - Handle key events
 */
public class MFXButtonBehaviorBase<B extends MFXButtonBase<?>> extends BehaviorBase<B> {
    //================================================================================
    // Properties
    //================================================================================
    protected MFXRippleGenerator rg;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBehaviorBase(B button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void init() {
        getRippleGenerator(); // Cache on init, vastly helps on first generated ripple
    }

    @Override
    public void dispose() {
        rg = null;
        super.dispose();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Responsible for acquiring the focus and generating the ripple effect.
     *
     * @see #generateRipple(MouseEvent)
     */
    public void mousePressed(MouseEvent me) {
        getNode().requestFocus();
        generateRipple(me);
    }

    /**
     * Responsible for releasing the ripple effect.
     *
     * @see MFXRippleGenerator
     * @see MFXRippleGenerator#release()
     */
    public void mouseReleased(MouseEvent me) {
        releaseRipple(me);
    }

    /**
     * Responsible for calling {@link MFXButtonBase#fire()} if the clicked mouse button was {@link MouseButton#PRIMARY}.
     */
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() != MouseButton.PRIMARY) return;
        getNode().fire();
    }

    /**
     * Responsible for releasing the ripple effect.
     *
     * @see MFXRippleGenerator
     * @see MFXRippleGenerator#release()
     */
    public void mouseExited(MouseEvent me) {
        releaseRipple(me);
    }

    /**
     * Responsible for handling key events.
     * <p>
     * By default, this only handles {@link KeyCode#ENTER} events that should: trigger a ripple effect at the center of
     * the button, call {@link MFXButtonBase#fire()}
     */
    public void keyPressed(KeyEvent ke) {
        B btn = getNode();
        if (ke.getCode() == KeyCode.ENTER) {
            Bounds b = btn.getLayoutBounds();
            getRippleGenerator().ifPresent(rg -> rg.generate(b.getCenterX(), b.getCenterY()));
            btn.fire();
        }
    }

    // Ripple Handling

    /**
     * Responsible for generating the ripple effect if the pressed mouse button was {@link MouseButton#PRIMARY}.
     * <p>
     * Basically a shortcut for {@code getRippleGenerator().ifPresent(rg -> rg.generate(me))}, see {@link #getRippleGenerator()}.
     */
    public void generateRipple(MouseEvent me) {
        if (me.getButton() != MouseButton.PRIMARY) return;
        getRippleGenerator().ifPresent(rg -> rg.generate(me));
    }

    /**
     * Responsible for releasing the ripple effect, see {@link MFXRippleGenerator#release()}.
     * <p>
     * Basically a shortcut for {@code getRippleGenerator().ifPresent(MFXRippleGenerator::release)}, see {@link #getRippleGenerator()}.
     */
    public void releaseRipple(MouseEvent me) {
        getRippleGenerator().ifPresent(MFXRippleGenerator::release);
    }

    /**
     * Since the behavior doesn't have access to the button' skin, and since the generators are wrapped in a
     * {@link MaterialSurface}, this is responsible for retrieving it in some way.
     * <p></p>
     * The list returned by {@link Region#getChildrenUnmodifiable()} may be unmodifiable, but nothing prevents us
     * from still taking values from it. A stream searches for instances of {@link MaterialSurface} and then uses
     * {@link MaterialSurface#getRippleGenerator()} to retrieve the generator's instance. This is protected so that other
     * components may specify a different search algorithm. The good thing about this, is that it's way faster than
     * {@link Node#lookup(String)}.
     * <p></p>
     * Note that this returns an {@link Optional}. It may be empty in two cases!
     * <p> 1) In case the component' skin is still null
     * <p> 2) In case the surface was not found
     * <p></p>
     * After the first search, (performed by {@link #init()}), if the generator was found, the instance is cached locally
     * so that we don't need to perform the search anymore. An additional step to clear the reference is needed in the
     * {@link #dispose()} method.
     */
    protected Optional<MFXRippleGenerator> getRippleGenerator() {
        if (rg == null) {
            B btn = getNode();
            Skin<?> skin = btn.getSkin();
            if (skin == null) return Optional.empty();
            Optional<MFXRippleGenerator> opt = btn.getChildrenUnmodifiable().stream()
                .filter(n -> n instanceof MaterialSurface)
                .map(n -> ((MaterialSurface) n).getRippleGenerator())
                .findFirst();
            rg = opt.orElse(null);
            return opt;
        }
        return Optional.of(rg);
    }
}
