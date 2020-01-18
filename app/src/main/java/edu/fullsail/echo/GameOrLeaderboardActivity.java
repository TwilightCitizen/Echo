package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class GameOrLeaderboardActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_or_leaderboard );
        setAmbientEnabled();
    }
}
