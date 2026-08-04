/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.enums;

import javafx.scene.Cursor;

import static javafx.scene.Cursor.*;

/// The eight edges and corners of a box, plus [#NONE] for "nowhere in particular".
public enum Zone {
    TOP_LEFT {
        @Override
        public Cursor cursor() {
            return NW_RESIZE;
        }
    },
    TOP_CENTER {
        @Override
        public Cursor cursor() {
            return N_RESIZE;
        }
    },
    TOP_RIGHT {
        @Override
        public Cursor cursor() {
            return NE_RESIZE;
        }
    },
    CENTER_RIGHT {
        @Override
        public Cursor cursor() {
            return E_RESIZE;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public Cursor cursor() {
            return SE_RESIZE;
        }
    },
    BOTTOM_CENTER {
        @Override
        public Cursor cursor() {
            return S_RESIZE;
        }
    },
    BOTTOM_LEFT {
        @Override
        public Cursor cursor() {
            return SW_RESIZE;
        }
    },
    CENTER_LEFT {
        @Override
        public Cursor cursor() {
            return W_RESIZE;
        }
    },
    NONE {
        @Override
        public Cursor cursor() {
            return null;
        }
    };

    private static final Zone[] ALL_ZONES = new Zone[] {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_RIGHT,
        BOTTOM_RIGHT, BOTTOM_CENTER, BOTTOM_LEFT, CENTER_LEFT
    };

    /// @return every zone except [#NONE]
    public static Zone[] all() {
        return ALL_ZONES;
    }

    public boolean isRight() {
        return this == TOP_RIGHT || this == CENTER_RIGHT || this == BOTTOM_RIGHT;
    }

    public boolean isLeft() {
        return this == TOP_LEFT || this == CENTER_LEFT || this == BOTTOM_LEFT;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
    }

    public boolean isBottom() {
        return this == BOTTOM_RIGHT || this == BOTTOM_CENTER || this == BOTTOM_LEFT;
    }

    public boolean isCorner() {
        return this == TOP_LEFT || this == TOP_RIGHT || this == BOTTOM_RIGHT || this == BOTTOM_LEFT;
    }

    /// @return the resize cursor for this zone, `null` for [#NONE].
    ///
    /// Deliberately not [Cursor#DEFAULT]: JavaFX resolves the cursor by walking up from the picked node and taking the
    /// first non-null one, so `DEFAULT` would claim "the cursor is an arrow here" and block every ancestor. `null`
    /// means "no opinion, inherit".
    public abstract Cursor cursor();
}
