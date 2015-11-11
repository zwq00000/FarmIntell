package com.lingya.farmintell.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
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
import com.lingya.farmintell.models.SensorType;
import com.lingya.farmintell.models.SensorsConfig;
import com.lingya.farmintell.services.SensorService;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 传感器状态 列表 适配器
 * Created by zwq00000 on 15-9-2.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SensorStatusListAdapter extends BaseAdapter {
    /**
     * 统计数据 更新间隔
     */
    private static final int UPDATE_SUMMARY_INTERVAL = 1000 * 60;
    private static SensorType[] SensorTypes = SensorType.values();
    private final Context context;
    private final LayoutInflater inflater;
    private final Realm realm;
    private final SensorService.ISensorBinder sensorBinder;
    /**
     * 下一次更新统计数据时间
     */
    private long lastUpdateTime = System.currentTimeMillis();


    public SensorStatusListAdapter(Context context, SensorService.ISensorBinder sensorBinder) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sensorBinder = sensorBinder;
        this.realm = RealmFactory.getInstance(context);
    }

    public static SensorStatusListAdapter createInstance(Context context, SensorService.ISensorBinder sensorBinder) {

        return new SensorStatusListAdapter(context, sensorBinder);
    }

    @Override
    public int getCount() {
        if (sensorBinder.getStatus() == null) {
            return 0;
        }
        return SensorTypes.length;
        //return sensorBinder.getStatus().size();
    }

    @Override
    public Object getItem(int position) {
        return sensorBinder.getStatus().find(getSensorTypeName(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String getSensorTypeName(int position) {
        return SensorTypes[position].name();
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
        final List<SensorStatus> statuses = (List<SensorStatus>) this.getItem(position);
        String typeName = SensorTypes[position].name();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_sensorlog, null);

            ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            iconView.setImageResource(getIcon(typeName));

            ((TextView) convertView.findViewById(R.id.list_item_value)).setText("");
        }
        if (statuses.size() > 0) {
            TextView sensorNameView = (TextView) convertView.findViewById(R.id.list_item_name);
            //传感器名称
            sensorNameView.setText(getDisplayString(statuses));

            final TextView sensorValue = (TextView) convertView.findViewById(R.id.list_item_value);
            if (lastUpdateTime < System.currentTimeMillis() || TextUtils.isEmpty(sensorValue.getText())) {
                sensorValue.post(new Runnable() {
                    @Override
                    public void run() {
                        sensorValue.setText(query(statuses.get(0).getConfig()));
                    }
                });
                lastUpdateTime += UPDATE_SUMMARY_INTERVAL;
            }
        }
        return convertView;
    }

    private String getDisplayString(List<SensorStatus> statuses) {
        SensorsConfig.SensorConfig config = statuses.get(0).getConfig();

        float summary = 0;
        for (SensorStatus status : statuses) {
            summary += status.getValue();
        }
        float average = summary / statuses.size();

        return config.getDisplayName() + "  " + config.formatValue(average) + " " + config.getUnit();
    }

    /**
     * 查询 24小时内 最大最小值
     *
     * @param config
     * @return
     */
    private String query(SensorsConfig.SensorConfig config) {
        RealmQuery<SensorLog> query = realm.where(SensorLog.class).equalTo("name", config.getName())
                .greaterThan("time", new Date(System.currentTimeMillis() - 1000 * 60 * 24));
        return "最大值:" + config.formatValue(query.maximumFloat("value")) + " 最小值:" + config.formatValue(query.minimumFloat("value"));
    }

    /**
     * 根据 传感器类型 获取 图标
     *
     * @param sensorName
     * @return
     */
    private int getIcon(String sensorName) {
        switch (sensorName) {
            case "temp":
                return (R.drawable.thermometer_3_4);
            case "hum":
                return (R.drawable.fog);
            case "co2":
                return (R.drawable.co2);
            case "light":
                return (R.drawable.sun);
            case "soilWater":
                return (R.drawable.umbrella);
            case "soilTemp":
                return (R.drawable.thermometer_full);
        }
        return 0;
    }

    public void onDestroy() {
        if (this.realm != null) {
            realm.close();
        }
    }
}
