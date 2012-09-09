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

public class LilyServiceDemo {
    private static final Logger LOGGER = Logger.getLogger(LilyServiceDemo.class);
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

        LilyServiceDemo lilyServiceDemo = new LilyServiceDemo();

        try {
            lilyServiceDemo.initialize(
                    commandLine.getOptionValue('z'),
                    commandLine.getOptionValue('s')
            );

            if (commandLine.hasOption('t')) {
                lilyServiceDemo.test();
            } else if (commandLine.hasOption('c')) {
                lilyServiceDemo.create(Integer.parseInt(commandLine.getOptionValue('c')));
            } else if (commandLine.hasOption('p')) {
                lilyServiceDemo.findPublisher(commandLine.getOptionValue('p'));
            } else {
                LOGGER.info("Nothing to do ... ");
            }
        } finally {
            lilyServiceDemo.destroy();
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

    public void findPublisher(String website) throws Exception {
        String query = "website_string:" + website;

        List<Publisher> publishers = lilyService.search(new SolrQuery(query), Publisher.class);

        LOGGER.info("Found " + publishers.size() + " publishers matching " + query + ":");
        for (Publisher publisher : publishers) {
            LOGGER.info(publisher);
        }
    }

    public void test() throws Exception {
        demoCreate();
        demoRetrieval();
        demoResolve();
        demoUpdate();
    //    demoDelete();
    }

    public void create(int count) throws Exception {
        int created = 0;
        while (created < count) {
            Publisher publisher = new Publisher("Publisher-" + created , "http://publisher" + created + ".publishingnetwork.com");

            if (!lilyService.exists(publisher)) {
                lilyService.create(publisher);
                LOGGER.info("Created " + publisher);
                created++;
            } else {
                LOGGER.info(publisher + " already existed");
            }
        }
    }

    public void demoCreate() {
        Author daan = new Author("Daan Gerits", "A developer with his mind in the cloud's.", "daan.gerits@gmail.com");
        Author wim = new Author("Wim Van Leuven", "A brilliant architect interested in BigData, Cloud, development principles and other geeky things", "wim.vanleuven@highestpoint.biz");

        Book myBook = new Book();
        myBook.setTitle("My Book");
        myBook.setLanguage("en");
        myBook.setPages(513);
        myBook.setAuthors(Arrays.asList(daan, wim));

        // -- create our entities
        if (!lilyService.exists(daan)) {
            lilyService.create(daan);
            LOGGER.info("Created " + daan);
        } else {
            LOGGER.info(daan + " already existed");
        }

        if (!lilyService.exists(wim)) {
            lilyService.create(wim);
            LOGGER.info("Created " + wim);
        } else {
            LOGGER.info(wim + " already existed");
        }

        if (!lilyService.exists(myBook)) {
            lilyService.create(myBook);
            LOGGER.info("Created " + myBook);
        } else {
            LOGGER.info(myBook + " already existed");
        }
    }

    public void demoRetrieval() {
        // -- get the book from lily
        Book book = lilyService.get(Book.class, "My Book");

        // -- print the details of our book
        LOGGER.info("Retrieved " + book);
    }

    public void demoResolve() {
        // -- get the record to resolve
        Book book = lilyService.get(Book.class, "My Book");

        // -- resolve additional information
        lilyService.resolve(book, "authors");

        // -- print the details of our book
        LOGGER.info("Resolved " + book);
    }

    public void demoUpdate() {
        // -- get the record to update
        Book book = lilyService.get(Book.class, "My Book");

        // -- change the book data
        book.setLanguage("nl");
        book.setPages(130);

        // -- update the details
        lilyService.update(book);

        // -- print the details of our book
        LOGGER.info("Updated " + book);
    }

    public void demoDelete() {
        // -- get the record to delete
        Book book = lilyService.get(Book.class, "My Book");

        // -- remove the book
        lilyService.delete(book);

        // -- try to get the book, this should result in an exception
        try {
            lilyService.get(Book.class, "My Book");

            // -- it would be incorrect if we got here
            LOGGER.error("Record was not deleted!");
        } catch (LilyException e) {
            LOGGER.info("Record deleted");
        }
    }
}
