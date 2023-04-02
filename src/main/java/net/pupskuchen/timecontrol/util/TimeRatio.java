package net.pupskuchen.timecontrol.util;

import net.pupskuchen.timecontrol.config.entity.Durations;

// TODO: rename
public class TimeRatio {
    public final double day;
    public final double night;

    public final boolean originalDoDaylightCycle;

    /**
     * Ticks to wait before progressing time.
     */
    private long intermediateTicks = 0;

    public TimeRatio(final Durations durations, final boolean doDaylightCycle) {
        day = TickUtil.cycleMinsToTickRatio(durations.day);
        night = TickUtil.cycleMinsToTickRatio(durations.night);
        this.originalDoDaylightCycle = doDaylightCycle;
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
