package org.frogpond.utils;

import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class ClasspathUtils {

    public static Collection<Class<Object>> getAnnotatedClasses(String basePackage, Class<? extends Annotation> annotation) {
        ComponentScanner scanner = new ComponentScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
        return scanner.findMatchingClasses(basePackage, Object.class);
    }

    public static <T> Collection<Class<T>> getClassesMatchingInterface(String basePackage, Class<T> type) {
        ComponentScanner scanner = new ComponentScanner();
        scanner.addIncludeFilter(new AssignableTypeFilter(type));
        return scanner.findMatchingClasses(basePackage, type);
    }
}
