package com.lingya.farmintell.httpserver.adapters;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zwq00000 on 2015/7/8.
 */
public class JsonAdapter {

  private static final String TAG = "JsonAdapter";

  public static void setPreferences(SharedPreferences pref, JSONObject jsonObject)
      throws JSONException {
    Iterator keys = jsonObject.keys();
    Map<String, ?> prefMap = pref.getAll();
    while (keys.hasNext()) {
      String key = keys.next().toString();
      Object currentValue = prefMap.get(key);
      Log.d(TAG, key + ":" + currentValue);
      if (currentValue instanceof String) {
        pref.edit().putString(key, jsonObject.getString(key)).commit();
      } else if (currentValue instanceof Integer) {
        pref.edit().putInt(key, jsonObject.getInt(key)).commit();
      } else if (currentValue instanceof Float) {
        pref.edit().putFloat(key, (float) jsonObject.getDouble(key)).commit();
      } else if (currentValue instanceof Long) {
        pref.edit().putLong(key, jsonObject.getLong(key)).commit();
      } else if (currentValue instanceof Set) {
        pref.edit().putStringSet(key, convertToSet(jsonObject.getJSONArray(key))).commit();
      }
    }
  }

  private static Set<String> convertToSet(JSONArray jsonArray) throws JSONException {
    if (jsonArray == null || jsonArray.length() == 0) {
      return null;
    }
    HashSet<String> set = new HashSet<String>();
    for (int i = 0; i < jsonArray.length(); i++) {
      set.add(jsonArray.getString(i));
    }
    return set;
  }

  private static JSONStringer appendJson(JSONStringer jsonStringer, Set<String> stringSet)
      throws JSONException {
    if (stringSet instanceof Set) {
      jsonStringer.array();
      for (String item : stringSet) {
        jsonStringer.value(item);
      }
      jsonStringer.endArray();
    }
    return jsonStringer;
  }

  private static JSONStringer appendJson(JSONStringer jsonStringer, String key, Object value)
      throws JSONException {
    jsonStringer.object()
        .key(key);
    if (value instanceof Set) {
      appendJson(jsonStringer, (Set) value);
    } else {
      jsonStringer.value(value);
    }
    jsonStringer.endObject();

    return jsonStringer;
  }

  private static JSONStringer appendJson(JSONStringer jsonStringer, Map<String, ?> prefMap)
      throws JSONException {
    jsonStringer.object();
    for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
      jsonStringer.key(entry.getKey());
      Object value = entry.getValue();
      if (value instanceof Set) {
        appendJson(jsonStringer, (Set) value);
      } else {
        jsonStringer.value(value);
      }
    }
    jsonStringer.endObject();
    return jsonStringer;
  }

  public static JSONStringer toJSONStringer(SharedPreferences pref, String key)
      throws JSONException {
    JSONStringer stringer = null;
    if (TextUtils.isEmpty(key)) {
      stringer = new JSONStringer();
      appendJson(stringer, pref.getAll());
    } else if (pref.contains(key)) {
      Object item = pref.getAll().get(key);
      stringer = new JSONStringer();
      appendJson(stringer, key, item);
    }
    return stringer;
  }
}
