package io.sethclark.auto.value.json;

import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public final class JsonProperty {

  public static ImmutableList<JsonProperty> from(AutoValueExtension.Context context) {
    ImmutableList.Builder<JsonProperty> builder = ImmutableList.builder();
    for (Map.Entry<String, ExecutableElement> entry : context.properties().entrySet()) {
      builder.add(new JsonProperty(entry.getKey(), entry.getValue()));
    }
    return builder.build();
  }

  private static final List<TypeName> SUPPORTED_TYPES = Arrays.asList( //
      TypeName.get(String.class), //
      TypeName.CHAR, TypeName.CHAR.box(), //
      TypeName.DOUBLE, TypeName.DOUBLE.box(), //
      TypeName.FLOAT, TypeName.FLOAT.box(), //
      TypeName.INT, TypeName.INT.box(), //
      TypeName.BYTE, TypeName.BYTE.box(), //
      TypeName.SHORT, TypeName.SHORT.box(), //
      TypeName.LONG, TypeName.LONG.box(), //
      TypeName.BOOLEAN, TypeName.BOOLEAN.box());

  final String methodName;
  final String humanName;
  final ExecutableElement element;
  final TypeName type;
  final ImmutableSet<String> annotations;
  final boolean supportedType;
  TypeMirror typeAdapter;

  public JsonProperty(String humanName, ExecutableElement element) {
    this.methodName = element.getSimpleName().toString();
    this.humanName = humanName;
    this.element = element;
    this.type = TypeName.get(element.getReturnType());
    this.annotations = buildAnnotations(element);

    supportedType = SUPPORTED_TYPES.contains(type);

    JsonAdapter jsonAdapter = element.getAnnotation(JsonAdapter.class);
    if(jsonAdapter != null) {
      try {
        jsonAdapter.value();
      } catch (MirroredTypeException e) {
        typeAdapter = e.getTypeMirror();
      }
    }
  }

  private ImmutableSet<String> buildAnnotations(ExecutableElement element) {
    ImmutableSet.Builder<String> builder = ImmutableSet.builder();

    List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
    for (AnnotationMirror annotation : annotations) {
      builder.add(annotation.getAnnotationType().asElement().getSimpleName().toString());
    }

    return builder.build();
  }

  public TypeMirror valueAdapter() {
    //return (TypeMirror) getAnnotation
    return null;
  }

  String serializedName() {

    //TODO Add support for custom names.
    return humanName;
  }

  //public String jsonMethod() {
  //  if (type.equals(TypeName.get(String.class))) {
  //    return "$N.getString($N)";
  //  } else if (type.equals(TypeName.INT) || type.equals(TypeName.INT.box())) {
  //    return "$N.getInt($N)";
  //  } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
  //    return "$N.getDouble($N)";
  //  } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
  //    return "(float) $N.getDouble($N)";
  //  } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
  //    return "$N.getBoolean($N)";
  //  } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
  //    return "$N.getLong($N)";
  //  } else if (type.equals(TypeName.SHORT) || type.equals(TypeName.SHORT.box())) {
  //    return "(short) $N.getInt($N)";
  //  } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())) {
  //    return "(byte) $N.getInt($N)";
  //  } else if (type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())) {
  //    return "$N.getString($N).charAt(0)"; //TODO Revisit this. It will fail with empty strings.
  //  }
  //
  //  throw new IllegalStateException(String.format("supportedType [%s] with not method.", type));
  //}
}
