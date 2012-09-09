package org.frogpond.annotation;

import org.frogpond.model.Primitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a field within a record.
 * <p/>
 * A record field always carries the primitive lily type for which it is meant. In most cases, the primitive
 * can be determined based on the field type or method return type, but a specific primitive can be set if needed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LilyField {

    /**
     * @return The type of this fields value.
     */
    Primitive primitive() default Primitive.None;

    /**
     * @return The java type of this field.
     *         <p/>
     *         This parameter should only be used when the type of the field is a generic collection.
     */
    Class<?> javaType() default Object.class;

    /**
     * @return A flag indicating if this field is mandatory.
     */
    boolean mandatory() default false;

    String javaTypeName() default "";
}
