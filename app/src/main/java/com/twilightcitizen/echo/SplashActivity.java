/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package com.twilightcitizen.echo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;

import java.util.concurrent.TimeUnit;

/*
Splash activity displays a splash screen for the user, featuring the Echo logo, and transitions to
the Game or Leaderboard activity after a configurable delay.  The Splash activity cannot be
returned to by the user via back navigation after transition.
*/
@SuppressWarnings("deprecation")
public class SplashActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        transitionSplash();
    }

    private void transitionSplash() {
        // Intent to load the Game or Leaderboard activity.
        Intent     gameOrLeaderBoardIntent = new Intent( this, GameOrLeaderboardActivity.class );

        // Runnable to transition from the Splash activity.
        Runnable   transitionSplash        = () -> {
            startActivity( gameOrLeaderBoardIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            // Prevent back navigation to the Splash activity.
            finish();
        };

        // Handler to manage the the runnable transition.
        Handler    transitionHandler       = new Handler();

        // Delay before transition.
        final int  delaySeconds            = getResources().getInteger( R.integer.splash_delay_seconds );
        final long delayMillis             = TimeUnit.SECONDS.toMillis( delaySeconds );

        // Transition to the Game or Leaderboard activity after delay.
        transitionHandler.postDelayed( transitionSplash, delayMillis );
    }
}
