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

package unit.observables;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.observables.OnChanged;
import io.github.palexdev.mfxcore.observables.OnInvalidated;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class WhenTests {

    private static final Set<When<?>> whens = new WeakHashSet<>();

    @BeforeEach
    void tearDown() {
        whens.forEach(Disposable::dispose);
        whens.clear();
    }

    @Test
    void testMultiple() {
        IntegerProperty prop = new SimpleIntegerProperty();
        AtomicInteger cnt = new AtomicInteger();

        When<Number> oc = onChanged(prop)
            .then((o, n) -> cnt.incrementAndGet())
            .listen();

        prop.set(1);
        assertEquals(1, cnt.get());

        When<Number> oi = onInvalidated(prop)
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

        onInvalidated(prop)
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

        onInvalidated(prop)
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

        onInvalidated(prop)
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

    @Test
    void testOnlyInvalidations() {
        IntegerProperty iProp = new SimpleIntegerProperty(-1);
        StringProperty sProp = new SimpleStringProperty("0");
        DoubleProperty dProp = new SimpleDoubleProperty(1.0);
        AtomicInteger counter = new AtomicInteger(0);

        When<?> when = When.observe(
            counter::incrementAndGet,
            iProp, sProp, dProp
        ).listen();
        assertEquals(0, counter.get());

        iProp.set(1);
        assertEquals(1, counter.get());

        sProp.set("1");
        assertEquals(2, counter.get());

        dProp.set(2.0);
        assertEquals(3, counter.get());

        when.dispose();
    }

    @Test
    void testGC() throws Exception {
        IntegerProperty iProp = new SimpleIntegerProperty();
        WeakReference<?> ref;
        When<?> when = onInvalidated(iProp).listen();
        assertEquals(1, When.totalSize());

        ref = new WeakReference<>(when);
        when = null;
        iProp = null;
        awaitGC(ref);
        assumeTrue(ref.get() == null, "GC did not collect the When - skipping");
        assertEquals(0, When.totalSize());
    }

    //================================================================================
    // Helpers
    //================================================================================

    private <T> OnChanged<T> onChanged(ObservableValue<T> ov) {
        OnChanged<T> when = When.onChanged(ov);
        whens.add(when);
        return when;
    }

    private <T> OnInvalidated<T> onInvalidated(ObservableValue<T> ov) {
        OnInvalidated<T> when = When.onInvalidated(ov);
        whens.add(when);
        return when;
    }

    /// Hints GC in a short loop until the referent is collected or the attempt limit is reached.
    /// GC is non-deterministic; callers must guard with assumeTrue(ref.get() == null).
    private static void awaitGC(WeakReference<?> ref) throws InterruptedException {
        for (int i = 0; i < 50 && ref.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }
}
