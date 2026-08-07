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

package io.github.palexdev.mfxresources.icon.packs;

import java.util.*;
import java.util.function.Function;

import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import javafx.scene.text.Font;

/// Central registry for all [FontIconsPacks][FontIconsPack] supported by `MFXResources`.
///
/// This mechanism allows [MFXFontIcon] to support any icon pack, even the ones coming from external sources such as
/// [Ikonli](https://github.com/kordamp/ikonli) as long as they are registered before the application is shown.
///
/// Usually, icon packs, prefix their icons with a vendor string. For example, FontAwesome Solid icons are prefixed
/// by `fas-`. The registry uses the prefix to register the various packs.
///
/// The official `MFXResources` icon packs ([io.github.palexdev.mfxresources.icon.packs]) are automatically registered.
///
/// ### Performance
///
/// Let's discuss a bit about performance and how you can improve it.
///
/// 1) This registry uses a [TreeMap] to get the appropriate [FontIconsPack] for any given icon's name. So, insertions
/// and lookups are fairly fast. The same applies to official icon packs as icons are loaded into a [HashMap].
/// Third party packs, on the contrary, are not guaranteed to perform the same.<br >
/// You could try improving this by creating a custom [FontIconsPack] that caches the values in a map for faster lookup.
/// 2) You probably won't notice any performance issues when using this system in your application.
/// However, there are certain situations where a delay might occur. For example, if you display a popup that contains
/// font icons, you might experience a noticeable delay the first time the popup appears. This is because the first time
/// the registry is referenced in your application, Java needs to load the class and executes the static initializer block.
/// At that point, official icon packs are created and registered. Unfortunately, this is a quite expensive operation.<br >
/// To improve situations like this, I suggest that you make a tradeoff. By calling [#init()] as soon as possible, before
/// the app is shown, you sacrifice a bit of startup time (not noticeable really), but the registry is initialized earlier
/// improving performance in later calls.
///
/// ### Third Party Packs
/// [//@formatter:off]
/// ```java
/// // Here's an example of how to register a third party icons pack, say from Ikonli.
/// FontIconsPacks.register(
///     "win10-",
///     Font.loadFont(new Win10IkonHandler().getFontResourceAsStream(), size),
///     name -> String.valueOf((char) Win10.findByDescription(name).getCode())
/// );
/// // Or even better...
/// FontIconsPacks.register("win10-", new FontIconsPack() {
///     @Override
///     public String name() {
///         return "Ikonli Win10 Icons";
///     }
///
///     @Override
///     public String[] iconNames() {
///         return Arrays.stream(Win10.values())
///             .map(Win10::getDescription)
///             .toArray(String[]:new);
///     }
///
///     @Override
///     public String icon(String name) {
///        return String.valueOf((char) Win10.findByDescription(name).getCde());
///     }
///
///     @Override
///     public Font font() {
///         return Font.loadFont(new Win10IkonHandler().getFontResourceAsStream(), size);
///     }
/// });
/// // The latter compared to the first one includes the pack name and the available icons names, which is mandatory for
/// // random capabilities offered by the IconUtils class.
/// ```
// @formatter:on
public class FontIconsPacks {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final NavigableMap<String, FontIconsPack> PACKS = new TreeMap<>();

    //================================================================================
    // Initializers
    //================================================================================
    static {
        register("mfx-", MaterialFX.instance());
        register("fab-", FontAwesomeBrands.instance());
        register("far-", FontAwesomeRegular.instance());
        register("fas-", FontAwesomeSolid.instance());
    }

    /// This method does not do much really. It can be used to reference the registry and force Java to initialize it.
    ///
    /// The only thing it does is call [FontIconsPack#font()] on all the registered icon packs. This way, if a pack needs
    /// to load its [Font] from somewhere, we force it to do so earlier. Loading fonts is also expensive.
    public static void init() {
        for (FontIconsPack value : PACKS.values()) {
            value.font();
        }
    }

    //================================================================================
    // Static Methods
    //================================================================================

    /// Registers the given [FontIconsPack] with the given prefix.
    public static void register(String prefix, FontIconsPack pack) {
        PACKS.put(prefix, pack);
    }

    /// Convenience method to register third-party icon packs. Creates an anonymous [FontIconsPack] with the
    /// given [Font] and the given function to resolve names to Unicode characters.
    /// Then delegates to [#register(String, FontIconsPack)].
    ///
    /// @see FontIconsPack#font()
    /// @see FontIconsPack#icon(String)
    public static void register(String prefix, Font font, Function<String, String> iconResolver) {
        register(prefix, new FontIconsPack() {
            @Override
            public String icon(String name) {
                return iconResolver.apply(name);
            }

            @Override
            public Font font() {
                return font;
            }
        });
    }

    /// Given the name of an icon, retrieves the prefix with [TreeMap#floorKey(Object)], checks if the name starts with it
    /// and finally retrieves the associated [FontIconsPack].
    ///
    /// Otherwise, returns `null`.
    public static FontIconsPack pack(String name) {
        String prefix = PACKS.floorKey(name);
        if (prefix != null && name.startsWith(prefix)) return PACKS.get(prefix);
        return null;
    }

    /// @return all the currently registered icon packs as an unmodifiable [SequencedMap].
    public static SequencedMap<String, FontIconsPack> packs() {
        return Collections.unmodifiableSequencedMap(PACKS);
    }
}
