package com.lingya.farmintell;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

public class CamMonitorActivity extends ActionBarActivity {

  private VideoView videoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    setContentView(R.layout.activity_cam_monitor);
    startVideo();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_cam_monitor, menu);
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

  private void startVideo() {
    String videoSource = "rtsp://admin:12345@192.168.0.64:554/mpeg4/ch1/sub/av_stream";
    // Create a new media player and set the listeners
    //playVideo(videoSource);
    videoView = (VideoView) this.findViewById(R.id.videoView);
    videoView.setVideoURI(Uri.parse(videoSource));
    videoView.requestFocus();
    videoView.start();
    videoView.seekTo(10);
  }
}
