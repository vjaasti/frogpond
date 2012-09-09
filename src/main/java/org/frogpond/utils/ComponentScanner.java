package org.frogpond.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComponentScanner extends ClassPathScanningCandidateComponentProvider {

    public ComponentScanner() {
        super(false);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<Class<T>> findMatchingClasses(String basePackage, Class<T> targetClass) {
        basePackage = basePackage == null ? "" : basePackage;

        List<Class<T>> classes = new ArrayList<Class<T>>();

        for (BeanDefinition candidate : findCandidateComponents(basePackage)) {
            try {
                Class<?> cls = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
                classes.add((Class<T>) cls);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return classes;
    }

    /**
     * Add an include type filter to the <i>end</i> of the inclusion list.
     */
    public void addIncludeFilter(TypeFilter includeFilter) {
        super.addIncludeFilter(includeFilter);
    }

    /**
     * Add an exclude type filter to the <i>front</i> of the exclusion list.
     */
    public void addExcludeFilter(TypeFilter excludeFilter) {
        super.addExcludeFilter(excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        super.resetFilters(useDefaultFilters);
    }
}
