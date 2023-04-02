package net.pupskuchen.timecontrol.config.entity;

import net.pupskuchen.pluginconfig.annotations.Serialize;
import net.pupskuchen.pluginconfig.annotations.validation.Min;

public class Durations {
    @Min(1)
    @Serialize
    public Integer day;
    @Min(1)
    @Serialize
    public Integer night;

    public Durations() {}

    public Durations(final Integer day, final Integer night) {
        this.day = day;
        this.night = night;
    }
}
