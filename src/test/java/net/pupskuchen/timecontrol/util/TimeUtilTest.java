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
        assertTrue(TimeUtil.isDay(13L));
        assertFalse(TimeUtil.isDay(14230L));
        assertTrue(TimeUtil.isDay(0L));
        assertFalse(TimeUtil.isDay(23000L));
        assertFalse(TimeUtil.isDay(-1L));
    }

    @Test
    public void isNight(@Mock World mockWorld) {
        assertFalse(TimeUtil.isNight(13L));
        assertTrue(TimeUtil.isNight(14230L));
        assertFalse(TimeUtil.isNight(0L));
        assertTrue(TimeUtil.isNight(23000L));
        assertTrue(TimeUtil.isNight(-1L));
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
