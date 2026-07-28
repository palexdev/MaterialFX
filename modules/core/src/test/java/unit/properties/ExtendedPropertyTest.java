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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.mfxcore.base.properties.base.ExtendedProperty;
import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ExtendedPropertyTest {

    @Test
    void testDefaultAndReset() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        assertEquals("abc", prop.defaultValue());
        assertNull(prop.getValue());
        assertFalse(prop.isDefault());

        prop.set("other");
        assertEquals("other", prop.getValue());
        assertFalse(prop.isDefault());

        prop.reset();
        assertEquals("abc", prop.getValue());
        assertTrue(prop.isDefault());
    }

    @Test
    void testFluentSetReturnsSelf() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        assertSame(prop, prop.set("a"));
        assertSame(prop, prop.reset());
    }

    @Test
    void testIsDefaultBoxing() {
        // Outside the Integer cache, so reference equality would fail here
        ExtendedProperty<Integer> ints = PropUtils.intProperty().extended(1000);
        assertFalse(ints.isDefault());
        ints.reset();
        assertTrue(ints.isDefault());

        ExtendedProperty<Double> doubles = PropUtils.doubleProperty().extended(1.5);
        assertFalse(doubles.isDefault());
        doubles.reset();
        assertTrue(doubles.isDefault());
    }

    @Test
    void testNumericDelegatesBoxToOneType() {
        ExtendedProperty<Integer> ints = PropUtils.intProperty().extended(0);
        ints.set(42);
        assertEquals(42, ints.getValue());

        ExtendedProperty<Float> floats = PropUtils.floatProperty().extended(0.0f);
        floats.set(1.25f);
        assertEquals(1.25f, floats.getValue());

        ExtendedProperty<Long> longs = PropUtils.longProperty().extended(0L);
        longs.set(7L);
        assertEquals(7L, longs.getValue());
    }

    @Test
    void testGetOrDefault() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        assertFalse(prop.isDefault());
        assertEquals("abc", prop.getOrDefault());
        assertEquals("fallback", prop.getOrElse("fallback"));
        assertEquals("supplied", prop.getOrElse(() -> "supplied"));

        prop.set("value");
        assertEquals("value", prop.getOrDefault());
        assertEquals("value", prop.getOrElse("fallback"));
        assertEquals("value", prop.getOrElse(() -> "supplied"));
    }

    @Test
    void testDefaultIsMapped() {
        // The default is folded through the mapper at build time, so reset() lands where isDefault() expects
        ExtendedProperty<Integer> prop = PropUtils.intProperty()
            .mapper(v -> Math.clamp(v, 5, 10))
            .extended(0);
        assertEquals(5, prop.defaultValue());

        prop.set(100);
        assertEquals(10, prop.getValue());

        prop.reset();
        assertEquals(5, prop.getValue());
        assertTrue(prop.isDefault());
    }

    @Test
    void testBuilderCallbacksSurviveDecoration() {
        AtomicReference<String> invalidated = new AtomicReference<>();
        ExtendedProperty<String> prop = PropUtils.stringProperty()
            .mapper(String::toUpperCase)
            .onInvalidated(invalidated::set)
            .extended("abc");

        prop.set("value");
        assertEquals("VALUE", prop.getValue());
        assertEquals("VALUE", invalidated.get());
    }

    @Test
    void testListeners() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        AtomicInteger invalidations = new AtomicInteger();
        AtomicReference<String> changed = new AtomicReference<>();
        AtomicReference<Observable> source = new AtomicReference<>();

        prop.addListener(o -> {
            invalidations.incrementAndGet();
            source.set(o);
        });
        prop.addListener((obs, o, n) -> changed.set(n));

        prop.set("value");
        assertEquals(1, invalidations.get());
        assertEquals("value", changed.get());
        // Listeners are registered on the delegate, so it is the reported source, not the decorator
        assertSame(prop.delegate(), source.get());
    }

    @Test
    void testRemoveListener() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        AtomicInteger count = new AtomicInteger();
        InvalidationListener listener = o -> count.incrementAndGet();

        prop.addListener(listener);
        prop.set("a");
        assertEquals(1, count.get());

        prop.removeListener(listener);
        prop.set("b");
        assertEquals(1, count.get());
    }

    @Test
    void testDelegateIsTheSameProperty() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        prop.delegate().setValue("written through delegate");
        assertEquals("written through delegate", prop.getValue());

        prop.set("written through decorator");
        assertEquals("written through decorator", prop.delegate().getValue());
    }

    @Test
    void testBind() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        StringProperty source = new SimpleStringProperty("bound");

        prop.bind(source);
        assertTrue(prop.isBound());
        assertEquals("bound", prop.getValue());

        source.set("updated");
        assertEquals("updated", prop.getValue());

        prop.unbind();
        assertFalse(prop.isBound());
        source.set("ignored");
        assertEquals("updated", prop.getValue());
    }

    @Test
    void testResetWhileBoundThrows() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        prop.bind(new SimpleStringProperty("bound"));
        assertThrows(RuntimeException.class, prop::reset);
        assertThrows(RuntimeException.class, () -> prop.set("value"));
    }

    @Test
    void testBindBidirectionalFromDecorator() {
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        StringProperty other = new SimpleStringProperty();

        prop.bindBidirectional(other);
        prop.set("from decorator");
        assertEquals("from decorator", other.getValue());
        other.set("from other");
        assertEquals("from other", prop.getValue());

        prop.unbindBidirectional(other);
        other.set("ignored");
        assertEquals("from other", prop.getValue());
    }

    @Test
    void testBindBidirectionalFromOther() {
        // The decorator lands in BidirectionalBinding's second slot here, which is the branch
        // that does not compare the reported source by identity
        ExtendedProperty<String> prop = PropUtils.stringProperty().extended("abc");
        StringProperty other = new SimpleStringProperty();

        other.bindBidirectional(prop);
        prop.set("from decorator");
        assertEquals("from decorator", other.getValue());
        other.set("from other");
        assertEquals("from other", prop.getValue());
    }
}
