package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class GameOrLeaderboardActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_or_leaderboard );
        
        // Enables Always-on
        setAmbientEnabled();
    }
}
