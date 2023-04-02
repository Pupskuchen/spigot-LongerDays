package net.pupskuchen.pluginconfig;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.pupskuchen.pluginconfig.entity.ConfigEntry;
import net.pupskuchen.pluginconfig.entity.ConfigList;

public class PluginConfig {

    private final FileConfiguration config;

    public PluginConfig(final JavaPlugin plugin) {
        config = plugin.getConfig();
    }

    public <T> T get(ConfigEntry<T> item) {
        return item.retrieve(config);
    }

    /**
     * @param <T> type of item to get
     * @param item config key to get item from
     * @param fallback used to patch individual missing values in config item
     * @return item retrieved from config with missing values filled from fallback
     */
    public <T extends O, O> T get(ConfigEntry<T> item, O fallback) {
        return item.retrieve(config, fallback);
    }

    public <T> List<T> get(ConfigList<T> item) {
        return item.retrieve(config);
    }

    /**
     * @param <T> type of items to get
     * @param item config key to get item from
     * @param fallback used if the configuration doesn't contain the list at all
     * @return either 1) list of items retrieved from config or 2) fallback
     */
    public <T> List<T> get(ConfigList<T> item, List<T> fallback) {
        return item.retrieve(config, fallback);
    }

    /**
     * @param <T> type of items to get
     * @param item config key to get item from
     * @param fallback used to patch individual missing values not set in configuration items
     * @return list of items retrieved from config with missing values filled from fallback
     */
    public <T extends O, O> List<T> get(ConfigList<T> item, O fallback) {
        return item.retrieve(config, fallback);
    }
}
