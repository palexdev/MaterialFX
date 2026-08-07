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

package unit.bindings;

import java.util.ArrayList;
import java.util.List;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.bindings.MappedBidirectionalBinding;
import io.github.palexdev.mfxcore.base.bindings.MappedBidirectionalBinding.Target;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import javafx.beans.property.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static io.github.palexdev.mfxcore.base.beans.Size.size;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BidirectionalBindingsTests {
    static List<Disposable> disposables = new ArrayList<>();

    @AfterAll
    static void dispose() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
    }

    @Test
    public void mappedBiBindingTest() {
        SizeProperty size = new SizeProperty();
        StringProperty width = new SimpleStringProperty();
        DoubleProperty height = new SimpleDoubleProperty();

        MappedBidirectionalBinding<Size, String> wb = MappedBidirectionalBinding.bind(size, width)
            .setFirstToSecondMapper(s -> String.valueOf(s.width()))
            .setSecondToFirstMapper(s -> size(Double.parseDouble(s), size.getHeight()))
            .bind();
        MappedBidirectionalBinding<Size, Number> hb = MappedBidirectionalBinding.bind(size, height)
            .setFirstToSecondMapper(Size::height)
            .setSecondToFirstMapper(h -> size(size.getWidth(), h.doubleValue()))
            .bind();

        size.set(size(10.0, 20.0));
        assertEquals("10.0", width.get());
        assertEquals(20.0, height.get());

        width.set("55.5");
        assertEquals(55.5, size.get().width());
        assertEquals(20.0, height.get());

        height.set(80.0);
        assertEquals(80.0, size.get().height());
        assertEquals("55.5", width.get());

        disposables.add(wb);
        disposables.add(hb);
    }

    @Test
    public void mappedBiBindingTestLazy() {
        IntegerProperty first = new SimpleIntegerProperty(4);
        StringProperty second = new SimpleStringProperty("10");

        disposables.add(MappedBidirectionalBinding.bind(first, second)
            .setFirstToSecondMapper(Object::toString)
            .setSecondToFirstMapper(Integer::parseInt)
            .bind(true)
        );

        assertEquals(4, first.get());
        assertEquals("10", second.get());

        first.set(5);
        assertEquals(5, first.get());
        assertEquals("5", second.get());
    }

    @Test
    public void mappedBiBindingDepsTest() {
        DoubleProperty mul = new SimpleDoubleProperty(1.0);
        StringProperty first = new SimpleStringProperty("10");
        DoubleProperty second = new SimpleDoubleProperty(4.0);

        disposables.add(MappedBidirectionalBinding.bind(first, second)
            .setFirstToSecondMapper(s -> Double.parseDouble(s) * mul.get())
            .setSecondToFirstMapper(Object::toString)
            .addDependenciesFor(Target.SECOND, mul)
            .bind()
        );

        assertEquals("10", first.get());
        assertEquals(10.0, second.get());

        mul.set(2.0);
        assertEquals("10", first.get());
        assertEquals(20.0, second.get());
    }

    @Test
    public void testJavaFXBinding() {
        IntegerProperty iA = new SimpleIntegerProperty();
        IntegerProperty iB = new SimpleIntegerProperty();
        IntegerProperty iC = new SimpleIntegerProperty();

        iA.bindBidirectional(iB);
        iA.bindBidirectional(iC);

        iA.set(8); // All properties must be 8
        assertEquals(8, iA.get());
        assertEquals(8, iB.get());
        assertEquals(8, iC.get());

        iB.set(10); // B updates A updates C
        assertEquals(10, iA.get());
        assertEquals(10, iB.get());
        assertEquals(10, iC.get());

        iC.set(12); // C updates A updates B
        assertEquals(12, iA.get());
        assertEquals(12, iB.get());
        assertEquals(12, iC.get());
    }
}
