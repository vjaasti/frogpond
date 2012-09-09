package org.frogpond.metadata;

import org.frogpond.annotation.LilyField;
import org.frogpond.metadata.LilyTypeHelper;
import org.frogpond.model.Primitive;
import org.frogpond.model.PropertyEntry;
import org.lilyproject.repository.api.FieldType;
import org.lilyproject.repository.api.QName;

public class FieldTypeMetadata {
    private FieldType fieldType;
    private PropertyEntry property;

    public FieldTypeMetadata() {
    }

    public FieldTypeMetadata(FieldType fieldType, PropertyEntry property) {
        this.fieldType = fieldType;
        this.property = property;
    }

    public QName getName() {
        if (fieldType == null) return null;

        return fieldType.getName();
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public PropertyEntry getProperty() {
        return property;
    }

    public void setProperty(PropertyEntry property) {
        this.property = property;
    }

    public Primitive getPrimitive() {
        Primitive result = this.property.getAnnotation(LilyField.class).primitive();

        if (result != Primitive.None) return result;

        if (this.fieldType == null) return Primitive.None;

        //if(this.property.getType().getName().toLowerCase().endsWith("enum")) return Primitive.ENUM;

        return Primitive.primitiveOf(this.property.getType());
    }
}
