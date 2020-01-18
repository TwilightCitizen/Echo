package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class SplashActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        // Enables Always-on
        setAmbientEnabled();
    }
}
