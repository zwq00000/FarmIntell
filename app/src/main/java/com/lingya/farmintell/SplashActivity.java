package com.lingya.farmintell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * Created by Administrator on 2014/11/5.
 */
public class SplashActivity extends Activity {

    private RelativeLayout splash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //ViewUtils.inject(this);
        //setupTabView();
    }

    public void mainClick(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        this.startActivity(mainIntent);
        this.finish();
    }
}
