package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.skins.MFXFilterComboBoxSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

public class MFXFilterComboBox<T> extends MFXComboBox<T> {

    public MFXFilterComboBox() {
    }

    public MFXFilterComboBox(ObservableList<T> items) {
        super(items);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXFilterComboBoxSkin<>(this);
    }
}
