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

package io.github.palexdev.mfxresources.icon;

import java.util.List;

import io.github.palexdev.mfxeffects.utils.ColorUtils;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPack;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPacks;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/// Custom component, extension of [Text], capable of showing and handling font icons.
///
/// Icons can be set by their name, e.g. "fas-user", through the [#iconNameProperty()]. Additionally, you can set the
/// size and color through the [#sizeProperty()] and [#colorProperty()] respectively.
///
/// Font icons, as the name suggests, usually come from a "special" font file that contains icon glyphs rather than
/// alphanumeric glyphs. Each icon glyph is represented as a Unicode character, which is converted to a string and set
/// as the text of this component. The icon is properly rendered only if the appropriate font is set.
///
/// The [FontIconsPack] API is responsible for resolving this mapping: `iconName -> Unicode character`.<br >
/// Thanks to the [FontIconsPacks] system, `MFXFontIcon` is capable of automatically determining the correct [FontIconsPack]
/// from the set icon name. Therefore, the appropriate [Font] is also automatically set when the [#iconsPackProperty()]
/// changes.
///
/// @see FontIconsPacks Performance Tips
public class MFXFontIcon extends Text implements Cloneable {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<FontIconsPack> pack = new SimpleObjectProperty<>();

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableStringProperty iconName = new SimpleStyleableStringProperty(
        StyleableProperties.ICON_NAME,
        this,
        "iconName",
        ""
    ) {
        @Override
        protected void invalidated() {
            update();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
        StyleableProperties.SIZE,
        this,
        "size",
        FontIconsPack.DEFAULT_FONT_SIZE
    ) {
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableObjectProperty<Color> color = new SimpleStyleableObjectProperty<>(
        StyleableProperties.COLOR,
        this,
        "color",
        FontIconsPack.DEFAULT_COLOR
    ) {
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    public String getIconName() {
        return iconName.get();
    }

    /// Sets the displayed icon by its name. The vendor prefix determines the [FontIconsPack] used to convert the name
    /// to a Unicode character and to set the right font.
    ///
    /// Can be set from CSS via the property: '-mfx-icon'.
    ///
    /// @see MFXFontIcon
    /// @see FontIconsPacks
    public StyleableStringProperty iconNameProperty() {
        return iconName;
    }

    public MFXFontIcon setIconName(String name) {
        iconName.set(name);
        return this;
    }

    public double getSize() {
        return size.get();
    }

    /// Sets the icon's size. Although, to be precise, this determines the component's font size.
    ///
    /// Can be set from CSS via the property: '-mfx-size'.
    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public MFXFontIcon setSize(double size) {
        this.size.set(size);
        return this;
    }

    public Color getColor() {
        return color.get();
    }

    /// Sets the icon's color. This is effectively a "bridge" to the [#fillProperty()] but it limits the values to
    /// [Colors][Color].
    ///
    /// Can be set from CSS via the property: '-mfx-color'.
    public StyleableObjectProperty<Color> colorProperty() {
        return color;
    }

    public MFXFontIcon setColor(Color color) {
        this.color.set(color);
        return this;
    }

    //================================================================================
    // Constructors
    //================================================================================

    public MFXFontIcon() {}

    public MFXFontIcon(String iconName) {
        setIconName(iconName);
    }

    public MFXFontIcon(String iconName, Color color) {
        setIconName(iconName);
        setColor(color);
    }

    public MFXFontIcon(String iconName, double size) {
        setIconName(iconName);
        setSize(size);
    }

    public MFXFontIcon(String iconName, double size, Color color) {
        setIconName(iconName);
        setSize(size);
        setColor(color);
    }

    {
        getStyleClass().add("mfx-font-icon");
        fillProperty().bind(color);
        fontProperty().bind(Bindings.createObjectBinding(
            () -> {
                FontIconsPack pack = getIconsPack();
                if (pack == null) return Font.getDefault();
                return pack.font(getSize());
            },
            pack, size
        ));
    }

    //================================================================================
    // Methods
    //================================================================================

    protected void update() {
        String name = getIconName();
        if (name == null || name.isBlank()) {
            setText("");
            return;
        }

        FontIconsPack pack = FontIconsPacks.pack(name);
        if (pack == null) {
            setText("");
            throw new IllegalStateException("No icons pack found for name: " + name);
        }
        setIconsPack(pack);

        String icon = pack.icon(name);
        if (icon == null) {
            setText("");
            throw new IllegalStateException("No icon found for name: " + name + " in pack: " + pack.name());
        }
        setText(icon);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MFXFontIcon clone() {
        MFXFontIcon clone = new MFXFontIcon();
        clone.pack.set(pack.get());
        clone.setIconName(getIconName());
        clone.setSize(getSize());
        clone.setColor(getColor());
        return clone;
    }

    @Override
    public String toString() {
        return "MFXFontIcon{" +
               "pack=" + (getIconsPack() != null ? getIconsPack().name() : "null") +
               ", icon=" + getIconName() +
               ", unicode=" + FontIconsPack.textToUnicode(getText()) +
               ", size=" + getSize() +
               ", color=" + ColorUtils.toCss(getColor()) +
               '}';

    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXFontIcon> FACTORY = new StyleablePropertyFactory<>(Text.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXFontIcon, String> ICON_NAME =
            FACTORY.createStringCssMetaData(
                "-mfx-icon",
                MFXFontIcon::iconNameProperty,
                ""
            );

        private static final CssMetaData<MFXFontIcon, Number> SIZE =
            FACTORY.createSizeCssMetaData(
                "-mfx-size",
                MFXFontIcon::sizeProperty,
                FontIconsPack.DEFAULT_FONT_SIZE
            );

        private static final CssMetaData<MFXFontIcon, Color> COLOR =
            FACTORY.createColorCssMetaData(
                "-mfx-color",
                MFXFontIcon::colorProperty,
                FontIconsPack.DEFAULT_COLOR
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Text.getClassCssMetaData(),
                ICON_NAME, SIZE, COLOR
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters
    //================================================================================
    public FontIconsPack getIconsPack() {
        return pack.get();
    }

    /// Specifies the current set [FontIconsPack] which is responsible for resolving the [#iconNameProperty()] to a Unicode
    /// character and for specifying the correct font to render its icons.
    public ReadOnlyObjectProperty<FontIconsPack> iconsPackProperty() {
        return pack;
    }

    protected void setIconsPack(FontIconsPack pack) {
        this.pack.set(pack);
    }
}
