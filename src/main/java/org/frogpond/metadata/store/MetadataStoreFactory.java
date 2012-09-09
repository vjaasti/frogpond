package org.frogpond.metadata.store;

public interface MetadataStoreFactory {

    /**
     * Provide a MetadataStore.
     * <p/>
     * Constructing the MetadataStore is a responsibility of the implementation.
     *
     * @return a MetadataStore containing the metadata regarding record types
     */
    MetadataStore create();

    void updateStoreForClass(MetadataStore ms, Class cls);

}
