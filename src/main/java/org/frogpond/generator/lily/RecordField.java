package org.frogpond.generator.lily;

public class RecordField {
    private String fieldTypeName;
    private boolean mandatory;

    public RecordField() { }

    public RecordField(String fieldTypeName, boolean mandatory) {
        this.fieldTypeName = fieldTypeName;
        this.mandatory = mandatory;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
