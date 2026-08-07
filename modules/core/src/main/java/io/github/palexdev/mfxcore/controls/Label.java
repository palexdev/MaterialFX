/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.controls;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

/// Simple extension of [javafx.scene.control.Label] to set the wrapping width in a more intuitive way.
/// By default, only [Text] has the capability of specifying the wrapping width.
///
/// For `Labels` this should be handled by setting its max width and by enabling the [#wrapTextProperty()].
/// However, this may not lead to the desired behavior, and it's not very intuitive as well.
/// Let me explain, by setting the max width, you are limiting the label's width regardless of the state of [#wrapTextProperty()].
/// But there are cases in which you may want to limit the width only if the text should be wrapped.
/// And here's when this comes in handy. The property can be set via code or CSS ('-fx-wrapping-width' property),
/// and it's implemented by overriding the [#computeMaxWidth(double)] method.
/// If the text should be wrapped and the specified wrapping width is greater than 0, then the latter will be used
/// as the label's max width. Otherwise, use the default computation.
///
/// This also adds a new feature/workaround. In JavaFX, Labels are composed of two nodes at max: the graphic and the text.
/// For performance reasons, probably, the text node is not added to the control until the text is not `null` and not empty.
/// A mechanism to detect and retrieve such node has been added, allowing custom text-based components to take full control
/// of the text node itself rather than the label as a whole.
///
/// The presence of this node can be detected via the read-only property [#textNodeProperty()]. You can set an action to
/// perform when the text node changes easily by setting a [BiConsumer] that accepts both the old and the new node (both can be `null`),
/// see [#onSetTextNode(BiConsumer)].
///
/// This allows implementing three other useful tricks:
///  1) A way to completely disable the text truncation by always showing the full text and removing the clip
///     (Now much more performant than previous implementation, no listeners involved. Everything is done at layout time
///     in a custom inline-skin.)
///  2) 'Backport' the [#fontSmoothingTypeProperty()] here, allowing to set the antialiasing method directly on the label.
///  The default font smoothing type for this is set to [FontSmoothingType#LCD].
///  3) A way to detect when the label's text is truncated, [#truncatedProperty()].
///     _(Use this instead of the new JavaFX property!)_
public class Label extends javafx.scene.control.Label {
    //================================================================================
    // Properties
    //================================================================================
    private final ReadOnlyObjectWrapper<Text> textNode = new ReadOnlyObjectWrapper<>() {
        @Override
        public void set(Text newValue) {
            Text oldValue = get();
            onSetTextNode(oldValue, newValue);
            super.set(newValue);
        }
    };
    private final ReadOnlyBooleanWrapper truncated = new ReadOnlyBooleanWrapper();
    private BiConsumer<Text, Text> onSetTextNode = (_, _) -> {};

    //================================================================================
    // Constructors
    //================================================================================
    public Label() {}

    public Label(String text) {
        super(text);
    }

    public Label(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void onSetTextNode(Text oldValue, Text newValue) {
        truncated.unbind();
        if (oldValue != null) oldValue.fontSmoothingTypeProperty().unbind();
        if (newValue != null) {
            truncated.bind(newValue.textProperty().map(s -> {
                if (isDisableTruncation()) return false;
                return !Objects.equals(s, getText());
            }));
            newValue.fontSmoothingTypeProperty().bind(fontSmoothingTypeProperty());
        }
        onSetTextNode.accept(oldValue, newValue);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new LabelSkin(this) {
            @Override
            protected void updateChildren() {
                super.updateChildren();
                for (Node child : getChildren()) {
                    if ("LabeledText".equals(child.getClass().getSimpleName())) {
                        textNode.set((Text) child);
                        break;
                    }
                }
            }

            @Override
            protected void layoutChildren(double x, double y, double w, double h) {
                super.layoutChildren(x, y, w, h);

                if (!isDisableTruncation()) return;
                Text tn = getTextNode();
                if (tn != null) {
                    tn.setText(getText());
                    if (tn.getClip() instanceof Rectangle r) {
                        r.setWidth(Double.MAX_VALUE);
                    }
                }
            }
        };
    }

    @Override
    protected double computeMaxWidth(double height) {
        double maxW = super.computeMaxWidth(height);
        double ww = getWrappingWidth();
        if (isWrapText() && ww > 0) return Math.min(maxW, ww);
        return maxW;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty disableTruncation = new StyleableBooleanProperty(
        StyleableProperties.DISABLE_TRUNCATION,
        this,
        "disableTruncation",
        false
    ) {
        @Override
        protected void invalidated() {
            Text tn = getTextNode();
            if (tn != null) {
                onSetTextNode(null, tn);
                requestLayout();
            }
        }
    };

    private final StyleableObjectProperty<FontSmoothingType> fontSmoothingType = new StyleableObjectProperty<>(
        StyleableProperties.FONT_SMOOTHING_TYPE,
        this,
        "fontSmoothingType",
        FontSmoothingType.LCD
    );

    private final StyleableDoubleProperty wrappingWidth = new StyleableDoubleProperty(
        StyleableProperties.WRAPPING_WIDTH,
        this,
        "wrappingWidth",
        USE_COMPUTED_SIZE
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    public boolean isDisableTruncation() {
        return disableTruncation.get();
    }

    /// Specifies whether to completely disable the text truncation capabilities of this label.
    ///
    /// Can be set from CSS via the property: '-fx-disable-truncation'.
    public StyleableBooleanProperty disableTruncationProperty() {
        return disableTruncation;
    }

    public void setDisableTruncation(boolean disableTruncation) {
        this.disableTruncation.set(disableTruncation);
    }

    public FontSmoothingType getFontSmoothingType() {
        return fontSmoothingType.get();
    }

    /// Specifies the font smoothing algorithm for the text node of this label, see [FontSmoothingType] and
    /// [#fontSmoothingTypeProperty()].
    ///
    /// Can be set from CSS via the property: '-fx-font-smoothing-type'.
    public StyleableObjectProperty<FontSmoothingType> fontSmoothingTypeProperty() {
        return fontSmoothingType;
    }

    public void setFontSmoothingType(FontSmoothingType fontSmoothingType) {
        this.fontSmoothingType.set(fontSmoothingType);
    }

    public double getWrappingWidth() {
        return wrappingWidth.get();
    }

    /// Allows specifying a maximum width for the label that is applied only when it is greater than 0 and
    /// [#wrapTextProperty()] set to true.
    ///
    /// Can be set from CSS via the property: '-fx-wrapping-width'.
    public StyleableDoubleProperty wrappingWidthProperty() {
        return wrappingWidth;
    }

    public void setWrappingWidth(double wrappingWidth) {
        this.wrappingWidth.set(wrappingWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<Label> FACTORY = new StyleablePropertyFactory<>(javafx.scene.control.Label.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<Label, Boolean> DISABLE_TRUNCATION =
            FACTORY.createBooleanCssMetaData(
                "-fx-disable-truncation",
                Label::disableTruncationProperty,
                false
            );

        private static final CssMetaData<Label, FontSmoothingType> FONT_SMOOTHING_TYPE =
            FACTORY.createEnumCssMetaData(
                FontSmoothingType.class,
                "-fx-font-smoothing-type",
                Label::fontSmoothingTypeProperty,
                FontSmoothingType.LCD
            );

        private static final CssMetaData<Label, Number> WRAPPING_WIDTH =
            FACTORY.createSizeCssMetaData(
                "-fx-wrapping-width",
                Label::wrappingWidthProperty,
                USE_COMPUTED_SIZE
            );


        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                javafx.scene.control.Label.getClassCssMetaData(),
                DISABLE_TRUNCATION, FONT_SMOOTHING_TYPE, WRAPPING_WIDTH
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Text getTextNode() {
        return textNode.get();
    }

    /// Specifies the text node of this label or `null` if the skin has not created the text node yet.
    public ReadOnlyObjectProperty<Text> textNodeProperty() {
        return textNode.getReadOnlyProperty();
    }

    public boolean isTruncated() {
        return truncated.get();
    }

    /// Specifies whether the text is currently truncated or not.
    public ReadOnlyBooleanProperty truncatedProperty() {
        return truncated.getReadOnlyProperty();
    }

    /// Sets the action to execute when the text node is detected and stored.
    ///
    /// Accepts both the old and the new node so that if you need to dispose of something on the old one, you can.
    public void onSetTextNode(BiConsumer<Text, Text> onSetTextNode) {
        this.onSetTextNode = Optional.ofNullable(onSetTextNode)
            .orElse((_, _) -> {});
    }
}
