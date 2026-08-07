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

package unit.properties;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedLongProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedLongTests {
    private final LongProperty longProperty = new SimpleLongProperty();

    @BeforeEach
    public void setUp() {
        longProperty.set(0L);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedLongProperty synced = new SynchronizedLongProperty();
        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();

        synced.setAndWait(9L, longProperty);
        assertFalse(fired.get());

        longProperty.set(7L);
        assertTrue(fired.get());

        assertEquals(9L, synced.get());
        assertEquals(7L, longProperty.get());
    }

    @Test
    public void testBind() {
        SynchronizedLongProperty synced = new SynchronizedLongProperty();
        synced.bind(longProperty);
        longProperty.set(8L);
        assertEquals(8L, synced.get());
        assertEquals(8L, longProperty.get());
    }

    @Test
    public void testBindBidirectional() {
        AtomicLong aValue = new AtomicLong();
        AtomicLong bValue = new AtomicLong();

        SynchronizedLongProperty synced = new SynchronizedLongProperty();
        synced.bindBidirectional(longProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(v -> aValue.set(v.longValue()))
            .oneShot()
            .listen();

        longProperty.set(8L);
        assertEquals(8L, aValue.get());
        assertEquals(8L, longProperty.get());

        When.onInvalidated(longProperty)
            .condition(Objects::nonNull)
            .then(v -> bValue.set(v.longValue()))
            .oneShot()
            .listen();

        synced.set(7L);
        assertEquals(7L, bValue.get());
        assertEquals(7L, synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedLongProperty synced1 = new SynchronizedLongProperty();
        SynchronizedLongProperty synced2 = new SynchronizedLongProperty();
        synced1.setAndWait(1L, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(2L, synced1));
    }
}
