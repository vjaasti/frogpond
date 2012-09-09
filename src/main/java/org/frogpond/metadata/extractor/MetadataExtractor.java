package org.frogpond.metadata.extractor;

import org.frogpond.MetadataException;

public interface MetadataExtractor {

    MetadataContainer extract(Class<?> objectType) throws MetadataException;

}
