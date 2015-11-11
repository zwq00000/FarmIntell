package com.lingya.farmintell.httpserver.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import org.json.JSONObject;

/**
 * Created by zwq00000 on 2015/7/9.
 */
public class JsonAdapterTest extends AndroidTestCase {

    private Context context;

    public void setUp() throws Exception {
        context = getContext();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_int", -1);
        editor.putString("key_string", " ");
        editor.putFloat("key_float", Float.NaN);
        editor.commit();
    }

    public void testSetPreferences() throws Exception {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        JsonAdapter.setPreferences(pref, new JSONObject("{\"key_int\":100}"));
        JsonAdapter.setPreferences(pref, new JSONObject("{\"key_string\":\"Test/7/9\"}"));
        JsonAdapter.setPreferences(pref, new JSONObject("{\"key_float\":2015.01}"));

        assertEquals(pref.getInt("key_int", 0), 100);
        assertEquals(pref.getString("key_string", ""), "Test/7/9");
        assertEquals(pref.getFloat("key_float", Float.NaN), 2015.01f);


    }

    public void testToJSONStringer() throws Exception {

    }
}