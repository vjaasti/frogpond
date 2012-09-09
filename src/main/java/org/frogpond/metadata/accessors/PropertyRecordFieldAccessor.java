package org.frogpond.metadata.accessors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyRecordFieldAccessor implements RecordFieldAccessor {
    private PropertyDescriptor propertyDescriptor;

    public PropertyRecordFieldAccessor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    @Override
    public Class<?> getType() {
        return propertyDescriptor.getPropertyType();
    }

    @Override
    public Object getValue(Object subject) throws InvocationTargetException, IllegalAccessException {
        // -- check if a valid subject has been provided
        if (subject == null) return null;

        // -- get the read method
        Method readMethod = propertyDescriptor.getReadMethod();

        // -- check if reading is supported
        if (readMethod == null)
            throw new UnsupportedOperationException("Reading property " + propertyDescriptor.getName() + " is not supported");

        // -- check if the method is a member of the subject type
        if (!hasMethod(subject.getClass(), readMethod))
            throw new UnsupportedOperationException("The method for reading property " + propertyDescriptor.getName() + " could not be found on " + subject.getClass());

        // -- get the property
        return propertyDescriptor.getReadMethod().invoke(subject);
    }

    @Override
    public void setValue(Object subject, Object value) throws InvocationTargetException, IllegalAccessException {
        // -- check if a valid subject has been provided
        if (subject == null) return;

        // -- get the read method
        Method writeMethod = propertyDescriptor.getReadMethod();

        // -- check if writing is supported
        if (propertyDescriptor.getWriteMethod() == null)
            throw new UnsupportedOperationException("Writing property " + propertyDescriptor.getName() + " is not supported");

        // -- check if the method is a member of the subject type
        if (!hasMethod(subject.getClass(), writeMethod))
            throw new UnsupportedOperationException("The method for writing property " + propertyDescriptor.getName() + " could not be found on " + subject.getClass());

        // -- set the property
        propertyDescriptor.getWriteMethod().invoke(subject, value);
    }

    protected boolean hasMethod(Class<?> subject, Method method) {
        try {
            subject.getMethod(method.getName(), method.getParameterTypes());

            return true;
        } catch (NoSuchMethodException nsme) {
            return false;
        }
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }
}
