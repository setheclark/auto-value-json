package io.sethclark.auto.value.json;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.json.JSONException;
import org.json.JSONObject;

import static io.sethclark.auto.value.json.JsonGeneratorUtils.JSON_EXCEPTION;
import static io.sethclark.auto.value.json.JsonGeneratorUtils.getTypeNameFromProperty;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(AutoValueExtension.class) public class AutoValueJsonExtension
    extends AutoValueExtension {

  private static final ClassName JSON_OBJ_CLASS_NAME = ClassName.get(JSONObject.class);

  @Override public boolean applicable(Context context) {
    return generateReadMethod(context) || getJsonWriteMethod(context).isPresent();
  }

  private static Optional<ExecutableElement> getJsonWriteMethod(Context context) {
    TypeElement type = context.autoValueClass();
    for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
      if (method.getParameters().size() == 0 && TypeName.get(method.getReturnType())
          .equals(JSON_OBJ_CLASS_NAME) && method.getModifiers().contains(ABSTRACT)) {
        return Optional.of(method);
      }
    }
    return Optional.absent();
  }

  private static boolean generateReadMethod(Context context) {
    TypeElement type = context.autoValueClass();
    //Find method with param as JSONObject and return type as autovalue class.
    for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
      if (method.getModifiers().contains(Modifier.STATIC) && method.getParameters().size() == 1) {
        TypeMirror rType = method.getReturnType();
        TypeName returnType = TypeName.get(rType);

        TypeMirror pType = method.getParameters().get(0).asType();
        TypeName paramType = TypeName.get(pType);
        if (returnType.equals(TypeName.get(type.asType())) && paramType.equals(
            JSON_OBJ_CLASS_NAME)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override public Set<ExecutableElement> consumeMethods(Context context) {
    Optional<ExecutableElement> writeMethod = getJsonWriteMethod(context);
    if (writeMethod.isPresent()) {
      return Collections.singleton(writeMethod.get());
    }
    return Collections.emptySet();
  }

  @Override public String generateClass(Context context, String className, String classToExtend,
      boolean isFinal) {
    List<JsonProperty> properties = JsonProperty.from(context);
    validateProperties(context.processingEnvironment(), properties);

    Map<String, TypeName> types = convertPropertiesToTypes(context.properties());
    NameAllocator nameAllocator = new NameAllocator();
    Map<TypeMirror, FieldSpec> typeAdapters = getTypeAdapters(properties, nameAllocator);

    TypeName superClass = TypeVariableName.get(classToExtend);
    TypeSpec.Builder subclass = TypeSpec.classBuilder(className)
        .superclass(superClass)
        .addMethod(generateConstructor(types));

    if (generateReadMethod(context)) {
      subclass.addMethod(
          generateFromJson(context, properties, typeAdapters, nameAllocator.clone()));
    }

    Optional<ExecutableElement> writeMethod = getJsonWriteMethod(context);
    if (writeMethod.isPresent()) {
      subclass.addMethod(generateWriteToJson(context, writeMethod.get(), properties, typeAdapters,
          nameAllocator.clone()));
    }

    if (!typeAdapters.isEmpty()) {
      for (FieldSpec field : typeAdapters.values()) {
        subclass.addField(field);
      }
    }

    if (isFinal) {
      subclass.addModifiers(FINAL);
    } else {
      subclass.addModifiers(ABSTRACT);
    }

    return JavaFile.builder(context.packageName(), subclass.build()).build().toString();
  }

  private void validateProperties(ProcessingEnvironment processingEnvironment,
      List<JsonProperty> properties) {
    Types typeUtils = processingEnvironment.getTypeUtils();
    for (JsonProperty property : properties) {
      if (property.typeAdapter != null) {
        continue;
      }

      TypeName type = getTypeNameFromProperty(property, typeUtils);

      if (!JsonGeneratorUtils.isSupportedType(type)) {
        processingEnvironment.getMessager()
            .printMessage(Diagnostic.Kind.ERROR,
                "Property " + property.methodName + " is not supported.", property.element);
      }
    }
  }

  MethodSpec generateConstructor(Map<String, TypeName> properties) {
    List<ParameterSpec> params = new ArrayList<>();
    for (Map.Entry<String, TypeName> entry : properties.entrySet()) {
      params.add(ParameterSpec.builder(entry.getValue(), entry.getKey()).build());
    }

    MethodSpec.Builder builder = MethodSpec.constructorBuilder().addParameters(params);

    builder.addCode("super").addStatement(paramsList(params.size()), params.toArray());

    return builder.build();
  }

  String paramsList(int numParams) {
    StringBuilder params = new StringBuilder("(");
    for (int i = numParams; i > 0; i--) {
      params.append("$N");
      if (i > 1) params.append(", ");
    }
    return params.append(")").toString();
  }

  MethodSpec generateFromJson(Context context, List<JsonProperty> properties,
      Map<TypeMirror, FieldSpec> typeAdapters, NameAllocator nameAllocator) {
    ParameterSpec json =
        ParameterSpec.builder(JSONObject.class, nameAllocator.newName("json")).build();

    ClassName finalClassName = getFinalClassName(context);

    MethodSpec.Builder builder = MethodSpec.methodBuilder("fromJson")
        .addParameter(json)
        .returns(finalClassName)
        .addModifiers(STATIC)
        .addException(JSONException.class);

    Map<JsonProperty, FieldSpec> fields = new LinkedHashMap<>(properties.size());
    for (JsonProperty property : properties) {
      FieldSpec field =
          FieldSpec.builder(property.type, nameAllocator.newName(property.humanName)).build();
      fields.put(property, field);

      builder.addStatement("$T $N = $L", field.type, field, defaultValue(field.type));
    }

    FieldSpec keyItr = FieldSpec.builder(ParameterizedTypeName.get(Iterator.class, String.class),
        nameAllocator.newName("keyIt")).build();

    builder.addStatement("$T $N = $N.keys()", keyItr.type, keyItr, json);

    FieldSpec key = FieldSpec.builder(String.class, nameAllocator.newName("key")).build();
    builder.addStatement("$T $N", key.type, key);

    builder.beginControlFlow("while ($N.hasNext())", keyItr);
    builder.addStatement("$N = $N.next()", key, keyItr);

    builder.beginControlFlow("if ($N.isNull($N))", json, key);
    builder.addStatement("continue");
    builder.endControlFlow();

    builder.beginControlFlow("switch ($N)", key);
    Types types = context.processingEnvironment().getTypeUtils();
    for (Map.Entry<JsonProperty, FieldSpec> entry : fields.entrySet()) {
      JsonProperty prop = entry.getKey();
      FieldSpec field = entry.getValue();

      builder.beginControlFlow("case $S:", prop.serializedName());
      if (prop.typeAdapter != null && typeAdapters.containsKey(prop.typeAdapter)) {
        FieldSpec typeAdapter = typeAdapters.get(prop.typeAdapter);
        builder.addCode(JsonGeneratorUtils.readWithAdapter(typeAdapter, json, field, key));
      } else {
        builder.addCode(JsonGeneratorUtils.readValue(types, prop, json, field, key, nameAllocator));
      }
      builder.addStatement("break");

      builder.endControlFlow();//case
    }
    builder.endControlFlow(); //switch

    builder.endControlFlow(); //while

    builder.addCode("return ")
        .addCode(CodeBlock.builder()
            .add(CodeBlock.of("new $T", finalClassName))
            .addStatement(paramsList(fields.size()), fields.values().toArray())
            .build());

    return builder.build();
  }

  private MethodSpec generateWriteToJson(Context context, ExecutableElement writeMethodElement,
      List<JsonProperty> properties, Map<TypeMirror, FieldSpec> typeAdapters,
      NameAllocator nameAllocator) {
    Set<Modifier> modifierSet = new TreeSet<>(writeMethodElement.getModifiers());
    modifierSet.remove(ABSTRACT);
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder(writeMethodElement.getSimpleName().toString())
            .addAnnotation(Override.class)
            .addModifiers(modifierSet)
            .returns(JSON_OBJ_CLASS_NAME);

    FieldSpec json = FieldSpec.builder(JSON_OBJ_CLASS_NAME, nameAllocator.newName("json")).build();
    builder.addStatement("$1T $2N = new $1T()", JSON_OBJ_CLASS_NAME, json);
    Types types = context.processingEnvironment().getTypeUtils();

    for (JsonProperty prop : properties) {
      if (prop.typeAdapter != null && typeAdapters.containsKey(prop.typeAdapter)) {

        builder.addCode(
            JsonGeneratorUtils.writeWithAdapter(typeAdapters.get(prop.typeAdapter), json, prop));
      } else {
        //TODO Discuss: Is null check needed?
        builder.beginControlFlow("try");
        if (prop.nullable()) {
          builder.beginControlFlow("if ($N() != null)", prop.methodName);
          builder.addCode(JsonGeneratorUtils.writeValue(types, prop, json));
          builder.endControlFlow();
        } else {
          builder.addCode(JsonGeneratorUtils.writeValue(types, prop, json));
        }
        builder.endControlFlow("catch($T e) {}", JSON_EXCEPTION);
      }
    }

    builder.addStatement("return $N", json);

    return builder.build();
  }

  private static ClassName getFinalClassName(Context context) {
    TypeElement autoValueClass = context.autoValueClass();
    String name = autoValueClass.getSimpleName().toString();

    Element enclosingElement = autoValueClass.getEnclosingElement();
    while (enclosingElement instanceof TypeElement) {
      name = enclosingElement.getSimpleName().toString() + "_" + name;
      enclosingElement = enclosingElement.getEnclosingElement();
    }

    return ClassName.get(context.packageName(), "AutoValue_" + name);
  }

  private String defaultValue(TypeName type) {
    if (type == TypeName.BOOLEAN) {
      return "false";
    } else if (type == TypeName.BYTE) {
      return "(byte) 0";
    } else if (type == TypeName.SHORT) {
      return "0";
    } else if (type == TypeName.INT) {
      return "0";
    } else if (type == TypeName.LONG) {
      return "0L";
    } else if (type == TypeName.CHAR) {
      return "'\0'";
    } else if (type == TypeName.FLOAT) {
      return "0.0f";
    } else if (type == TypeName.DOUBLE) {
      return "0.0d";
    } else {
      return "null";
    }
  }

  private ImmutableMap<TypeMirror, FieldSpec> getTypeAdapters(List<JsonProperty> properties,
      NameAllocator nameAllocator) {
    Map<TypeMirror, FieldSpec> typeAdapters = new LinkedHashMap<>();
    for (JsonProperty property : properties) {
      if (property.typeAdapter != null && !typeAdapters.containsKey(property.typeAdapter)) {
        ClassName typeName = (ClassName) TypeName.get(property.typeAdapter);
        String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeName.simpleName());
        name = nameAllocator.newName(name, typeName);

        typeAdapters.put(property.typeAdapter,
            FieldSpec.builder(typeName, NameAllocator.toJavaIdentifier(name), PRIVATE, STATIC,
                FINAL).initializer("new $T()", typeName).build());
      }
    }

    return ImmutableMap.copyOf(typeAdapters);
  }

  private Map<String, TypeName> convertPropertiesToTypes(
      Map<String, ExecutableElement> properties) {
    Map<String, TypeName> types = new LinkedHashMap<>();
    for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
      ExecutableElement el = entry.getValue();
      types.put(entry.getKey(), TypeName.get(el.getReturnType()));
    }
    return types;
  }
}
