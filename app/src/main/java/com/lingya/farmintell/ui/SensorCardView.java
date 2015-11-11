package com.lingya.farmintell.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.lingya.farmintell.R;
import com.lingya.ui.GaugeView;

/**
 * TODO: document your custom view class.
 */
public class SensorCardView extends CardView {

    private TextView headerView;
    private GaugeView gaugeView;
    private String id;
    private String headerText;

    public SensorCardView(Context context) {
        super(context);
        init(null, 0);
    }

    public SensorCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SensorCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        Context context = getContext();
        LayoutInflater.from(context).inflate(R.layout.sensor_label, this, true);
        headerView = (TextView) findViewById(R.id.headerView);
        gaugeView = (GaugeView) findViewById(R.id.gaugeView);

        //final TypedArray a = context.obtainStyledAttributes(
        //    attrs, R.styleable.SensorCardView, defStyle, 0);
        //  headerText = a.getString(R.style.headerText);
        //a.recycle();
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String header) {
        this.headerView.setText(header);
        this.headerText = header;
    }

    public void setText(String str) {
        try {
            float value = Float.parseFloat(str);
            setValue(value);
        } catch (NumberFormatException nfe) {
            this.gaugeView.setText(str);
        }
    }

    public void setValue(float value) {
        this.gaugeView.setValue(value);
    }

    public String getSensorId() {
        return this.id;
    }

    public void setSensorId(String id) {
        this.id = id;
    }

    public void setMaxValue(int maxValue) {
        this.gaugeView.setMaxValue(maxValue);
    }

    public void setMinValue(int minValue) {
        this.gaugeView.setMinValue(minValue);
    }

    public void setNumberFormat(String numberFormat) {
        this.gaugeView.setNumberFormat(numberFormat);
    }
}
