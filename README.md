# AutoValue: org.json Extension

An extension to generage AutoValue generated object from JSONObjects as well as convert AutoValue generated objects into a JSONObject.

## Usage

Include auto-value-json in your project that also hav declared org.json as a dependency (Already part of Android distribution).  Then depending on whether you want to read from a JSONObject or generate a JSONObject you can add the following methods:
```java
@AutoValue public abstract class TestObject {
  abstract int value();
  
  static TestObject create(JSONObject json) throws JSONException {
    return AutoValue_TestObject.fromJson(json);
  }
  
  abstract JSONObject toJson();
}
```
Either one is optional depending on your use-case.
