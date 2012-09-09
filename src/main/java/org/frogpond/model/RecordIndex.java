package org.frogpond.model;

public class RecordIndex {
    private String variant;
    private String vtags;

    public RecordIndex() {
    }

    public RecordIndex(String variant, String vtags) {
        this.variant = variant;
        this.vtags = vtags;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getVtags() {
        return vtags;
    }

    public void setVtags(String vtags) {
        this.vtags = vtags;
    }
}
