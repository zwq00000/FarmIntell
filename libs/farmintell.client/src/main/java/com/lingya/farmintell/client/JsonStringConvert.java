package com.lingya.farmintell.client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * retrofit RestAdapter Converter
 * 如果 发送数据是 字符串 类型，则当做 Json 数据发送，否则 使用 @see GsonConverter 进行转换
 * Created by zwq00000 on 15-9-16.
 */
class JsonStringConvert implements Converter {
    private GsonConverter baseConvert = new GsonConverter(new Gson());
    private String charset = "UTF-8";


    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        return baseConvert.fromBody(body, type);
    }

    @Override
    public TypedOutput toBody(Object object) {
        if (object instanceof String) {
            return new JsonTypedOutput(object.toString().getBytes(), "UTF-8");
        } else {
            return baseConvert.toBody(object);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        JsonTypedOutput(byte[] jsonBytes, String encode) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(jsonBytes);
        }
    }
}

