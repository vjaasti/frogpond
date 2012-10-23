package org.frogpond.metadata;

import java.util.HashMap;
import java.util.Map;

import org.frogpond.MetadataException;
import org.frogpond.annotation.LilyField;
import org.frogpond.annotation.Versioned;
import org.frogpond.model.Primitive;
import org.frogpond.model.PrimitiveValueTypePlaceholder;
import org.frogpond.model.PropertyEntry;
import org.frogpond.utils.MetadataUtilities;
import org.lilyproject.repository.api.FieldType;
import org.lilyproject.repository.api.RecordType;
import org.lilyproject.repository.api.Scope;
import org.lilyproject.repository.api.ValueType;
import org.lilyproject.repository.impl.FieldTypeImpl;
import org.lilyproject.repository.impl.RecordTypeImpl;
//import org.lilyproject.repository.impl.ValueTypeImpl;
import org.lilyproject.client.LilyClient;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.TypeManager;

import java.util.Collection;

public class LilyTypeHelper {
    //--vinay
    private static Map<String, String> _primitiveTypes = new HashMap<String, String>();
    
    private static LilyClient lilyClient;

    static
    {
        _primitiveTypes.put("java.lang.String", "STRING");
        _primitiveTypes.put("java.lang.Integer", "INTEGER");
        _primitiveTypes.put("java.util.concurrent.atomic.AtomicInteger", "ATOMICINTEGER");
        _primitiveTypes.put("int", "INTEGER");
        _primitiveTypes.put("java.lang.Long", "LONG");
        _primitiveTypes.put("long", "LONG");
        _primitiveTypes.put("short", "INTEGER");
        _primitiveTypes.put("java.lang.Double", "DOUBLE");
        _primitiveTypes.put("double", "DOUBLE");
        _primitiveTypes.put("float", "FLOAT");
        _primitiveTypes.put("java.math.BigDecimal", "DECIMAL");
        _primitiveTypes.put("bigdecimal", "DECIMAL");
        _primitiveTypes.put("java.lang.Boolean", "BOOLEAN");
        _primitiveTypes.put("boolean", "BOOLEAN");
        _primitiveTypes.put("java.net.URI", "URI");
        _primitiveTypes.put("java.util.UUID", "UUID");
        _primitiveTypes.put("java.lang.Enum", "ENUM");
        _primitiveTypes.put("java.util.Map.Entry", "MAPENTRY");
        _primitiveTypes.put("java.util.Date", "DATE");
        _primitiveTypes.put("java.lang.StringBuffer", "STRINGBUFFER");
        _primitiveTypes.put("java.util.Properties", "PROPERTIES");
        _primitiveTypes.put("byte[]", "BYTEARRAY");
        _primitiveTypes.put("[B", "BYTEARRAY");
    }
    //--vinay
    public static RecordType createRecordType(Class<?> objectType) {
        return new RecordTypeImpl(null, MetadataUtilities.getQualifiedName(objectType));
    }

    public static FieldType getFieldType(PropertyEntry property) throws MetadataException {
        // -- get the annotation
        LilyField fieldAnnotation = property.getAnnotation(LilyField.class);

        if (fieldAnnotation == null)
            throw new MetadataException(String.format(
                    "The field with name %s does not contain a LilyField annotation.",
                    property.getName()
            ));

        Versioned versionedAnnotation = property.getAnnotation(Versioned.class);
        Scope scope = (versionedAnnotation == null) ? Scope.NON_VERSIONED : Scope.VERSIONED;

        return new FieldTypeImpl(
                null, getValueType(property, fieldAnnotation), property.getName(), scope
        );
    }

    protected static ValueType getValueType(PropertyEntry property, LilyField fieldAnnotation) throws MetadataException {
        Primitive primitive = fieldAnnotation.primitive();


        if (primitive == null || primitive == Primitive.None) {
            if (Collection.class.isAssignableFrom(property.getType())) {
                throw new MetadataException(String.format(
                        "The field with name %s is a collection in which case the annotation should contain the actual valueType.",
                        property.getName()
                ));

            } else {
                primitive = Primitive.primitiveOf(property.getType().getName());
            }
        }

        String valueType = primitive.getLilyType();
        if((primitive == Primitive.List) || (primitive == Primitive.Set))
        {
            String javaType = fieldAnnotation.javaTypeName();
            String argType = getLilyPrimitive(javaType);
            valueType = primitive.getLilyType();
            if(argType != null)
                valueType = valueType+"<"+argType+">";
            else //--vinay
                valueType = valueType+"<LINK>";
        }

        //TEST --vinay
        try
        {
            if(lilyClient == null) lilyClient  = new LilyClient("localhost:2181", 60000);
            Repository repository = lilyClient.getRepository();
            TypeManager typeManager = repository.getTypeManager();
            return typeManager.getValueType(valueType);
        }catch(Exception ex)
        {ex.printStackTrace();
        }


        return null;

//        return new ValueTypeImpl(
  //              new PrimitiveValueTypePlaceholder(primitive),
    //            (Collection.class.isAssignableFrom(property.getType())),
      //          false
       // );
    }

    public static String getLilyPrimitive(String clsName)
    {
        return _primitiveTypes.get(clsName);
    }

    //TODO replace 
    public static boolean isEnum(String desc)
    {
        if(desc.toLowerCase().endsWith("enum"))
            return true;
        else
            return false;
    }

    public static boolean isByteArray(String desc)
    {
        if((desc.toLowerCase().equals("byte[]")) || (desc.toLowerCase().equals("[b")))
                return true;
        return false;
    }
}
