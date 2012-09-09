{
    "namespaces": {
    <#list namespaces as namespace>
        "${namespace.name}": "${namespace.prefix}"<#if namespace_has_next>,</#if>
    </#list>
    },
    "fieldTypes": [
    <#list fieldTypes as fieldType>
        {
            "name": "${fieldType.name}",
            "valueType": {
                "primitive": "${fieldType.primitive}",
                "multiValue": ${fieldType.multivalue?string},
                "hierarchical": ${fieldType.hierarchical?string}
            },
            "scope": "${fieldType.scope?lower_case}"
        }<#if fieldType_has_next>,</#if>
    </#list>
    ],
    "recordTypes": [
    <#list recordTypes as recordType>
        {
            "name": "${recordType.name}",
            "version": ${recordType.version},
            "fields": [
            <#list recordType.fields as field>
                {
                    "name": "${field.fieldTypeName}",
                    "mandatory": ${field.mandatory?string}
                }<#if field_has_next>,</#if>
            </#list>
            ]
        }<#if recordType_has_next>,</#if>
    </#list>
    ],
    "records": [ ]
}