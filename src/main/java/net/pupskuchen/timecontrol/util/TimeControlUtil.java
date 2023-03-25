package net.pupskuchen.timecontrol.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeControlUtil {

    public static final String LOG_PREFIX = "[TimeControl] ";

    public static void console(final String message) {
        Bukkit.getLogger().info(LOG_PREFIX + message);
    }

    public static void consoleWarning(final String message) {
        Bukkit.getLogger().warning(LOG_PREFIX + message);
    }

    public static boolean isDay(final World world) {
        final long time = world.getTime();
        return time >= 0 && time < 12000;
    }

    public static boolean isNight(final World world) {
        return !isDay(world);
    }

}
