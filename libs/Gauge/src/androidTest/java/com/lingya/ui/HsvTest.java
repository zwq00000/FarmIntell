package com.lingya.ui;

import android.graphics.Color;

import junit.framework.TestCase;

/**
 * Created by zwq00000 on 2015/8/10.
 */
public class HsvTest extends TestCase {

    private static final char[] HEXES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] toByteArray(int i) {
        return new byte[]{(byte) (i >> 24 & 255), (byte) (i >> 16 & 255), (byte) (i >> 8 & 255), (byte) (i & 255)};
    }

    public static String toHexString(byte value) {
        char[] chars = new char[]{HEXES[value >> 4 & 15], HEXES[value & 15]};
        return new String(chars);
    }

    public static String toHexString(int value) {
        byte[] bytes = toByteArray(value);
        char[] chars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; ++i) {
            chars[2 * i] = HEXES[bytes[i] >> 4 & 15];
            chars[2 * i + 1] = HEXES[bytes[i] & 15];
        }
        return new String(chars);
    }

    static String toColorString(int color) {
        return "#" + toHexString(color);
    }

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testDark() throws Exception {
        System.out.println("RED:" + toColorString(Color.RED));
        System.out.println("Dark RED:" + toColorString(Hsv.dark(Color.RED)));
    }

    public void testDarker() throws Exception {
        System.out.println("RED:" + toColorString(Color.RED));
        System.out.println("Darker RED:" + toColorString(Hsv.darker(Color.RED)));
    }

    public void testLight() throws Exception {

    }

    public void testLighter() throws Exception {

    }

    public void testGray() throws Exception {

    }

    public void testGrayer() throws Exception {

    }

    public void testPure() throws Exception {

    }

    public void testPureer() throws Exception {

    }

    public void testToColor() throws Exception {

    }

    public void testMiddle() throws Exception {

    }

    public void testMiddle1() throws Exception {

    }

    public void testGradient() throws Exception {

    }

    public void testGradient1() throws Exception {
        new Hsv("#a9d70b").gradient(new Hsv("#FF9800"), 0.2f).toColor();
    }

    public void testGrayer1() throws Exception {

    }

    public void testSetColor() throws Exception {

    }

    public void testDark1() throws Exception {

    }

    public void testPlatteColor() throws Exception {
        String baseColor = "#9c27b0";
        int c = Color.parseColor(baseColor);
        Hsv hsv = new Hsv(baseColor);
        System.out.println("color: " + hsv.toString());
        //assertEquals(hsv.toString(),baseColor);
        for (int i = 0; i < 10; i++) {
            System.out.println("<item>" + hsv.gray().toString() + "</item>");
        }
        hsv = new Hsv(baseColor);
        for (int i = 0; i < 10; i++) {
            System.out.println("<item>" + hsv.pure().dark().toString() + "</item>");
        }
        hsv = new Hsv(baseColor);
        for (int i = 0; i < 10; i++) {
            System.out.println("<item>" + hsv.dark().toString() + "</item>");
        }

    }
}