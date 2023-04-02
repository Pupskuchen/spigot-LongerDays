package net.pupskuchen.pluginconfig.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TypeUtils {
    private TypeUtils() {}

    private static final Set<Class<?>> PRIMITIVE_WRAPPERS =
            new HashSet<>(Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class,
                    Integer.class, Long.class, Float.class, Double.class, Void.class));

    public static boolean isWrapperType(final Class<?> type) {
        return PRIMITIVE_WRAPPERS.contains(type);
    }

    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        return type.isPrimitive() || isWrapperType(type);
    }
}
