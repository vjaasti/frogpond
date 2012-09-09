package org.frogpond.model;

import java.util.Comparator;

import org.lilyproject.bytes.api.DataInput;
import org.lilyproject.bytes.api.DataOutput;
import org.lilyproject.repository.api.ValueType;
import org.lilyproject.repository.api.IdentityRecordStack;
import org.lilyproject.repository.api.UnknownValueTypeEncodingException;
import org.lilyproject.repository.impl.valuetype.AbstractValueType;

public class PrimitiveValueTypePlaceholder extends AbstractValueType implements ValueType {
    private Primitive primitive;
    public PrimitiveValueTypePlaceholder(Primitive primitive) {
        this.primitive = primitive;
    }

    @Override
    public String getName() {
        return primitive.name();
    }

    @Override
    public String getBaseName() {
        return primitive.name();
    }
    @Override
    public Object read(DataInput dataInput) throws UnknownValueTypeEncodingException {
        return null;
    }
    @Override
    public Comparator getComparator() {
        return null;
    }

    @Override
    public void write(Object o, DataOutput dataOutput, IdentityRecordStack parentRecords) {
    }

    @Override
    public Class getType() {
        return null;
    }
    @Override
    public ValueType getDeepestValueType() {
        return this;
    }
}
