package edu.fullsail.echo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        setAmbientEnabled();

        Runnable   transitionSplash  = () -> {
            startActivity( new Intent( this, GameOrLeaderboardActivity.class ) );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            finish();
        };

        Handler    transitionHandler = new Handler();

        final int  delaySeconds      = getResources().getInteger( R.integer.splash_delay_seconds );
        final long delayMillis       = TimeUnit.SECONDS.toMillis( delaySeconds );

        transitionHandler.postDelayed( transitionSplash, delayMillis );
    }
}
