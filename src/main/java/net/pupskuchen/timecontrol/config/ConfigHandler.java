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
import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.config.entity.TimeControlConfig;
import net.pupskuchen.timecontrol.config.entity.WorldTimeControlConfig;
import net.pupskuchen.timecontrol.util.DurationUtil;

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
        debug = pluginConfig.get(DEBUG, false);
        plugin.getTCLogger().setDebug(debug);
    }

    public void validate() {
        defaultConfig = pluginConfig.get(DEFAULTS);

        worldConfigs = loadWorlds(pluginConfig.get(WORLDS, defaultConfig));
    }

    private TimeControlConfig getWorldConfig(World world) {
        return worldConfigs.get(world.getName());
    }

    // casting should be fine, we're normalizing the durations on init
    @SuppressWarnings("unchecked")
    public Durations<Double, Double> getDurations(World world) {
        return (Durations<Double, Double>) getWorldConfig(world).getDurations();
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

        for (WorldTimeControlConfig config : worldConfigs) {
            config.setDurations(DurationUtil.normalize(config.getDurations()));
        }

        return worldConfigs.stream()
                .collect(Collectors.toMap(WorldTimeControlConfig::getName, config -> config));
    }
}
