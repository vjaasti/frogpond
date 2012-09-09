package org.frogpond.demo.model;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.frogpond.LilyException;
import org.frogpond.service.SimpleLilyService;

import java.util.Arrays;
import java.util.List;

public class SimpleRetriever{
    private static final Logger LOGGER = Logger.getLogger(SimpleRetriever.class);
    private SimpleLilyService lilyService;

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("z", "zookeeper", true, "The zookeeper url");
        options.addOption("s", "solr", true, "The solr url");
        options.addOption("p", "find-publisher", true, "Find the publisher with the given website");
        options.addOption("c", "create", true, "Create some records");
        options.addOption("t", "test", false, "Perform a full test of the system, creating, resolving, and updating records");

        CommandLineParser commandLineParser = new PosixParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        SimpleRetriever sr = new SimpleRetriever();

        try {
            sr.initialize(
                    commandLine.getOptionValue('z'),
                    commandLine.getOptionValue('s')
            );


            sr.demoRetrieval();
        } finally {
            sr.destroy();
        }

    }

    protected void initialize(String zookeeperUrl, String solrUrl) throws Exception {
        // -- create the lily service
        lilyService = new SimpleLilyService();
        lilyService.setZookeeperUrl(zookeeperUrl);
        lilyService.setSolrUrl(solrUrl);
        lilyService.initialize();
    }

    protected void destroy() {
        if (lilyService != null) {
            lilyService.close();
        }
    }



    public void demoRetrieval() {
        // -- get the book from lily
        Book book = lilyService.get(Book.class, "My Book");

        // -- print the details of our book
        LOGGER.info("Retrieved " + book);
    }

}
