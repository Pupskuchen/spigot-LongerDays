package net.pupskuchen.timecontrol.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.util.TCLogger;

public class ConfigManager {

    private final FileConfiguration config;
    private final TCLogger logger;

    private int day;
    private int night;
    private Set<String> worlds;

    private boolean nightSkippingEnabled;
    private boolean percentageEnabled;
    private int configPercentage;
    private boolean debug = false;

    public ConfigManager(final TimeControl plugin) {
        this.config = plugin.getConfig();
        this.logger = plugin.getTCLogger();
    }

    public void validate() {
        final int day = this.config.getInt("day", 30);
        if (day <= 0) {
            this.day = 30;
            logger.warn("Setting day cycle to %d minutes is not safe", day);
        } else {
            this.day = day;
        }
        logger.info("Set day cycle to %d minutes", this.day);

        final int night = this.config.getInt("night", 5);
        if (night <= 0) {
            this.night = 5;
            logger.warn("Setting night cycle to %d minutes is not safe", night);
        } else {
            this.night = night;
        }
        logger.info("Set night cycle to %d minutes", this.night);

        final List<String> worlds = this.config.getStringList("worlds");
        this.worlds = new HashSet<>();
        this.worlds.addAll(worlds);
        this.worlds = Collections.unmodifiableSet(this.worlds);

        percentageEnabled = this.config.getBoolean("players-sleeping-percentage.enabled");
        configPercentage = this.config.getInt("players-sleeping-percentage.percentage");
        nightSkippingEnabled = this.config.getBoolean("night-skipping.enabled");

        debug = this.config.getBoolean("debug", false);
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

    public int getConfigPercentage() {
        return configPercentage;
    }

    public boolean isNightSkippingEnabled() {
        return nightSkippingEnabled;
    }

    public boolean isDebug() {
        return debug;
    }
}
