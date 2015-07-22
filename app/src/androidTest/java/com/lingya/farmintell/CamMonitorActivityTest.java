package com.lingya.farmintell;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by zwq00000 on 2015/7/10.
 */
public class CamMonitorActivityTest extends ActivityInstrumentationTestCase2<CamMonitorActivity> {

  public CamMonitorActivityTest() {
    super(CamMonitorActivity.class);
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {

  }

  public void testOnCreate() throws Exception {
    CamMonitorActivity activity = super.getActivity();
    this.getInstrumentation().callActivityOnCreate(activity, null);
    Thread.sleep(1000 * 1000);
  }
}