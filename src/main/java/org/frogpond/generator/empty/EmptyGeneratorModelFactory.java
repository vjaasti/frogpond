package org.frogpond.generator.empty;

import org.frogpond.generator.GeneratorModelFactory;
import org.frogpond.metadata.store.MetadataStore;

import java.util.HashMap;
import java.util.Map;

public class EmptyGeneratorModelFactory implements GeneratorModelFactory {
    @Override
    public Map<String, Object> constructModel(MetadataStore metadataStore) throws Exception {
        return new HashMap<String, Object>();
    }
}
