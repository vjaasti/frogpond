package org.frogpond.service.resolvers;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.frogpond.LilyException;
import org.frogpond.metadata.LilyTypeHelper;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.metadata.store.MetadataStore;
import org.frogpond.model.Primitive;
import org.frogpond.service.LilyRecordConverter;
import org.frogpond.service.SilentObjectCreator;
import org.lilyproject.repository.api.*;
import org.springframework.core.CollectionFactory;

import java.util.Collection;

public class LinkFieldResolver implements FieldResolver {
    private Repository repository;
    private MetadataStore metadataStore;
    private LilyRecordConverter recordConverter;
    private static final Logger LOGGER = Logger.getLogger(LinkFieldResolver.class);

    @Override
    public Primitive getPrimitive() {
        return Primitive.Link;
    }

    @Override
    public Object resolve(RecordFieldMetadata recordField, Record record) {
        Object obj = record.getField(recordField.getQName());


        if (obj instanceof Collection) {
            Collection collection = CollectionFactory.createCollection(recordField.getFieldType().getProperty().getType(), ((Collection) obj).size());

            for (Object item : (Collection) obj) {
                if (item instanceof Link)
                    collection.add(resolveLink(recordField, record, (Link) item));
                else
                    collection.add(item);
            }

            return collection;
        } else {
            return resolveLink(recordField, record, (Link) obj);
        }
    }

    public Map.Entry resolveMapEntry(RecordFieldMetadata recordField, Record record, Map.Entry me)
    {
        if(me == null) return null;

        //resolve MapEntry
        //RecordId[] mapEntryIds = me.resolve(record, repository.getIdGenerator());
        //Object key = resolveId(mapEntryIds[0]);
        //Object value = resolveId(mapEntryIds[1]);
        
        Object key = me.getKey();
        Object value = me.getValue();

        if(key instanceof Link)
        {
            key = resolveId(((Link)key).resolve(record, repository.getIdGenerator()));
        }

        if(value instanceof Link)
        {
            value = resolveId(((Link)value).resolve(record, repository.getIdGenerator()));
        }

        if((key != null))
            return new AbstractMap.SimpleEntry(key, value);
        return null;

    }

    private Object resolveId(RecordId recordId)
    {
        if(recordId == null) return null;
        try {
            // -- get the record from ID
            Record linkedRecord = repository.read(recordId);

            // -- get the metadata for the linked record type
            QName linkedRecordTypeQName = linkedRecord.getRecordTypeName(Scope.VERSIONED);
            RecordTypeMetadata linkedRecordTypeMetadata = metadataStore.getRecordTypeMetadata(linkedRecordTypeQName);

            // -- check if the metadata was available
            if (linkedRecordTypeMetadata == null)
                throw new LilyException("No recordType found for " + linkedRecord.getRecordTypeName());

            // -- convert the linked record into an object
            Object result = SilentObjectCreator.create(linkedRecordTypeMetadata.getJavaType());
            log("----------------------------------> Created ...:"+result);
            
            recordConverter.copyIntoPojo(linkedRecord, linkedRecordTypeMetadata, result);
            
            // -- return the resulting object
            return result;
        } catch (Exception e) {
            return null;
        }

    }



    public Object resolveLink(RecordFieldMetadata recordField, Record record, Link link) {
        // -- check if a link is set
        if (link == null) return null;

        // -- resolve the link
        RecordId linkedRecordId = link.resolve(record, repository.getIdGenerator());

        if (linkedRecordId == null) return null;



        try {
            // -- get the linked record
            Record linkedRecord = repository.read(linkedRecordId);

            // -- get the metadata for the linked record type
            QName linkedRecordTypeQName = linkedRecord.getRecordTypeName(Scope.VERSIONED);
            RecordTypeMetadata linkedRecordTypeMetadata = metadataStore.getRecordTypeMetadata(linkedRecordTypeQName);

            // -- check if the metadata was available
            if (linkedRecordTypeMetadata == null)
                throw new LilyException("No recordType found for " + linkedRecord.getRecordTypeName());

            // -- convert the linked record into an object
            Object result = SilentObjectCreator.create(linkedRecordTypeMetadata.getJavaType());
            log("----------------------------------> Created ...:"+result);
            
            recordConverter.copyIntoPojo(linkedRecord, linkedRecordTypeMetadata, result);
            
            // -- return the resulting object
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    //--vinay START
    @Override
    public Object resolve(Map<RecordId, Object> retrievedRecords, RecordFieldMetadata recordField, Record record) {
        // -- get the field
        Object obj = record.getField(recordField.getQName());


        if((Map.class.isAssignableFrom(recordField.getFieldType().getProperty().getType())) && !(Properties.class.getName().equals(recordField.getFieldType().getProperty().getType().getName())))
        {

            Map map = CollectionFactory.createMap(recordField.getFieldType().getProperty().getType(), ((Collection) obj).size());

            for(Object item : (Collection) obj)  //List of MapEntries
            {
                if (item instanceof Map.Entry)
                {
                    Map.Entry me = resolveMapEntry(recordField, record,  (Map.Entry)item);
                    if(me != null)
                        map.put(me.getKey(), me.getValue());
            }
            }


                return map;
        }
        if (obj instanceof Collection) {
            Collection collection = CollectionFactory.createCollection(recordField.getFieldType().getProperty().getType(), ((Collection) obj).size());

            for (Object item : (Collection) obj) {
                if (item instanceof Link)
                    collection.add(resolveLink(retrievedRecords, recordField, record, (Link) item));
                else
                    collection.add(item);
            }

            return collection;
        }
        else if(LilyTypeHelper.isEnum(recordField.getFieldType().getProperty().getType().getName()))
        {
            log("------------------------------------------ Resolving ENUM:"+obj);
            try
            {
                String str = (String) obj;
                if(str == null) return null;

                if(str.indexOf(":") == -1) return null;

                String clsName = str.substring(0, str.indexOf(":"));
                Class enumCls = Class.forName(clsName);
                String value = str.substring(str.indexOf(":")+1, str.length());
                return Enum.valueOf(enumCls, value);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return null;
            }

        }
        else {
            return resolveLink(retrievedRecords, recordField, record, (Link) obj);
        }
    }

    public Object resolveLink(Map<RecordId, Object> retrievedRecords, RecordFieldMetadata recordField, Record record, Link link) {
        // -- check if a link is set
        if (link == null) return null;

        // -- resolve the link
        RecordId linkedRecordId = link.resolve(record, repository.getIdGenerator());

        if (linkedRecordId == null) return null;


        //Check if this record is already retrieved from repo. if yes return pojo from hashmap
        if(retrievedRecords.get(linkedRecordId) !=null)
        {
            return retrievedRecords.get(linkedRecordId);
        }

        log("RESOLVING LINK...............:"+linkedRecordId);

        try {
                //System.out.println(" LINK ID:"+new String(linkedRecordId.toBytes())+"...."+linkedRecordId.getTableSpace());
            // -- get the linked record
            Record linkedRecord = repository.read(linkedRecordId);

            // -- get the metadata for the linked record type
            QName linkedRecordTypeQName = linkedRecord.getRecordTypeName(Scope.VERSIONED);
            RecordTypeMetadata linkedRecordTypeMetadata = metadataStore.getRecordTypeMetadata(Class.forName(linkedRecordTypeQName.getNamespace()), true);
            //RecordTypeMetadata linkedRecordTypeMetadata = metadataStore.getRecordTypeMetadata(linkedRecordTypeQName);

            // -- check if the metadata was available
            if (linkedRecordTypeMetadata == null)
                throw new LilyException("No recordType found for " + linkedRecord.getRecordTypeName());

            // -- convert the linked record into an object
            Object result = SilentObjectCreator.create(linkedRecordTypeMetadata.getJavaType());
            log("----------------------------------> Created ...:"+result);
            
            recordConverter.copyIntoPojo(retrievedRecords, linkedRecord, linkedRecordTypeMetadata, result);

            // -- return the resulting object
            return result;
        } catch (Exception e) {
            log("Error in Resolving Link..."+recordField.getFieldType().getProperty().getType().getName());
            e.printStackTrace();
            return null;
        }
    }
    //--vinay END

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setRecordConverter(LilyRecordConverter recordConverter) {
        this.recordConverter = recordConverter;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    private static void log(String msg)
    {
        System.out.println(msg);
        LOGGER.debug(msg);
    }
}
