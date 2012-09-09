package org.frogpond.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark fields or properties delivering a recordId.
 * <p/>
 * For a field or method being a valid recordId supplier, the type of the field or the return type of the method
 * should be one of the following:
 * <ul>
 * <li><b>a LilyId</b>, in which case the unmodified value will be used as the record id</li>
 * <li><b>a String</b>, in which case a recordId will be generated using the repository's IdGenerator</li>
 * </ul>
 * Any other kind of return type or field type is considered invalid.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface LilyId {

}
