package com.lingya.farmintell.adapters;

import android.content.Context;
import android.text.TextUtils;

import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zwq00000 on 2015/7/29.
 */
public class SensorLogRequestCallback implements HttpServerRequestCallback {

    private final Context context;

    public SensorLogRequestCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String sensorId = request.getMatcher().replaceAll("");
        if (TextUtils.isEmpty(sensorId)) {
            response.code(404);
            response.end();
            return;
        }
        long lastDayMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        Realm realm = RealmFactory.getInstance(context);
        try {
            RealmResults<SensorLog>
                    logs = realm
                    .where(SensorLog.class)
                    .equalTo("sensorId", sensorId)
                    .greaterThan("time", new Date(lastDayMillis))
                    .findAll();
            JSONObject json = new JSONObject();
            try {
                JSONArray jsonArray = new JSONArray();
                for (SensorLog log : logs) {
                    jsonArray.put(log.getValue());
                }
                json.put("sensorId", sensorId)
                        .put("count", logs.size())
                        .put("values", jsonArray);
                response.send(json);
                response.end();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } finally {
            realm.close();
        }
    }
}
