package org.frogpond.generator.lily;

public class FieldType {
    private String name;
    private String primitive;
    private boolean multivalue;
    private boolean hierarchical;
    private String scope;

    public FieldType() { }

    public FieldType(String name, String primitive, boolean multivalue, boolean hierarchical, String scope) {
        this.name = name;
        this.primitive = primitive;
        this.multivalue = multivalue;
        this.hierarchical = hierarchical;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimitive() {
        return primitive;
    }

    public void setPrimitive(String primitive) {
        this.primitive = primitive;
    }

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    public boolean isHierarchical() {
        return hierarchical;
    }

    public void setHierarchical(boolean hierarchical) {
        this.hierarchical = hierarchical;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
