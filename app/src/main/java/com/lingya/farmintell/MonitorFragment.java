package com.lingya.farmintell;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 */
public class MonitorFragment extends Fragment {

  private static final String TAG = MonitorFragment.class.getSimpleName();



  private VideoView videoView;

  public MonitorFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_monitor, container, false);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onResume() {
    super.onResume();
    startVideo();
  }

  public void onPause() {
    super.onPause();

  }

  @Override
  public void onStop() {
    super.onStop();
    if (this.videoView != null) {
      this.videoView.stopPlayback();

    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  private void startVideo() {
    String videoSource = "rtsp://admin:12345@192.168.0.64:554/mpeg4/ch1/sub/av_stream";
    // Create a new media player and set the listeners
    //playVideo(videoSource);
    Activity activity = this.getActivity();
    videoView = (VideoView) activity.findViewById(R.id.videoView);
    videoView.setVideoURI(Uri.parse(videoSource));
    //videoView.requestFocus();
    videoView.start();
  }
}
