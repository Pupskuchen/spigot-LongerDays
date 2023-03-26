package net.pupskuchen.timecontrol.util;

import org.bukkit.World;

public class TimeUtil {
    // https://minecraft.fandom.com/wiki/Daylight_cycle
    public static final TimeRange DAY = new TimeRange(0, 12999);
    // https://minecraft.fandom.com/wiki/Bed#Sleeping
    public static final TimeRange SLEEP_ALLOWED_CLEAR = new TimeRange(12542, 23459);
    public static final TimeRange SLEEP_ALLOWED_RAIN = new TimeRange(12010, 23991);

    public static boolean isDay(final World world) {
        final long time = world.getTime();

        return DAY.isInRange(time);
    }

    public static boolean isNight(final World world) {
        return !isDay(world);
    }

    public static boolean sleepAllowed(final World world) {
        if (world.isThundering()) {
            return true;
        }

        final TimeRange range = world.hasStorm() ? SLEEP_ALLOWED_RAIN : SLEEP_ALLOWED_CLEAR;

        return range.isInRange(world.getTime());
    }

    public static int getWakeTime(final World world) {
        final int nightEnd = world.hasStorm() ? SLEEP_ALLOWED_RAIN.end : SLEEP_ALLOWED_CLEAR.end;

        return nightEnd + 1;
    }
}
