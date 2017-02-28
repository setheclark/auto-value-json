package io.sethclark.auto.value.json;

import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;
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

  final String methodName;
  final String humanName;
  final ExecutableElement element;
  final TypeName type;
  final ImmutableSet<String> annotations;
  TypeMirror typeAdapter;
  String defaultValue;

  public JsonProperty(String humanName, ExecutableElement element) {
    this.methodName = element.getSimpleName().toString();
    this.humanName = humanName;
    this.element = element;
    this.type = TypeName.get(element.getReturnType());
    this.annotations = buildAnnotations(element);

    JsonAdapter jsonAdapter = element.getAnnotation(JsonAdapter.class);
    if (jsonAdapter != null) {
      try {
        jsonAdapter.value();
      } catch (MirroredTypeException e) {
        typeAdapter = e.getTypeMirror();
      }
    }

    DefaultValue defaultValue = element.getAnnotation(DefaultValue.class);
    if (defaultValue != null) {
      this.defaultValue = defaultValue.value();
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

  String serializedName() {
    JsonName jsonName = element.getAnnotation(JsonName.class);
    if (jsonName != null) {
      return jsonName.value();
    }

    return humanName;
  }

  boolean nullable() {
    return annotations.contains("Nullable");
  }
}
