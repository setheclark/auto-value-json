package io.sethclark.auto.value.json;

import com.google.auto.value.AutoValue;
import org.json.JSONException;
import org.json.JSONObject;

@AutoValue public abstract class NamedObject {

  static NamedObject create(JSONObject json) throws JSONException {
    return AutoValue_NamedObject.fromJson(json);
  }

  public abstract JSONObject toJson();

  @JsonName("MyInt") public abstract int aInt();
}
