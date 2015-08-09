package com.lingya.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;


/**
 * TODO: document your custom view class.
 */
public class GaugeView extends View {

  private static final int DEFAULT_LONG_POINTER_SIZE = 1;
  private DecimalFormat valueFormat;
  private float mFontSize;

  private Paint paint;
  private float mStrokeWidth;
  private int mStrokeColor;
  private RectF mRect;
  private Paint.Cap mStrokeCap = Paint.Cap.BUTT;
  private int mStartAngle;
  private int mSweepAngle;
  /**
   * 数值范围 最小值
   */
  private int minValue;
  /**
   * 数值范围 最大值
   */
  private int maxValue;
  private float value;
  private double mPointAngel;
  private int mPointColor;
  private String valueFormatPattern;
  /**
   * 指示部分大小
   */
  private int mPointSize;

  /**
   * 数值指示色
   */
  private int pointColor;
  /**
   * 数值指示 修饰渐变色
   */
  private int gradientPointColor;
  private Paint mTextPaint;
  private String text;
  private int mFontColor;
  private float mTextWidth;
  private float mTextHeight;

  public GaugeView(Context context) {
    super(context);
    init();
  }

  public GaugeView(Context context, AttributeSet attrs) {
    super(context, attrs);

    mStrokeWidth = 10;
    mStrokeColor = Color.parseColor("#ffaaa000");
    mStartAngle = 60;
    mSweepAngle = 300;
    minValue = 0;
    maxValue = 1000;

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GaugeView, 0, 0);

    // stroke style
    mStrokeWidth = a.getDimension(R.styleable.GaugeView_strokeWidth, mStrokeWidth);
    mStrokeColor = a.getColor(R.styleable.GaugeView_strokeColor, mStrokeColor);
    int index = a.getInt(R.styleable.GaugeView_strokeCap, -1);
    if (index > -1) {
      setCapIndex(index);
    }

    // angel start and sweep (opposite direction 0, 270, 180, 90)
    mStartAngle = a.getInt(R.styleable.GaugeView_startAngle, mStartAngle);
    mSweepAngle = a.getInt(R.styleable.GaugeView_sweepAngle, mSweepAngle);

    // scale (from minValue to maxValue)
    minValue = a.getInt(R.styleable.GaugeView_minValue, minValue);
    maxValue = a.getInt(R.styleable.GaugeView_maxValue, maxValue);

    // pointer size and color
    mPointSize = a.getInt(R.styleable.GaugeView_pointSize, 0);
    pointColor = a.getColor(R.styleable.GaugeView_pointColor, Color.WHITE);
    gradientPointColor = HsvHelper.dark(pointColor).toColor();
    //gradientPointColor = HsvHelper.pure(pointColor).darker().toColor();

    // calculating one point sweep
    mPointAngel = mSweepAngle / Math.abs(maxValue - minValue);

    String pattern = a.getString(R.styleable.GaugeView_valueFormatPattern);
    if (!TextUtils.isEmpty(pattern)) {
      this.valueFormatPattern = pattern;
      this.valueFormat = new DecimalFormat(pattern);
    } else {
      this.valueFormat = new DecimalFormat("#.0");
    }

    value = a.getFloat(R.styleable.GaugeView_value, minValue);
    mFontColor = a.getColor(R.styleable.GaugeView_fontColor, Color.BLACK);
    mFontSize = a.getDimension(R.styleable.GaugeView_fontSize, 10);

    a.recycle();
    init();

    setValue(value);
  }

  private void init() {
    //main Paint
    paint = new Paint();
    paint.setColor(mStrokeColor);
    paint.setStrokeWidth(mStrokeWidth);
    paint.setAntiAlias(true);
    paint.setStrokeCap(mStrokeCap);
    paint.setStyle(Paint.Style.STROKE);

    //set shadow, 5dp down, 5 dp left, with radius of 15 dp
    paint.setShadowLayer(this.mStrokeWidth / 2, this.mStrokeWidth / 4, this.mStrokeWidth / 4,
                         Color.BLACK);
    //-- warning, Honeycomb and above only
    //-- this will reduce draw performance of view
    //-- but is required to support drawing filters, like shadow, blur etc
    setLayerType(LAYER_TYPE_HARDWARE, paint);

    mRect = new RectF();

    // Set up a default TextPaint object
    mTextPaint = new TextPaint();
    mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

    // Update TextPaint and text measurements from attributes
    invalidateTextPaintAndMeasurements();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int paddingLeft = getPaddingLeft();
    int paddingTop = getPaddingTop();
    int paddingRight = getPaddingRight();
    int paddingBottom = getPaddingBottom();
    int contentWidth = getWidth() - paddingLeft - paddingRight;
    int contentHeight = getHeight() - paddingTop - paddingBottom;

    float
        radius =
        Math.min(contentWidth, contentHeight) / 2; //(width > height ? width / 2 : height / 2);
    mStrokeWidth = radius / 3;
    radius -= mStrokeWidth / 2;

    float mRectLeft = contentWidth / 2 - radius + paddingLeft;
    float mRectTop = contentHeight / 2 - radius + paddingTop;
    float mRectRight = mRectLeft + radius * 2;
    float mRectBottom = mRectTop + radius * 2;

    mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);

    //// TODO: 2015/8/6 测试渐变色
    paint.setStrokeWidth(mStrokeWidth);
    //paint.setStrokeWidth(radius);
    //绘制仪表盘底色
    paint.setColor(mStrokeColor);
    paint.setShader(createRedialGradient(mRect, radius, mStrokeColor));
    canvas.drawArc(mRect, mStartAngle, mSweepAngle, false, paint);

    //// TODO: 2015/8/6 测试渐变色
    paint.setStrokeWidth(mStrokeWidth);
    //绘制仪表指示
    paint.setColor(pointColor);
    paint.setShader(createRedialGradient(mRect, radius, pointColor, gradientPointColor));
    //paint.setShader(new LinearGradient(mRect.left, mRect.top, mRect.left, mRect.bottom,
    //                                   pointColor,
    //                                   gradientPointColor,
    //                                   Shader.TileMode.CLAMP));
    // calculating one point sweep
    float pointAngle = (float) ((value - minValue) * mPointAngel);
    canvas.drawArc(mRect, mStartAngle, pointAngle, false, paint);
    drawText(canvas, mRect);
  }

  private void invalidateTextPaintAndMeasurements() {
    if (text == null) {
      return;
    }
    mTextPaint.setTextSize(mFontSize);
    mTextPaint.setColor(mFontColor);
    mTextWidth = mTextPaint.measureText(text);
    mTextPaint.setAntiAlias(true);
    mTextPaint.setTextAlign(Paint.Align.CENTER);
    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
    mTextHeight = fontMetrics.bottom;
  }

  /**
   * 绘制 仪表文字
   */
  private void drawText(Canvas canvas, RectF rect) {
    if (this.text != null) {
      float x = rect.left + rect.width() / 2;//- mTextWidth / 2;
      float y = rect.top + rect.height() / 2 + mTextHeight;
      if (this.mStartAngle >= 180 && this.mSweepAngle <= 180) {
        y = rect.top + rect.height() / 2 - mTextHeight;
      }
      canvas.drawText(this.text,
                      x, y,
                      mTextPaint);
    }
  }

  /**
   * 创建 径向渐变
   *
   * @return RadialGradient 实例
   */
  Shader createRedialGradient(RectF rect, float radius, int color) {
    int darkColor = HsvHelper.grayer(color).toColor();
    return createRedialGradient(rect, radius, color, darkColor);
  }

  /**
   * 创建 径向渐变
   *
   * @return RadialGradient 实例
   */
  Shader createRedialGradient(RectF rect, float radius, int color, int darkColor) {
    int[] colors = new int[]{darkColor, color, color, color, darkColor};
    return new RadialGradient(rect.centerX(), rect.centerY(), radius + mStrokeWidth,
                              colors, null, Shader.TileMode.CLAMP);
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
    setText(valueFormat.format(value));
    invalidate();
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    invalidateTextPaintAndMeasurements();
  }

  public void setCapIndex(int capIndex) {
    switch (capIndex) {
      case 0:
        this.mStrokeCap = Paint.Cap.BUTT;
        break;
      case 1:
        this.mStrokeCap = Paint.Cap.ROUND;
        break;
      case 2:
        this.mStrokeCap = Paint.Cap.SQUARE;
        break;
      default:
        this.mStrokeCap = Paint.Cap.BUTT;
    }
  }

  public void setMinValue(int minValue) {
    this.minValue = minValue;
    initPointAngle();
  }

  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
    initPointAngle();
  }

  private void initPointAngle() {
    mPointAngel = mSweepAngle / Math.abs(maxValue - minValue);
  }

  public void setPointColor(int pointColor) {
    this.pointColor = pointColor;
    this.gradientPointColor = HsvHelper.dark(pointColor).toColor();
  }
}
