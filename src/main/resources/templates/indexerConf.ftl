<?xml version="1.0"?>
<indexer>

  <records>
    <!-- A record tag without any match condition will match all records.
         Each record has the last vtag, as is is built-in into Lily.
    -->
    <record vtags="last"/>
  </records>

  <dynamicFields>
    <!--
       A dynamic field without any match condition will match anything.
       It is allowed to specify extractContent always, it will only do
       something in case of blob fields.
    -->
    <dynamicField name="${r"${name}_${primitiveTypeLC}${multiValue?_mv}"}" extractContent="true"/>
  </dynamicFields>

</indexer>