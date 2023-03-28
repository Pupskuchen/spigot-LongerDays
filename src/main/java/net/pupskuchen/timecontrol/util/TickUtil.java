package net.pupskuchen.timecontrol.util;

public final class TickUtil {
    private TickUtil() {
    }

    private static double getTickRatio(final long ticks) {
        return 1.0 / (ticks / 12000.0);
    }

    private static long convertMinsToTicks(final int min) {
        return min * 60 * 20;
    }

    public static double cycleMinsToTickRatio(final int min) {
        return getTickRatio(convertMinsToTicks(min));
    }
}
