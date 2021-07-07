package treeview;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.utils.NumberUtils;
import junit.framework.TestCase;

public class NumberUtilsTest extends TestCase {
    private double val;
    private NumberRange<Double> fromRange;
    private NumberRange<Double> toRange;

    public void testMap1() {
        val = 0;
        fromRange = NumberRange.of(-50.0, 100.0);
        toRange = NumberRange.of(0.0, 100.0);

        double mapped = NumberUtils.mapOneRangeToAnother(val, fromRange, toRange, 1);
        assertEquals(33.3, mapped);
    }

    public void testMap2() {
        val = -50;
        fromRange = NumberRange.of(-50.0, 100.0);
        toRange = NumberRange.of(0.0, 100.0);

        double mapped = NumberUtils.mapOneRangeToAnother(val, fromRange, toRange, 1);
        assertEquals(0.0, mapped);
    }

    public void testMap3() {
        val = 100;
        fromRange = NumberRange.of(-50.0, 100.0);
        toRange = NumberRange.of(0.0, 100.0);

        double mapped = NumberUtils.mapOneRangeToAnother(val, fromRange, toRange, 1);
        assertEquals(100.0, mapped);
    }

    public void testMap4() {
        val = -10;
        fromRange = NumberRange.of(-50.0, 100.0);
        toRange = NumberRange.of(0.0, 100.0);

        double mapped = NumberUtils.mapOneRangeToAnother(val, fromRange, toRange, 1);
        assertEquals(26.7, mapped);
    }
}
