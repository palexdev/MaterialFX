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

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedFloatProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedFloatTests {
    private final FloatProperty floatProperty = new SimpleFloatProperty();

    @BeforeEach
    public void setUp() {
        floatProperty.set(0.0F);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();

        synced.setAndWait(0.9F, floatProperty);
        assertFalse(fired.get());

        floatProperty.set(0.7F);
        assertTrue(fired.get());

        assertEquals(0.9F, synced.get());
        assertEquals(0.7F, floatProperty.get());
    }

    @Test
    public void testBind() {
        SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
        synced.bind(floatProperty);
        floatProperty.set(0.8F);
        assertEquals(0.8F, synced.get());
        assertEquals(0.8F, floatProperty.get());
    }

    @Test
    public void testBindBidirectional() {
        AtomicReference<Float> aValue = new AtomicReference<>();
        AtomicReference<Float> bValue = new AtomicReference<>();

        SynchronizedFloatProperty synced = new SynchronizedFloatProperty();
        synced.bindBidirectional(floatProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(v -> aValue.set(v.floatValue()))
            .oneShot()
            .listen();

        floatProperty.set(0.8F);
        assertEquals(0.8F, aValue.get());
        assertEquals(0.8F, floatProperty.get());

        When.onInvalidated(floatProperty)
            .condition(Objects::nonNull)
            .then(v -> bValue.set(v.floatValue()))
            .oneShot()
            .listen();

        synced.set(0.7F);
        assertEquals(0.7F, bValue.get());
        assertEquals(0.7F, synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedFloatProperty synced1 = new SynchronizedFloatProperty();
        SynchronizedFloatProperty synced2 = new SynchronizedFloatProperty();
        synced1.setAndWait(0.1F, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(0.2F, synced1));
    }
}
