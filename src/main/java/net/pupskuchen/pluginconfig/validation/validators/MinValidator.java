package net.pupskuchen.pluginconfig.validation.validators;

import net.pupskuchen.pluginconfig.annotations.validation.Min;
import net.pupskuchen.pluginconfig.validation.AnnotationValidator;

public class MinValidator implements AnnotationValidator<Min, Number> {

    private Number minValue;

    @Override
    public void initialize(final Min annotation) {
        this.minValue = annotation.value();
    }

    @Override
    public boolean isValid(final Number value) {
        if (value == null) {
            return true;
        }

        return Double.valueOf(value.doubleValue())
                .compareTo(Double.valueOf(minValue.doubleValue())) >= 0;
    }
}
