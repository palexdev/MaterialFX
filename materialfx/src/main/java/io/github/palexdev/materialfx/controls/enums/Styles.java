/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.enums;

public class Styles {
    public enum ComboBoxStyles {
        STYLE1("css/mfx-combobox-style1.css"),
        STYLE2("css/mfx-combobox-style2.css");

        private final String styleSheetPath;

        ComboBoxStyles(String styleSheetPath) {
            this.styleSheetPath = styleSheetPath;
        }

        public String getStyleSheetPath() {
            return styleSheetPath;
        }
    }

    public enum LabelStyles {
        STYLE1("css/mfx-label-style1.css"),
        STYLE2("css/mfx-label-style2.css");

        private final String styleSheetPath;

        LabelStyles(String styleSheetPath) {
            this.styleSheetPath = styleSheetPath;
        }

        public String getStyleSheetPath() {
            return styleSheetPath;
        }
    }
}
