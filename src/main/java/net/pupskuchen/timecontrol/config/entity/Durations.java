package net.pupskuchen.timecontrol.config.entity;

import net.pupskuchen.pluginconfig.annotations.Serialize;
import net.pupskuchen.pluginconfig.annotations.validation.Min;

public class Durations<T extends Number, S> {
    @Min(1)
    @Serialize
    public T day;
    @Min(1)
    @Serialize
    public T night;

    @Serialize
    public S sunset;
    @Serialize
    public S sunrise;

    public Durations() {}

    public Durations(final T day, final T night, final S sunset, final S sunrise) {
        this.day = day;
        this.night = night;
        this.sunset = sunset;
        this.sunrise = sunrise;
    }
}
