package net.pupskuchen.pluginconfig.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigList<T> extends ConfigItem<T> {

    public ConfigList(final String name, final Class<T> type) {
        super(name, type);
    }

    @Override
    public List<T> retrieve(final ConfigurationSection config) {
        return retrieve(config, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> retrieve(final ConfigurationSection config, final Object fallback) {
        final List<?> list = config.getList(name);

        if (list == null) {
            return (fallback instanceof List) ? (List<T>) fallback
                    : (fallback == null) ? new ArrayList<>(0) : Arrays.asList((T) fallback);
        }

        final List<T> result = new ArrayList<>();

        for (Object object : list) {
            if (type.isInstance(object)) {
                result.add(type.cast(object));
            } else if (object instanceof Map) {
                final T deserialized =
                        attemptDeserialization((Map<String, Object>) object, (T) fallback);

                if (deserialized != null) {
                    result.add(deserialized);
                }
            }
        }

        return result;
    }

}
