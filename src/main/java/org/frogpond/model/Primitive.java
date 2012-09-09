package org.frogpond.model;

//import org.lilyproject.repository.api.PrimitiveValueType;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.frogpond.metadata.LilyTypeHelper;

public enum Primitive {
    None("", "", null, ""),
    String("STRING", "string", String.class, "string"),
    ByteArray("BYTEARRAY", "string", String.class, "string"),
    Integer("INTEGER", "number", Integer.class, "int"),
    Long("LONG", "number", Long.class, "long"),
    Double("DOUBLE", "number", Double.class, "double"),
    Decimal("DECIMAL", "number", Float.class, "float"),
    BigDecimal("DECIMAL", "number", BigDecimal.class, "decimal"),
    Boolean("BOOLEAN", "boolean", Boolean.class, "string"),
    Date("DATE", "string", Date.class, "date"),
    DateTime("DATETIME", "string", Date.class, "date"),
    Uri("URI", "string", URI.class, "string"),
    Link("LINK", "string", Object.class, "string"),
    //--vinay
    Properties("PROPERTIES", "string", Properties.class, "string"),
    StringBuffer("STRINGBUFFER", "string", StringBuffer.class, "string"),
    List("LIST", "string", List.class, "string"),
    Set("SET", "string", Set.class, "string"),
    UUID("UUID", "string", UUID.class, "string"),
    ENUM("ENUM", "string", Enum.class, "string"),
    MapEntry("MAPENTRY", "string", Map.Entry.class, "string"),    
    POMapEntry("POMAPENTRY", "string", Map.Entry.class, "string"),    
    AutomicInteger("ATOMICINTEGER", "number", AtomicInteger.class, "int"),
    //--vinay
    Blob("BLOB", "object", File.class, "text");

    private String lilyType;
    private String jsonType;
    private Class<?> javaType;
    private String solrType;

    //private PrimitiveValueType cachedValueType;

    private Primitive(String lilyType, String jsonType, Class<?> javaType, String solrType) {
        this.lilyType = lilyType;
        this.jsonType = jsonType;
        this.javaType = javaType;
        this.solrType = solrType;
    }

    public String getLilyType() {
        return lilyType;
    }

    public String getJsonType() {
        return jsonType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getSolrType() {
        return solrType;
    }

    public static Primitive primitiveOf(Class<?> cls) {
        String clsName = cls.getName();
        for (Primitive primitive : Primitive.values())
            if ((primitive != None) &&
                    (clsName.equals(primitive.javaType.getName())))
                return primitive;

        if(cls.isEnum())
            return Primitive.ENUM;

        /*
        if(cls.getSuperclass() != null)
        {
            clsName = cls.getSuperclass().getName();
            if(clsName.endsWith("Enum"))
                return Primitive.ENUM;
        }
        */

        return None;
    }

    public static Primitive primitiveOf(String fldClassName) {

        for(Primitive primitive : Primitive.values())
            if ((primitive != None) &&
                    ((fldClassName.equals(primitive.javaType.getName()))|| fldClassName.equals(primitive.solrType) || fldClassName.equals(primitive.jsonType )))
                return primitive;

        if(LilyTypeHelper.isEnum(fldClassName) )
            return ENUM;

        if(LilyTypeHelper.isByteArray(fldClassName))
            return ByteArray;

        return None;
    }
}
