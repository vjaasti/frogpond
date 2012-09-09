<schema name="example" version="1.2">

    <types>
        <fieldType name="string" class="solr.StrField" sortMissingLast="true" omitNorms="true"/>
        <fieldType name="boolean" class="solr.BoolField" omitNorms="true"/>

        <fieldType name="text" class="solr.TextField" positionIncrementGap="100">
            <analyzer type="index">
                <tokenizer class="solr.WhitespaceTokenizerFactory"/>

                <!-- Case insensitive stop word removal.
                     add enablePositionIncrements=true in both the index and query
                     analyzers to leave a 'gap' for more accurate phrase queries.
                -->
                <filter class="solr.StopFilterFactory"
                        ignoreCase="true"
                        words="stopwords.txt"
                        enablePositionIncrements="true"
                        />
                <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1"
                        catenateWords="1" catenateNumbers="1" catenateAll="0" splitOnCaseChange="1"/>
                <filter class="solr.LowerCaseFilterFactory"/>
                <filter class="solr.SnowballPorterFilterFactory" language="English" protected="protwords.txt"/>
            </analyzer>
            <analyzer type="query">
                <tokenizer class="solr.WhitespaceTokenizerFactory"/>
                <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
                <filter class="solr.StopFilterFactory"
                        ignoreCase="true"
                        words="stopwords.txt"
                        enablePositionIncrements="true"
                        />
                <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1"
                        catenateWords="0" catenateNumbers="0" catenateAll="0" splitOnCaseChange="1"/>
                <filter class="solr.LowerCaseFilterFactory"/>
                <filter class="solr.SnowballPorterFilterFactory" language="English" protected="protwords.txt"/>
            </analyzer>
        </fieldType>

        <!-- Default numeric field types. For faster range queries, consider the tint/tfloat/tlong/tdouble types. -->
        <fieldType name="int" class="solr.TrieIntField" precisionStep="0" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="float" class="solr.TrieFloatField" precisionStep="0" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="long" class="solr.TrieLongField" precisionStep="0" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="double" class="solr.TrieDoubleField" precisionStep="0" omitNorms="true" positionIncrementGap="0"/>

        <!--
         Numeric field types that index each value at various levels of precision
         to accelerate range queries when the number of values between the range
         endpoints is large. See the javadoc for NumericRangeQuery for internal
         implementation details.

         Smaller precisionStep values (specified in bits) will lead to more tokens
         indexed per value, slightly larger index size, and faster range queries.
         A precisionStep of 0 disables indexing at different precision levels.
        -->
        <fieldType name="tint" class="solr.TrieIntField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="tfloat" class="solr.TrieFloatField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="tlong" class="solr.TrieLongField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
        <fieldType name="tdouble" class="solr.TrieDoubleField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>

        <!-- The format for this date field is of the form 1995-12-31T23:59:59Z, and
         is a more restricted form of the canonical representation of dateTime
         http://www.w3.org/TR/xmlschema-2/#dateTime
         The trailing "Z" designates UTC time and is mandatory.
         Optional fractional seconds are allowed: 1995-12-31T23:59:59.999Z
         All other components are mandatory.

         Expressions can also be used to denote calculations that should be
         performed relative to "NOW" to determine the value, ie...

               NOW/HOUR
                  ... Round to the start of the current hour
               NOW-1DAY
                  ... Exactly 1 day prior to now
               NOW/DAY+6MONTHS+3DAYS
                  ... 6 months and 3 days in the future from the start of
                      the current day

         Consult the DateField javadocs for more information.

         Note: For faster range queries, consider the tdate type
        -->
        <fieldType name="date" class="solr.TrieDateField" omitNorms="true" precisionStep="0" positionIncrementGap="0"/>

        <!-- A Trie based date field for faster date range queries and date faceting. -->
        <fieldType name="tdate" class="solr.TrieDateField" omitNorms="true" precisionStep="6" positionIncrementGap="0"/>
    </types>

    <fields>
        <!-- Fields which are required by Lily -->
        <field name="lily.key" type="string" indexed="true" stored="true" required="true"/>
        <field name="lily.id" type="string" indexed="true" stored="true" required="true"/>

        <!-- Fields which are required by Lily, but which are not required to be indexed or stored -->
        <field name="lily.vtagId" type="string" indexed="true" stored="true"/>
        <field name="lily.vtag" type="string" indexed="true" stored="true"/>
        <field name="lily.version" type="long" indexed="true" stored="true"/>

        <!-- Project Declared fields -->
    <#list fields as field>
        <field name="${field.name}" type="${field.type}" indexed="${field.indexed?string}" stored="${field.stored?string}" required="${field.required?string}" <#if field.multivalue >multiValued="true"</#if>/>
    </#list>

        <!-- Dynamic Fields -->
        <dynamicField name="*_string"       type="string" indexed="true" stored="true"/>
        <dynamicField name="*_string_mv"    type="string" indexed="true" stored="true" multiValued="true"/>
        <dynamicField name="*_integer"      type="int" indexed="true" stored="true"/>
        <dynamicField name="*_long"         type="long" indexed="true" stored="true"/>
        <dynamicField name="*_double"       type="double" indexed="true" stored="true"/>
        <dynamicField name="*_decimal"      type="double" indexed="true" stored="true"/>
        <dynamicField name="*_boolean"      type="boolean" indexed="true" stored="true"/>
        <dynamicField name="*_date"         type="date" indexed="true" stored="true"/>
        <dynamicField name="*_datetime"     type="date" indexed="true" stored="true"/>
        <dynamicField name="*_blob"         type="text" indexed="true" stored="true"/>
        <dynamicField name="*_uri"          type="string" indexed="true" stored="true"/>

        <!-- This tells Solr to ignore any fields not matched by the previously.
             They will not be stored nor indexed. -->
        <dynamicField name="*" type="string" indexed="false" stored="false"/>
    </fields>

    <uniqueKey>lily.key</uniqueKey>

    <defaultSearchField>lily.id</defaultSearchField>

    <solrQueryParser defaultOperator="OR"/>
</schema>