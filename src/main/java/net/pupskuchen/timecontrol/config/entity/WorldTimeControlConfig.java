package net.pupskuchen.timecontrol.config.entity;

import net.pupskuchen.pluginconfig.annotations.EntityMapSerializable;
import net.pupskuchen.pluginconfig.annotations.Serialize;

@EntityMapSerializable()
public class WorldTimeControlConfig extends TimeControlConfig {
    @Serialize
    private String name;

    public String getName() {
        return name;
    }
}
