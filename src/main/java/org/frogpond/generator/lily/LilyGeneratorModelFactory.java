package org.frogpond.generator.lily;

import org.frogpond.generator.GeneratorModelFactory;
import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.RecordFieldMetadata;
import org.frogpond.metadata.RecordTypeMetadata;
import org.frogpond.metadata.store.MetadataStore;
import org.frogpond.utils.MetadataUtilities;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LilyGeneratorModelFactory implements GeneratorModelFactory {
    @Override
    public Map<String, Object> constructModel(MetadataStore metadataStore) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        Map<String, Namespace> namespaces = getNamespaces(metadataStore);
        Map<String, FieldType> fieldTypes = getFieldTypes(metadataStore, namespaces);

        model.put("namespaces", namespaces.values());
        model.put("fieldTypes", fieldTypes.values());
        model.put("recordTypes", getRecordTypes(metadataStore, namespaces));

        return model;
    }

    public Map<String, Namespace> getNamespaces(MetadataStore metadataStore) {
        Map<String, Namespace> result = new HashMap<String, Namespace>();

        int counter = 1;
        for (String namespace : metadataStore.getNamespaces()) {
            result.put(namespace, new Namespace(("ns" + counter++), namespace));
        }

        return result;
    }

    public Map<String, FieldType> getFieldTypes(MetadataStore metadataStore, Map<String, Namespace> namespaces) {
        Map<String, FieldType> result = new HashMap<String, FieldType>();

        for (FieldTypeMetadata fieldTypeMetadata : metadataStore.getFieldTypes()) {
            QName qualifiedName = fieldTypeMetadata.getName();

            String name = String.format("%s$%s", namespaces.get(qualifiedName.getNamespace()).getPrefix(), qualifiedName.getName());

            ValueType valueType = fieldTypeMetadata.getFieldType().getValueType();

            result.put(
                    MetadataUtilities.getQNameAsLongString(qualifiedName),
                    new FieldType(
                        name,
                        fieldTypeMetadata.getPrimitive().getLilyType(),
                        valueType.isMultiValue(),
                        valueType.isHierarchical(),
                        fieldTypeMetadata.getFieldType().getScope().name()
                    )
            );
        }

        return result;
    }

    public List<RecordType> getRecordTypes(MetadataStore metadataStore, Map<String, Namespace> namespaces) {
        List<RecordType> result = new ArrayList<RecordType>();

        for (RecordTypeMetadata recordTypeMetadata : metadataStore.getRecordTypes()) {
            result.add(new RecordType(
                    MetadataUtilities.getQNameAsShortString(recordTypeMetadata.getName(), namespaces),
                    recordTypeMetadata.getVersion().toString(),
                    getRecordFields(namespaces, recordTypeMetadata)
            ));
        }

        return result;
    }

    public List<RecordField> getRecordFields(Map<String, Namespace> namespaces, RecordTypeMetadata recordTypeMetadata) {
        List<RecordField> result = new ArrayList<RecordField>();

        for (RecordFieldMetadata recordFieldMetadata : recordTypeMetadata.getFields()) {
            result.add(new RecordField(
                    MetadataUtilities.getQNameAsShortString(recordFieldMetadata.getQName(), namespaces),
                    recordFieldMetadata.isMandatory()

            ));
        }

        return result;
    }
}
