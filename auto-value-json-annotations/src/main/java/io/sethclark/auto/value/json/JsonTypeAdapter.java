package io.sethclark.auto.value.json;

import org.json.JSONObject;

public interface JsonTypeAdapter<T> {
  T fromJson(JSONObject json, String name);

  void toJson(JSONObject json, String name, T value);
}
