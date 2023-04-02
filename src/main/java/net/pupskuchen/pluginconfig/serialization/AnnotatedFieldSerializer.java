package net.pupskuchen.pluginconfig.serialization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.MemorySection;
import net.pupskuchen.pluginconfig.serialization.Serializer.MapSerializerWithType;
import net.pupskuchen.pluginconfig.utils.FieldUtils;

public class AnnotatedFieldSerializer<T> implements MapSerializerWithType<T> {

    @Override
    public Map<String, Object> serialize(final T unserialized) {
        final Map<String, Object> result = new HashMap<>();
        // TODO?
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(final Map<String, Object> serialized, final Class<T> type) {
        T result = null;
        boolean wroteValues = false;

        try {
            result = type.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }

        for (Field field : FieldUtils.getSerializableFields(type)) {
            field.setAccessible(true);

            final String key = FieldUtils.getConfigKey(field);

            if (key == null) {
                continue;
            }

            Object value = getValueDeep(key, serialized);

            if (value instanceof Map && !field.getType().isInstance(value)) {
                // ugly, but should get the job done
                value = deserialize((Map<String, Object>) value, (Class<T>) field.getType());
            }

            try {
                field.set(result, value);
                wroteValues = true;
            } catch (Exception e) {
            }
        }

        return wroteValues ? result : null;
    }

    @SuppressWarnings("unchecked")
    private Object getValueDeep(String path, Map<String, Object> values) {
        while (path.contains(".")) {
            final int split = path.indexOf(".");

            if (split >= 0) {
                final String parent = path.substring(0, split);
                values = (Map<String, Object>) memorySectionToMap(values.get(parent));
                path = path.substring(split + 1);
            }

            if (values == null) {
                return null;
            }
        }

        return memorySectionToMap(values.get(path));
    }

    private Object memorySectionToMap(Object value) {
        if (value instanceof MemorySection) {
            value = ((MemorySection) value).getValues(false);
        }

        return value;
    }
}
