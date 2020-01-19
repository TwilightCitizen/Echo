/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

public class GameOrLeaderboardActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_or_leaderboard );
        setAmbientEnabled();
        setupButtonStartNewGame();
        setupButtonViewLeaderboard();
    }

    private void setupButtonStartNewGame() {
        setupNavigationButton( R.id.buttonStartNewGame, GoogleOrGuestActivity.class );
    }

    private void setupButtonViewLeaderboard() {
        // setupNavigationButton( R.id.buttonViewLeaderboard, LeaderboardActivity.class );
    }

    private< T extends WearableActivity > void setupNavigationButton( int buttonId, Class< T > targetActivity ) {
        Button navigationButton  = findViewById( buttonId );
        Intent activityIntent    = new Intent( this, targetActivity );

        navigationButton.setOnClickListener( ( View v ) -> {
            startActivity( activityIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
        } );
    }
}
