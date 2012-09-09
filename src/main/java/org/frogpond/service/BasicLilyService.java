package org.frogpond.service;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.frogpond.LilyException;
import org.frogpond.SearchException;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.metadata.store.MetadataStore;
import org.frogpond.service.resolvers.FieldResolver;
import org.frogpond.service.resolvers.FieldResolverManager;
import org.frogpond.utils.LilyUtilities;
import org.frogpond.utils.MetadataUtilities;
import org.lilyproject.repository.api.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class BasicLilyService implements LilyService {
    private static final Logger LOGGER = Logger.getLogger(BasicLilyService.class);

    private LilyRecordConverter recordConverter;
    private Repository repository;
    private MetadataStore metadataStore;
    private FieldResolverManager fieldResolverManager;
    private SolrServer solrServer;

    @Override
    public <T> List<T> search(SolrQuery query, Class<T> resultType) throws SearchException {
        try {
            // -- perform the query
            QueryResponse response = solrServer.query(query);

            // -- get the record type metadata
            RecordTypeMetadata recordTypeMetadata = metadataStore.getRecordTypeMetadata(resultType);
            if (recordTypeMetadata == null) throw new SearchException("No metadata found for " + resultType);

            List<RecordId> lilyRecordIds = new ArrayList<RecordId>();
            //String tableSpace = LilyUtilities.getTableSpace();
            //System.out.println("TableSpace for Searching:"+tableSpace);

            for (SolrDocument document : response.getResults()) {
                // -- get the record id
                Object objRecordId = document.getFieldValue("lily.id");
                String recordId = (objRecordId == null) ? null : (String) objRecordId;

                // -- add the record id to the list so we can retrieve the record ids later on
                //RecordId recordId = repository.getIdGenerator().fromString(recordId);
                //recordId.setTableSpace(tableSpace);
                lilyRecordIds.add(LilyUtilities.getRecordId(repository, recordId));
            }


            // -- get the records for which we found the id's
            List<Record> records = repository.read(lilyRecordIds);

            // -- iterate the records and convert them into real types
            List<T> lilyBeans = new ArrayList<T>();
            for (Record record : records) {
                // -- create a new bean instance
                //T bean = resultType.newInstance();
                T bean = SilentObjectCreator.create(resultType);

                // -- check which fields can be copied into the bean
                boolean filled = false;
                Map<RecordId, Object> retrievedRecords = new HashMap<RecordId, Object>();
                for (Scope scope : Scope.values()) {
                    if (recordTypeMetadata.getName().equals(record.getRecordTypeName(scope))) {
                        //recordConverter.copyIntoPojo(record, recordTypeMetadata, bean); //--vinay
                        // -- convert the record to the correct type
                        recordConverter.copyIntoPojo(retrievedRecords, record, recordTypeMetadata, bean);
                        retrievedRecords.clear();

                        filled = true;
                    }
                }

                if (filled)
                    lilyBeans.add(bean);
            }

            return lilyBeans;
        } catch (Exception e) {
            throw new SearchException(
                    "Unable to retrieve the records from lily.", e
            );
        }
    }

    //--vinay
    public <T> List<T> search(Class<T> resultType, String query)
    {
        return search(new SolrQuery(query), resultType);
    }
    //--vinay

    @Override
    public void resolve(Object pojo, String... links) throws LilyException {
        if (pojo == null) throw new LilyException("Unable to resolve 'null'");

        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

        // -- get the record id from the pojo
        RecordId recordId = LilyUtilities.getRecordId(repository, recordType, pojo);

        resolve(recordId, pojo, links);

    }


    public void resolve(RecordId recordId, Object pojo, String... links) throws LilyException {

        try {
            // -- get the record with the discovered id
            Record record = repository.read(recordId);
            RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

            // -- iterate the links
            for (String link : links) {
                try {
                    // -- try to retrieve the field
                    QName fieldName = MetadataUtilities.getQualifiedName(pojo.getClass().getDeclaredField(link));

                    // -- get the recordField
                    RecordFieldMetadata recordFieldMetadata = recordType.getField(fieldName);
                    if (recordFieldMetadata == null) throw new NoSuchFieldException();

                    // -- check the field type
                    ValueType valueType = recordFieldMetadata.getFieldType().getFieldType().getValueType();

                    if (valueType.isMultiValue() != recordFieldMetadata.isMultiValue())
                        throw new Exception("The field type of the record could not be stored within the field type of the object since one of them is a collection.");

                    // -- get the fieldResolver
                    FieldResolver resolver = fieldResolverManager.getResolver(recordFieldMetadata.getPrimitive());
                    if (resolver == null)
                        throw new Exception("No field resolver could be found for primitive " + recordFieldMetadata.getPrimitive());

                    // -- resolve
                    Object result = resolver.resolve(recordFieldMetadata, record);

                    // -- set the result
                    recordFieldMetadata.getAccessor().setValue(pojo, result);

                } catch (NoSuchFieldException nsfe) {
                    LOGGER.warn("Not resolving field with name " + link + " because no such field was found!");
                } catch (Exception e) {
                    LOGGER.warn("Not resolving field with name " + link, e);
                }
            }
        } catch (Exception e) {
            throw new LilyException("Unable to resolve links", e);
        }
    }

    @Override
    public <T> T get(Class<T> resultType, String id) throws LilyException {
        return get(resultType, id, null, null);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Long version) throws LilyException {
        return get(resultType, id, null, version);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants) throws LilyException {
        return get(resultType, id, recordVariants, null);
    }

    @Override
    public <T> T get(Class<T> resultType, String id, Map<String, String> recordVariants, Long version) throws LilyException {
        IdGenerator generator = repository.getIdGenerator();

        // -- create a new record id
        RecordId recordId = (recordVariants == null) ? generator.newRecordId(id) : generator.newRecordId(id, recordVariants);

        // -- return the object with the given record id
        return get(resultType, recordId, version);
    }

    @Override
    public boolean exists(Object pojo) throws LilyException {
        RecordTypeMetadata metadata = metadataStore.getRecordTypeMetadata(pojo.getClass());

        RecordId recordId = LilyUtilities.getRecordId(repository, metadata, pojo);

        try {
            Record result = repository.read(recordId);

            return (result != null);
        } catch (VersionNotFoundException e) {
            return false;
        } catch (RecordNotFoundException e) {
            return false;
        } catch (Exception e) {
            throw new LilyException(e);
        }
    }

    protected <T> T get(Class<T> resultType, RecordId recordId, Long version) throws LilyException {
        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(resultType);

        if(recordType == null) 
        {
            recordType = metadataStore.getRecordTypeMetadata(resultType, true);
        }

        try {
            // -- get the record from the lily repository
            Record record = (version == null) ? repository.read(recordId) : repository.read(recordId, version);

            // -- create the result object
            //T result = resultType.newInstance();
            T result = SilentObjectCreator.create(resultType);


            // -- convert the record to the correct type
            //recordConverter.copyIntoPojo(record, recordType, result);
            
            //--vinay START
            
            Map<RecordId, Object> retrievedRecords = new HashMap<RecordId, Object>();
            // -- convert the record to the correct type
            recordConverter.copyIntoPojo(retrievedRecords, record, recordType, result);

            retrievedRecords.clear();

            //--vinay END
            // -- return the result
            return result;
        }
        catch (RecordNotFoundException e) {
                        return null;
        } catch (Exception e) {
            throw new LilyException(e);
        }
    }

    @Override
    public void store(Object pojo) throws LilyException {
        // -- return if no pojo was given
        if (pojo == null) return;

        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

        try {
            // -- get the record id from the pojo
            RecordId recordId = LilyUtilities.getRecordId(repository, recordType, pojo);

            // -- create a new record
            Record resultRecord = repository.newRecord(recordId);

            // -- copy the data from the pojo into the record
            recordConverter.copyIntoRecord(resultRecord, recordType, pojo);

            // -- try to retrieve the record with the given id
            resultRecord = repository.createOrUpdate(resultRecord);

            // -- copy the result values back into the pojo
            recordConverter.copyIntoPojo(resultRecord, recordType, pojo);

        } catch (Exception e) {
            throw new LilyException(
                    String.format("Unable to store POJO of %s", pojo.getClass()),
                    e
            );
        }
    }

    @Override
    public void create(Object pojo) throws LilyException {
        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

        // -- get the record id
        RecordId recordId = LilyUtilities.getRecordId(repository, recordType, pojo);

        // -- create the record
        create(recordType, recordId, pojo);
    }

    protected <T> void create(RecordTypeMetadata recordType, RecordId recordId, T pojo) throws LilyException {
        try {
            // -- create a new record
            Record resultRecord = repository.newRecord(recordId);

            // -- copy the data from the pojo into the record
            recordConverter.copyIntoRecord(resultRecord, recordType, pojo);

            // -- create the record with the given id
            //repository.create(resultRecord);
            //--vinay
            repository.createOrUpdate(resultRecord);
            //--vinay
        } catch (Exception e) {
            throw new LilyException(
                    String.format("Unable to create an object of %s", pojo.getClass()),
                    e
            );
        }
    }

    @Override
    public void update(Object pojo) throws LilyException {
        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

        // -- get the record id
        RecordId recordId = LilyUtilities.getRecordId(repository, recordType, pojo);

        try {
            // -- get the record from the repository
            Record record = repository.read(recordId);

            // -- update the record
            update(recordType, record, pojo);
        } catch (Exception e) {
            throw new LilyException(
                    String.format("Unable to update POJO of %s", pojo.getClass()),
                    e
            );
        }
    }

    protected <T> void update(RecordTypeMetadata recordType, Record record, T pojo) throws LilyException {
        try {
            // -- copy the data from the pojo into the record
            recordConverter.copyIntoRecord(record, recordType, pojo);

            // -- update the record with the given id
            repository.update(record);
        } catch (Exception e) {
            throw new LilyException(
                    String.format("Unable to update POJO of %s", pojo.getClass()),
                    e
            );
        }
    }

    @Override
    public void delete(Object pojo) throws LilyException {
        // -- get the recordType for the given pojo
        RecordTypeMetadata recordType = metadataStore.getRecordTypeMetadata(pojo.getClass());

        // -- get the record id
        RecordId recordId = LilyUtilities.getRecordId(repository, recordType, pojo);

        try {
            // -- delete the record with the given id
            repository.delete(recordId);
        } catch (Exception e) {
            throw new LilyException(
                    String.format("Unable to remove POJO of type %s", pojo.getClass()),
                    e
            );
        }
    }

    public LilyRecordConverter getRecordConverter() {
        return recordConverter;
    }

    public void setRecordConverter(LilyRecordConverter recordConverter) {
        this.recordConverter = recordConverter;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public MetadataStore getMetadataStore() {
        return metadataStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public FieldResolverManager getFieldResolverManager() {
        return fieldResolverManager;
    }

    public void setFieldResolverManager(FieldResolverManager fieldResolverManager) {
        this.fieldResolverManager = fieldResolverManager;
    }

    public SolrServer getSolrServer() {
        return solrServer;
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void setSolrServer(String solrUrl) 
        throws MalformedURLException {
        this.solrServer = new CommonsHttpSolrServer(solrUrl);
    }
}
