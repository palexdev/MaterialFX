/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package unit.base;

import io.github.palexdev.mfxcore.base.beans.range.ExcludingIntegerRange;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcludingIntegerRangeTests {

    @Test
    void testExclusion() {
        ExcludingIntegerRange er = ExcludingIntegerRange.of(0, 100);
        List<Integer> exclusions = List.of(0, 6, 9, 10, 11, 35, 70, 92, 105);
        er.excludeAll(exclusions.toArray(Integer[]::new));

        List<Integer> expected = IntStream.rangeClosed(0, 100)
            .boxed()
            .collect(Collectors.toList());
        expected.removeAll(exclusions);

        List<Integer> inRange = new ArrayList<>();
        for (Integer i : er) inRange.add(i);
        assertEquals(expected, inRange);
    }

    @Test
    void testStreamSupport() {
        ExcludingIntegerRange er = ExcludingIntegerRange.of(0, 100);
        List<Integer> exclusions = List.of(0, 6, 9, 10, 11, 35, 70, 92, 105);
        er.excludeAll(exclusions.toArray(Integer[]::new));

        List<Integer> expected = IntStream.rangeClosed(0, 100)
            .boxed()
            .collect(Collectors.toList());
        expected.removeAll(exclusions);

        List<Integer> inRange = er.stream().toList();
        assertEquals(expected, inRange);
    }
}
