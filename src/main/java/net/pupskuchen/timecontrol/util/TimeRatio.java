package net.pupskuchen.timecontrol.util;

import org.bukkit.World;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigManager;

public class TimeRatio {
    public final double day;
    public final double night;

    /**
     * Ticks to wait before progressing time.
     */
    private long intermediateTicks = 0;

    public TimeRatio(final TimeControl plugin, final World world) {
        final ConfigManager config = plugin.getConfigManager();

        day = TickUtil.cycleMinsToTickRatio(config.getDay(world));
        night = TickUtil.cycleMinsToTickRatio(config.getNight(world));
    }

    public long getIntermediateTicks() {
        return intermediateTicks;
    }

    public void setIntermediateTicks(final long intermediate) {
        intermediateTicks = intermediate;
    }

    public double getApplicableRatio(final long worldTime) {
        return TimeUtil.isDay(worldTime) ? day : night;
    }
}
