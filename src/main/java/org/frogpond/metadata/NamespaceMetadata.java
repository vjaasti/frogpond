package org.frogpond.metadata;

public class NamespaceMetadata {
    private String prefix;
    private String name;

    public NamespaceMetadata() {
    }

    public NamespaceMetadata(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
