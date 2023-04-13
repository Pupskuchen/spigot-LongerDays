package net.pupskuchen.timecontrol.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import net.pupskuchen.timecontrol.config.entity.Durations;

public class DurationUtilTest {
    @Test
    public void useNumericValues() {
        final Durations<Integer, Object> durations = new Durations<Integer, Object>(1, 2, 3, 4);
        final Durations<Integer, Double> normalized = DurationUtil.normalize(durations);

        assertEquals(1, normalized.day);
        assertEquals(2, normalized.night);
        assertEquals(3, normalized.sunset);
        assertEquals(4, normalized.sunrise);
    }

    @Test
    public void normalizeStringValues() {
        final Durations<Integer, String> durations =
                new Durations<Integer, String>(10, 20, "night", "day");
        final Durations<Integer, Double> normalized = DurationUtil.normalize(durations);

        assertEquals(10, normalized.day);
        assertEquals(20, normalized.night);
        assertEquals(20d * ((double) TimeUtil.SUNSET.duration() / TimeUtil.NIGHTTIME.duration()),
                normalized.sunset);
        assertEquals(10d * ((double) TimeUtil.SUNRISE.duration() / TimeUtil.DAYTIME.duration()),
                normalized.sunrise);
    }

    @Test
    public void useFallbackValues() {
        final Durations<Integer, Object> durations =
                new Durations<Integer, Object>(10, 20, null, null);
        final Durations<Integer, Double> normalized = DurationUtil.normalize(durations);

        assertEquals(10, normalized.day);
        assertEquals(20, normalized.night);
        assertEquals(10d * ((double) TimeUtil.SUNSET.duration() / TimeUtil.DAYTIME.duration()),
                normalized.sunset);
        assertEquals(20d * ((double) TimeUtil.SUNRISE.duration() / TimeUtil.NIGHTTIME.duration()),
                normalized.sunrise);
    }

    @Test
    public void useFallbackForInvalidStrings() {
        final Durations<Integer, String> durations =
                new Durations<Integer, String>(10, 20, "oops", null);
        final Durations<Integer, Double> normalized = DurationUtil.normalize(durations);

        assertEquals(10, normalized.day);
        assertEquals(20, normalized.night);
        assertEquals(10d * ((double) TimeUtil.SUNSET.duration() / TimeUtil.DAYTIME.duration()),
                normalized.sunset);
        assertEquals(20d * ((double) TimeUtil.SUNRISE.duration() / TimeUtil.NIGHTTIME.duration()),
                normalized.sunrise);
    }
}
