package org.frogpond.model;

import org.lilyproject.repository.api.QName;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class PropertyEntry {
    private QName name;
    private Field field;
    private PropertyDescriptor property;

    private Annotation[] annotations;

    public PropertyEntry(QName name) {
        this.name = name;
    }

    public <T> T getAnnotation(Class<T> annotationClass) {
        if (annotations == null) return null;

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass))
                return (T) annotation;
        }

        return null;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public Class<?> getType() {
        if (field != null) return field.getType();
        else if (property != null) return property.getPropertyType();
        else return null;
    }

    public QName getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public PropertyDescriptor getProperty() {
        return property;
    }

    public void setProperty(PropertyDescriptor property) {
        this.property = property;
    }
}
