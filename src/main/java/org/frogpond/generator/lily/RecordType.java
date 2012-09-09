package org.frogpond.generator.lily;

import java.util.ArrayList;
import java.util.List;

public class RecordType {
    private String name;
    private String version;
    private List<RecordField> fields = new ArrayList<RecordField>();

    public RecordType() { }

    public RecordType(String name, String version, List<RecordField> fields) {
        this.name = name;
        this.version = version;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<RecordField> getFields() {
        return fields;
    }

    public void setFields(List<RecordField> fields) {
        this.fields = fields;
    }
}
