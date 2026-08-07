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
import java.util.concurrent.atomic.AtomicInteger;

import io.github.palexdev.mfxcore.base.properties.synced.SynchronizedIntegerProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedIntegerTests {
    private final IntegerProperty integerProperty = new SimpleIntegerProperty();

    @BeforeEach
    public void setUp() {
        integerProperty.set(0);
    }

    @Test
    public void testSync() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
        When.onInvalidated(synced)
            .then(_ -> fired.set(true))
            .oneShot()
            .listen();

        synced.setAndWait(9, integerProperty);
        assertFalse(fired.get());

        integerProperty.set(7);
        assertTrue(fired.get());

        assertEquals(9, synced.get());
        assertEquals(7, integerProperty.get());
    }

    @Test
    public void testBind() {
        SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
        synced.bind(integerProperty);
        integerProperty.set(8);
        assertEquals(8, synced.get());
        assertEquals(8, integerProperty.get());
    }

    @Test
    public void testBindBidirectional() {
        AtomicInteger aValue = new AtomicInteger();
        AtomicInteger bValue = new AtomicInteger();

        SynchronizedIntegerProperty synced = new SynchronizedIntegerProperty();
        synced.bindBidirectional(integerProperty);

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(v -> aValue.set(v.intValue()))
            .oneShot()
            .listen();

        integerProperty.set(8);
        assertEquals(8, aValue.get());
        assertEquals(8, integerProperty.get());

        When.onInvalidated(synced)
            .condition(Objects::nonNull)
            .then(v -> bValue.set(v.intValue()))
            .oneShot()
            .listen();

        synced.set(7);
        assertEquals(7, bValue.get());
        assertEquals(7, synced.get());
    }

    @Test
    public void testFailSync() {
        SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
        SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
        synced1.setAndWait(1, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(2, synced1));
    }

    @Test
    public void testChain1() {
        AtomicInteger a1 = new AtomicInteger();
        AtomicInteger a2 = new AtomicInteger();
        AtomicInteger a3 = new AtomicInteger();

        SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
        SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
        synced1.addListener((observable, oldValue, newValue) -> a1.set(newValue.intValue()));
        synced2.addListener((observable, oldValue, newValue) -> a2.set(newValue.intValue()));
        integerProperty.addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                a3.set(newValue.intValue());
                integerProperty.removeListener(this);
            }
        });

        synced1.setAndWait(8, synced2);
        synced2.setAndWait(10, integerProperty);
        integerProperty.set(12);

        assertEquals(8, a1.get());
        assertEquals(10, a2.get());
        assertEquals(12, a3.get());
    }

    @Test
    public void testChain2() {
        SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
        SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
        synced1.setAndWait(8, synced2);
        assertThrows(IllegalArgumentException.class, () -> synced2.setAndWait(10, synced1));
    }

    @Test
    public void testOverrideWait() {
        SynchronizedIntegerProperty synced1 = new SynchronizedIntegerProperty();
        SynchronizedIntegerProperty synced2 = new SynchronizedIntegerProperty();
        synced1.setAndWait(8, synced2);
        assertThrows(IllegalStateException.class, () -> synced1.setAndWait(10, integerProperty));
    }
}
