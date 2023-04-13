package net.pupskuchen.timecontrol.timer;

import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.util.TickUtil;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class WorldState {
    public final Durations<Double, Double> ratios;
    public final boolean originalDoDaylightCycle;

    /**
     * Ticks to wait before progressing time.
     */
    private long intermediateTicks = 0;

    public WorldState(final Durations<Double, Double> durations, final boolean doDaylightCycle) {
        ratios = new Durations<>(
                TickUtil.cycleMinsToTickRatio(durations.day, TimeUtil.DAYTIME.duration()),
                TickUtil.cycleMinsToTickRatio(durations.night, TimeUtil.NIGHTTIME.duration()),
                TickUtil.cycleMinsToTickRatio(durations.sunset, TimeUtil.SUNSET.duration()),
                TickUtil.cycleMinsToTickRatio(durations.sunrise, TimeUtil.SUNRISE.duration()));
        this.originalDoDaylightCycle = doDaylightCycle;
    }

    public long getIntermediateTicks() {
        return intermediateTicks;
    }

    public void setIntermediateTicks(final long intermediate) {
        intermediateTicks = intermediate;
    }

    public double getApplicableRatio(final long worldTime) {
        if (TimeUtil.DAYTIME.isInRange(worldTime)) {
            return ratios.day;
        } else if (TimeUtil.NIGHTTIME.isInRange(worldTime)) {
            return ratios.night;
        } else if (TimeUtil.SUNRISE.isInRange(worldTime)) {
            return ratios.sunrise;
        } else {
            return ratios.sunset;
        }
    }
}
