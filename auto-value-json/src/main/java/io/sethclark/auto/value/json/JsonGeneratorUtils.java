package io.sethclark.auto.value.json;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonGeneratorUtils {

  public static final TypeName JSON_EXCEPTION = TypeName.get(JSONException.class);

  static final TypeName STRING = ClassName.get("java.lang", "String");
  static final TypeName ENUM = ClassName.get(Enum.class);
  static final TypeName COLLECTION = ClassName.get("java.util", "Collection");

  private static final Set<TypeName> SUPPORTED_TYPES = ImmutableSet.of(STRING, COLLECTION);

  public static boolean isSupportedType(TypeName type) {
    return type.isPrimitive() || type.isBoxedPrimitive() || SUPPORTED_TYPES.contains(type);
  }

  public static CodeBlock getter(TypeName type, String json, FieldSpec field, Object key) {
    CodeBlock.Builder builder = CodeBlock.builder();
    if (type.equals(STRING)) {
      builder.add("$L.getString($L)", json, key);
    } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
      builder.add("$N.getInt($L)", json, key);
    } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
      builder.add("$N.getDouble($L)", json, key);
    } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
      builder.add("(float) $N.getDouble($L)", json, key);
    } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
      builder.add("$N.getBoolean($L)", json, key);
    } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
      builder.add("$N.getLong($L)", json, key);
    } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
      builder.add("(short) $N.getInt($L)", json, key);
    } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
      builder.add("(byte) $N.getInt($L)", json, key);
    } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
      builder.add("$N.getString($L).charAt(0)", json, key);
    } else if (type.equals(ENUM)) {
      builder.add("$T.valueOf($N.getString($L))", field.type, json, key);
    } else {
      throw new IllegalStateException(String.format("supportedType [%s] with not method.", type));
    }

    return builder.build();
  }

  static CodeBlock readValue(Types types, JsonProperty property, ParameterSpec json,
      FieldSpec field, FieldSpec key) {
    TypeName type = getTypeNameFromProperty(property, types);
    CodeBlock.Builder builder = CodeBlock.builder();

    if (type.equals(STRING)) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(ENUM)) {
      builder.addStatement("$N = $L", field, getter(type, json.name, field, key.name));
    } else if (type.equals(COLLECTION)) {
      builder.addStatement("$N = new $T()", field, field.type);
      FieldSpec jsonArr = FieldSpec.builder(JSONArray.class, "jsonArr", Modifier.FINAL).build();
      builder.addStatement("$T $N = $N.getJSONArray($N)", jsonArr.type, jsonArr, json, key);
      builder.beginControlFlow("for(int i = 0; i < jsonArr.length(); i++)");
      builder.addStatement("$N.add($L)", field, getter(type, "jsonArr", field, "i"));
      builder.endControlFlow();
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

  public static CodeBlock writeValue(Types types, JsonProperty property, FieldSpec json) {
    //TODO If write method declares JSONException then don't wrap all puts.
    //TODO Handle collections.
    TypeName type = getTypeNameFromProperty(property, types);
    CodeBlock.Builder builder = CodeBlock.builder();

    if (type.equals(STRING)) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
      builder.addStatement("$N.put($S, $N())", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
      builder.addStatement("$N.put($S, String.valueOf($N()))", json, property.serializedName(),
          property.methodName);
    } else if (type.equals(ENUM)) {
      builder.addStatement("$N.put($S, $N().name())", json, property.serializedName(),
          property.methodName);
    } else {
      throw new IllegalStateException(String.format("supportedType [%s] with no method.", type));
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

  static TypeName getTypeNameFromProperty(JsonProperty property, Types types) {
    TypeMirror returnType = property.element.getReturnType();

    TypeElement element = (TypeElement) types.asElement(returnType);
    if (element != null && element.getKind() == ElementKind.ENUM) {
      return ENUM;
    }
    return property.type;
  }
}
