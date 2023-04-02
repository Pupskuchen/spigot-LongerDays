package net.pupskuchen.pluginconfig.entity;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigEntry<T> extends ConfigItem<T> {

    public ConfigEntry(final String name, final Class<T> type) {
        super(name, type);
    }

    @Override
    public T retrieve(final ConfigurationSection config) {
        return retrieve(config, null);
    }

    @Override
    public T retrieve(final ConfigurationSection config, final Object fallback) {
        final T result = config.getObject(name, type);

        if (result != null) {
            return result;
        }

        Map<String, Object> value = config.getConfigurationSection(name).getValues(false);

        if (value.size() == 0) {
            value = config.getDefaultSection().getConfigurationSection(name).getValues(false);
        }

        return attemptDeserialization(value,
                (fallback != null && fallback.getClass().isAssignableFrom(type))
                        ? type.cast(fallback)
                        : null);
    }
}
