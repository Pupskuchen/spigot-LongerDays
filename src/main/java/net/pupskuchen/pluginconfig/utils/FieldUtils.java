package net.pupskuchen.pluginconfig.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.pupskuchen.pluginconfig.annotations.Serialize;

public final class FieldUtils {
    private FieldUtils() {}

    /**
     * Retrieve configuration key for given field.
     *
     * @see {@link Serialize}
     */
    public static String getConfigKey(final Field field) {
        final Serialize annotation = field.getAnnotation(Serialize.class);

        if (annotation == null) {
            return null;
        }

        final String annotationValue = annotation.value();
        if (annotationValue != null && !annotationValue.equals("")) {
            return annotationValue;
        }

        return field.getName();
    }

    /**
     * Retrieve serializable fields for given type, including inherited (serializable) fields.
     */
    public static List<Field> getSerializableFields(Class<?> type) {
        final List<Field> fields = new ArrayList<>();

        do {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        } while (type != Object.class);

        return fields.stream().filter(field -> field.isAnnotationPresent(Serialize.class))
                .collect(Collectors.toUnmodifiableList());
    }
}
