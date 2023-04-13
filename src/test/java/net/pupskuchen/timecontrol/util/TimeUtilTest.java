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
    public void timeRangeDaylightCycle() {
        assertEquals(0, TimeUtil.DAYTIME.start);
        assertEquals(TimeUtil.SUNSET.start - 1, TimeUtil.DAYTIME.end);
        assertEquals(TimeUtil.DAYTIME.end + 1, TimeUtil.SUNSET.start);
        assertEquals(TimeUtil.NIGHTTIME.start - 1, TimeUtil.SUNSET.end);
        assertEquals(TimeUtil.SUNSET.end + 1, TimeUtil.NIGHTTIME.start);
        assertEquals(TimeUtil.SUNRISE.start - 1, TimeUtil.NIGHTTIME.end);
        assertEquals(TimeUtil.NIGHTTIME.end + 1, TimeUtil.SUNRISE.start);
        assertEquals(23999, TimeUtil.SUNRISE.end);
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
