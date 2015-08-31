package com.lingya.farmintell.httpserver;

import android.content.Intent;
import android.test.ServiceTestCase;

/**
 * Created by zwq00000 on 2015/7/10.
 */
public class HttpServiceTest extends ServiceTestCase {

  /**
   * Constructor
   */
  public HttpServiceTest() {
    //noinspection unchecked
    super(HttpService.class);
  }

  public void setUp() throws Exception {
    super.setUp();
    startService(new Intent());
  }

  public void tearDown() throws Exception {

  }

  public void testOnBind() throws Exception {
    this.bindService(new Intent());
    Thread.sleep(1000 * 60 * 60 * 24);
  }
}