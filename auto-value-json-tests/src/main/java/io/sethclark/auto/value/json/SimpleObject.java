package io.sethclark.auto.value.json;

import com.google.auto.value.AutoValue;
import org.json.JSONException;
import org.json.JSONObject;

@AutoValue public abstract class SimpleObject {

  public static SimpleObject create(JSONObject json) throws JSONException {
    return AutoValue_SimpleObject.fromJson(json);
  }

  public abstract JSONObject toJson();

  public abstract int aInt();

  public abstract byte aByte();

  public abstract short aShort();

  public abstract double aDouble();

  public abstract float aFloat();

  public abstract long aLong();

  public abstract boolean aBoolean();

  public abstract char aChar();

  public abstract Integer aIntObj();

  public abstract Byte aByteObj();

  public abstract Short aShortObj();

  public abstract Double aDoubleObj();

  public abstract Float aFloatObj();

  public abstract Long aLongObj();

  public abstract Boolean aBooleanObj();

  public abstract Character aCharObj();

  public abstract String aString();
}
