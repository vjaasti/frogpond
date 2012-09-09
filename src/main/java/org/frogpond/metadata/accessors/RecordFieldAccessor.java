package org.frogpond.metadata.accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

public interface RecordFieldAccessor {

    Class<?> getType();

    Object getValue(Object subject) throws InvocationTargetException, IllegalAccessException;

    void setValue(Object subject, Object value) throws InvocationTargetException, IllegalAccessException;

}
