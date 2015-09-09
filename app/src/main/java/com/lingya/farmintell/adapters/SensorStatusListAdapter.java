package com.lingya.farmintell.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingya.farmintell.R;
import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorLog;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorsConfig;
import com.lingya.farmintell.services.SensorService;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * 传感器状态 列表 适配器
 * Created by zwq00000 on 15-9-2.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SensorStatusListAdapter extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private final Realm realm;
    private final SensorService.ISensorBinder sensorBinder;
    private TextView sensorId;
    private TextView sensorName;
    private TextView updateTime;

    private NumberFormat numberFormat = DecimalFormat.getCurrencyInstance();


    public SensorStatusListAdapter(Context context, SensorService.ISensorBinder sensorBinder) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sensorBinder = sensorBinder;
        this.realm = RealmFactory.getInstance(context);;
    }

    public static SensorStatusListAdapter createInstance(Context context, SensorService.ISensorBinder sensorBinder) {

            return new SensorStatusListAdapter(context, sensorBinder);
    }

    @Override
    public int getCount() {
        if(sensorBinder.getStatus() == null){
            return 0;
        }
        return sensorBinder.getStatus().size();
    }

    @Override
    public Object getItem(int position) {
        return sensorBinder.getStatus().getStatuses()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_sensorlog, null);
        }
        //updateTime = (TextView) convertView.findViewById(R.id.list_item_time);
        sensorName = (TextView) convertView.findViewById(R.id.list_item_name);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);

        SensorStatus status = (SensorStatus) this.getItem(position);
        //传感器名称
        sensorName.setText(status.getDisplayName() + "  " + status.getFormatedValue() + " " + status.getConfig().getUnit());

        //设置 icon
        if (iconView.getDrawable() == null) {
            iconView.setImageDrawable(getIcon(status.getName()));
        }

        //更新时间
        //updateTime.setText(DateFormat.getTimeFormat(context).format(new Date()));
        //值
        final String sensorId = status.getId();
        final TextView sensorValue = (TextView) convertView.findViewById(R.id.list_item_value);
        sensorValue.post(new Runnable() {
            @Override
            public void run() {
                sensorValue.setText(query(sensorId));
            }
        });//.setText(numberFormat.format(sensorLog.getValue()));
        return convertView;
    }

    private String query(String sensorId) {
        //new SensorLog().getSensorId()
        RealmQuery<SensorLog> query = realm.where(SensorLog.class).equalTo("sensorId", sensorId)
                .greaterThan("time", new Date(System.currentTimeMillis() - 1000 * 60 * 24));
        //query.maximumFloat("value");
        //query.minimumFloat("value");
        return "最大值:" + query.maximumFloat("value") + " 最小值" + query.minimumFloat("value");

    }

    private Drawable getIcon(String sensorName) {
        switch (sensorName) {
            case "temp":
                return this.context.getResources().getDrawable(R.drawable.thermometer_3_4);
            case "hum":
                return context.getResources().getDrawable(R.drawable.fog);
            case "co2":
                return context.getResources().getDrawable(R.drawable.co2);
            case "light":
                return context.getResources().getDrawable(R.drawable.sun);
            case "soilWater":
                return context.getResources().getDrawable(R.drawable.umbrella);
            case "soilTemp":
                return context.getResources().getDrawable(R.drawable.thermometer_full);
        }
        return null;
    }

    private Drawable getAssetsIcon(String iconName) {
        InputStream stream = null;
        try {
            stream = this.context.getAssets().open("icons/" + iconName + ".png");
            return Drawable.createFromStream(stream, iconName);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public void onDestroy() {
        if(this.realm!=null){
            realm.close();
        }
    }
}
