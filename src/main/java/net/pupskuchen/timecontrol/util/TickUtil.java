package net.pupskuchen.timecontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TickUtil {
    private TickUtil() {}

    private static double getTickRatio(final double ticks, final int baseTicks) {
        return new BigDecimal(1.0 / (ticks / baseTicks)).setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static double convertMinsToTicks(final double minutes) {
        return minutes * 60 * 20;
    }

    public static double cycleMinsToTickRatio(final Number minutes, final int baseTicks) {
        return getTickRatio(convertMinsToTicks(minutes.doubleValue()), baseTicks);
    }
}
