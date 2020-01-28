/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import java.util.Map;

/*
Leaderboard activity will show players' top scores, sorted from highest to lowest score, to be
obtained by a Firebase database.  Presently, functionality just displays an empty activity so
navigation could be stubbed out.
*/
public class LeaderboardActivity extends WearableActivity implements EchoLeaderboard.EchoLeaderboardListener {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_leaderboard );

        EchoLeaderboard.getInstance().getTopLimitLeaders( this );
    }

    @Override public void onGotTopLimitleaders( Map< String, Integer > topLimitLeaders ) {

    }
}
