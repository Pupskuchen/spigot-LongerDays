package net.pupskuchen.timecontrol.config.entity;

import net.pupskuchen.pluginconfig.annotations.EntityMapSerializable;
import net.pupskuchen.pluginconfig.annotations.Serialize;

/**
 * Time control configuration that can be global or for a specific world.
 */
@EntityMapSerializable()
public class TimeControlConfig {
    @Serialize
    protected Durations<Double, ?> durations;
    @Serialize("night-skipping.enabled")
    protected Boolean nightSkippingEnabled;
    @Serialize("players-sleeping-percentage.enabled")
    protected Boolean playersSleepingPercentageEnabled;
    @Serialize("players-sleeping-percentage.percentage")
    protected Integer playersSleepingPercentage;

    public TimeControlConfig() {}

    public TimeControlConfig(final Durations<Double, ?> durations,
            final boolean nightSkippingEnabled, final boolean playersSleepingPercentageEnabled,
            final int playersSleepingPercentage) {
        setDurations(durations);
        this.nightSkippingEnabled = nightSkippingEnabled;
        this.playersSleepingPercentageEnabled = playersSleepingPercentageEnabled;
        this.playersSleepingPercentage = playersSleepingPercentage;
    }

    public Durations<Double, ?> getDurations() {
        return durations;
    }

    public void setDurations(final Durations<Double, ?> durations) {
        this.durations = durations;
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
