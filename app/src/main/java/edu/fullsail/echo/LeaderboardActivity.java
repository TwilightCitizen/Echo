/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
Leaderboard activity will show players' top scores, sorted from highest to lowest score, to be
obtained by a Firebase database.  Presently, functionality just displays an empty activity so
navigation could be stubbed out.
*/
public class LeaderboardActivity extends WearableActivity {
    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_leaderboard );

        // Get a handle to the retrieving frame.
        FrameLayout frameRetrieving = findViewById( R.id.frameRetrieving );

        EchoLeaderboard.getInstance().getTopLimitLeaders( this, 100,
            ( ArrayList< EchoLeaderBoardEntry > topLimitLeaders ) -> {
                // Hide the retrieving progress.
                frameRetrieving.setVisibility( View.GONE );

                // Show the retrieved leaderboard or the empty progress.
                FrameLayout frameLeaders = findViewById( R.id.frameLeaders );
                FrameLayout frameEmpty   = findViewById( R.id.frameEmpty );

                boolean zeroLimitLeaders = topLimitLeaders.size() < 1;

                frameLeaders.setVisibility( zeroLimitLeaders ? View.GONE    : View.VISIBLE );
                frameEmpty.setVisibility(   zeroLimitLeaders ? View.VISIBLE : View.GONE    );
            },

            ( Exception e ) -> {
                // Hide the retrieving progress.
                frameRetrieving.setVisibility( View.GONE );

                // Show the empty progress.
                FrameLayout frameEmpty = findViewById( R.id.frameEmpty );

                frameEmpty.setVisibility( View.VISIBLE );
            }
        );
    }
}
