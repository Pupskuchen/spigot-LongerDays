package net.pupskuchen.timecontrol.util;

import org.bukkit.Bukkit;

public class LogUtil {
    public static final String LOG_PREFIX = "[TimeControl] ";

    public static void console(final String message) {
        Bukkit.getLogger().info(LOG_PREFIX + message);
    }

    public static void consoleWarning(final String message) {
        Bukkit.getLogger().warning(LOG_PREFIX + message);
    }

}
