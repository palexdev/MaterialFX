/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package unit.props;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropsTests {

	@Test
	public void testSizeProp1() {
		AtomicInteger notify = new AtomicInteger(0);
		SizeProperty s = new SizeProperty() {
			@Override
			protected void invalidated() {
				notify.incrementAndGet();
			}
		};
		s.addListener((observable, oldValue, newValue) -> notify.incrementAndGet());

		// The object is null, both "invalidation" and "change" occur
		s.setSize(1, 1);
		assertEquals(2, notify.get());

		// Despite what JavaFX shitty documentation says, listeners actually check for OBJECT EQUALITY (ffs)
		// This means that the Position object will change, triggering the "invalidation" but not the "change" listener
		s.setSize(1, 1);
		assertEquals(3, notify.get());

		// Objects is the same, width too, nothing happens
		s.setWidth(1);
		assertEquals(3, notify.get());

		// Object is the same, height too, nothing happens
		s.setHeight(1);
		assertEquals(3, notify.get());

		// Object is the same, values change, "invalidation" is invoked once for each
		s.setWidth(2);
		s.setHeight(2);
		assertEquals(5, notify.get());

		// New object with different values, both "invalidation" and "change" are triggered
		s.set(Size.of(5, 5));
		assertEquals(7, notify.get());
	}

	@Test
	public void testPositionProp1() {
		AtomicInteger notify = new AtomicInteger(0);
		PositionProperty p = new PositionProperty() {
			@Override
			protected void invalidated() {
				notify.incrementAndGet();
			}
		};
		p.addListener((observable, oldValue, newValue) -> notify.incrementAndGet());

		// The object is null, both "invalidation" and "change" occur
		p.setPosition(1, 1);
		assertEquals(2, notify.get());

		// Despite what JavaFX shitty documentation says, listeners actually check for OBJECT EQUALITY (ffs)
		// This means that the Position object will change, triggering the "invalidation" but not the "change" listener
		p.setPosition(1, 1);
		assertEquals(3, notify.get());

		// Objects is the same, width too, nothing happens
		p.setX(1);
		assertEquals(3, notify.get());

		// Object is the same, height too, nothing happens
		p.setY(1);
		assertEquals(3, notify.get());

		// Object is the same, values change, "invalidation" is invoked once for each
		p.setX(2);
		p.setY(2);
		assertEquals(5, notify.get());

		// New object with different values, both "invalidation" and "change" are triggered
		p.set(Position.of(5, 5));
		assertEquals(7, notify.get());
	}
}
