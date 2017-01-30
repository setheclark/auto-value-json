# AutoValue: org.json Extension

An extension to populate AutoValue generated objects from JSONObjects as well as convert AutoValue generated objects into a JSONObject.

## Usage

Include auto-value-json in your project that is utilizing the org.json library. (Already part of Android distribution).  Then depending on whether you want to read from a JSONObject or generate a JSONObject you can add the following methods:
```java
@AutoValue public abstract class TestObject {
  
  static TestObject create(JSONObject json) throws JSONException {
    return AutoValue_TestObject.fromJson(json);
  }
  
  abstract JSONObject toJson();
}
```
Either one is optional depending on your use-case.
