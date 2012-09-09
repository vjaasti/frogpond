package org.frogpond.metadata;

import org.frogpond.model.PropertyEntry;
import org.frogpond.model.RecordIndex;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.RecordType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecordTypeMetadata {
    private RecordType recordType;
    private Class<?> javaType;
    private RecordIndex index;

    private PropertyEntry recordIdProperty;
    private Map<String, PropertyEntry> variants = new HashMap<String, PropertyEntry>();

    private Map<QName, RecordFieldMetadata> fields = new HashMap<QName, RecordFieldMetadata>();

// -- LilyField Manipulation ------------------------------------------------------

    public void addField(RecordFieldMetadata fieldMetadata) {
        QName name = fieldMetadata.getFieldType().getFieldType().getName();

        fields.put(name, fieldMetadata);
    }

    public void removeField(QName name) {
        fields.remove(name);
    }

    public RecordFieldMetadata getField(QName name) {
        return fields.get(name);
    }

// -- LilyRecord Ids --------------------------------------------------------------

    public PropertyEntry getRecordIdProperty() {
        return recordIdProperty;
    }

    public void setRecordIdProperty(PropertyEntry recordIdProperty) {
        this.recordIdProperty = recordIdProperty;
    }

    public Map<String, PropertyEntry> getVariants() {
        return Collections.unmodifiableMap(variants);
    }

    public void addVariant(String variant, PropertyEntry variantProperty) {
        this.variants.put(variant, variantProperty);
    }

// -- Indexes -----------------------------------------------------------------

    public void setIndex(RecordIndex index) {
        this.index = index;
    }

    public RecordIndex getIndex() {
        return index;
    }

// -- Getters -----------------------------------------------------------------

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public Collection<RecordFieldMetadata> getFields() {
        return Collections.unmodifiableCollection(fields.values());
    }

    public QName getName() {
        if (this.recordType == null) return null;

        return this.recordType.getName();
    }

    public Long getVersion() {
        if (this.recordType == null) return 0L;

        return this.recordType.getVersion();
    }
}
