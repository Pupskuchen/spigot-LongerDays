package net.pupskuchen.pluginconfig.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AnnotationValidation {

    private final Field field;
    private static final Set<Map.Entry<Class<? extends Annotation>, Class<? extends AnnotationValidator<? extends Annotation, ?>>>> VALIDATORS =
            AnnotationValidators.getAll().entrySet();

    public AnnotationValidation(final Field field) {
        this.field = field;
    }

    @SuppressWarnings("unchecked")
    public Object validate(final Object value, final Object fallbackValue) {
        boolean valueUsable = true;
        boolean fallbackUsable = true;

        for (Entry<Class<? extends Annotation>, Class<? extends AnnotationValidator<? extends Annotation, ?>>> entry : VALIDATORS) {
            final Annotation annotation = field.getAnnotation(entry.getKey());

            if (annotation == null) {
                continue;
            }

            final Class<? extends AnnotationValidator<? extends Annotation, ?>> validatorClass =
                    entry.getValue();
            // TODO: big meh
            final Type[] genericTypes =
                    ((ParameterizedType) validatorClass.getGenericInterfaces()[0])
                            .getActualTypeArguments();
            final Class<? extends Annotation> expectedAnnotation =
                    (Class<? extends Annotation>) genericTypes[0];
            final Class<?> valueType = (Class<?>) genericTypes[1];

            if (!expectedAnnotation.isInstance(annotation)) {
                continue;
            }

            if (!valueType.isInstance(value) && !valueType.isInstance(fallbackValue)) {
                continue;
            }

            boolean validValue = false;
            boolean validFallback = false;

            try {
                final AnnotationValidator<Annotation, Object> validator =
                        (AnnotationValidator<Annotation, Object>) validatorClass.getConstructor()
                                .newInstance();

                validator.initialize(annotation);

                validValue = validator.isValid(valueType.cast(value));
                validFallback = validator.isValid(valueType.cast(fallbackValue));
            } catch (Exception e) {
            }

            valueUsable &= validValue;
            fallbackUsable &= validFallback;

            if (!valueUsable && !fallbackUsable) {
                break;
            }
        }

        return valueUsable ? value : (fallbackUsable ? fallbackValue : null);
    }
}
