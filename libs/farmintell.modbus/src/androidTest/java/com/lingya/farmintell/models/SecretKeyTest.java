package com.lingya.farmintell.models;

import android.test.AndroidTestCase;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zwq00000 on 15-9-16.
 */
public class SecretKeyTest extends AndroidTestCase {

    DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

    public void testGen() throws Exception {

    }

    public void testDigest() throws Exception {
        String val = "20150916144020";
        System.out.println("sha-1:" + toString(digest(val)));

    }

    public String Gen(Date date) throws NoSuchAlgorithmException {
        return toString(digest(format.format(date)));
    }

    private String toString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT); //ByteUtils.toHexString(bytes);
    }

    public byte[] digest(String string) throws NoSuchAlgorithmException {
        java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
        return sha1.digest(string.getBytes());
    }
}
