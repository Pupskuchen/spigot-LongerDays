package net.pupskuchen.pluginconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark field as serializable.
 *
 * A "path" can be provided to retrieve the field's value. This can also be a nested/deep path, e.g.
 * "someobject.somevalue" will retrieve the value using the key "somevalue" inside "someobject".
 *
 * If no path is provided, the field name will be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialize {
    public String value() default "";
}
