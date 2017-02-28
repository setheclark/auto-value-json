package io.sethclark.auto.value.json;

import com.google.auto.value.AutoValue;
import org.json.JSONException;
import org.json.JSONObject;

@AutoValue public abstract class DefaultValuesObject {

  public static DefaultValuesObject create(JSONObject json) throws JSONException {
    return AutoValue_DefaultValuesObject.fromJson(json);
  }

  @DefaultValue("-1") public abstract int aInt();

  @DefaultValue("(byte) -1") public abstract byte aByte();

  @DefaultValue("-1") public abstract short aShort();

  @DefaultValue("-1.2") public abstract double aDouble();

  @DefaultValue("-1.2f") public abstract float aFloat();

  @DefaultValue("-1L") public abstract long aLong();

  @DefaultValue("true") public abstract boolean aBoolean();

  @DefaultValue("'r'") public abstract char aChar();

  @DefaultValue("-1") public abstract Integer aIntObj();

  @DefaultValue("(byte) -1") public abstract Byte aByteObj();

  @DefaultValue("-1") public abstract Short aShortObj();

  @DefaultValue("-1.2") public abstract Double aDoubleObj();

  @DefaultValue("-1.2f") public abstract Float aFloatObj();

  @DefaultValue("-1L") public abstract Long aLongObj();

  @DefaultValue("true") public abstract Boolean aBooleanObj();

  @DefaultValue("'r'") public abstract Character aCharObj();

  @DefaultValue("\"test\"") public abstract String aString();

  @DefaultValue("TestEnum.A") public abstract TestEnum aEnum();
}
