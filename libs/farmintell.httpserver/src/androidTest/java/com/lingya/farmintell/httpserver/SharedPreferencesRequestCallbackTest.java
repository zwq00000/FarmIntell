package com.lingya.farmintell.httpserver;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by zwq00000 on 2015/7/8.
 */
public class SharedPreferencesRequestCallbackTest extends AndroidTestCase {

    private static final String TAG = "SharedPreferencesRequestCallbackTest";
    private AsyncHttpServer server;
    private SharedPreferences preferences;
    private int requestCount = 0;

    public void setUp() throws Exception {
        super.setUp();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        server = new AsyncHttpServer();
        SharedPreferencesRequestCallback
                prefCallback =
                new SharedPreferencesRequestCallback(getContext());
        server.get("/pref/.*?", prefCallback);
        server.post("/pref/.*?", prefCallback);
        server.listen(AsyncServer.getDefault(), 8080);
        Log.d(TAG, "start httpserver 8080");
    }

    public void tearDown() throws Exception {
        server.stop();
    }

    private void initPreferences() {
        HashSet<String> stringSet = new HashSet<String>();
        stringSet.add("test1");
        stringSet.add("test2");
        stringSet.add("test3");
        stringSet.add("test4");
        SharedPreferences.Editor editor = preferences.edit().putBoolean("key_bool", false)
                .putFloat("key_float", 1.21f)
                .putInt("key_int", 12)
                .putLong("key_long", 123l)
                .putString("key_string", "test request")
                .putStringSet("key_stringset", stringSet);
        assertTrue(editor.commit());
    }

    public void testOnRequest() throws Exception {
        initPreferences();
        testGetMethod();

    }

    public void testGetMethod() throws Exception {
        String url = "http://192.168.0.196:8080/pref/";
        AsyncHttpClient client = new AsyncHttpClient(new AsyncServer());
        testHttpRequestGetString(client, url + "key_bool", "{\"key_bool\":false}");
        testHttpRequestGetString(client, url + "key_int", "{\"key_int\":false}");
        testHttpRequestGetString(client, url + "key_float", "{\"key_float\":12.1}");
        testHttpRequestGetString(client, url + "key_string", "{\"key_string\":\"test request\"}");
        testHttpRequestGetString(client, url + "key_stringset",
                "{\"key_stringset\":[\"test4\",\"test1\",\"test2\",\"test3\"]}");

        while (requestCount < 0) {
            Log.d(TAG, "request Count :" + requestCount);
            Thread.sleep(1000);
        }
    }

    public void testPostMethod() throws Exception {
        String url = "http://192.168.0.196:8080/pref/";
        AsyncHttpClient client = new AsyncHttpClient(new AsyncServer());

        testHttpRequestPostJson(client, url, new JSONObject("{\"key_bool\":true}"));

        while (requestCount < 0) {
            Log.d(TAG, "request Count :" + requestCount);
            Thread.sleep(1000);
        }
    }

    private void testHttpRequestPostJson(AsyncHttpClient client, String url,
                                         final JSONObject jsonObject) {
        requestCount--;
        AsyncHttpPost post = new AsyncHttpPost(url);
        post.setBody(new JSONObjectBody(jsonObject));
        client.executeJSONObject(
                post,
                new AsyncHttpClient.JSONObjectCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                        try {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            assertEquals(source.code(), 200);
                            Log.d(TAG, "JSON Result:" + result.toString());
                        } finally {
                            requestCount++;
                        }
                    }
                });
    }

    private void testHttpRequestGetString(AsyncHttpClient client, String url,
                                          final String responseString) {
        requestCount--;
        client.executeString(
                new AsyncHttpGet(url),
                new AsyncHttpClient.StringCallback() {
                    @Override
                    public void onCompleted(Exception e,
                                            AsyncHttpResponse response,
                                            String s) {
                        try {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            assertNull(e);
                            System.out.println("Response " + s);
                            assertNotNull(response);
                            assertEquals(response.code(), 200);
                            assertEquals(s, responseString);

                        } finally {
                            requestCount++;
                        }
                    }
                });
    }
}