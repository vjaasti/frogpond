package org.frogpond.metadata.accessors;

import java.lang.reflect.Field;

public class FieldRecordFieldAccessor implements RecordFieldAccessor {
    private Field field;

    public FieldRecordFieldAccessor(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Object getValue(Object subject) throws IllegalAccessException {
        // -- check if a valid subject has been provided
        if (subject == null) return null;

        // -- check if the field is available on the provided subject
        if (!hasField(subject.getClass(), field))
            throw new UnsupportedOperationException("The field with name " + field.getName() + " could not be found on " + subject.getClass());

        // -- set the field value
        return field.get(subject);
    }

    @Override
    public void setValue(Object subject, Object value) throws IllegalAccessException {
        // -- check if a valid subject has been provided
        if (subject == null) return;

        // -- check if the field is available on the provided subject
        if (!hasField(subject.getClass(), field))
            throw new UnsupportedOperationException("The field with name " + field.getName() + " could not be found on " + subject.getClass());

        // -- return the field value
        field.set(subject, value);
    }

    /*
    protected boolean hasField(Class<?> subject, Field field) {
        try {
            subject.getDeclaredField(field.getName());

            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
    */
    //--vinay START
    protected boolean hasField(Class<?> subject, Field field) {
        try {
            subject.getDeclaredField(field.getName());

            return true;
        } catch (NoSuchFieldException e) {
            if(subject.getSuperclass() == null)
                return false;
            else
                return hasField(subject.getSuperclass(), field);
        }
    }
    //--vinay END

    public Field getField() {
        return field;
    }
}
