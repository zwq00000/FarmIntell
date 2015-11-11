package com.lingya.farmintell.httpserver.adapters;

import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by zwq00000 on 2015/7/31.
 */
public class DefaultDocumentAdapter implements HttpServerRequestCallback {

    private File defaultDoc;

    public DefaultDocumentAdapter(String doc) {
        defaultDoc = new File(doc);
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(defaultDoc);
            response.code(200);
            Util.pump(is, response, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    response.end();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
