package org.frogpond.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LilyVariant {
    /**
     * @return the variant name
     */
    String value();
}
