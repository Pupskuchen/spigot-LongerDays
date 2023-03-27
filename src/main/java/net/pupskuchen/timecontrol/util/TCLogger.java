package net.pupskuchen.timecontrol.util;

import java.util.logging.Logger;

import net.pupskuchen.timecontrol.TimeControl;

public class TCLogger {
    private final Logger logger;
    private final Logger debugLogger;
    private Boolean debugEnabled;

    public TCLogger(final TimeControl plugin) {
        this.logger = plugin.getLogger();
        this.debugLogger = Logger.getLogger(format("%s DEBUG", plugin.getName()));
    }

    public void setDebug(final boolean debug) {
        debugEnabled = debug;
    }

    public void debug(String msg, Object... args) {
        if (debugEnabled == null) {
            debugLogger.warning("debug mode hasn't been configured yet, ignoring message");
            return;
        } else if (!debugEnabled) {
            return;
        }

        // logging actual debug messages just seems to suck with spigot/bukkit
        // so we just use the "info" level and toggle the messages ourselves
        debugLogger.info(format(msg, args));
    }

    public void info(String msg, Object... args) {
        logger.info(format(msg, args));
    }

    public void warn(String msg, Object... args) {
        logger.warning(format(msg, args));
    }

    public void error(String msg, Object... args) {
        logger.severe(format(msg, args));
    }

    private String format(String msg, Object... args) {
        return String.format(msg, args);
    }
}
