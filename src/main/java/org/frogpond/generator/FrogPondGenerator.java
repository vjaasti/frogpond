package org.frogpond.generator;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

public class FrogPondGenerator {
    private static final Logger LOGGER = Logger.getLogger(FrogPondGenerator.class);
    private ClassPathXmlApplicationContext applicationContext;
    private HelpFormatter helpFormatter = new HelpFormatter();

    private Options options;
    private CommandLine commandLine;

    public static void main(String[] args) {
        try {
            // -- create the generator
            FrogPondGenerator frogPondGenerator = new FrogPondGenerator();

            // -- initialize the generator
            frogPondGenerator.initialize(args);

            // -- generate the lily artifacts
            frogPondGenerator.generate();
        } catch (Exception e) {
            LOGGER.error("Unable to generate the lily artifacts.", e);
        }
    }

    protected void initialize(String[] args) throws Exception {
        options = new Options();
        options.addOption("o", true, "The output directory to which the files will be generated");
        options.addOption("p", true, "The package in which to search for Lily Entities");

        CommandLineParser commandLineParser = new PosixParser();
        commandLine = commandLineParser.parse(options, args);

        // -- check if all options are set
//        if ((! commandLine.hasOption("o")) || (! commandLine.hasOption("p"))) {
//            helpFormatter.printHelp("ant", options);
//        }

        // -- expose the options as system properties
        if (! commandLine.hasOption("p")) throw new Exception("No package has been provided");
        System.setProperty("frogpond.package", commandLine.getOptionValue("p"));

        // -- load the application context
        applicationContext = new ClassPathXmlApplicationContext(
                "applicationcontext.xml"
        );
    }

    protected void generate() throws Exception {
        // -- get the output directory from the command line
        Resource outputDirectory = applicationContext.getResource(commandLine.getOptionValue("o"));

        // -- get the generator
        ModelBasedGenerator generator = applicationContext.getBean(ModelBasedGenerator.class);

        // -- generate all lily artifacts
        for (GenerateableLilyArtifact artifact : GenerateableLilyArtifact.values()) {
            generator.generate(artifact, outputDirectory.getFile());
        }
    }


}
