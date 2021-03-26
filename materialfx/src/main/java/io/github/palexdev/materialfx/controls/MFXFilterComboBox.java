package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXFilterComboBoxSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

/**
 * This combo box allows to filter the items shown in the popup's listview.
 * <p>
 * Extends {@code MFXComboBox} and redefines the style class to "mfx-filter-combo-box".
 *
 * @param <T> The type of the value that has been selected
 * @see MFXComboBox
 */
public class MFXFilterComboBox<T> extends MFXComboBox<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-filter-combo-box";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-filter-combobox.css");

    /**
     * When the popup is shown and the text field is added to the scene the text field is not focused,
     * to change this behavior and force it to be focused you can set this boolean to true.
     * <p>
     * For more details see {@link MFXFilterComboBoxSkin}
     */
    private boolean forceFieldFocusOnShow = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterComboBox() {
        initialize();
    }

    public MFXFilterComboBox(ObservableList<T> items) {
        super(items);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public boolean isForceFieldFocusOnShow() {
        return forceFieldFocusOnShow;
    }

    public void setForceFieldFocusOnShow(boolean forceFieldFocusOnShow) {
        this.forceFieldFocusOnShow = forceFieldFocusOnShow;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXFilterComboBoxSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
