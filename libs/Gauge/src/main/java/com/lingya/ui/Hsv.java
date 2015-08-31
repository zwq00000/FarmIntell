package com.lingya.ui;

import android.graphics.Color;

/**
 * Created by zwq00000 on 2015/8/10.
 */
public class Hsv {
    /**
     * HSV 结构中 色相 位置
     */
    private static final int HUE = 0;
    /**
     * HSV 结构中 饱和度 位置
     */
    private static final int STATURATION = 1;
    /**
     * HSV 结构中 亮度 位置
     */
    private static final int VALUE = 2;
    private static Hsv defaultInstance = new Hsv(0);
    float[] hsv = new float[3];

    Hsv(int color) {
        Color.colorToHSV(color, hsv);
    }

    /**
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey',
     * 'aqua', 'fuschia', 'lime', 'maroon', 'navy', 'olive', 'purple',
     * 'silver', 'teal'
     *
     * @param colorString
     */
    Hsv(String colorString) {
        int color = Color.parseColor(colorString);
        Color.colorToHSV(color, hsv);
    }

    Hsv(float[] hsv) {
        if (hsv == null || hsv.length != 3) {
            throw new IllegalArgumentException("hsv is not Illegal");
        }
        this.hsv = hsv;
    }

    public static int grayer(int color) {
        return defaultInstance.setColor(color).grayer().toColor();
    }

    public static int dark(int color) {
        return defaultInstance.setColor(color).dark().toColor();
    }

    public static int darker(int color) {
        return defaultInstance.setColor(color).darker().toColor();
    }

    //加深
    public Hsv dark() {
        hsv[VALUE] -= hsv[VALUE] * 0.1f;
        return this;
    }

    //加深
    public Hsv darker() {
        hsv[VALUE] -= hsv[VALUE] * 0.2f;
        return this;
    }

    //增加亮度
    public Hsv light() {
        hsv[VALUE] += hsv[VALUE] * 0.1f;
        return this;
    }

    //更亮
    public Hsv lighter() {
        hsv[VALUE] += hsv[VALUE] * 0.2f;
        return this;
    }

    //增加灰度
    public Hsv gray() {
        hsv[STATURATION] -= 0.1f;
        return this;
    }

    //更灰
    public Hsv grayer() {
        hsv[STATURATION] -= 0.2f;
        return this;
    }

    public Hsv pure() {
        hsv[STATURATION] += 0.1f;
        return this;
    }

    public Hsv pureer() {
        hsv[STATURATION] += 0.2f;
        return this;
    }

    public int toColor() {
        checkHsvRange();
        return Color.HSVToColor(hsv);
    }

    public Hsv middle(int color) {
        return this.middle(new Hsv(color));
    }

    /**
     * 中间色
     */
    public Hsv middle(Hsv other) {
        return gradient(other, 0.5f);
    }

    private float middle(float value, float value1) {
        return Math.min(value, value1) + Math.abs(value - value1) / 2;
    }

    /**
     * 渐变色
     */
    public Hsv gradient(int other, float rate) {
        return gradient(new Hsv(other), rate);
    }

    /**
     * 渐变色
     */
    public Hsv gradient(Hsv other, float rate) {
        float[] hsv1 = new float[3];
        if (this.hsv[HUE] < other.hsv[HUE]) {
            hsv1[HUE] = gradient(this.hsv[HUE], other.hsv[HUE], rate);
        } else {
            hsv1[HUE] = gradient(this.hsv[HUE], other.hsv[HUE], 1 - rate);
        }
        hsv1[STATURATION] = gradient(this.hsv[STATURATION], other.hsv[STATURATION],
                rate);
        hsv1[VALUE] = gradient(this.hsv[VALUE], other.hsv[VALUE], rate);
        return new Hsv(hsv1);
    }

    private float gradient(float min, float max, float rate) {
        return Math.min(min, max) + Math.abs(min - max) * rate;
    }

    private void checkHsvRange() {
        if (hsv[STATURATION] < 0) {
            hsv[STATURATION] = 0;
        } else if (hsv[STATURATION] > 1) {
            hsv[STATURATION] = 1;
        }
    }

    Hsv setColor(int color) {
        Color.colorToHSV(color, this.hsv);
        return this;
    }
}
