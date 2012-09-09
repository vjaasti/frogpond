package org.frogpond.generator;

import org.apache.log4j.Logger;
import org.frogpond.metadata.store.MetadataStore;

import java.io.File;

public class ModelBasedGenerator extends FreemarkerGenerator {
    private static final Logger LOGGER = Logger.getLogger(ModelBasedGenerator.class);
    private MetadataStore metadataStore;

    public void generate(GenerateableLilyArtifact artifact, File outputDirectory) throws Exception {
        LOGGER.info(String.format("Generating %s to %s", artifact.name(), outputDirectory));

        // -- create the model factory
        GeneratorModelFactory modelFactory = artifact.modelFactory.newInstance();

        // -- generate the artifact
        generate(
                artifact.templateName,
                modelFactory.constructModel(metadataStore),
                artifact.outputFileName,
                outputDirectory
        );

        LOGGER.info(String.format("Succesfully generated %s to %s", artifact.name(), outputDirectory));
    }

    public MetadataStore getMetadataStore() {
        return metadataStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
