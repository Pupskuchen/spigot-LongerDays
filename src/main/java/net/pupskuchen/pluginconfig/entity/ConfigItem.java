package net.pupskuchen.pluginconfig.entity;

import java.lang.reflect.Method;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import net.pupskuchen.pluginconfig.annotations.EntityMapSerializable;
import net.pupskuchen.pluginconfig.serialization.Serializer.MapSerialize;
import net.pupskuchen.pluginconfig.serialization.Serializer.MapSerializer;
import net.pupskuchen.pluginconfig.serialization.Serializer.MapSerializerWithType;
import net.pupskuchen.pluginconfig.validation.FallbackValidation;

public abstract class ConfigItem<T> {
    protected final String name;
    protected final Class<T> type;

    public ConfigItem(final String name, final Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public abstract Object retrieve(final ConfigurationSection config);

    public abstract Object retrieve(final ConfigurationSection config, final Object fallback);

    @SuppressWarnings("unchecked")
    protected final T attemptDeserialization(final Map<String, Object> values,
            final Object fallback) {
        T result = null;

        if (ConfigurationSerializable.class.isAssignableFrom(type)) {
            for (String deserializationMethod : new String[] {"deserialize", "valueOf"}) {
                try {
                    final Method method = type.getMethod(deserializationMethod, Map.class);
                    result = type.cast(method.invoke(null, values));
                    break;
                } catch (Exception e) {
                }
            }
        } else if (type.isAnnotationPresent(EntityMapSerializable.class)) {
            try {
                final EntityMapSerializable serializable =
                        type.getAnnotation(EntityMapSerializable.class);
                final MapSerialize<T> serializer =
                        (MapSerialize<T>) serializable.serializer().getConstructor().newInstance();

                if (serializer instanceof MapSerializer) {
                    result = ((MapSerializer<T>) serializer).deserialize(values);
                } else if (serializer instanceof MapSerializerWithType) {
                    result = ((MapSerializerWithType<T>) serializer).deserialize(values, type);
                }
            } catch (Exception e) {
            }
        }

        if (result != null && fallback != null) {
            new FallbackValidation<>(result, fallback);
        }

        return result;
    }
}
