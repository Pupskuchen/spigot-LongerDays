package net.pupskuchen.pluginconfig.entity;

import org.bukkit.configuration.ConfigurationSection;
import net.pupskuchen.pluginconfig.utils.TypeUtils;

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

        if (TypeUtils.isPrimitiveOrWrapper(type)) {
            return getFallbackValue(fallback);
        }

        ConfigurationSection section = config.getConfigurationSection(name);

        if (section == null || section.getValues(false).size() == 0) {
            section = config.getDefaultSection().getConfigurationSection(name);
        }

        if (section == null) {
            return getFallbackValue(fallback);
        }

        return attemptDeserialization(section.getValues(false), getFallbackValue(fallback));
    }

    private T getFallbackValue(Object fallback) {
        return (fallback != null && fallback.getClass().isAssignableFrom(type))
                ? type.cast(fallback)
                : null;
    }
}
