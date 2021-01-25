package io.github.palexdev.materialfx.font;

/**
 * Enumerator class for MaterialFX font resources.
 */
public enum FontResources {
    CALENDAR_BLACK("mfx-calendar-black", '\uE904'),
    CALENDAR_SEMI_BLACK("mfx-calendar-semi-black", '\uE905'),
    CALENDAR_WHITE("mfx-calendar-white", '\uE906'),
    CHEVRON_DOWN("mfx-chevron-down", '\uE900'),
    CHEVRON_LEFT("mfx-chevron-left", '\uE901'),
    CHEVRON_RIGHT("mfx-chevron-right", '\uE902'),
    CHEVRON_UP("mfx-chevron-right", '\uE903'),
    CIRCLE("mfx-circle", '\uE909'),
    GOOGLE("mfx-google", '\uE90a'),
    MINUS_CIRCLE("mfx-minus-circle", '\uE907'),
    X_CIRCLE("mfx-x-circle", '\uE908')
    ;

    public static FontResources findByDescription(String description) {
        for (FontResources font : values()) {
            if (font.getDescription().equals(description)) {
                return font;
            }
        }
        throw new IllegalArgumentException("Icon description '" + description + "' is invalid!");
    }

    private final String description;
    private final char code;

    FontResources(String description, char code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public char getCode() {
        return code;
    }

}
