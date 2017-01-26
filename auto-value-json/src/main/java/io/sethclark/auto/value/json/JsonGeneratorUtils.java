package io.sethclark.auto.value.json;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.json.JSONException;

public class JsonGeneratorUtils {

  public static final TypeName JSON_EXCEPTION = TypeName.get(JSONException.class);

  static CodeBlock readValue(JsonProperty property, ParameterSpec json, FieldSpec field,
      FieldSpec key) {
    //TODO Handle characters
    //TODO Handle collections.
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

  public static CodeBlock readWithAdapter(FieldSpec typeAdapter, ParameterSpec json,
      FieldSpec field, FieldSpec key) {
    return CodeBlock.builder()
        .addStatement("$N = $N.fromJson($N, $N)", field, typeAdapter, json, key)
        .build();
  }

  public static CodeBlock writeValue(JsonProperty property, FieldSpec json) {
    //TODO If write method declares JSONException then don't wrap all puts.
    //TODO Handle characters
    //TODO Handle collections.
    //TODO Write tests for write method.
    TypeName type = property.type;
    CodeBlock.Builder builder = CodeBlock.builder();

    if (type.equals(TypeName.get(String.class))) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
      builder.beginControlFlow("try");
      builder.addStatement("$N.put($S, String.valueOf($N()))", json, property.serializedName(),
          property.methodName);
      builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
    } else {
      throw new IllegalStateException(String.format("supportedType [%s] with not method.", type));
    }

    return builder.build();
  }

  public static CodeBlock writeWithAdapter(FieldSpec typeAdapter, FieldSpec json,
      JsonProperty property) {
    return CodeBlock.builder()
        .addStatement("$N.toJson($N, $S, $N())", typeAdapter, json, property.serializedName(),
            property.methodName)
        .build();
  }
}
