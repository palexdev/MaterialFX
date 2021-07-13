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

package io.github.palexdev.materialfx.controls.enums;

/**
 * This class contains various enumerators used in MaterialFX controls which
 * support two or more styles that can be changed at runtime.
 * <p>
 * These emulators basically are just helpers that store the path to the right css file.
 */
public class Styles {

    private Styles() {}

    public enum ComboBoxStyles {
        STYLE1("css/MFXComboBoxStyle1.css"),
        STYLE2("css/MFXComboBoxStyle2.css"),
        STYLE3("css/MFXComboBoxStyle3.css");

        private final String styleSheetPath;

        ComboBoxStyles(String styleSheetPath) {
            this.styleSheetPath = styleSheetPath;
        }

        public String getStyleSheetPath() {
            return styleSheetPath;
        }
    }

    public enum LabelStyles {
        STYLE1("css/MFXLabelStyle1.css"),
        STYLE2("css/MFXLabelStyle2.css"),
        STYLE3("css/MFXLabelStyle3.css");
        private final String styleSheetPath;

        LabelStyles(String styleSheetPath) {
            this.styleSheetPath = styleSheetPath;
        }

        public String getStyleSheetPath() {
            return styleSheetPath;
        }
    }
}
