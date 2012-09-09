package org.frogpond.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark the object to be indexed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LilyRecordIndex {

    /**
     * @return A pattern describing which variants should be indexed. '*' can be used to indicate all variants
     *         should be indexed.
     */
    String indexVariantPattern() default "*";

    /**
     * @return A pattern describing which version tags should be indexed.
     */
    String indexVersionTags() default "live,last";
}
