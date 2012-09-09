package org.frogpond.service.resolvers;

import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.model.Primitive;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordId;

import java.util.Map;

public class DefaultFieldResolver implements FieldResolver {

    @Override
    public Primitive getPrimitive() {
        return Primitive.None;
    }

    @Override
    public Object resolve(RecordFieldMetadata recordField, Record record) {
        return record.getField(recordField.getQName());
    }

    @Override
    public Object resolve(Map<RecordId, Object> retrievedRecords, RecordFieldMetadata recordField, Record record) {
        return record.getField(recordField.getQName());
    }
}
