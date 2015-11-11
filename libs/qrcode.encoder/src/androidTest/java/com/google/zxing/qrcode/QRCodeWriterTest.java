package com.google.zxing.qrcode;

import android.graphics.Bitmap;

import com.lingya.qrcodegenerator.QRCodeFactory;

import junit.framework.TestCase;

/**
 * Created by zwq00000 on 2015/7/8.
 */
public class QRCodeWriterTest extends TestCase {


    public void testEncode() throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        String text = "http://192.168.0.196/index.html";
        Bitmap image = QRCodeFactory.renderToBitmap(text);
        assertNotNull(image);

    }


    public void testEncode1() throws Exception {

    }
}