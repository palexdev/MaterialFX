package io.github.palexdev.materialfx.font;

/**
 * Enumerator class for MaterialFX font resources.
 */
public enum FontResources {
    CALENDAR_BLACK("mfx-calendar-black", '\uE904'),
    CALENDAR_SEMI_BLACK("mfx-calendar-semi-black", '\uE905'),
    CALENDAR_WHITE("mfx-calendar-white", '\uE906'),
    CASPIAN_MARK("mfx-caspian-mark", '\uE90b'),
    CHEVRON_DOWN("mfx-chevron-down", '\uE900'),
    CHEVRON_LEFT("mfx-chevron-left", '\uE901'),
    CHEVRON_RIGHT("mfx-chevron-right", '\uE902'),
    CHEVRON_UP("mfx-chevron-right", '\uE903'),
    CIRCLE("mfx-circle", '\uE909'),
    GOOGLE("mfx-google", '\uE90a'),
    MINUS_CIRCLE("mfx-minus-circle", '\uE907'),
    MODENA_MARK("mfx-modena-mark", '\uE90c'),
    VARIANT3_MARK("mfx-variant3-mark", '\uE90d'),
    VARIANT4_MARK("mfx-variant4-mark", '\uE90e'),
    VARIANT5_MARK("mfx-variant5-mark", '\uE90f'),
    VARIANT6_MARK("mfx-variant6-mark", '\uE910'),
    VARIANT7_MARK("mfx-variant7-mark", '\uE911'),
    VARIANT8_MARK("mfx-variant8-mark", '\uE912'),
    VARIANT9_MARK("mfx-variant9-mark", '\uE913'),
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
