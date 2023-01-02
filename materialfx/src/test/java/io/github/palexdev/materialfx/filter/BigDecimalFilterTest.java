/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class BigDecimalFilterTest {

    @Test
    public void constructor() {
        final String NAME1 = "amount", NAME2 = "balance";

        BigDecimalFilter<Account> f = new BigDecimalFilter<>("amount", Account::balance);
        assertEquals(NAME1, f.name()); assertNotNull(f.getExtractor());
        f = new BigDecimalFilter<>("amount", null);
        assertEquals(NAME1, f.name()); assertNull(f.getExtractor());
    }

    @Test
    public void predicates() {
        BigDecimalFilter<Account> f = new BigDecimalFilter<>("amount", Account::balance);

        List<BiPredicateBean<BigDecimal, BigDecimal>> predicates = f.defaultPredicates();
        assertEquals(6, predicates.size());
        assertEquals("is", predicates.get(0).name());
        assertEquals("is not", predicates.get(1).name());
        assertEquals("greater than", predicates.get(2).name());
        assertEquals("greater or equal to", predicates.get(3).name());
        assertEquals("lesser than", predicates.get(4).name());
        assertEquals("lesser or equal to", predicates.get(5).name());
    }

    @Test public void getValue() {
        final String D1 = "0.0000000000001", D2 = "123456789012345";

        BigDecimalFilter<Account> f = new BigDecimalFilter<>("amount", Account::balance);

        assertEquals(new BigDecimal(D1), f.getValue(D1));
        assertEquals(new BigDecimal(D2), f.getValue(D2));
    }

    // Account -----------------------------------------------------------------
    private class Account {
        private BigDecimal balance;

        public Account(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal balance() {
            return balance;
        }
    }
}