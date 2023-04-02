package net.pupskuchen.pluginconfig.validation;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import net.pupskuchen.pluginconfig.utils.FieldUtils;
import net.pupskuchen.pluginconfig.utils.TypeUtils;

public class FallbackValidation<T> {
    public FallbackValidation(final T obj, final Object fallback) {
        performValidation(obj, fallback);
    }

    private void performValidation(final T obj, final Object fallback) {
        if (obj == null || fallback == null) {
            return;
        }

        final Class<?> objClass = obj.getClass();
        final Class<?> fallbackClass = fallback.getClass();

        if (!fallbackClass.isAssignableFrom(objClass)) {
            return;
        }

        for (final Field field : getValidatableFields(obj, fallback)) {
            try {
                field.setAccessible(true);

                final Object objValue = field.get(obj);
                final Object fallbackValue = field.get(fallback);

                if (objValue == null) {
                    field.set(obj, fallbackValue);
                    continue;
                }

                if (!TypeUtils.isPrimitiveOrWrapper(field.getType())) {
                    new FallbackValidation<>(objValue, fallbackValue);
                }

                final Object validatedValue =
                        new AnnotationValidation(field).validate(objValue, fallbackValue);

                field.set(obj, validatedValue);
            } catch (Exception e) {
            }
        }
    }

    private List<Field> getValidatableFields(final T obj, final Object fallback) {
        final List<Field> objFields = FieldUtils.getSerializableFields(obj.getClass());
        final List<Field> fallbackFields = FieldUtils.getSerializableFields(fallback.getClass());

        return objFields.stream()
                .filter(field -> fallbackFields.stream()
                        .anyMatch(fallbackField -> (field.getName().equals(fallbackField.getName())
                                && field.getType().equals(fallbackField.getType()))))
                .collect(Collectors.toList());
    }

}
