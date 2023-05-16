package io.github.palexdev.mfxcomponents.theming.enums;

import io.github.palexdev.mfxcomponents.theming.base.Variant;

public enum ButtonVariants implements Variant {
    ELEVATED("elevated"),
    FILLED("filled"),
    FILLED_TONAL("filled-tonal"),
    OUTLINED("outlined"),
    TEXT("text"),
    ;

    private final String styleClass;

    ButtonVariants(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public String variantStyleClass() {
        return styleClass;
    }
}
