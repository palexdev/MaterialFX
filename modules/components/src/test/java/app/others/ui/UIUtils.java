/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package app.others.ui;

import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class UIUtils {
    public static final Size PREF_WINDOW_SIZE = Size.of(800, 800);

    private UIUtils() {
    }

    /**
     * Attempt at supporting various screen sizes (I'm developing from my tablet too lately whoops)
     */
    public static Size getWindowSize() {
        Screen primary = Screen.getPrimary();
        Rectangle2D bounds = primary.getVisualBounds();
        return Size.of(
                NumberUtils.clamp(PREF_WINDOW_SIZE.getWidth(), 400, bounds.getWidth() - 100),
                NumberUtils.clamp(PREF_WINDOW_SIZE.getHeight(), 400, bounds.getHeight() - 100)
        );
    }
}
