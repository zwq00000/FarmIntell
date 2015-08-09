package com.lingya.ui;

import android.graphics.Color;

/**
 * Created by zwq00000 on 2015/8/7.
 */
class HsvHelper {

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

  public static int custom(int color, ManualToning toning) {
    if (toning == null) {
      return color;
    }
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    toning.process(hsv);
    checkHsvRange(hsv);
    return Color.HSVToColor(hsv);
  }

  public static Hsv getInstance(int color) {
    return new Hsv(color);
  }

  //加深
  public static Hsv dark(int color) {
    return new Hsv(color).dark();
  }

  //加深
  public static Hsv darker(int color) {
    return new Hsv(color).darker();
  }

  //增加亮度
  public static Hsv light(int color) {
    return new Hsv(color).light();
  }

  //更亮
  public static Hsv lighter(int color) {
    return new Hsv(color).lighter();
  }

  //增加灰度
  public static Hsv gray(int color) {
    return new Hsv(color).gray();
  }

  //更灰
  public static Hsv grayer(int color) {
    return new Hsv(color).grayer();
  }

  static void checkHsvRange(float[] hsv) {
    if (hsv[STATURATION] < 0) {
      hsv[STATURATION] = 0;
    } else if (hsv[STATURATION] > 1) {
      hsv[STATURATION] = 1;
    }
  }

  //增加饱和度
  public static Hsv pure(int color) {
    return new Hsv(color).pure();
  }

  //增加饱和度
  static void pure(float[] hsv) {
    hsv[STATURATION] += 0.1f;
  }

  //更纯
  public static Hsv pureer(int color) {
    return new Hsv(color).pureer();
  }

  /**
   * 手动调色
   */
  public interface ManualToning {

    /**
     * 处理HSV 数据
     */
    void process(float[] hsv);
  }

  public static class Hsv {

    float[] hsv = new float[3];

    Hsv(int color) {
      Color.colorToHSV(color, hsv);
    }

    Hsv(float[] hsv) {
      if (hsv == null || hsv.length != 3) {
        throw new IllegalArgumentException("hsv is not Illegal");
      }
      this.hsv = hsv;
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
      float[] hsv1 = new float[3];
      hsv1[HUE] = middle(this.hsv[HUE], other.hsv[HUE]);
      hsv1[STATURATION] = middle(this.hsv[STATURATION], other.hsv[STATURATION]);
      hsv1[VALUE] = middle(this.hsv[VALUE], other.hsv[VALUE]);
      return new Hsv(hsv1);
    }

    private float middle(float value, float value1) {
      return Math.min(value, value1) + Math.abs(value - value1) / 2;
    }

    private void checkHsvRange() {
      if (hsv[STATURATION] < 0) {
        hsv[STATURATION] = 0;
      } else if (hsv[STATURATION] > 1) {
        hsv[STATURATION] = 1;
      }
    }
  }
}
