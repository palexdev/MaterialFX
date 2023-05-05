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

package unit;

import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSSFragmentTest {

    @Test
    void testBuilder() {
        String expected = """
            .selector {
              -key1: value1;
              -key2: value2;
            }
                        
            .selector2 {
              -key3: value3;
            }
                        
            .selector3 {
            }""";
        String built = CSSFragment.Builder.build()
            .addSelector(".selector")
            .addStyle("-key1: value1")
            .addStyle("-key2: value2")
            .closeSelector()
            .addSelector(".selector2")
            .addStyle("-key3: value3")
            .closeSelector()
            .addSelector(".selector3")
            .closeSelector()
            .toString();
        assertEquals(expected, built);
    }

    @Test
    void testBuilder2() {
        String expected = """
            .selector1,
            .selector2 {
              -key: value;
            }
                        
            .selector3,
            .selector4,
            .selector5 {
              -key2: value2;
            }""";
        String built = CSSFragment.Builder.build()
            .addSelector(".selector1")
            .addSelector(".selector2")
            .addStyle("-key: value")
            .closeSelector()
            .addSelector(".selector3")
            .addSelector(".selector4")
            .addSelector(".selector5")
            .addStyle("-key2: value2")
            .closeSelector()
            .toString();
        assertEquals(expected, built);
    }
}
