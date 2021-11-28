/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXLabelSkin;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * This is the implementation of a label following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Control} and provides a new skin since it is built from scratch.
 * <p>
 * Side note: lacks some features like text wrapping, overrun and ellipsis but there are also
 * new features like leading and trailing icons support, prompt text, changeable styles at runtime
 * and it can also be set to editable like a text field (double click on the label to edit).
 */
@Deprecated(forRemoval = true, since = "11.13.0")
public class MFXLabel extends Labeled {
    private final String STYLE_CLASS = "mfx-label";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXLabelStyle1.css");

    private final StringProperty floatingText = new SimpleStringProperty();
    private final BooleanProperty centerWhenEmptyFloat = new SimpleBooleanProperty(false);
    private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

    protected static final PseudoClass EDITING_PSEUDO_CLASS = PseudoClass.getPseudoClass("editing");
    private final BooleanProperty editing = new SimpleBooleanProperty() {
        @Override
        public void set(boolean newValue) {
            if (!isEditable()) return;
            super.set(newValue);
        }
    };

    public MFXLabel() {
        this("");
    }

    public MFXLabel(String text) {
        this(text, "");
    }

    public MFXLabel(String text, String floatingText) {
        this(text, floatingText, null);
    }

    public MFXLabel(String text, String floatingText, Node graphic) {
        super(text, graphic);
        setFloatingText(floatingText);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        editing.addListener(invalidated -> pseudoClassStateChanged(EDITING_PSEUDO_CLASS, editing.get()));
    }

    public String getFloatingText() {
        return floatingText.get();
    }

    public StringProperty floatingTextProperty() {
        return floatingText;
    }

    public void setFloatingText(String floatingText) {
        this.floatingText.set(floatingText);
    }

    public boolean isCenterWhenEmptyFloat() {
        return centerWhenEmptyFloat.get();
    }

    public BooleanProperty centerWhenEmptyFloatProperty() {
        return centerWhenEmptyFloat;
    }

    public void setCenterWhenEmptyFloat(boolean centerWhenEmptyFloat) {
        this.centerWhenEmptyFloat.set(centerWhenEmptyFloat);
    }

    public Node getTrailingIcon() {
        return trailingIcon.get();
    }

    public ObjectProperty<Node> trailingIconProperty() {
        return trailingIcon;
    }

    public void setTrailingIcon(Node trailingIcon) {
        this.trailingIcon.set(trailingIcon);
    }

    public boolean isEditing() {
        return editing.get();
    }

    public BooleanProperty editingProperty() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing.set(editing);
    }

    private final StyleableBooleanProperty editable = new SimpleStyleableBooleanProperty(
            StyleableProperties.EDITABLE,
            this,
            "editable",
            false
    ) {
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableDoubleProperty gap = new SimpleStyleableDoubleProperty(
            StyleableProperties.GAP,
            this,
            "gap",
            5.0
    ) {
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    public boolean isEditable() {
        return editable.get();
    }

    public StyleableBooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public double getGap() {
        return gap.get();
    }

    public StyleableDoubleProperty gapProperty() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap.set(gap);
    }

    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXLabel> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXLabel, Boolean> EDITABLE =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-editable",
                        MFXLabel::editableProperty,
                        false
                );

        private static final CssMetaData<MFXLabel, Number> GAP =
                FACTORY.createSizeCssMetaData(
                        "-mfx-gap",
                        MFXLabel::gapProperty,
                        5.0
                );

        static {
            cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
                    Labeled.getClassCssMetaData(),
                    EDITABLE, GAP
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXLabelSkin(this);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXLabel.getControlCssMetaDataList();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
