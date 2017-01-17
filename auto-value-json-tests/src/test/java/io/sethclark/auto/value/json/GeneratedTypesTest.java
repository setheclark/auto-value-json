package io.sethclark.auto.value.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedTypesTest {

  @Test public void simpleObject() throws JSONException {
    JSONObject testJsonObj = new JSONObject(
        "{" + "    \"aInt\": 1," + "    \"aByte\": 4," + "    \"aShort\": 6,"
            + "    \"aDouble\": 1.2," + "    \"aFloat\": 1.0," + "    \"aLong\": 1123581321345589,"
            + "    \"aBoolean\": true," + "    \"aChar\":\"d\"," + "    \"aIntObj\": 2,"
            + "    \"aByteObj\": 16," + "    \"aShortObj\": 5," + "    \"aDoubleObj\": 1.3,"
            + "    \"aFloatObj\": 1.1," + "    \"aLongObj\": 9855431231853211,"
            + "    \"aBooleanObj\": false," + "    \"aCharObj\":\"s\","
            + "    \"aString\": \"String\"" + "}");

    SimpleObject simpleObject = SimpleObject.create(testJsonObj);

    assertThat(simpleObject.aInt()).isEqualTo(1);
    assertThat(simpleObject.aByte()).isEqualTo((byte) 4);
    assertThat(simpleObject.aShort()).isEqualTo((short) 6);
    assertThat(simpleObject.aDouble()).isEqualTo(1.2);
    assertThat(simpleObject.aFloat()).isEqualTo(1.0f);
    assertThat(simpleObject.aLong()).isEqualTo(1123581321345589L);
    assertThat(simpleObject.aBoolean()).isTrue();
    assertThat(simpleObject.aChar()).isEqualTo('d');
    assertThat(simpleObject.aIntObj()).isEqualTo(2);
    assertThat(simpleObject.aByteObj()).isEqualTo((byte) 16);
    assertThat(simpleObject.aShortObj()).isEqualTo((short) 5);
    assertThat(simpleObject.aDoubleObj()).isEqualTo(1.3);
    assertThat(simpleObject.aFloatObj()).isEqualTo(1.1f);
    assertThat(simpleObject.aLongObj()).isEqualTo(9855431231853211L);
    assertThat(simpleObject.aBooleanObj()).isFalse();
    assertThat(simpleObject.aCharObj()).isEqualTo('s');
    assertThat(simpleObject.aString()).isEqualTo("String");
  }

  @Test public void adapterObject() throws JSONException {
    JSONObject testJsonObj = new JSONObject(
        "{\n" + "    \"aInt\": 1,\n" + "    \"aSubType\": {\n" + "        \"value\": \"subType\"\n"
            + "    }\n" + "}");

    AdapterObject adapterObject = AdapterObject.create(testJsonObj);

    assertThat(adapterObject.aInt()).isEqualTo(1);
    assertThat(adapterObject.aSubType().value).isEqualTo("subType");
  }
}
