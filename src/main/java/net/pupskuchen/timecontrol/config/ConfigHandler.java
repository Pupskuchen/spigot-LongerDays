package net.pupskuchen.timecontrol.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.World;
import net.pupskuchen.pluginconfig.PluginConfig;
import net.pupskuchen.pluginconfig.entity.ConfigEntry;
import net.pupskuchen.pluginconfig.entity.ConfigList;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.entity.TimeControlConfig;
import net.pupskuchen.timecontrol.config.entity.WorldTimeControlConfig;

public class ConfigHandler {
    private static final ConfigEntry<Boolean> DEBUG = new ConfigEntry<>("debug", Boolean.class);

    private static final ConfigEntry<TimeControlConfig> DEFAULTS =
            new ConfigEntry<>("defaults", TimeControlConfig.class);

    private static final ConfigList<WorldTimeControlConfig> WORLDS =
            new ConfigList<>("worlds", WorldTimeControlConfig.class);

    private final TimeControl plugin;
    private final PluginConfig pluginConfig;

    private boolean debug = false;
    private TimeControlConfig defaultConfig;
    private Map<String, WorldTimeControlConfig> worldConfigs = new HashMap<>();

    public ConfigHandler(final TimeControl plugin) {
        this.plugin = plugin;
        pluginConfig = new PluginConfig(plugin);
    }

    public void initializeDebugMode() {
        debug = pluginConfig.get(DEBUG);
        plugin.getTCLogger().setDebug(debug);
    }

    public void validate() {
        defaultConfig = pluginConfig.get(DEFAULTS);

        worldConfigs = loadWorlds(pluginConfig.get(WORLDS, defaultConfig));
    }

    private TimeControlConfig getWorldConfig(World world) {
        return worldConfigs.get(world.getName());
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

    public boolean isWorldEnabled(final World world) {
        return worldConfigs.containsKey(world.getName());
    }

    public List<World> getWorlds() {
        return plugin.getServer().getWorlds().stream().filter(this::isWorldEnabled)
                .collect(Collectors.toUnmodifiableList());
    }

    private Map<String, WorldTimeControlConfig> loadWorlds(
            List<WorldTimeControlConfig> worldConfigs) {
        return worldConfigs.stream()
                .collect(Collectors.toMap(WorldTimeControlConfig::getName, config -> config));
    }
}
