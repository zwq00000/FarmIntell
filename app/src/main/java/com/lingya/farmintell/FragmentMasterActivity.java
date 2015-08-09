package com.lingya.farmintell;

import com.google.zxing.WriterException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.lingya.farmintell.adapters.MultipleSeriesChartAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;
import com.lingya.farmintell.adapters.SensorLogRequestCallback;
import com.lingya.farmintell.adapters.SensorStatusViewAdapter;
import com.lingya.farmintell.httpserver.HttpService;
import com.lingya.qrcodegenerator.QRCodeFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

//@WindowFeature(Window.FEATURE_NO_TITLE)
//@Fullscreen
@EActivity(R.layout.activity_main)
public class FragmentMasterActivity extends Activity {

  private static final String TAG = "MainActivity";

  @AnimationRes
  Animation left_in;
  @AnimationRes
  Animation left_out;
  @AnimationRes
  Animation right_in;
  @AnimationRes
  Animation right_out;

  @ViewById(R.id.viewFlipper)
  ViewFlipper viewFlipper;

  @ViewById
  ImageView barCodeView;

  /**
   * 手势侦听器
   */
  GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
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
        viewFlipper.setInAnimation(left_in);
        viewFlipper.setOutAnimation(left_out);
        viewFlipper.showNext();//向右滑动
        return true;
      } else if (e1.getX() - e2.getX() < -50) {
        viewFlipper.setInAnimation(right_in);
        viewFlipper.setOutAnimation(right_out);
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


  /*
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  /*
    setContentView(R.layout.activity_main);
    viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);

    mInflater = LayoutInflater.from(this);

    detector = new GestureDetector(this, gestureListener);
    //往viewFlipper添加View
    viewFlipper.addView(mInflater.inflate(R.layout.fragment_main, null));
    viewFlipper.addView(mInflater.inflate(R.layout.fragment_monitor, null));

    //动画效果
    left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
    left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
    right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
    right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);

    initSensorStatusViewAdapter();

  } */

  /**
   * 获取主机 IpV4 地址
   *
   * @param interfaceName 网络接口名称 如 'eth0' 'wlan0'...
   * @return 本机的ip地址 xxx.xxx.xxx.xxx
   */
  private static String getHostIpv4(String interfaceName) throws SocketException {
    String ipaddress = "";
    NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
    for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
         inetAddresses.hasMoreElements(); ) {
      InetAddress inetAddress = inetAddresses.nextElement();
      if (!inetAddress.isLoopbackAddress()) {
        ipaddress = inetAddress.getHostAddress().toString();
        if (!ipaddress.contains("::")) {
          return ipaddress;
        }
      }
    }
    return ipaddress;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (this.adapterFactory != null) {
      adapterFactory.close();
    }
  }

  @AfterViews
  void initFipper() {
    LayoutInflater inflater = LayoutInflater.from(this);

    detector = new GestureDetector(this, gestureListener);
    //往viewFlipper添加View
    viewFlipper.addView(inflater.inflate(R.layout.fragment_main, null));
    viewFlipper.addView(inflater.inflate(R.layout.fragment_monitor, null));
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return this.detector.onTouchEvent(event); //touch事件交给手势处理。
    //return super.onTouchEvent(event);
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

  @AfterViews
  void initBarCodeView() {
    try {
      String url = "http://" + getHostIpv4("eth0") + ":8080/index.html";
      Bitmap image = QRCodeFactory.renderToBitmap(url);
      barCodeView.setImageBitmap(image);
    } catch (SocketException e) {
      e.printStackTrace();
    } catch (WriterException e) {
      e.printStackTrace();
    }

  }

  /**
   * 初始化 传感器状态 适配器
   */
  @AfterViews
  void initSensorStatusViewAdapter() {

    this.adapterFactory = SensorAdapterFactory.getInstance(this);
    adapterFactory.bindService();

    this.sensorAdapter = new SensorStatusViewAdapter();
    sensorAdapter.onBindView((ViewGroup) this.findViewById(R.id.mainView));
    sensorAdapter.setViewData(adapterFactory.getBinder());
    adapterFactory.registViewAdapter(sensorAdapter);

    //chart adapter
    chartAdapter = new MultipleSeriesChartAdapter(this);
    chartAdapter.onBindView((ViewGroup) this.findViewById(R.id.chartView));
    chartAdapter.setViewData(adapterFactory.getBinder());

    sensorAdapter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof String) {
          chartAdapter.showSensorHistory(tag.toString());
        }
      }
    });

    //start HttpService
  }

  @AfterViews
  void startHttpService() {
    Intent
        intent =
        new Intent(HttpService.ACTIVITY_SERVICE, Uri.EMPTY, this, HttpService.class);
    final SensorLogRequestCallback sensorLogCallback = new SensorLogRequestCallback(this);
    this.bindService(intent, new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        HttpService.IHttpServerBinder binder = (HttpService.IHttpServerBinder) service;
        AsyncHttpServer httpServer = binder.getHttpServer();
        httpServer.get("/sensors/.*?", sensorLogCallback);
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {

      }
    }, Context.BIND_AUTO_CREATE);
  }
}
