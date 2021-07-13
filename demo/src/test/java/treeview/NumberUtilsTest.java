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

    public void testMap5() {
        val = 0;
        fromRange = NumberRange.of(-100.0, 100.0);
        toRange = NumberRange.of(0.0, 100.0);

        double mapped = NumberUtils.mapOneRangeToAnother(val, fromRange, toRange, 1);
        assertEquals(50.0, mapped);
    }

    public void testClamp1() {
        val = 10;

        double clamped = NumberUtils.clamp(val, 0, 100);
        assertEquals(10.0, clamped);
    }

    public void testClamp2() {
        val = 50;

        double clamped = NumberUtils.clamp(val, 0, 100);
        assertEquals(50.0, clamped);
    }

    public void testClamp3() {
        val = 102;

        double clamped = NumberUtils.clamp(val, 0, 100);
        assertEquals(100.0, clamped);
    }

    public void testClamp4() {
        val = -9;

        double clamped = NumberUtils.clamp(val, 0, 100);
        assertEquals(0.0, clamped);
    }
}
