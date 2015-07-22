package com.lingya.farmintell;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.lingya.farmintell.adapters.MultipleSeriesChartAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;
import com.lingya.farmintell.adapters.SensorStatusViewAdapter;


public class FragmentMasterActivity extends Activity {

  private static final String TAG = "MainActivity";
  Animation leftInAnimation;
  Animation leftOutAnimation;
  Animation rightInAnimation;
  Animation rightOutAnimation;
  private LayoutInflater mInflater;
  // @ViewInject(R.id.viewFlipper)
  private ViewFlipper viewFlipper;
  GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
    @Override
    public boolean onDown(MotionEvent e) {
      return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      if (e1.getX() - e2.getX() > 50) {
        viewFlipper.setInAnimation(leftInAnimation);
        viewFlipper.setOutAnimation(leftOutAnimation);
        viewFlipper.showNext();//向右滑动
        return true;
      } else if (e1.getX() - e2.getX() < -50) {
        viewFlipper.setInAnimation(rightInAnimation);
        viewFlipper.setOutAnimation(rightOutAnimation);
        viewFlipper.showPrevious();//向左滑动
        return true;
      }
      return false;
    }
  };
  private GestureDetector detector; //手势检测
  private SensorStatusViewAdapter sensorAdapter;
  private SensorAdapterFactory adapterFactory;
  private MultipleSeriesChartAdapter chartAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);

    mInflater = LayoutInflater.from(this);

    detector = new GestureDetector(this, listener);
    //往viewFlipper添加View
    viewFlipper.addView(mInflater.inflate(R.layout.fragment_main, null));
    viewFlipper.addView(mInflater.inflate(R.layout.fragment_monitor, null));

    //动画效果
    leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
    leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
    rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
    rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

    initSensorStatusViewAdapter();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (this.adapterFactory != null) {
      adapterFactory.close();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    if (hasFocus) {
//            try{
//                Uri uri = Uri.parse("rtsp://admin:12345@192.168.133.64:554/h264/ch1/main/av_stream");
//                videoView = (VideoView)this.findViewById(R.id.videoView);
//                videoView.setMediaController(new MediaController(this));
//                videoView.setVideoURI(uri);
//                videoView.requestFocus();
//                videoView.start();
//            }catch (Exception ex){
//                videoView.pause();
//            }
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return this.detector.onTouchEvent(event); //touch事件交给手势处理。
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

  private void initSensorStatusViewAdapter() {

    this.adapterFactory = SensorAdapterFactory.getInstance(this);
    adapterFactory.bindService();

    this.sensorAdapter = new SensorStatusViewAdapter((ViewGroup) this.findViewById(R.id.mainView));
    sensorAdapter.setViewData(adapterFactory.getBinder());
    adapterFactory.registViewAdapter(sensorAdapter);
    chartAdapter = new MultipleSeriesChartAdapter(this);
    chartAdapter.onBindView((ViewGroup) this.findViewById(R.id.chartView));
  }
}
