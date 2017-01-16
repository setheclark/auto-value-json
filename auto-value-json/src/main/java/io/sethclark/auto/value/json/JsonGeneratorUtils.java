package io.sethclark.auto.value.json;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

public class JsonGeneratorUtils {

  static CodeBlock readValue(JsonProperty property, ParameterSpec json, FieldSpec field,
      FieldSpec key) {
    TypeName type = property.type;
    CodeBlock.Builder builder = CodeBlock.builder();

    if (type.equals(TypeName.get(String.class))) {
      builder.addStatement("$N = $N.getString($N)", field, json, key);
    } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
      builder.addStatement("$N = $N.getInt($N)", field, json, key);
    } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
      builder.addStatement("$N = $N.getDouble($N)", field, json, key);
    } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
      builder.addStatement("$N = (float) $N.getDouble($N)", field, json, key);
    } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
      builder.addStatement("$N = $N.getBoolean($N)", field, json, key);
    } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
      builder.addStatement("$N = $N.getLong($N)", field, json, key);
    } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
      builder.addStatement("$N = (short) $N.getInt($N)", field, json, key);
    } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
      builder.addStatement("$N = (byte) $N.getInt($N)", field, json, key);
    } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
      builder.addStatement("$N = $N.getString($N).charAt(0)", field, json, key);
      //TODO Revisit this. It will fail with empty strings.
    } else {
      throw new IllegalStateException(String.format("supportedType [%s] with not method.", type));
    }

    return builder.build();
  }
}
