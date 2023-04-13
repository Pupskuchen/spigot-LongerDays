package net.pupskuchen.timecontrol.util;

import net.pupskuchen.timecontrol.config.entity.Durations;

public final class DurationUtil {
    private DurationUtil() {}

    public static <T extends Number> Durations<T, Double> normalize(final Durations<T, ?> durations) {
        return new Durations<T, Double>(durations.day, durations.night,
                normalizeValue(durations.sunset, durations,
                        adjustDuration(durations.day, TimeUtil.SUNSET, TimeUtil.DAYTIME)),
                normalizeValue(durations.sunrise, durations,
                        adjustDuration(durations.night, TimeUtil.SUNRISE, TimeUtil.NIGHTTIME)));
    }

    private static Double normalizeValue(final Object value, final Durations<?, ?> durations,
            final double fallback) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            if (value.equals("day")) {
                return adjustDuration(durations.day, TimeUtil.SUNSET, TimeUtil.DAYTIME);
            } else if (value.equals("night")) {
                return adjustDuration(durations.night, TimeUtil.SUNSET, TimeUtil.NIGHTTIME);
            }
        }

        // TODO: warn
        return fallback;
    }

    private static Double adjustDuration(final Number value, final TimeRange duration,
            final TimeRange baseDuration) {
        double ratio = (double) duration.duration() / baseDuration.duration();

        return value.doubleValue() * ratio;
    }
}
