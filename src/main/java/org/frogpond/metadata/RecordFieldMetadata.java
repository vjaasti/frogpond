package org.frogpond.metadata;

import org.frogpond.metadata.accessors.RecordFieldAccessor;
import org.frogpond.model.FieldIndex;
import org.frogpond.model.Primitive;
import org.lilyproject.repository.api.QName;

public class RecordFieldMetadata {
    private RecordTypeMetadata recordType;
    private FieldTypeMetadata fieldType;
    private RecordFieldAccessor accessor;
    private boolean mandatory;

    private FieldIndex fieldIndex;

    public RecordFieldMetadata(RecordTypeMetadata recordType, FieldTypeMetadata fieldType, RecordFieldAccessor accessor, boolean mandatory) {
        this.recordType = recordType;
        this.fieldType = fieldType;
        this.accessor = accessor;
        this.mandatory = mandatory;
    }

    public QName getQName() {
        if (this.fieldType == null) return null;
        if (this.fieldType.getFieldType() == null) return null;

        return this.fieldType.getFieldType().getName();
    }

    public boolean isMultiValue() {
        if (this.fieldType == null) return false;
        if (this.fieldType.getFieldType() == null) return false;
        if (this.fieldType.getFieldType().getValueType() == null) return false;

        //return this.fieldType.getFieldType().getValueType().isMultiValue();
        //--vinay
        if((this.fieldType.getFieldType().getValueType().getBaseName().equals("LIST")) || 
                (this.fieldType.getFieldType().getValueType().getBaseName().equals("SET")))
            return true;

        return false;
        //--vinay
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public Primitive getPrimitive() {
        if (this.fieldType == null) return Primitive.None;

        return this.fieldType.getPrimitive();
    }

    public RecordTypeMetadata getRecordType() {
        return recordType;
    }

    public FieldTypeMetadata getFieldType() {
        return fieldType;
    }

    public RecordFieldAccessor getAccessor() {
        return accessor;
    }

    public FieldIndex getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(FieldIndex fieldIndex) {
        this.fieldIndex = fieldIndex;
    }
}
