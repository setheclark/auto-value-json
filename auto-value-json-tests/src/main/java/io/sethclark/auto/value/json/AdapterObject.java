package io.sethclark.auto.value.json;

import com.google.auto.value.AutoValue;
import org.json.JSONException;
import org.json.JSONObject;

@AutoValue public abstract class AdapterObject {

  public static AdapterObject create(JSONObject json) throws JSONException {
    return AutoValue_AdapterObject.fromJson(json);
  }

  public abstract JSONObject toJson();

  @JsonAdapter(IntAdapter.class) public abstract int aInt();

  @JsonAdapter(SubTypeAdapter.class) public abstract SubType aSubType();

  public static class SubType {
    public final String value;

    public SubType(String value) {
      this.value = value;
    }
  }

  public static class IntAdapter implements JsonTypeAdapter<Integer> {

    @Override public Integer fromJson(JSONObject json, String name) {
      return json.optInt(name);
    }

    @Override public void toJson(JSONObject json, String name, Integer value) {
      try {
        json.put(name, value);
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class SubTypeAdapter implements JsonTypeAdapter<SubType> {

    @Override public SubType fromJson(JSONObject json, String name) {
      return new SubType(json.optJSONObject(name).optString("value"));
    }

    @Override public void toJson(JSONObject json, String name, SubType value) {
      try {
        json.put(name, new JSONObject().put("value", value.value));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
