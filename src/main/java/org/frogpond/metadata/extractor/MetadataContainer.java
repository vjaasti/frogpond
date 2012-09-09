package org.frogpond.metadata.extractor;

import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.lilyproject.repository.api.QName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetadataContainer {
    private RecordTypeMetadata recordTypeMetadata;
    private Map<QName, FieldTypeMetadata> fieldTypeMetadata = new HashMap<QName, FieldTypeMetadata>();

    public MetadataContainer() {
    }

    public void addFieldTypeMetadata(FieldTypeMetadata metadata) {
        this.fieldTypeMetadata.put(metadata.getName(), metadata);
    }

    public Map<QName, FieldTypeMetadata> getFieldTypeMetadata() {
        return Collections.unmodifiableMap(fieldTypeMetadata);
    }

    public RecordTypeMetadata getRecordTypeMetadata() {
        return recordTypeMetadata;
    }

    public void setRecordTypeMetadata(RecordTypeMetadata recordTypeMetadata) {
        this.recordTypeMetadata = recordTypeMetadata;
    }
}
