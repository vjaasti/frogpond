package org.frogpond.service;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.frogpond.LilyException;
import org.frogpond.SearchException;
import org.frogpond.metadata.extractor.ReflectiveMetadataExtractor;
import org.frogpond.metadata.store.DefaultMetadataStoreFactory;
import org.frogpond.metadata.store.MetadataStore;
import org.frogpond.model.Primitive;
import org.frogpond.service.resolvers.DefaultFieldResolver;
import org.frogpond.service.resolvers.DefaultFieldResolverManager;
import org.frogpond.service.resolvers.FieldResolver;
import org.frogpond.service.resolvers.LinkFieldResolver;
import org.lilyproject.client.LilyClient;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RecordId;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimpleLilyService implements LilyService {
    private static final Logger LOGGER = Logger.getLogger(SimpleLilyService.class);
    public static final int DEFAULT_TIMEOUT = 60000;

    private LilyClient lilyClient;
    private Repository repository;
    private BasicLilyService serviceDelegate;

    private String zookeeperUrl;
    private String solrUrl;

    @PostConstruct
    public void initialize() throws LilyException {
        if (solrUrl == null) throw new LilyException("No SolrUrl has been defined");
        if (zookeeperUrl == null) throw new LilyException("No ZookeeperUrl has been defined");

        try {
            // -- create the lily client
            lilyClient = new LilyClient(zookeeperUrl, DEFAULT_TIMEOUT);
            repository = lilyClient.getRepository();

            // -- construct the metadata store
            DefaultMetadataStoreFactory metadataStoreFactory = new DefaultMetadataStoreFactory();
            metadataStoreFactory.setMetadataExtractor(new ReflectiveMetadataExtractor());
            metadataStoreFactory.setBasePackage("org.frogpond.demo.model");
            MetadataStore metadataStore = metadataStoreFactory.create();

            // -- create the record converter
            LilyRecordConverter recordConverter = new LilyRecordConverter(metadataStore, repository);

            // -- create the link field resolver
            LinkFieldResolver linkFieldResolver = new LinkFieldResolver();
            linkFieldResolver.setRecordConverter(recordConverter);
            linkFieldResolver.setRepository(repository);
            linkFieldResolver.setMetadataStore(metadataStore);

            // -- create the field resolver
            DefaultFieldResolverManager fieldResolverManager = new DefaultFieldResolverManager();
            fieldResolverManager.setDefaultResolver(new DefaultFieldResolver());
            fieldResolverManager.setResolvers(Collections.singletonMap(Primitive.Link, (FieldResolver) linkFieldResolver));

            //--vinay
            recordConverter.setLinkFieldResolver(linkFieldResolver);

            // -- create the lily service
            serviceDelegate = new BasicLilyService();
            serviceDelegate.setRepository(repository);
            serviceDelegate.setFieldResolverManager(fieldResolverManager);
            serviceDelegate.setMetadataStore(metadataStore);
            serviceDelegate.setRecordConverter(recordConverter);
            serviceDelegate.setSolrServer(new CommonsHttpSolrServer(solrUrl));

        } catch (Exception e) {
            throw new LilyException("Unable to initialize the " + SimpleLilyService.class.getSimpleName(), e);
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (repository != null) repository.close();
        } catch (IOException e) {
            LOGGER.warn("Unable to close the repository", e);
        }

        try {
            if (lilyClient != null) lilyClient.close();
        } catch (IOException e) {
            LOGGER.warn("Unable to close the lily client", e);
        }
    }

    @Override
    public <T> List<T> search(SolrQuery query, Class<T> resultType) throws SearchException {
        return getServiceDelegate().search(query, resultType);
    }

    @Override
    public void resolve(Object pojo, String... links) throws LilyException {
        getServiceDelegate().resolve(pojo, links);
    }

    @Override
    public void resolve(RecordId recordId, Object pojo, String... links) throws LilyException {
        getServiceDelegate().resolve(recordId, pojo, links);
    }

    @Override
    public <T> T get(Class<T> resultType, String id) throws LilyException {
        return getServiceDelegate().get(resultType, id);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Long version) throws LilyException {
        return getServiceDelegate().get(resultType, id, version);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants) throws LilyException {
        return getServiceDelegate().get(resultType, id, recordVariants);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants, Long version) throws LilyException {
        return getServiceDelegate().get(resultType, id, recordVariants, version);
    }

    @Override
    public boolean exists(Object pojo) throws LilyException {
        return getServiceDelegate().exists(pojo);
    }

    @Override
    public void store(Object pojo) throws LilyException {
        getServiceDelegate().store(pojo);
    }

    @Override
    public void create(Object pojo) throws LilyException {
        getServiceDelegate().create(pojo);
    }

    @Override
    public void update(Object pojo) throws LilyException {
        getServiceDelegate().update(pojo);
    }

    @Override
    public void delete(Object pojo) throws LilyException {
        getServiceDelegate().delete(pojo);
    }

    protected BasicLilyService getServiceDelegate() {
        if (serviceDelegate == null) {
            throw new LilyException("This " + SimpleLilyService.class.getSimpleName() + " has not been initialized.");
        }

        return serviceDelegate;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
    }

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }
}
