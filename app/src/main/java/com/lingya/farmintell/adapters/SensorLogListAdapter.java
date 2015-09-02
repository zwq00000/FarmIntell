package com.lingya.farmintell.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorLog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by zwq00000 on 15-9-2.
 */
public class SensorLogListAdapter extends RealmBaseAdapter<SensorLog> {
    private TextView sensorId;
    private TextView sensorValue;
    private TextView sensorName;
    private TextView updateTime;

    private NumberFormat numberFormat = DecimalFormat.getCurrencyInstance();

    public SensorLogListAdapter(Context context, RealmResults realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    public static SensorLogListAdapter createInstance(Context context, Realm realm) {
        RealmResults<SensorLog> result = realm.where(SensorLog.class)
                .greaterThan("time", new Date(System.currentTimeMillis() - 1000 * 60 * 24))
                .findAll();
        return new SensorLogListAdapter(context, result, true);
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
            sensorId = (TextView) convertView.findViewById(R.id.list_item_sensorId);
            updateTime = (TextView) convertView.findViewById(R.id.list_item_time);
            sensorName = (TextView) convertView.findViewById(R.id.list_item_name);
            sensorValue = (TextView) convertView.findViewById(R.id.list_item_value);
        }
        SensorLog sensorLog = this.getItem(position);
        sensorId.setText(sensorLog.getSensorId());
        updateTime.setText(sensorLog.getTime().toString());
        sensorValue.setText(numberFormat.format(sensorLog.getValue()));
        return convertView;
    }
}
