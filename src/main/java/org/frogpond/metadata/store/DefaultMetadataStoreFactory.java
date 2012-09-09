package org.frogpond.metadata.store;

import org.apache.log4j.Logger;
import org.frogpond.MetadataException;
import org.frogpond.annotation.LilyRecord;
import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.extractor.MetadataContainer;
import org.frogpond.metadata.extractor.MetadataExtractor;
import org.frogpond.utils.ClasspathUtils;

import java.util.Collection;

public class DefaultMetadataStoreFactory implements MetadataStoreFactory {
    private static final Logger LOGGER = Logger.getLogger(DefaultMetadataStoreFactory.class);

    private MetadataExtractor metadataExtractor;
    private String basePackage;

    @Override
    public MetadataStore create() {
        // -- create the result
        MetadataStore result = new MetadataStore();
        result.setMetadataStoreFactory(this);

        // -- find all classes annotated with the given annotation
        Collection<Class<Object>> records = ClasspathUtils.getAnnotatedClasses(basePackage, LilyRecord.class);

        // -- iterate the record classes
        for (Class<Object> recordClass : records) {
            try {
                // -- extract the metadata
                MetadataContainer container = metadataExtractor.extract(recordClass);

                // -- add the recordType and fieldTypes to the metadataStore
                result.addRecordTypeMetadata(container.getRecordTypeMetadata());
                for (FieldTypeMetadata fieldTypeMetadata : container.getFieldTypeMetadata().values())
                {
                    result.addFieldTypeMetadata(fieldTypeMetadata);
                }

                LOGGER.info("Added record " + container.getRecordTypeMetadata().getName() + " for " + recordClass);
            } catch (MetadataException me) {
                LOGGER.error("Unable to process " + recordClass, me);
            }
        }

        result.setMetadataStoreFactory(this);

        // -- return the constructed store
        return result;
    }

    public void updateStoreForClass(MetadataStore store, Class recordClass)
    {
        try
        {
            MetadataContainer container = metadataExtractor.extract(recordClass);

            // -- add the recordType and fieldTypes to the metadataStore
            store.addRecordTypeMetadata(container.getRecordTypeMetadata());
            for (FieldTypeMetadata fieldTypeMetadata : container.getFieldTypeMetadata().values())
            {
                store.addFieldTypeMetadata(fieldTypeMetadata);
            }

            LOGGER.info("Added record " + container.getRecordTypeMetadata().getName() + " for " + recordClass);

        }
        catch (MetadataException me) 
        {
            LOGGER.error("Unable to process " + recordClass, me);
        }

    }

    public void setMetadataExtractor(MetadataExtractor metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
