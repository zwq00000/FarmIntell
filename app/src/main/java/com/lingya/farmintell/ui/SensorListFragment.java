package com.lingya.farmintell.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingya.farmintell.adapters.SensorAdapterFactory;
import com.lingya.farmintell.adapters.SensorStatusListAdapter;
import com.lingya.farmintell.adapters.ViewAdapter;

/**
 * Created by zwq00000 on 15-9-8.
 */
public class SensorListFragment extends ListFragment {

    private static final String TAG = "SensorListFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SensorStatusListAdapter listAdapter;
    private SensorAdapterFactory adapterFactory;
    private boolean isShown;

    public SensorListFragment() {

    }

    public static SensorListFragment newInstance(int sectionNum, SensorAdapterFactory sensorAdapterFactory) {
        SensorListFragment fragment = new SensorListFragment();
        fragment.adapterFactory = sensorAdapterFactory;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity();

        listAdapter = SensorStatusListAdapter.createInstance(context, adapterFactory.getBinder());
        this.setListAdapter(listAdapter);
        adapterFactory.registViewAdapter(new ViewAdapter() {
            @Override
            public void setViewData(Object viewData) {

            }

            @Override
            public void bindView(ViewGroup container) {

            }

            @Override
            public void notifyDataChanged() {
                if (isShown) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        this.listAdapter.onDestroy();
        super.onDestroyView();
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void setShown(boolean isShown) {
        this.isShown = isShown;
        Log.d(TAG, "isShown:" + isShown + " isVisible:" + isVisible());
        if (isShown) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
