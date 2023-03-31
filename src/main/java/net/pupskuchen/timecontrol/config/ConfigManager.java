package net.pupskuchen.timecontrol.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.util.TCLogger;

public class ConfigManager {

    private final FileConfiguration config;
    private final TCLogger logger;

    private TimeControlConfig defaultConfig;
    private Map<String, TimeControlConfig> worldConfigs = new HashMap<>();
    private boolean debug = false;

    public ConfigManager(final TimeControl plugin) {
        this.config = plugin.getConfig();
        this.logger = plugin.getTCLogger();
    }

    public void registerSerializables() {
        ConfigurationSerialization.registerClass(TimeControlConfig.class);
    }

    public void initializeDebugMode() {
        debug = config.getBoolean("debug", false);
        logger.setDebug(debug);
    }

    public void validate() {
        checkForLegacyOptions();

        defaultConfig = TimeControlConfig.validate(
                // try to load user defined default configs
                TimeControlConfig
                        .deserialize(config.getConfigurationSection("defaults").getValues(false)),
                // TODO: remove
                // try to load "legacy" global configs
                TimeControlConfig.deserialize(config.getConfigurationSection("").getValues(false)),
                // load internal fallbacks as last resort
                TimeControlConfig.deserialize(
                        config.getDefaults().getConfigurationSection("defaults").getValues(false)));

        loadWorldConfigs();

        // if no world config was loaded, try the "legacy" config style
        if (worldConfigs.size() == 0) {
            loadWorldConfigsLegacy();
        }
    }

    private void checkForLegacyOptions() {
        // check for legacy style config of day/night duration
        if (config.isSet(TimeControlConfig.KEY.DURATION_DAY_LEGACY.toString())
                || config.isSet(TimeControlConfig.KEY.DURATION_NIGHT_LEGACY.toString())) {
            logger.warn(
                    "Using deprecated durations configs for \"%s\" and/or \"%s\". Please define those in the \"defaults.durations\" config.",
                    TimeControlConfig.KEY.DURATION_DAY_LEGACY.toString(),
                    TimeControlConfig.KEY.DURATION_NIGHT_LEGACY.toString());
            logger.warn(
                    "Please consult the plugin documentation if you're not sure what this means.");
        }

        // check for missing "defaults" wrap which will stop working when the legacy style config
        // isn't supported anymore
        if (config.isSet(TimeControlConfig.KEY.DURATIONS.toString())
                || config.isSet(TimeControlConfig.KEY.NIGHT_SKIPPING.toString())
                || config.isSet(TimeControlConfig.KEY.PLAYERS_SLEEPING.toString())) {
            logger.warn("Global/default options should be wrapped in a \"defaults\" map.");
            logger.warn(
                    "Please consult the plugin documentation if you're not sure what this means.");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadWorldConfigs() {
        List<Map<?, ?>> worlds = config.getMapList("worlds");


        for (int i = 0; i < worlds.size(); i++) {
            Map<?, ?> worldConfig = worlds.get(i);
            String name = (String) worldConfig.get("name");

            if (name == null || name.length() == 0) {
                logger.error(
                        "\"worlds\" configuration entry #%d is missing the world's name, ignoring the entry.",
                        i);
                continue;
            }

            worldConfigs.put(name,
                    TimeControlConfig.validate(
                            TimeControlConfig.deserialize((Map<String, Object>) worldConfig),
                            defaultConfig));
        }
    }

    private void loadWorldConfigsLegacy() {
        List<String> worlds = config.getStringList("worlds");

        for (String world : worlds) {
            worldConfigs.put(world, defaultConfig);
        }

        if (worlds.size() > 0) {
            logger.warn(
                    "You're currently using a deprecated way of defining worlds to control time for.");
            logger.warn(
                    "Check the plugin documentation to update your config since legacy support may be dropped in the future.");
        }
    }

    public Set<String> getWorlds() {
        return Collections.unmodifiableSet(worldConfigs.keySet());
    }

    public TimeControlConfig getWorldConfig(World world) {
        return worldConfigs.containsKey(world.getName()) ? worldConfigs.get(world.getName())
                : defaultConfig;
    }

    public int getDay(World world) {
        return getWorldConfig(world).getDurationDay();
    }

    public int getNight(World world) {
        return getWorldConfig(world).getDurationNight();
    }

    public boolean isPercentageEnabled(World world) {
        return getWorldConfig(world).getPlayersSleepingPercentageEnabled();
    }

    public int getConfigPercentage(World world) {
        return getWorldConfig(world).getPlayersSleepingPercentage();
    }

    public boolean isNightSkippingEnabled(World world) {
        return getWorldConfig(world).getNightSkippingEnabled();
    }

    public boolean nightSkippingDisabledGlobally() {
        for (TimeControlConfig worldConfig : worldConfigs.values()) {
            if (worldConfig.getNightSkippingEnabled()) {
                return false;
            }
        }

        return true;
    }

    public boolean isDebug() {
        return debug;
    }
}
