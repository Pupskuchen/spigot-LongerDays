package net.pupskuchen.pluginconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.pupskuchen.pluginconfig.serialization.AnnotatedFieldSerializer;
import net.pupskuchen.pluginconfig.serialization.Serializer.MapSerialize;

/**
 * Make class serializable.
 *
 * Unless you provide your own serializer, the {@link AnnotatedFieldSerializer} will be used which
 * requires the annotated class to have a public, empty constructor (no parameters).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityMapSerializable {
    @SuppressWarnings("rawtypes")
    Class<? extends MapSerialize> serializer() default AnnotatedFieldSerializer.class;
}
