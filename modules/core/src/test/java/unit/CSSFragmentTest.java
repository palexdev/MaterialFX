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

package unit;

import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static io.github.palexdev.mfxcore.utils.fx.InsetsUtils.uniform;
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
            .selector3 {}
            """.trim();
        String built = CSSFragment.Builder.build()
            .select(".selector")
            .style("-key1: value1")
            .style("-key2: value2")
            .select(".selector2")
            .style("-key3: value3")
            .select(".selector3")
            .toCSS();
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
            }
            """.trim();
        String built = CSSFragment.Builder.build()
            .select(".selector1").and(".selector2")
            .style("-key: value")
            .select(".selector3").and(".selector4").and(".selector5")
            .style("-key2: value2")
            .toCSS();
        assertEquals(expected, built);
    }

    @Test
    void testBuilder3() {
        Pane pane = new Pane();
        pane.getStyleClass().addAll("my-pane", "extra");
        String expected = """
            .my-pane.extra {
              -fx-border-color: #ff0000ff;
              -fx-border-radius: 12.0;
              -fx-padding: 10.0;
            }
            .my-pane.extra .label {
              -fx-text-fill: #008000;
              -fx-font-size: 24.0;
            }
            """.trim();
        String built = CSSFragment.Builder.build()
            .select(pane)
            .border(Color.RED)
            .borderRadius(uniform(12))
            .padding(uniform(10))
            .select(pane, ".label")
            .textFill(Color.GREEN)
            .fontSize(24.0)
            .toCSS();
        assertEquals(expected, built);
    }
}
