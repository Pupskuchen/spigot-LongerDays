package net.pupskuchen.pluginconfig.validation.validators;

import net.pupskuchen.pluginconfig.annotations.validation.Min;
import net.pupskuchen.pluginconfig.validation.AnnotationValidator;

public class MinValidator implements AnnotationValidator<Min, Integer> {

    private Integer minValue;

    @Override
    public void initialize(final Min annotation) {
        this.minValue = annotation.value();
    }

    @Override
    public boolean isValid(final Integer value) {
        if (value == null) {
            return true;
        }

        return value.compareTo(minValue) >= 0;
    }
}
