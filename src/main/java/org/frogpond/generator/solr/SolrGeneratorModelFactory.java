package org.frogpond.generator.solr;

import org.frogpond.annotation.LilyFieldIndex;
import org.frogpond.generator.GeneratorModelFactory;
import org.frogpond.metadata.FieldTypeMetadata;
import org.frogpond.metadata.store.MetadataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrGeneratorModelFactory implements GeneratorModelFactory {
    private static final String SOLR_FIELD_NAME_FORMAT = "%s_%s%s";

    @Override
    public Map<String, Object> constructModel(MetadataStore metadataStore) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("fields", getSolrFields(metadataStore));

        return result;
    }

    protected List<SolrField> getSolrFields(MetadataStore metadataStore) {
        List<SolrField> result = new ArrayList<SolrField>();

        for (FieldTypeMetadata field : metadataStore.getFieldTypes()) {
            LilyFieldIndex fieldIndex = field.getProperty().getAnnotation(LilyFieldIndex.class);

            if (fieldIndex != null)
                result.add(getSolrField(fieldIndex, field));
        }

        return result;
    }

    protected SolrField getSolrField(LilyFieldIndex annotation, FieldTypeMetadata fieldMetadata) {
        SolrField result = new SolrField();

        result.setName(getSolrFieldName(fieldMetadata));
        result.setType(fieldMetadata.getPrimitive().getSolrType());
        result.setIndexed(annotation.indexed());
        result.setStored(annotation.stored());
        //result.setMultivalue(fieldMetadata.getFieldType().getValueType().isMultiValue());
        //--vinay
        result.setMultivalue(fieldMetadata.getFieldType().getValueType().getBaseName().equals("LIST"));
        //--vinay

        return result;
    }

    /*
    ${name}_${primitiveTypeLC}${multiValue?_mv}
     */
    protected String getSolrFieldName(FieldTypeMetadata fieldMetadata) {
        return String.format(
                SOLR_FIELD_NAME_FORMAT,
                fieldMetadata.getName().getName(),
                fieldMetadata.getFieldType().getValueType().getBaseName().toLowerCase(),
                (fieldMetadata.getFieldType().getValueType().getBaseName().equals("LIST")) ? "mv" : ""
        );
    }
}
