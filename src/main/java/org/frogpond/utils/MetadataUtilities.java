package org.frogpond.utils;

import org.frogpond.MetadataException;
import org.frogpond.generator.lily.Namespace;
import org.frogpond.metadata.accessors.FieldRecordFieldAccessor;
import org.frogpond.metadata.accessors.PropertyRecordFieldAccessor;
import org.frogpond.metadata.accessors.RecordFieldAccessor;
import org.frogpond.model.PropertyEntry;
import org.lilyproject.repository.api.QName;
import org.springframework.core.annotation.AnnotationUtils;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

public class MetadataUtilities {
    private static final String MSG_NO_ANNOTATION = "No %s annotation is present on %s";

    public static String getQNameAsLongString(QName qname) {
        return String.format("%s$%s", qname.getNamespace(), qname.getName());
    }

    public static String getQNameAsShortString(QName qname, Map<String, Namespace> namespaces) {
        Namespace namespace = namespaces.get(qname.getNamespace());

        if (namespace == null)
            return qname.getName();

        return String.format("%s$%s", namespace.getPrefix(), qname.getName());
    }

    public static QName getQualifiedName(Field field) {
        //--vinay
        if((1==1) || field.getDeclaringClass().getPackage() == null)
        {
        return new QName(
                field.getDeclaringClass().getName(),
                field.getName()
        );
        }
        //--vinay
        return new QName(
                field.getDeclaringClass().getPackage().getName(),
                field.getName()
        );
    }

    public static QName getQualifiedName(BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor) {
        //--vinay
        if(beanDescriptor.getBeanClass().getPackage() == null)
        {
            return new QName(
                                    beanDescriptor.getBeanClass().getName(),
                                                    propertyDescriptor.getName()
                                                            );
        }
        //--vinay
        return new QName(
                beanDescriptor.getBeanClass().getPackage().getName(),
                propertyDescriptor.getName()
        );
    }

    public static QName getQualifiedName(Class<?> javaClass) {
        //--vinay
         if((1==1) || (javaClass.getPackage() == null))
         {
            return new QName(javaClass.getName(), javaClass.getSimpleName());
         }
        //--vinay
        return new QName(javaClass.getPackage().getName(), javaClass.getSimpleName());
    }

    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationType) throws MetadataException {
        return getAnnotation(clazz, annotationType, true);
    }

    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationType, boolean shouldExist) throws MetadataException {
        // -- try to find the annotation
        A annotation = AnnotationUtils.findAnnotation(clazz, annotationType);

        // -- throw an exception if the annotation was not found
        if (shouldExist && annotation == null)
        {
            throw new MetadataException(String.format(
                    MSG_NO_ANNOTATION,
                    annotationType.getSimpleName(),
                    clazz
            ));
        }

        // -- return the annotation
        return annotation;
    }

    public static <A extends Annotation> A getAnnotation(PropertyDescriptor property, Class<A> annotationType) throws MetadataException {
        // -- find the given annotation on the property
        A annotation = findAnnotation(property, annotationType);

        // -- throw an exception if we still have no annotation
        if (annotation == null)
            throw new MetadataException(String.format(
                    MSG_NO_ANNOTATION,
                    annotationType.getSimpleName(),
                    "property " + property.getName()
            ));

        // -- return the annotation
        return annotation;
    }

    public static <A extends Annotation> A findAnnotation(PropertyDescriptor property, Class<A> annotationType) throws MetadataException {
        A annotation = null;

        // -- try to get the annotation from the setter
        if ((annotation == null) && (property.getWriteMethod() != null))
            annotation = AnnotationUtils.findAnnotation(property.getWriteMethod(), annotationType);

        // -- try to get the annotation from the getter
        if ((annotation == null) && (property.getReadMethod() != null))
            annotation = AnnotationUtils.findAnnotation(property.getReadMethod(), annotationType);

        // -- return the annotation
        return annotation;
    }

    public static RecordFieldAccessor getFieldAccessor(PropertyEntry propertyEntry) {
        Field field = propertyEntry.getField();
        PropertyDescriptor property = propertyEntry.getProperty();

        if (field == null && property == null) return null;
        else if (field == null) return new PropertyRecordFieldAccessor(property);
        else if (property == null) return new FieldRecordFieldAccessor(field);
        else return new PropertyRecordFieldAccessor(property);
    }


}
