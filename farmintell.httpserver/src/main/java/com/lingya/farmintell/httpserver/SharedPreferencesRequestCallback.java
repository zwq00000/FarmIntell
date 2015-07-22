package com.lingya.farmintell.httpserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.lingya.farmintell.httpserver.adapters.JsonAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * 默认首选项 请求响应 Created by zwq00000 on 2015/7/2.
 */
class SharedPreferencesRequestCallback implements HttpServerRequestCallback {

  private static final String TAG = "RequestCallback";
  private final SharedPreferences pref;

  public SharedPreferencesRequestCallback(Context context) {
    pref = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public SharedPreferencesRequestCallback(SharedPreferences sharedPreferences) {
    if (sharedPreferences == null) {
      throw new IllegalArgumentException("sharedPreferences is not been null");
    }
    this.pref = sharedPreferences;
  }

  @Override
  public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
    String requestMethod = request.getMethod();
    if (TextUtils.equals(requestMethod, AsyncHttpGet.METHOD)) {
      onGetRequest(request, response);
    } else if (TextUtils.equals(requestMethod, AsyncHttpPost.METHOD)) {
      onPostRequest(request, response);
    }
  }

  /**
   * 响应 Http POST 方法
   */
  private void onPostRequest(AsyncHttpServerRequest request,
                             AsyncHttpServerResponse response) {
    Log.d(TAG, "onPostRequest");

    AsyncHttpRequestBody body = request.getBody();
    if (body instanceof JSONObjectBody) {
      JSONObjectBody jsonBody = (JSONObjectBody) body;
      JSONObject json = jsonBody.get();
      try {
        JsonAdapter.setPreferences(pref, json);
        response.code(200);
        response.end();
      } catch (JSONException e) {
        e.printStackTrace();
        response.code(500);
        response.send(e.getMessage());
        response.end();
      }
    }
    response.send(body.get().toString());
    response.end();
  }

  /**
   * 响应 Http GET 方法
   */
  private void onGetRequest(AsyncHttpServerRequest request,
                            AsyncHttpServerResponse response) {
    Log.d(TAG, "onGetRequest");
    String key = request.getMatcher().replaceAll("");

    try {
      JSONStringer stringer = JsonAdapter.toJSONStringer(pref, key);
      if (stringer == null) {
        response.code(404);
        response.end();
      } else {
        response.send("application/json; charset=utf-8", stringer.toString());
        response.end();
      }
    } catch (JSONException e) {
      response.send(e.getMessage());
      response.end();
    }
  }
}
