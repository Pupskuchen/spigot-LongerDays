package net.pupskuchen.timecontrol.timer;

import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.util.TickUtil;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class WorldState {
    public final double dayRatio;
    public final double nightRatio;

    public final boolean originalDoDaylightCycle;

    /**
     * Ticks to wait before progressing time.
     */
    private long intermediateTicks = 0;

    public WorldState(final Durations durations, final boolean doDaylightCycle) {
        dayRatio = TickUtil.cycleMinsToTickRatio(durations.day);
        nightRatio = TickUtil.cycleMinsToTickRatio(durations.night);
        this.originalDoDaylightCycle = doDaylightCycle;
    }

    public long getIntermediateTicks() {
        return intermediateTicks;
    }

    public void setIntermediateTicks(final long intermediate) {
        intermediateTicks = intermediate;
    }

    public double getApplicableRatio(final long worldTime) {
        return TimeUtil.isDay(worldTime) ? dayRatio : nightRatio;
    }
}
