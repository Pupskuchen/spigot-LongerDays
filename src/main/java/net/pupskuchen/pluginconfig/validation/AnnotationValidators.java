package net.pupskuchen.pluginconfig.validation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import net.pupskuchen.pluginconfig.annotations.validation.Min;
import net.pupskuchen.pluginconfig.validation.validators.MinValidator;

public final class AnnotationValidators {
    private AnnotationValidators() {}

    private static final Map<Class<? extends Annotation>, Class<? extends AnnotationValidator<? extends Annotation, ?>>> VALIDATORS =
            new HashMap<>();

    static {
        VALIDATORS.put(Min.class, MinValidator.class);
    }

    public static Map<Class<? extends Annotation>, Class<? extends AnnotationValidator<? extends Annotation, ?>>> getAll() {
        return VALIDATORS;
    }
}
