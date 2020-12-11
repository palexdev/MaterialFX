package io.github.palexdev.materialfx.validation.base;

import io.github.palexdev.materialfx.beans.binding.BooleanListBinding;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;

/**
 * Interface for all validators, most of these methods are wrappers
 * for {@link BooleanListBinding}
 */
public interface IMFXValidator {
    boolean isValid();
    void addInvalidationListener(InvalidationListener invalidationListener);
    void addChangeListener(ChangeListener<? super Boolean> changeListener);
    void removeInvalidationListener(InvalidationListener invalidationListener);
    void removeChangeListener(ChangeListener<? super Boolean> changeListener);
}
