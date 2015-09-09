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
  private static final int[] DefaultPercentColors = new int[]{
          Color.GREEN, Color.YELLOW, Color.RED
  };

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
  private int mPointColor;
  /**
   * 指示部分大小
   */
  private int mPointSize;

  /**
   * 数值指示色
   */
  private int pointColor;
  private Paint mTextPaint;
  private String text;
  private int mFontColor;
  private float mTextWidth;
  private float mTextHeight;
  private float valueRange;
  private Hsv[] percentColors;

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
      setStrokeCap(index);
    }

    // angel start and sweep (opposite direction 0, 270, 180, 90)
    mStartAngle = a.getInt(R.styleable.GaugeView_startAngle, mStartAngle);
    mSweepAngle = a.getInt(R.styleable.GaugeView_sweepAngle, mSweepAngle);

    // scale (from minValue to maxValue)
    setMinValue(a.getInt(R.styleable.GaugeView_minValue, minValue));
    setMaxValue(a.getInt(R.styleable.GaugeView_maxValue, maxValue));

    // pointer size and color
    mPointSize = a.getInt(R.styleable.GaugeView_pointSize, 0);
    pointColor = a.getColor(R.styleable.GaugeView_pointColor, Color.WHITE);

    // calculating one point sweep
    initPointAngle();

    String pattern = a.getString(R.styleable.GaugeView_valueFormatPattern);
    if (!TextUtils.isEmpty(pattern)) {
      this.valueFormat = new DecimalFormat(pattern);
    } else {
      this.valueFormat = new DecimalFormat("#");
    }

    value = a.getFloat(R.styleable.GaugeView_value, minValue);
    mFontColor = a.getColor(R.styleable.GaugeView_fontColor, Color.BLACK);
    mFontSize = a.getDimension(R.styleable.GaugeView_fontSize, 10);

    a.recycle();

    init();
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
    paint.setShadowLayer(2, 1, 1, Color.BLACK);
    //-- warning, Honeycomb and above only
    //-- this will reduce draw performance of view
    //-- but is required to support drawing filters, like shadow, blur etc
    setLayerType(LAYER_TYPE_HARDWARE, paint);

    mRect = new RectF();

    // Set up a default TextPaint object
    mTextPaint = new TextPaint();
    mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

    if (isInEditMode()) {
      this.percentColors = new Hsv[DefaultPercentColors.length];
      for (int i = 0; i < DefaultPercentColors.length; i++) {
        percentColors[i] = new Hsv(DefaultPercentColors[i]);
      }
    } else {
      int[] colors = getContext().getResources().getIntArray(R.array.percentColors);
      this.percentColors = new Hsv[colors.length];
      for (int i = 0; i < colors.length; i++) {
        percentColors[i] = new Hsv(colors[i]);
      }
    }
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
    pointColor = getPointColor();
    paint.setColor(pointColor);
    paint.setShader(null);
    // calculating one point sweep
    float pointSweep = (value - minValue) / valueRange * mSweepAngle;
    canvas.drawArc(mRect, mStartAngle, pointSweep, false, paint);
    drawText(canvas, mRect);
  }

  /**
   * 重新测算 绘图参数
   */
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
    int darkColor = Hsv.grayer(color);
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


  /**
   * 计算 仪表指示盘 颜色
   * @return
   */
  int getPointColor() {
    float percent = Math.abs(this.value - this.minValue) / Math.abs(maxValue - minValue);
    if (percent < 0.5f) {
      return percentColors[0].gradient(percentColors[1], percent * 2).toColor();
    }
    return percentColors[1].gradient(percentColors[2], percent * 2 - 1).toColor();
  }

  /**
   * 获取 仪表数值
   * @return 仪表数值
   */
  public float getValue() {
    return value;
  }

  /**
   * 设置 仪表 数值
   * @param value
   */
  public void setValue(float value) {
    this.value = value;
    setText(valueFormat.format(value));
    invalidate();
  }

  /**
   * 获取 仪表盘 文字内容
   * @return
   */
  public String getText() {
    return text;
  }

  /**
   * 设置 仪表盘 文字
   * @param text
   */
  public void setText(String text) {
    this.text = text;
    invalidateTextPaintAndMeasurements();
  }

  /**
   * 设置 顶端样式 @eee Paint.Cap
   *
   * @param capIndex
   */
  public void setStrokeCap(int capIndex) {
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
    valueRange = Math.abs(maxValue - minValue);
  }

  /**
   * 设置 数值格式
   * @param numberFormat
   */
  public void setNumberFormat(String numberFormat) {
    this.valueFormat.applyPattern(numberFormat);

  }
}
