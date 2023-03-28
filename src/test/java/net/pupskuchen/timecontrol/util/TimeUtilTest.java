package net.pupskuchen.timecontrol.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimeUtilTest {

    @Test
    public void timeRangeDay() {
        final TimeRange range = TimeUtil.DAY;

        assertEquals(0, range.start);
        assertEquals(12999, range.end);
    }

    @Test
    public void timeRangeSleepClear() {
        final TimeRange range = TimeUtil.SLEEP_ALLOWED_CLEAR;

        assertEquals(12542, range.start);
        assertEquals(23459, range.end);
    }

    @Test
    public void timeRangeSleepRain() {
        final TimeRange range = TimeUtil.SLEEP_ALLOWED_RAIN;

        assertEquals(12010, range.start);
        assertEquals(23991, range.end);
    }

    @Test
    public void isDay(@Mock World mockWorld) {
        when(mockWorld.getTime()).thenReturn(13L, 14230L, 0L, 23000L, -1L);

        assertTrue(TimeUtil.isDay(mockWorld));
        assertFalse(TimeUtil.isDay(mockWorld));
        assertTrue(TimeUtil.isDay(mockWorld));
        assertFalse(TimeUtil.isDay(mockWorld));
        assertFalse(TimeUtil.isDay(mockWorld));
    }

    @Test
    public void isNight(@Mock World mockWorld) {
        when(mockWorld.getTime()).thenReturn(13L, 14230L, 0L, 23000L, -1L);

        assertFalse(TimeUtil.isNight(mockWorld));
        assertTrue(TimeUtil.isNight(mockWorld));
        assertFalse(TimeUtil.isNight(mockWorld));
        assertTrue(TimeUtil.isNight(mockWorld));
        assertTrue(TimeUtil.isNight(mockWorld));
    }

    @Test
    public void sleepAllowed(@Mock World mockWorld) {
        when(mockWorld.isThundering()).thenReturn(true, false, false);
        when(mockWorld.hasStorm()).thenReturn(true, true, false, false);

        assertTrue(TimeUtil.sleepAllowed(mockWorld));

        when(mockWorld.getTime()).thenReturn(22000L, 1000L, 22000L, 1000L);

        assertTrue(TimeUtil.sleepAllowed(mockWorld));
        assertFalse(TimeUtil.sleepAllowed(mockWorld));
        assertTrue(TimeUtil.sleepAllowed(mockWorld));
        assertFalse(TimeUtil.sleepAllowed(mockWorld));
    }

    @Test
    public void getWakeTime(@Mock World mockWorld) {
        when(mockWorld.hasStorm()).thenReturn(true, false);

        assertEquals(TimeUtil.SLEEP_ALLOWED_RAIN.end+1, TimeUtil.getWakeTime(mockWorld));
        assertEquals(TimeUtil.SLEEP_ALLOWED_CLEAR.end+1, TimeUtil.getWakeTime(mockWorld));
    }
}
