package org.frogpond.service;

import org.apache.log4j.Logger;
import org.frogpond.LilyException;
import org.frogpond.annotation.LilyField;
import org.frogpond.metadata.LilyTypeHelper;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.metadata.store.MetadataStore;
import org.frogpond.model.Primitive;
import org.frogpond.model.PropertyEntry;
import org.frogpond.utils.LilyUtilities;
import org.frogpond.utils.MetadataUtilities;
import org.frogpond.service.resolvers.LinkFieldResolver;
import org.lilyproject.repository.api.*;

import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LilyRecordConverter {
    private MetadataStore metadataStore;
    private Repository repository;
    private LinkFieldResolver resolver;
    private static final Logger LOGGER = Logger.getLogger(LilyRecordConverter.class);

    public LilyRecordConverter() {
    }

    public LilyRecordConverter(MetadataStore metadataStore, Repository repository) {
        this.metadataStore = metadataStore;
        this.repository = repository;
    }

    public void copyIntoPojo(Record record, RecordTypeMetadata recordType, Object pojo) throws LilyException {
        for (RecordFieldMetadata recordFieldMetadata : recordType.getFields()) {
            QName fieldName = recordFieldMetadata.getFieldType().getFieldType().getName();

            try {
                // -- check if the record has the current field
                if (! record.hasField(fieldName)) continue;

                // -- get the record value
                Object recordValue = record.getField(fieldName);

                /* Resolving links here itself --vinay */
                if((recordFieldMetadata.getFieldType().getPrimitive() == Primitive.Link)
                        || (recordFieldMetadata.getFieldType().getPrimitive() == Primitive.List) 
                        || (recordFieldMetadata.getFieldType().getPrimitive() == Primitive.Set))
                {
                    recordValue = resolver.resolve(recordFieldMetadata, record);
                }
                // -- set the field value
                recordFieldMetadata.getAccessor().setValue(pojo, recordValue);
            } catch (Exception e) {
                throw new LilyException(String.format(
                        "Unable to copy data into field %s of record %s",
                        recordFieldMetadata.getFieldType().getFieldType().getName(),
                        recordFieldMetadata.getRecordType().getRecordType().getName()
                ), e);
            }
        }

        // -- we need to copy the recordId into the field annotated with @LilyId in case the field isn't annotated
        // -- with @LilyField too.
        PropertyEntry recordIdProperty = recordType.getRecordIdProperty();
        if (recordIdProperty.getAnnotation(LilyField.class) == null) {
            try {
                if (RecordId.class.isAssignableFrom(recordIdProperty.getType())) {
                    // -- set the field value
                    MetadataUtilities.getFieldAccessor(recordIdProperty).setValue(pojo, record.getId());
                } else if (String.class.isAssignableFrom(recordIdProperty.getType())) {
                    // -- set the field value
                    MetadataUtilities.getFieldAccessor(recordIdProperty).setValue(pojo, record.getId().toString());
                } else {
                    throw new Exception("The lily record id cannot be converted to a " + recordIdProperty.getType().getName());
                }
            } catch (Exception e) {
                throw new LilyException(String.format(
                        "Unable to copy the system generated id into field %s of record %s",
                        recordIdProperty.getName(),
                        recordType.getName()
                ), e);
            }
        }
    }
    
    //--vinay START
    public void copyIntoPojo(Map<RecordId, Object> retrievedRecords, Record record, RecordTypeMetadata recordType, Object pojo) throws LilyException {
        //First copy thispojo instance into map
        retrievedRecords.put(record.getId(), pojo);

        //System.out.println("COPYING INTO POJO:::"+pojo);

        for (RecordFieldMetadata recordFieldMetadata : recordType.getFields()) {
            QName fieldName = recordFieldMetadata.getFieldType().getFieldType().getName();


            try {
                // -- check if the record has the current field
                if (! record.hasField(fieldName))
                {
                    continue;
                }

                //if(java.util.Map.class.isAssignableFrom(recordFieldMetadata.getFieldType().getFieldType().getValueType().getType())) continue; //for now

                // -- get the record value
                Object recordValue = record.getField(fieldName);

                /* Resolving links here itself --vinay */
                if((recordFieldMetadata.getFieldType().getPrimitive() == Primitive.Link)
                        || (recordFieldMetadata.getFieldType().getPrimitive() == Primitive.List) 
                        || (recordFieldMetadata.getFieldType().getPrimitive() == Primitive.ENUM) 
                        || (recordFieldMetadata.getFieldType().getPrimitive() == Primitive.Set))
                {
                    recordValue = resolver.resolve(retrievedRecords, recordFieldMetadata, record);
                }
                // -- set the field value
                recordFieldMetadata.getAccessor().setValue(pojo, recordValue);
            } catch (Exception e) {
                throw new LilyException(String.format(
                        "Unable to copy data into field %s of record %s",
                        recordFieldMetadata.getFieldType().getFieldType().getName(),
                        recordFieldMetadata.getRecordType().getRecordType().getName()
                ), e);
            }
        }

        // -- we need to copy the recordId into the field annotated with @LilyId in case the field isn't annotated
        // -- with @LilyField too.
        PropertyEntry recordIdProperty = recordType.getRecordIdProperty();
        if (recordIdProperty.getAnnotation(LilyField.class) == null) {
            try {
                if (RecordId.class.isAssignableFrom(recordIdProperty.getType())) {
                    // -- set the field value
                    MetadataUtilities.getFieldAccessor(recordIdProperty).setValue(pojo, record.getId());
                } else if (String.class.isAssignableFrom(recordIdProperty.getType())) {
                    // -- set the field value
                    MetadataUtilities.getFieldAccessor(recordIdProperty).setValue(pojo, record.getId().toString());
                } else {
                    throw new Exception("The lily record id cannot be converted to a " + recordIdProperty.getType().getName());
                }
            } catch (Exception e) {
                throw new LilyException(String.format(
                        "Unable to copy the system generated id into field %s of record %s",
                        recordIdProperty.getName(),
                        recordType.getName()
                ), e);
            }
        }
    }
    //--vinay END
    public void copyIntoRecord(Record record, RecordTypeMetadata recordType, Object pojo) throws LilyException {
        record.setRecordType(recordType.getName(), recordType.getVersion());

        for (RecordFieldMetadata recordFieldMetadata : recordType.getFields()) {
            try {

                Object pojoValue = recordFieldMetadata.getAccessor().getValue(pojo);

               if((pojoValue != null) && (pojoValue instanceof HashMap))
               {
                   LilyField fieldAnnotation = recordFieldMetadata.getFieldType().getProperty().getAnnotation(LilyField.class);
                   Class keyJavaTypeClass;
                   Class valueJavaTypeClass;
                   String[] javaTypeCls;

                   if(fieldAnnotation.javaTypeName() == null)
                       throw new LilyException("A Java type should be declared when a field holds a generic Map.");
                   else
                   {
                       javaTypeCls = fieldAnnotation.javaTypeName().split(",");
                   }
                   keyJavaTypeClass = Class.forName(javaTypeCls[0]);
                   valueJavaTypeClass = Class.forName(javaTypeCls[1]);

                   RecordTypeMetadata keyRecordTypeMetadata = null;
                   RecordTypeMetadata valueRecordTypeMetadata = null;

                   if(LilyTypeHelper.getLilyPrimitive(keyJavaTypeClass.getName()) == null)// -- Non Primitive
                   {
                       keyRecordTypeMetadata = metadataStore.getRecordTypeMetadata(keyJavaTypeClass, true);
                   }
                   if(LilyTypeHelper.getLilyPrimitive(valueJavaTypeClass.getName()) == null)// -- Non Primitive
                   {
                       valueRecordTypeMetadata = metadataStore.getRecordTypeMetadata(valueJavaTypeClass, true);
                   }

                   pojoValue = createMapEntryCollection(keyRecordTypeMetadata, valueRecordTypeMetadata, (Map)pojoValue);


               }

                // -- in case of a link field, we need to construct a link
               else if ((pojoValue != null) && ((recordFieldMetadata.getPrimitive() == Primitive.Link) ||
                                                (recordFieldMetadata.getPrimitive() == Primitive.List) ||
                                                (recordFieldMetadata.getPrimitive() == Primitive.Set))) {
                    if (recordFieldMetadata.isMultiValue()) {
                        // -- get the lilyField annotation
                        LilyField fieldAnnotation = recordFieldMetadata.getFieldType().getProperty().getAnnotation(LilyField.class);

                        Class javaTypeCls;

                        // -- check if the field annotation holds a java type
                        if (fieldAnnotation.javaType() == Object.class)
                        {
                            if((fieldAnnotation.javaTypeName() == null) || fieldAnnotation.javaTypeName().equals(""))
                            {
                                String javaTypeClsStr = getJavaTypeFromPOJO(recordFieldMetadata, pojoValue);
                                if(javaTypeClsStr == null)
                                    //throw new LilyException("A Java type should be declared when a field holds a generic collection.");
                                    continue;
                                javaTypeCls = Class.forName(javaTypeClsStr);
                            }
                            else
                            {
                                javaTypeCls = Class.forName(fieldAnnotation.javaTypeName());
                            }
                        }
                        else
                            javaTypeCls = fieldAnnotation.javaType();
                        if(LilyTypeHelper.getLilyPrimitive(javaTypeCls.getName()) == null)// --vinay If Non-Primitive List
                        {
                            RecordTypeMetadata linkRecordTypeMetadata;
                            // -- get the recordTypeMetadata for the type
                            linkRecordTypeMetadata = metadataStore.getRecordTypeMetadata(javaTypeCls, true);
                            pojoValue = createLinkCollection(linkRecordTypeMetadata, (Collection<?>) pojoValue);
                        }

                    } else {
                        /*
                        // -- get the recordTypeMetadata for the type
                        RecordTypeMetadata linkRecordTypeMetadata = metadataStore.getRecordTypeMetadata(
                                recordFieldMetadata.getFieldType().getProperty().getType(), true
                        );
                        */
                        //--vinay
                        RecordTypeMetadata linkRecordTypeMetadata = metadataStore.getRecordTypeMetadata(
                                pojoValue.getClass(), true
                        );

                        //--vinay

                        pojoValue = createLink(linkRecordTypeMetadata, pojoValue);
                    }
                }

                //System.out.println("SETTING POJO FIELD:"+recordFieldMetadata.getFieldType().getFieldType().getName()+"::::"+pojoValue);

                if (pojoValue == null) continue;

                // -- set the record value
                record.setField(recordFieldMetadata.getFieldType().getFieldType().getName(), pojoValue);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LilyException(String.format(
                        "Unable to copy data from field %s of record %s",
                        recordFieldMetadata.getFieldType().getFieldType().getName(),
                        recordFieldMetadata.getRecordType().getRecordType().getName()
                ), e);
            }
        }


    }

    protected List<Map.Entry> createMapEntryCollection(RecordTypeMetadata keyReacordTypeMetadata, RecordTypeMetadata valueRecordTypeMetadata, Map map)
    {
        List<Map.Entry> mapEntries = new ArrayList<Map.Entry>();
        
        Set<Map.Entry> entrySet = map.entrySet();

        for(Map.Entry entry : entrySet)
        {
            mapEntries.add(createMapEntry(keyReacordTypeMetadata, valueRecordTypeMetadata, entry));
        }

        return mapEntries;
    }

    protected Map.Entry createMapEntry(RecordTypeMetadata keyRecordTypeMetadata, RecordTypeMetadata valueRecordTypeMetadata, Map.Entry<?,?> entry)
    {

        log(" Creating Map Entry .... ");
        RecordId keyRecordId = null;
        RecordId valueRecordId = null;
        Object key = null;
        Object value = null;
        if(keyRecordTypeMetadata == null)
        {
            //throw new LilyException("Null RecordTypeMetadata for KEY entry of MAP field");
            // Primitive Key
            log(" KEY IS PRIMITIVE .. ");
            key = entry.getKey();
        }
        else 
        {
            key = new Link(LilyUtilities.getRecordId(repository, keyRecordTypeMetadata, entry.getKey()));
        }
        if(valueRecordTypeMetadata == null)
        {
            //throw new LilyException("Null RecordTypeMetadata for VALUE entry of MAP field");
            //Primitive
            log(" VALUE IS PRIMITIVE .. ");
            value = entry.getValue();
        }
        else
        {
            value = new Link(LilyUtilities.getRecordId(repository, valueRecordTypeMetadata, entry.getValue()));
        }

        // -- construct the link
        return new AbstractMap.SimpleEntry(key, value);
    }

    protected List<Link> createLinkCollection(RecordTypeMetadata recordTypeMetadata, Collection<?> collection) throws LilyException {
        List<Link> links = new ArrayList<Link>();

        for (Object collectionItem : collection) {
            links.add(createLink(recordTypeMetadata, collectionItem));
        }

        return links;
    }

    protected Link createLink(RecordTypeMetadata recordTypeMetadata, Object fieldValue) throws LilyException {
        if (fieldValue instanceof Link) {
            return ((Link) fieldValue);
        } 
        else 
        {
            RecordId recordId = null;
            

//--vinay
           /* Check if fieldValue is of Primitive type, */
            if(Primitive.primitiveOf(fieldValue.getClass().getName()) != Primitive.None)
            {
                recordId = LilyUtilities.createRecordId(repository.getIdGenerator(), fieldValue);
            }
            else if(Primitive.primitiveOf(fieldValue.getClass()) != Primitive.None)
            {
                recordId = LilyUtilities.createRecordId(repository.getIdGenerator(), fieldValue);
            }
            
            else if(recordTypeMetadata == null)
            {
                throw new LilyException("Null RecordTypeMetadata for Link field");
            }
            else
            {
                // -- get the recordId of the pojo value
                recordId = LilyUtilities.getRecordId(repository, recordTypeMetadata, fieldValue);
            }
//--vinay

            // -- construct the link
            return new Link(recordId);
        }
    }

    public MetadataStore getMetadataStore() {
        return metadataStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    //--vinay
    public void setLinkFieldResolver(LinkFieldResolver lfr)
    {
        this.resolver = lfr;
    }

    public LinkFieldResolver getLinkFieldResolver()
    {
        return resolver;
    }

    public  static String getJavaTypeFromPOJO(RecordFieldMetadata recordFieldMetadata, Object pojoValue)
    {
        String javaTypeCls = null;
        try
        {
            if(pojoValue != null)
            {
                if(recordFieldMetadata.getPrimitive() == Primitive.Link)
                {
                    javaTypeCls = pojoValue.getClass().getName();
                }
                else if(recordFieldMetadata.getPrimitive() == Primitive.List)
                {
                    List pojoCollection = (List)pojoValue;
                    Object pojo = pojoCollection.get(0);
                    javaTypeCls = pojo.getClass().getName();
                }
            }
        }
        catch(Exception ex)
        {
            log("No elements in List...");
            return null;
        }
        return javaTypeCls;
    }

    private static void log(String msg)
    {
        System.out.println(msg);
        LOGGER.debug(msg);
    }
}
