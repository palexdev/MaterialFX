package io.github.palexdev.materialfx.utils;

import javafx.util.StringConverter;

/**
 * Implementation of {@link StringConverter} to work with a generic {@link Enum}.
 * <p></p>
 * For this to work, it's necessary to specify the enumerator class, see {@link Enum#valueOf(Class, String)}.
 */
public class EnumStringConverter<E extends Enum<E>> extends StringConverter<E> {
    //================================================================================
    // Properties
    //================================================================================
    private final Class<E> type;

    //================================================================================
    // Constructors
    //================================================================================
    public EnumStringConverter(Class<E> type) {
        this.type = type;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * Calls toString() on the given enumeration.
     */
    @Override
    public String toString(E e) {
        return e.toString();
    }

    /**
     * Uses {@link Enum#valueOf(Class, String)} to convert the given String to an enumeration.
     */
    @Override
    public E fromString(String string) {
        return E.valueOf(type, string);
    }
}
