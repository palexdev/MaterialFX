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

package io.github.palexdev.mfxresources.utils;

import java.util.SequencedMap;
import java.util.concurrent.ThreadLocalRandom;

import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.mfxresources.icon.packs.FontAwesomeSolid;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPack;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPacks;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class IconUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private IconUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// Given the prefix of a [FontIconsPack], returns a [Pair] wrapping the retrieved [FontIconsPack] and a random
    /// icon name from [FontIconsPack#iconNames()].
    ///
    /// ### Important Notes
    /// 1) The prefix should match the one registered in [FontIconsPacks]
    /// 2) This method relies on the [FontIconsPack#iconNames()] method, which is optional by design. Official icon packs
    /// have it, but there's no guarantee for third party packs.
    ///
    /// @see FontIconsPacks
    public static Pair<FontIconsPack, String> randomIconName(String packPrefix) {
        FontIconsPack pack = FontIconsPacks.pack(packPrefix);
        if (pack == null) throw new IllegalArgumentException("No icons pack found for prefix: " + packPrefix);
        String[] names = pack.iconNames();
        if (names.length == 0) return new Pair<>(pack, null);
        int rand = ThreadLocalRandom.current().nextInt(0, names.length);
        return new Pair<>(pack, names[rand]);
    }

    /// Delegates to [#randomIconName(String)] and builds a new [MFXFontIcon] with the random name and the given size
    /// and color.
    public static MFXFontIcon randomIcon(String packPrefix, double size, Color color) {
        String iconName = randomIconName(packPrefix).getValue();
        return new MFXFontIcon(iconName, size, color);
    }

    /// Delegates to [#randomIcon(String, double, Color)] with [FontIconsPack#DEFAULT_FONT_SIZE] and [FontIconsPack#DEFAULT_COLOR]
    /// as the size and color.
    public static MFXFontIcon randomIcon(String packPrefix) {
        return randomIcon(packPrefix, FontIconsPack.DEFAULT_FONT_SIZE, FontIconsPack.DEFAULT_COLOR);
    }

    /// @return a new [MFXFontIcon] with a random icon name from a random [FontIconsPack].
    public static MFXFontIcon randomIcon() {
        return randomIcon(randomPackPrefix());
    }

    /// @return a new [MFXFontIcon] with a random icon name from the [FontAwesomeSolid] icons' pack.
    public static MFXFontIcon randomFAS() {
        return randomIcon("fas-");
    }

    /// @return a random [FontIconsPack] among those registered in [FontIconsPacks].
    public static FontIconsPack randomPack() {
        return FontIconsPacks.pack(randomPackPrefix());
    }

    /// @return a random icons' pack prefix among those registered in [FontIconsPacks].
    public static String randomPackPrefix() {
        SequencedMap<String, FontIconsPack> packs = FontIconsPacks.packs();
        int i = ThreadLocalRandom.current().nextInt(0, packs.size());
        return packs.keySet().toArray(String[]::new)[i];
    }
}
