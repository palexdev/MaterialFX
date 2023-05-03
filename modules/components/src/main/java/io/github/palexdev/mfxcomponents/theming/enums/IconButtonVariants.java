package io.github.palexdev.mfxcomponents.theming.enums;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.theming.base.Variant;

/**
 * Enumerator implementing {@link Variant} to define the variants of {@link MFXIconButton}.
 */
public enum IconButtonVariants implements Variant {
    FILLED("filled"),
    FILLED_TONAL("filled-tonal"),
    OUTLINED("outlined"),
    ;

    private final String styleClass;

    IconButtonVariants(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public String variantStyleClass() {
        return styleClass;
    }
}
