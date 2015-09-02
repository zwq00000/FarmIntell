package com.lingya.farmintell;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.lingya.farmintell.adapters.MPLineChartAdapter;
import com.lingya.farmintell.adapters.MainClockAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;
import com.lingya.farmintell.adapters.SensorLogListAdapter;
import com.lingya.farmintell.adapters.SensorStatusViewAdapter;
import com.lingya.farmintell.models.RealmFactory;

import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends android.support.v4.app.Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        // TODO: Rename and change types of parameters
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private View view;
        private SensorAdapterFactory adapterFactory;
        private SensorStatusViewAdapter sensorAdapter;

        public MainFragment() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        static MainFragment newInstance(int sectionNum) {
            MainFragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNum);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_main, container, false);
            }
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            initSensorStatusViewAdapter();
        }

        /**
         * 初始化 传感器状态 适配器
         */
        void initSensorStatusViewAdapter() {

            this.adapterFactory = SensorAdapterFactory.getInstance(this.getActivity());
            adapterFactory.bindService();

            this.sensorAdapter = new SensorStatusViewAdapter();
            sensorAdapter.bindView((ViewGroup) this.getView().findViewById(R.id.statusView));
            sensorAdapter.setViewData(adapterFactory.getBinder());
            adapterFactory.registViewAdapter(sensorAdapter);

            MainClockAdapter mainBlock = new MainClockAdapter();
            mainBlock.bindView((ViewGroup) this.getView().findViewById(R.id.mainView));
            mainBlock.setViewData(adapterFactory.getBinder());
            adapterFactory.registViewAdapter(mainBlock);


            final MPLineChartAdapter lineChart = new MPLineChartAdapter(this.getActivity(),
                    (LineChart) this.getView().findViewById(R.id.chart));
            lineChart.setViewData(adapterFactory.getBinder());
            sensorAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = v.getTag();
                    if (tag != null && tag instanceof String) {
                        lineChart.showSensorHistory(tag.toString());
                    }
                }
            });
            adapterFactory.registViewAdapter(lineChart);
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Realm realm;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MainFragment.newInstance(position);
                case 1:
                    ListFragment listFragment = new ListFragment();
                    Context context = MainActivity.this;
                    if (realm == null) {
                        realm = RealmFactory.getInstance(context);
                    }
                    SensorLogListAdapter listAdapter = SensorLogListAdapter.createInstance(context, realm);
                    listFragment.setListAdapter(listAdapter);
                    return listFragment;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (position == 1) {
                if (realm != null) {
                    realm.close();
                    realm = null;
                }
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }

}
