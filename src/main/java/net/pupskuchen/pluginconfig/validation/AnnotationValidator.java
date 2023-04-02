package net.pupskuchen.pluginconfig.validation;

import java.lang.annotation.Annotation;

public interface AnnotationValidator<A extends Annotation, T> {
    public void initialize(A annotation);

    public boolean isValid(T value);
}
