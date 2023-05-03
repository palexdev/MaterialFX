package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXIconButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Default skin implementation for {@link MFXIconButton}s.
 * <p>
 * This skin uses behaviors of type {@link MFXIconButtonBehavior}.
 * <p></p>
 * The layout is simple, there are just the button's icon specified by {@link MFXIconButton#iconProperty()} and the
 * {@link MFXRippleGenerator} to generate ripples.
 */
public class MFXIconButtonSkin extends MFXSkinBase<MFXIconButton, MFXIconButtonBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXRippleGenerator rg;
    protected When<?> icWhen;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconButtonSkin(MFXIconButton button) {
        super(button);

        // Init ripple generator
        rg = new MFXRippleGenerator(button);
        rg.setManaged(false);
        rg.setAnimateBackground(false);
        rg.setAutoClip(true);
        rg.setRipplePrefSize(50);
        rg.setRippleColor(Color.web("d7d1e7"));

        // Finalize init
        MFXFontIcon icon = button.getIcon();
        getChildren().add(rg);
        if (icon != null) getChildren().add(icon);
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
        icWhen = When.onChanged(button.iconProperty())
                .then((o, n) -> {
                    if (o != null) getChildren().remove(o);
                    if (n != null) getChildren().add(n);
                })
                .listen();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * Initializes the given {@link MFXIconButtonBehavior} to handle events such as: {@link MouseEvent#MOUSE_PRESSED},
     * {@link MouseEvent#MOUSE_CLICKED}, {@link KeyEvent#KEY_PRESSED}.
     */
    @Override
    protected void initBehavior(MFXIconButtonBehavior behavior) {
        MFXIconButton button = getSkinnable();
        handle(button, MouseEvent.MOUSE_PRESSED, behavior::generateRipple);
        handle(button, MouseEvent.MOUSE_PRESSED, behavior::mousePressed);
        handle(button, MouseEvent.MOUSE_CLICKED, behavior::handleSelection);
        handle(button, KeyEvent.KEY_PRESSED, behavior::handleSelection);
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
        MFXIconButton button = getSkinnable();
        MFXFontIcon icon = button.getIcon();
        double size = button.getSize();
        double val;
        if (icon == null) {
            val = size;
        } else {
            val = Math.max(size, Math.max(
                    LayoutUtils.boundWidth(icon),
                    LayoutUtils.boundHeight(icon)
            ));
        }
        return leftInset + val + rightInset;
    }

    @Override
    public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXIconButton button = getSkinnable();
        MFXFontIcon icon = button.getIcon();
        double size = button.getSize();
        double val;
        if (icon == null) {
            val = size;
        } else {
            val = Math.max(size, Math.max(
                    LayoutUtils.boundWidth(icon),
                    LayoutUtils.boundHeight(icon)
            ));
        }
        return leftInset + val + rightInset;
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
        MFXFontIcon icon = button.getIcon();

        rg.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        if (icon == null) return;
        layoutInArea(icon, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    public void dispose() {
        rg.dispose();
        icWhen.dispose();
        super.dispose();
    }
}
