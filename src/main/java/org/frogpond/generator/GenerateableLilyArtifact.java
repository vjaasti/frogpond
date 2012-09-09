package org.frogpond.generator;

import org.frogpond.generator.empty.EmptyGeneratorModelFactory;
import org.frogpond.generator.lily.LilyGeneratorModelFactory;
import org.frogpond.generator.solr.SolrGeneratorModelFactory;

public enum GenerateableLilyArtifact {
    LilySchema("lilySchema.ftl", "lilySchema.json", LilyGeneratorModelFactory.class),
    IndexerConf("indexerConf.ftl", "indexerConf.xml", EmptyGeneratorModelFactory.class),
    SolrSchema("solrSchema.ftl", "solrSchema.xml", SolrGeneratorModelFactory.class);

    public String templateName;
    public String outputFileName;
    public Class<? extends GeneratorModelFactory> modelFactory;

    private GenerateableLilyArtifact(String templateName, String outputFileName, Class<? extends GeneratorModelFactory> modelFactory) {
        this.templateName = templateName;
        this.outputFileName = outputFileName;
        this.modelFactory = modelFactory;
    }
}
