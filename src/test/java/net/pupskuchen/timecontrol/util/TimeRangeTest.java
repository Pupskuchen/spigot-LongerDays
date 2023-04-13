package net.pupskuchen.timecontrol.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TimeRangeTest {
    @Test
    public void range1() {
        final TimeRange range = new TimeRange(0, 0);

        assertEquals(0, range.start);
        assertEquals(0, range.end);

        assertTrue(range.isInRange(0));
        assertFalse(range.isInRange(-1));
        assertFalse(range.isInRange(1));
    }

    @Test
    public void range2() {
        final TimeRange range = new TimeRange(10, 15);

        assertEquals(10, range.start);
        assertEquals(15, range.end);

        assertTrue(range.isInRange(10));
        assertTrue(range.isInRange(13));
        assertTrue(range.isInRange(15));
        assertFalse(range.isInRange(-1));
        assertFalse(range.isInRange(0));
        assertFalse(range.isInRange(9));
        assertFalse(range.isInRange(16));
        assertFalse(range.isInRange(20));
    }

    @Test
    public void duration() {
        assertEquals(1, new TimeRange(0, 0).duration());
        assertEquals(2, new TimeRange(0, 1).duration());
        assertEquals(11, new TimeRange(0, 10).duration());
        assertEquals(1000, new TimeRange(12000, 12999).duration());
    }
}
