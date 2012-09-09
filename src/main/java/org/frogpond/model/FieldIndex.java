package org.frogpond.model;

public class FieldIndex {
    private boolean indexed;
    private boolean stored;

    public FieldIndex(boolean indexed, boolean stored) {
        this.indexed = indexed;
        this.stored = stored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldIndex that = (FieldIndex) o;

        if (indexed != that.indexed) return false;
        if (stored != that.stored) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (indexed ? 1 : 0);
        result = 31 * result + (stored ? 1 : 0);
        return result;
    }


    /**
     * Get the flag indicating if the field is indexed.
     *
     * If (and only if) a field is indexed, then it is searchable, sortable, and facetable.
     *
     * @return True if this field should be "indexed"
     */
    public boolean isIndexed() {
        return indexed;
    }

    /**
     * Get the flag indicating if the field should be retrievable during a search
     *
     * @return  True if the value of the field should be retrievable during a search.
     */
    public boolean isStored() {
        return stored;
    }
}
