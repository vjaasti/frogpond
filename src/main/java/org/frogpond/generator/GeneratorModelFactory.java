package org.frogpond.generator;

import org.frogpond.metadata.store.MetadataStore;

import java.util.Map;

public interface GeneratorModelFactory {

    Map<String, Object> constructModel(MetadataStore metadataStore) throws Exception;

}
