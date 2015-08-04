package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 测试传感器状态 Created by zwq00000 on 2015/6/22.
 */
public class SensorStatusTest extends AndroidTestCase {

  private Realm realm;

  public void setUp() throws Exception {
    super.setUp();
    // Set the module in the RealmConfiguration to allow only classes defined by the module.
    //RealmConfiguration config = new RealmConfiguration.Builder(getContext()).build();
    //Realm.deleteRealm(config);
    realm = RealmFactory.getInstance(getContext());
  }

  public void tearDown() throws Exception {
    if (realm != null) {
      realm.close();
    }
    RealmConfiguration config = new RealmConfiguration.Builder(getContext()).build();
    Realm.deleteRealm(config);
  }

}