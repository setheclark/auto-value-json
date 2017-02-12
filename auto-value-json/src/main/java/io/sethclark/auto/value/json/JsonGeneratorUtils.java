package io.sethclark.auto.value.json;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import org.json.JSONException;

public class JsonGeneratorUtils {

  public static final TypeName JSON_EXCEPTION = TypeName.get(JSONException.class);

  static final TypeName STRING = ClassName.get("java.lang", "String");
  static final TypeName ENUM = ClassName.get(Enum.class);

  private static final Set<TypeName> SUPPORTED_TYPES = ImmutableSet.of(STRING, ENUM);

  public static boolean isSupportedType(TypeName type) {
    return type.isPrimitive() || type.isBoxedPrimitive() || SUPPORTED_TYPES.contains(type);
  }

  static CodeBlock readValue(Types types, JsonProperty property, ParameterSpec json,
      FieldSpec field, FieldSpec key, NameAllocator nameAllocator) {
    //TODO Handle collections.
    TypeName type = getTypeNameFromProperty(property, types);
    CodeBlock.Builder builder = CodeBlock.builder();

    if (type.equals(STRING)) {
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
      FieldSpec tempVal =
          FieldSpec.builder(String.class, nameAllocator.newName("tempVal"), Modifier.FINAL).build();
      builder.addStatement("$T $N = $N.getString($N)", tempVal.type, tempVal, json, key);
      builder.beginControlFlow("if(!$N.isEmpty())", tempVal);
      builder.addStatement("$N = $N.charAt(0)", field, tempVal);
      builder.endControlFlow();
    } else if (type.equals(ENUM)) {
      builder.addStatement("$N = $T.valueOf($N.getString($N))", field, field.type, json, key);
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
