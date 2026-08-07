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

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedObjectProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedObjectTests {
    private final ObjectProperty<Person> objectProperty = new SimpleObjectProperty<>();

    @BeforeEach
    public void setUp() {
        objectProperty.set(null);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();

        When.onInvalidated(synced)
            .then((_) -> fired.set(true))
            .oneShot()
            .listen();
        synced.setAndWait(new Person("Jack"), objectProperty);
        assertFalse(fired.get());

        objectProperty.set(new Person("Rose"));
        assertTrue(fired.get());

        assertEquals("Jack", synced.get().getName());
        assertEquals("Rose", objectProperty.get().getName());
    }

    @Test
    public void testBind() {
        SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();
        synced.bind(objectProperty);
        objectProperty.set(new Person("Mark"));
        assertEquals("Mark", synced.get().getName());
        assertEquals("Mark", objectProperty.get().getName());
    }

    @Test
    public void testBindBidirectional() {
        AtomicReference<Person> aValue = new AtomicReference<>();
        AtomicReference<Person> bValue = new AtomicReference<>();

        SynchronizedObjectProperty<Person> synced = new SynchronizedObjectProperty<>();
        synced.bindBidirectional(objectProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(aValue::set)
            .oneShot()
            .listen();

        objectProperty.set(new Person("Jack"));
        assertEquals("Jack", aValue.get().getName());
        assertEquals("Jack", objectProperty.get().getName());

        When.onInvalidated(objectProperty)
            .condition(Objects::nonNull)
            .then(bValue::set)
            .oneShot()
            .listen();

        synced.set(new Person("Rose"));
        assertEquals("Rose", bValue.get().getName());
        assertEquals("Rose", synced.get().getName());
    }

    @Test
    public void testFailSync() {
        SynchronizedObjectProperty<Person> synced1 = new SynchronizedObjectProperty<>();
        SynchronizedObjectProperty<Person> synced2 = new SynchronizedObjectProperty<>();
        synced1.setAndWait(new Person("Mark"), synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(new Person("Leia"), synced1));
    }
}
