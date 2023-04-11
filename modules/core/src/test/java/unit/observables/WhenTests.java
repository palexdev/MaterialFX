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

package unit.observables;

import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class WhenTests {

    @Test
    void testMultiple() {
        IntegerProperty prop = new SimpleIntegerProperty();
        AtomicInteger cnt = new AtomicInteger();

        When<Number> oc = When.onChanged(prop)
            .then((o, n) -> cnt.incrementAndGet())
            .listen();

        prop.set(1);
        assertEquals(1, cnt.get());

        When<Number> oi = When.onInvalidated(prop)
            .then(v -> cnt.incrementAndGet())
            .listen();

        prop.set(3);
        assertEquals(3, cnt.get());

        assertEquals(2, When.totalSize());
        When.dispose(oc, oi);
        assertEquals(0, When.totalSize());
    }

    @Test
    void testOneShot1() {
        IntegerProperty prop = new SimpleIntegerProperty();
        AtomicBoolean changed = new AtomicBoolean(false);

        When.onInvalidated(prop)
            .then(v -> changed.set(true))
            .oneShot(true)
            .executeNow()
            .listen();

        assertTrue(changed.get());
        assertEquals(0, When.totalSize());
    }

    @Test
    void testOneShot2() {
        IntegerProperty prop = new SimpleIntegerProperty(-1);
        AtomicBoolean changed = new AtomicBoolean(false);

        When.onInvalidated(prop)
            .then(v -> changed.set(true))
            .oneShot(true)
            .executeNow(() -> prop.get() != -1)
            .listen();

        assertFalse(changed.get());
        assertEquals(1, When.totalSize());

        prop.set(0);
        assertTrue(changed.get());
        assertEquals(0, When.totalSize());
    }

    @Test
    void testOneShot3() {
        IntegerProperty prop = new SimpleIntegerProperty(-1);
        StringProperty sProp = new SimpleStringProperty("");
        AtomicBoolean changed = new AtomicBoolean(false);

        When.onInvalidated(prop)
            .condition(v -> v.intValue() != -1)
            .then(v -> changed.set(true))
            .oneShot(true)
            .executeNow(() -> prop.get() != -1)
            .invalidating(sProp)
            .listen();

        assertFalse(changed.get());
        assertEquals(1, When.totalSize());

        sProp.set("Don't change yet!");
        assertFalse(changed.get());
        assertEquals(1, When.totalSize());

        prop.set(0);
        assertTrue(changed.get());
        assertEquals(0, When.totalSize());
    }
}
