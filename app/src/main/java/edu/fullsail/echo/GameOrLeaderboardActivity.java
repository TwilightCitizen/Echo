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

/*
Game or Leaderboard activity presents binary navigation actions to the user for the Google or Guest
activity or the Leaderboard activity.
*/
public class GameOrLeaderboardActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_or_leaderboard );
        setAmbientEnabled();
        setupButtonStartNewGame();
        setupButtonViewLeaderboard();
    }

    private void setupButtonStartNewGame() {
        // Setup the Start New Game Button to navigate to the Google or Guest activity.
        setupNavigationButton( R.id.buttonStartNewGame, GoogleOrGuestActivity.class );
    }

    private void setupButtonViewLeaderboard() {
        // Setup the View Leaderboard Button to navigate to the Leaderboard activity.
        setupNavigationButton( R.id.buttonViewLeaderboard, LeaderboardActivity.class );
    }

    // Accept the class of a WearableActivity and the ID of a button for which the on click listener
    // will be set to navigate to the activity.
    private< T extends WearableActivity > void setupNavigationButton( int buttonId, Class< T > targetActivity ) {
        // Button specified by ID and activity to navigate to specified by class.
        Button navigationButton  = findViewById( buttonId );
        Intent activityIntent    = new Intent( this, targetActivity );

        // Set the button on click listener to navigate to the activity.
        navigationButton.setOnClickListener( ( View v ) -> {
            startActivity( activityIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
        } );
    }
}
