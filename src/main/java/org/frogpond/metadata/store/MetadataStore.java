package org.frogpond.metadata.store;

import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.utils.MetadataUtilities;
import org.lilyproject.repository.api.QName;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

public class MetadataStore
{
    private Set<String> namespaces = new HashSet<String>();
    private Map<QName, RecordTypeMetadata> recordTypeMetadataMap = new HashMap<QName, RecordTypeMetadata>();
    private Map<QName, FieldTypeMetadata> fieldTypeMetadataMap = new HashMap<QName, FieldTypeMetadata>();

    private MetadataStoreFactory _factory;

// -- RecordType Manipulation -------------------------------------------------

    public void addRecordTypeMetadata(RecordTypeMetadata recordType) {
        QName name = recordType.getRecordType().getName();

        this.recordTypeMetadataMap.put(name, recordType);

        // -- add the namespace if it isn't already available
        if (!this.namespaces.contains(name.getNamespace()))
            this.namespaces.add(name.getNamespace());
    }

    public void removeRecordTypeMetadata(QName qName) {
        this.recordTypeMetadataMap.remove(qName);
    }

    public RecordTypeMetadata getRecordTypeMetadata(QName qName) {
        return this.recordTypeMetadataMap.get(qName);
    }

    /**
     * Get the RecordTypeMetadata to which the given javaClass maps.
     * <p/>
     * A Qualified name will be constructed using the java class information. Once the QName is available, it will
     * be used to retrieve the RecordTypeMetadata
     *
     * @param javaClass the javaClass for which the metadata is retrieved
     * @return a RecordTypeMetadata instance corresponding to the given javaClass or null if none could be found.
     */
    public RecordTypeMetadata getRecordTypeMetadata(Class<?> javaClass) {
        // -- construct the qualified name
        QName name = MetadataUtilities.getQualifiedName(javaClass);

        // -- retrieve the metadata for the qualified name
        return getRecordTypeMetadata(name);
    }

    public RecordTypeMetadata getRecordTypeMetadata(Class<?> javaClass, boolean create) {
        // -- construct the qualified name
        QName name = MetadataUtilities.getQualifiedName(javaClass);

        // -- retrieve the metadata for the qualified name
        if(getRecordTypeMetadata(name) != null)
            return getRecordTypeMetadata(name);

        if(create == false)
            return null;

        //if record type not present and create is true create the record type
        _factory.updateStoreForClass(this, javaClass);
        return getRecordTypeMetadata(name);


    }

// -- FieldType Manipulation --------------------------------------------------

    public void addFieldTypeMetadata(FieldTypeMetadata fieldTypeMetadata) {
        QName name = fieldTypeMetadata.getFieldType().getName();

        this.fieldTypeMetadataMap.put(name, fieldTypeMetadata);
    }

    public void removeFieldTypeMetadata(QName qName) {
        this.fieldTypeMetadataMap.remove(qName);
    }

    public FieldTypeMetadata getFieldTypeMetadata(QName qName) {
        return fieldTypeMetadataMap.get(qName);
    }

    public FieldTypeMetadata getFieldTypeMetadata(Field field) {
        QName name = MetadataUtilities.getQualifiedName(field);

        return getFieldTypeMetadata(name);
    }

    /**
     * Get the FieldTypeMetadata to which the given property maps.
     *
     * @param bean     the bean descriptor
     * @param property the property descriptor
     * @return a FieldTypeMetadata instance corresponding to the given property or null if none could be found.
     */
    public FieldTypeMetadata getFieldTypeMetadata(BeanDescriptor bean, PropertyDescriptor property) {
        QName name = MetadataUtilities.getQualifiedName(bean, property);

        // -- retrieve the metadata for the qualified name
        return getFieldTypeMetadata(name);
    }

// -- Getters -----------------------------------------------------------------

    public Collection<String> getNamespaces() {
        return Collections.unmodifiableCollection(namespaces);
    }

    public Collection<RecordTypeMetadata> getRecordTypes() {
        return Collections.unmodifiableCollection(recordTypeMetadataMap.values());
    }

    public Collection<FieldTypeMetadata> getFieldTypes() {
        return Collections.unmodifiableCollection(fieldTypeMetadataMap.values());
    }

    public void setMetadataStoreFactory(MetadataStoreFactory factory) { _factory = factory; }
}
