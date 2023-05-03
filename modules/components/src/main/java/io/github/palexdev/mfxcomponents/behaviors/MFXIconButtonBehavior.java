package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.behaviors.base.MFXSelectableBehavior;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.Bounds;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This is the default behavior used by all {@link MFXIconButton} components.
 * <p>
 * Although it doesn't extend directly {@link MFXButtonBehavior}, this can be considered an extension of it
 * since it offers the same methods but also the necessary capabilities to handle selection, inherited from {@link MFXSelectableBehavior}.
 */
public class MFXIconButtonBehavior extends MFXSelectableBehavior<MFXIconButton> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXRippleGenerator rg;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconButtonBehavior(MFXIconButton selectable) {
        super(selectable);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Instructs the button's {@link MFXRippleGenerator} to generate a ripple for the given
     * {@link MouseEvent}.
     */
    public void generateRipple(MouseEvent me) {
        getRippleGenerator().ifPresent(rg -> rg.generate(me));
    }

    /**
     * Requests focus on mouse pressed.
     */
    public void mousePressed(MouseEvent me) {
        getNode().requestFocus();
    }

    /**
     * Delegates to {@link #handleSelection(MouseEvent)} but also checks if the icon button is in selection mode,
     * {@link MFXIconButton#selectableProperty()}. If it's not, exits.
     */
    public void mouseClicked(MouseEvent me) {
        MFXIconButton btn = getNode();
        if (!btn.isSelectable()) return;
        handleSelection(me);
    }

    /**
     * Handles {@link KeyEvent}s.
     * <p></p>
     * By default, if the pressed key is {@link KeyCode#ENTER}, first triggers the generation of a ripple effect at the
     * center of the button, then delegates to {@link #handleSelection(KeyEvent)} for the selection handling.
     */
    public void keyPressed(KeyEvent ke) {
        MFXIconButton node = getNode();
        if (ke.getCode() == KeyCode.ENTER) {
            Bounds b = node.getLayoutBounds();
            getRippleGenerator().ifPresent(rg -> rg.generate(b.getCenterX(), b.getCenterY()));
            handleSelection(ke);
        }
    }

    /**
     * Attempts at getting the {@link MFXRippleGenerator} from the icon' skin.
     * <p>
     * This is null and exception safe as it uses {@link Stream} and {@link Optional}.
     * Once found, it is also cached to avoid useless computations.
     */
    public Optional<MFXRippleGenerator> getRippleGenerator() {
        if (rg == null) {
            MFXIconButton btn = getNode();
            Skin<?> skin = btn.getSkin();
            if (skin == null) return Optional.empty();
            Optional<MFXRippleGenerator> opt = btn.getChildrenUnmodifiable().stream()
                    .filter(n -> n instanceof MFXRippleGenerator)
                    .map(n -> ((MFXRippleGenerator) n))
                    .findFirst();
            rg = opt.orElse(null);
            return opt;
        }
        return Optional.of(rg);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p></p>
     * Also clears the cached instance of the {@link MFXRippleGenerator} if found.
     */
    @Override
    public void dispose() {
        rg = null;
        super.dispose();
    }
}
