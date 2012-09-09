package org.frogpond.service.resolvers;

import java.util.Map;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.model.Primitive;
import org.lilyproject.repository.api.*;

public interface FieldResolver {

    Primitive getPrimitive();

    Object resolve(RecordFieldMetadata recordField, Record record);
    Object resolve(Map<RecordId, Object> retrievedRecords, RecordFieldMetadata recordField, Record record);

}
