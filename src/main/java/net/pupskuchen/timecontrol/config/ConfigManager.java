package net.pupskuchen.timecontrol.config;

import org.bukkit.configuration.file.FileConfiguration;

import net.pupskuchen.timecontrol.util.TimeControlUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final FileConfiguration config;
    private int day;
    private int night;
    private Set<String> worlds;

    private boolean nightSkippingEnabled;
    private boolean percentageEnabled;
    private int percentage;

    public ConfigManager(final FileConfiguration config) {
        this.config = config;
    }

    public void validate() {

        // day
        final int day = this.config.getInt("day", 30);
        if (day <= 0) {
            this.day = 30;
            TimeControlUtil.consoleWarning("Set day cycle to " + day + " minutes is not safe, reverting to default...");
        } else {
            this.day = day;
        }
        TimeControlUtil.console("Set day cycle to " + this.day + " minutes");

        // night
        final int night = this.config.getInt("night", 5);
        if (night <= 0) {
            this.night = 5;
            TimeControlUtil
                    .consoleWarning("Set night cycle to " + night + " minutes is not safe, reverting to default...");
        } else {
            this.night = night;
        }
        TimeControlUtil.console("Set night cycle to " + this.night + " minutes");

        // worlds
        final List<String> worlds = this.config.getStringList("worlds");
        this.worlds = new HashSet<>();
        this.worlds.addAll(worlds);
        this.worlds = Collections.unmodifiableSet(this.worlds);

        percentageEnabled = this.config.getBoolean("players-sleeping-percentage.enabled");
        percentage = this.config.getInt("players-sleeping-percentage.percentage");
        nightSkippingEnabled = this.config.getBoolean("night-skipping.enabled");
    }

    public int getDay() {
        return this.day;
    }

    public int getNight() {
        return this.night;
    }

    public Set<String> getWorlds() {
        return Collections.unmodifiableSet(this.worlds);
    }

    public boolean isPercentageEnabled() {
        return percentageEnabled;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isNightSkippingEnabled() {
        return nightSkippingEnabled;
    }
}
