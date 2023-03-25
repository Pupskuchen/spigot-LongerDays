package net.pupskuchen.timecontrol.util;

import org.bukkit.World;

public class TimeUtil {
    public static final int DAY_START = 0;
    public static final int DAY_END = 12999;

    public static boolean isDay(final World world) {
        final long time = world.getTime();

        return time >= DAY_START && time < DAY_END;
    }

    public static boolean isNight(final World world) {
        return !isDay(world);
    }
}
