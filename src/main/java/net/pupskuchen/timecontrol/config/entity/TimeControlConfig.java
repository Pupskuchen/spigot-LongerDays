package net.pupskuchen.timecontrol.config.entity;

import net.pupskuchen.pluginconfig.annotations.EntityMapSerializable;
import net.pupskuchen.pluginconfig.annotations.Serialize;

/**
 * Time control configuration that can be global or for a specific world.
 */
@EntityMapSerializable()
public class TimeControlConfig {
    @Serialize
    protected Durations durations;
    @Serialize("night-skipping.enabled")
    protected Boolean nightSkippingEnabled;
    @Serialize("players-sleeping-percentage.enabled")
    protected Boolean playersSleepingPercentageEnabled;
    @Serialize("players-sleeping-percentage.percentage")
    protected Integer playersSleepingPercentage;

    public Integer getDurationDay() {
        return durations.day;
    }

    public Integer getDurationNight() {
        return durations.night;
    }

    public Boolean getNightSkippingEnabled() {
        return nightSkippingEnabled;
    }

    public Boolean getPlayersSleepingPercentageEnabled() {
        return playersSleepingPercentageEnabled;
    }

    public Integer getPlayersSleepingPercentage() {
        return playersSleepingPercentage;
    }
}
