package org.frogpond.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class FreemarkerGenerator {
    private Configuration configuration;

    protected void generate(String templateName, Map<String, Object> data, String targetName, File outputDirectory) {
        try {
            // -- get the template
            Template template = configuration.getTemplate(templateName, "UTF-8");

            // -- construct the output file
            File outputFile = new File(outputDirectory, targetName);

            // -- process the template and write the result to the output file
            FileWriter writer = new FileWriter(outputFile);
            template.process(data, writer);
            writer.close();
        } catch (Exception e) {
            if (e instanceof GeneratorException) throw (GeneratorException) e;
            else throw new GeneratorException("Unable to generate " + templateName, e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
