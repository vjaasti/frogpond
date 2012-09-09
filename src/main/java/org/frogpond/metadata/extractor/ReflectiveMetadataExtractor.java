package org.frogpond.metadata.extractor;

import org.apache.log4j.Logger;
import org.frogpond.MetadataException;
import org.frogpond.annotation.*;
import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.LilyTypeHelper;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.model.FieldIndex;
import org.frogpond.model.PropertyEntry;
import org.frogpond.model.RecordIndex;
import org.frogpond.utils.MetadataUtilities;
import org.lilyproject.repository.api.QName;
import org.springframework.core.annotation.AnnotationUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectiveMetadataExtractor implements MetadataExtractor {
    private static final Logger LOGGER = Logger.getLogger(ReflectiveMetadataExtractor.class);

    @Override
    public MetadataContainer extract(Class<?> objectType) throws MetadataException {
        MetadataContainer result = new MetadataContainer();

        parseType(result, objectType);
        parseMembers(result, objectType);

        return result;
    }

    protected void parseType(MetadataContainer container, Class<?> objectType) throws MetadataException {
        // -- get the LilyRecord annotation from the object type
        LilyRecord recordAnnotation = MetadataUtilities.getAnnotation(objectType, LilyRecord.class);

        // -- check if a record annotation was found
        if (recordAnnotation != null) 
        {
            RecordTypeMetadata recordTypeMetadata = new RecordTypeMetadata();

            // -- fill in the recordTypeMetadata properties
            recordTypeMetadata.setJavaType(objectType);

            // -- set the record type
            recordTypeMetadata.setRecordType(LilyTypeHelper.createRecordType(objectType));
            recordTypeMetadata.getRecordType().setVersion(recordAnnotation.value());
            // get the index
            LilyRecordIndex indexAnnotation = MetadataUtilities.getAnnotation(objectType, LilyRecordIndex.class, false);
            if (indexAnnotation != null) {
                recordTypeMetadata.setIndex(new RecordIndex(
                            indexAnnotation.indexVariantPattern(),
                            indexAnnotation.indexVersionTags()
                            ));
            }


            container.setRecordTypeMetadata(recordTypeMetadata);

        } else {
            LOGGER.debug("Skipping java " + objectType + " since no LilyRecord annotation was found");
        }
    }

    protected void parseMembers(MetadataContainer container, Class<?> objectType) throws MetadataException {
        // -- get the properties
        Map<QName, PropertyEntry> properties = new HashMap<QName, PropertyEntry>();
        parseFields(properties, objectType);
        parseProperties(properties, objectType);

        // -- iterate the properties
        for (QName propertyName : properties.keySet()) {
            // -- get the propertyEntry
            PropertyEntry propertyEntry = properties.get(propertyName);

            // -- get the field annotation
            LilyField fieldAnnotation = propertyEntry.getAnnotation(LilyField.class);
            if (fieldAnnotation != null) {
                // -- add the field type metadata
                FieldTypeMetadata fieldTypeMetadata = new FieldTypeMetadata(
                        LilyTypeHelper.getFieldType(propertyEntry),
                        propertyEntry
                );
                container.addFieldTypeMetadata(fieldTypeMetadata);

                // -- add the fieldTypeEntry metadata to the record Type Metadata
                RecordFieldMetadata recordFieldMetadata = new RecordFieldMetadata(
                        container.getRecordTypeMetadata(),
                        fieldTypeMetadata,
                        MetadataUtilities.getFieldAccessor(propertyEntry),
                        fieldAnnotation.mandatory()
                );

                // -- check if a the field is indexed
                LilyFieldIndex indexAnnotation = propertyEntry.getAnnotation(LilyFieldIndex.class);
                if (indexAnnotation != null) {
                    recordFieldMetadata.setFieldIndex(new FieldIndex(true, true));
                }

                // -- add the field to the record type
                container.getRecordTypeMetadata().addField(recordFieldMetadata);
            }

            // -- check if the field is an id
            LilyId idAnnotation = propertyEntry.getAnnotation(LilyId.class);
            if (idAnnotation != null) {
                container.getRecordTypeMetadata().setRecordIdProperty(propertyEntry);
            }

            // -- check if the field is a variant
            LilyVariant variantAnnotation = propertyEntry.getAnnotation(LilyVariant.class);
            if (variantAnnotation != null) {
                // -- variant properties should always be Strings
                if (! String.class.isAssignableFrom(propertyEntry.getType()))
                    throw new MetadataException("A variant property should always be a String");

                container.getRecordTypeMetadata().addVariant(variantAnnotation.value(), propertyEntry);
            }
        }
    }

    protected void parseFields(Map<QName, PropertyEntry> container, Class<?> objectType) throws MetadataException {
        //--vinay START
        List<Field> fldList = getAllFields(objectType);
        //for (Field field : objectType.getDeclaredFields()) {
        for (Field field : fldList) {
            // -- get the annotation from the field
            LilyField fieldAnnotation = field.getAnnotation(LilyField.class);
            LilyId idAnnotation = field.getAnnotation(LilyId.class);
            LilyVariant variantAnnotation = field.getAnnotation(LilyVariant.class);
            LilyFieldIndex indexAnnotation = field.getAnnotation(LilyFieldIndex.class);



            // -- continue if the annotation wasn't there
            if (fieldAnnotation == null && idAnnotation == null && variantAnnotation == null && indexAnnotation == null) {
                LOGGER.info("Skipping java field " + field.getName() + " because no frogpond annotation was found.");
                continue;
            }

            // -- construct the qualified name
            QName qname = MetadataUtilities.getQualifiedName(field);

            // -- retrieve the property entry
            PropertyEntry entry = container.get(qname);

            // -- if no entry was available, we'll create a new one
            if (entry == null) {
                entry = new PropertyEntry(qname);
            }

            // -- set the field
            entry.setField(field);

            // -- append the annotations
            if (field.getAnnotations() != null) {
                List<Annotation> annotations = new ArrayList<Annotation>();

                if (entry.getAnnotations() != null)
                    annotations.addAll(Arrays.asList(entry.getAnnotations()));

                annotations.addAll(Arrays.asList(field.getAnnotations()));

                entry.setAnnotations(annotations.toArray(new Annotation[annotations.size()]));
            }

            // -- update the entry
            container.put(qname, entry);
        }
    }

    protected void parseProperties(Map<QName, PropertyEntry> container, Class<?> objectType) throws MetadataException {
        try {
            // -- next parse the properties
            BeanInfo beanInfo = Introspector.getBeanInfo(objectType);

            // -- iterate the properties
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                LilyField fieldAnnotation = MetadataUtilities.findAnnotation(propertyDescriptor, LilyField.class);
                LilyId idAnnotation = MetadataUtilities.findAnnotation(propertyDescriptor, LilyId.class);
                LilyVariant variantAnnotation = MetadataUtilities.findAnnotation(propertyDescriptor, LilyVariant.class);
                LilyFieldIndex indexAnnotation = MetadataUtilities.findAnnotation(propertyDescriptor, LilyFieldIndex.class);

                // -- check if the field annotation was available
                if (fieldAnnotation == null && idAnnotation == null && variantAnnotation == null && indexAnnotation == null) {
                    LOGGER.info("Skipping Property " + propertyDescriptor.getName() + " because no frogpond annotation was found.");
                    continue;
                }

                // -- construct the qualified name
                QName qname = MetadataUtilities.getQualifiedName(beanInfo.getBeanDescriptor(), propertyDescriptor);

                // -- retrieve the property entry
                PropertyEntry entry = container.get(qname);

                // -- if no entry was available, we'll create a new one
                if (entry == null) {
                    entry = new PropertyEntry(qname);
                }

                // -- set the property
                entry.setProperty(propertyDescriptor);

                // -- append the annotations
                List<Annotation> annotations = new ArrayList<Annotation>();

                if (entry.getAnnotations() != null)
                    annotations.addAll(Arrays.asList(entry.getAnnotations()));

                if (propertyDescriptor.getReadMethod() != null)
                    annotations.addAll(Arrays.asList(AnnotationUtils.getAnnotations(propertyDescriptor.getReadMethod())));

                if (propertyDescriptor.getWriteMethod() != null)
                    annotations.addAll(Arrays.asList(AnnotationUtils.getAnnotations(propertyDescriptor.getWriteMethod())));

                entry.setAnnotations(annotations.toArray(new Annotation[annotations.size()]));

                // -- update the entry
                container.put(qname, entry);
            }
        } catch (Exception e) {
            throw new MetadataException("Unable to retrieve property information using introspection", e);
        }
    }

    private List<Field> getAllFields(Class<?> cls)
    {
        List<Field> fldList = new ArrayList<Field>();


        for( ; cls != null; cls = cls.getSuperclass())
        {
            fldList.addAll(Arrays.asList(cls.getDeclaredFields()));
        }
        return fldList;
    }

}
