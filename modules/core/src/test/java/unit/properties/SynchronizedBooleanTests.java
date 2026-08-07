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

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedBooleanProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SynchronizedBooleanTests {
    private final BooleanProperty booleanProperty = new SimpleBooleanProperty();

    @BeforeEach
    public void setUp() {
        booleanProperty.set(false);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();

        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();
        synced.setAndWait(true, booleanProperty);
        assertFalse(fired.get());

        booleanProperty.set(true);
        assertTrue(fired.get());

        assertTrue(synced.get());
        assertTrue(booleanProperty.get());
    }

    @Test
    public void testBind1() {
        AtomicBoolean changed = new AtomicBoolean(false);
        SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
        synced.bind(booleanProperty);
        synced.addListener((observable, oldValue, newValue) -> changed.set(true));
        booleanProperty.set(true);
        assertTrue(changed.get());
    }

    @Test
    public void testBind2() {
        Throwable th = null;

        SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
        synced.bind(booleanProperty);

        try {
            synced.setAndWait(true, booleanProperty);
        } catch (Exception ex) {
            th = ex;
        }

        assertNotNull(th);
        assertEquals("A bound value cannot be set!", th.getMessage());
        assertFalse(synced.isWaiting());
    }

    @Test
    public void testBindBidirectional() {
        AtomicBoolean aValue = new AtomicBoolean();
        AtomicBoolean bValue = new AtomicBoolean();

        SynchronizedBooleanProperty synced = new SynchronizedBooleanProperty();
        synced.bindBidirectional(booleanProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(aValue::set)
            .oneShot()
            .listen();

        booleanProperty.set(true);
        assertTrue(aValue.get());
        assertTrue(booleanProperty.get());
        synced.set(false); // Need to reset in order to fire change event

        When.onInvalidated(booleanProperty)
            .condition(Objects::nonNull)
            .then(bValue::set)
            .oneShot()
            .listen();

        synced.set(true);
        assertTrue(bValue.get());
        assertTrue(synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedBooleanProperty synced1 = new SynchronizedBooleanProperty();
        SynchronizedBooleanProperty synced2 = new SynchronizedBooleanProperty();
        synced1.setAndWait(true, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(true, synced1));
    }
}
