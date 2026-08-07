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

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedStringProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedStringTests {
    private final StringProperty stringProperty = new SimpleStringProperty();

    @BeforeEach
    public void setUp() {
        stringProperty.set(null);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedStringProperty synced = new SynchronizedStringProperty();
        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();

        synced.setAndWait("SString", stringProperty);
        assertFalse(fired.get());

        stringProperty.set("PString");
        assertTrue(fired.get());

        assertEquals("SString", synced.get());
        assertEquals("PString", stringProperty.get());
    }

    @Test
    public void testBind() {
        SynchronizedStringProperty synced = new SynchronizedStringProperty();
        synced.bind(stringProperty);
        stringProperty.set("BString");
        assertEquals("BString", synced.get());
        assertEquals("BString", stringProperty.get());
    }

    @Test
    public void testBindBidirectional() {
        AtomicReference<String> aValue = new AtomicReference<>();
        AtomicReference<String> bValue = new AtomicReference<>();

        SynchronizedStringProperty synced = new SynchronizedStringProperty();
        synced.bindBidirectional(stringProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(aValue::set)
            .oneShot()
            .listen();

        stringProperty.set("PString");
        assertEquals("PString", aValue.get());
        assertEquals("PString", stringProperty.get());

        When.onInvalidated(stringProperty)
            .condition(Objects::nonNull)
            .then(bValue::set)
            .oneShot()
            .listen();

        synced.set("SString");
        assertEquals("SString", bValue.get());
        assertEquals("SString", synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedStringProperty synced1 = new SynchronizedStringProperty();
        SynchronizedStringProperty synced2 = new SynchronizedStringProperty();
        synced1.setAndWait("SS1", synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait("SS2", synced1));
    }
}
