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
import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedDoubleProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SynchronizedDoubleTests {
    private final DoubleProperty doubleProperty = new SimpleDoubleProperty();

    @BeforeEach
    public void setUp() {
        doubleProperty.set(0.0);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();

        synced.setAndWait(9.9, doubleProperty);
        assertFalse(fired.get());

        doubleProperty.set(7.5);
        assertTrue(fired.get());

        assertEquals(9.9, synced.get());
        assertEquals(7.5, doubleProperty.get());
    }

    @Test
    public void testBind() {
        SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
        synced.bind(doubleProperty);
        doubleProperty.set(8.8);
        assertEquals(8.8, synced.get());
        assertEquals(8.8, doubleProperty.get());
    }

    @Test
    public void testBindBidirectional() {
        AtomicReference<Double> aValue = new AtomicReference<>();
        AtomicReference<Double> bValue = new AtomicReference<>();

        SynchronizedDoubleProperty synced = new SynchronizedDoubleProperty();
        synced.bindBidirectional(doubleProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(v -> aValue.set(v.doubleValue()))
            .oneShot()
            .listen();

        doubleProperty.set(8.5);
        assertEquals(8.5, aValue.get());
        assertEquals(8.5, doubleProperty.get());

        When.onInvalidated(doubleProperty)
            .condition(Objects::nonNull)
            .then(v -> bValue.set(v.doubleValue()))
            .oneShot()
            .listen();

        synced.set(7.5);
        assertEquals(7.5, bValue.get());
        assertEquals(7.5, synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedDoubleProperty synced1 = new SynchronizedDoubleProperty();
        SynchronizedDoubleProperty synced2 = new SynchronizedDoubleProperty();
        synced1.setAndWait(0.36, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(0.56, synced1));
    }
}
