/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
Leaderboard activity will show players' top scores, sorted from highest to lowest score, to be
obtained by a Firebase database.  Presently, functionality just displays an empty activity so
navigation could be stubbed out.
*/
public class LeaderboardActivity extends WearableActivity {
    private class LeaderAdapter extends RecyclerView.Adapter< LeaderAdapter.LeaderViewHolder > {
        class LeaderViewHolder extends RecyclerView.ViewHolder {
            TextView textLeaderScore;
            TextView textLeaderName;

            LeaderViewHolder( @NonNull View itemView ) {
                super( itemView );

                textLeaderScore = itemView.findViewById( R.id.textLeaderScore );
                textLeaderName  = itemView.findViewById( R.id.textLeaderName  );
            }
        }

        private ArrayList< EchoLeaderBoardEntry > echoLeaderBoardEntries;

        LeaderAdapter( ArrayList< EchoLeaderBoardEntry > leaderBoardEntries ) {
            this.echoLeaderBoardEntries = leaderBoardEntries;
        }

        @NonNull @Override public LeaderViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
            View leaderboardItem = LayoutInflater
                .from( parent.getContext() ).inflate( R.layout.item_leaderboard, parent, false );

            return new LeaderViewHolder( leaderboardItem );
        }

        @Override public void onBindViewHolder( @NonNull LeaderViewHolder holder, int position ) {
            EchoLeaderBoardEntry echoLeaderBoardEntry = echoLeaderBoardEntries.get( position );

            holder.textLeaderScore.setText( String.valueOf( echoLeaderBoardEntry.getFinalScore() ) );
            holder.textLeaderName.setText( echoLeaderBoardEntry.getDisplayName() );
        }

        @Override public int getItemCount() { return echoLeaderBoardEntries.size(); }
    }

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_leaderboard );
        setupLeaderboard();
    }

    private void setupLeaderboard() {
        // Get a handle to the retrieving frame.
        FrameLayout frameRetrieving = findViewById( R.id.frameRetrieving );

        EchoLeaderboard.getInstance().getTopLimitLeaders( this, 100,
            ( ArrayList< EchoLeaderBoardEntry > topLimitLeaders ) -> {
                // Hide the retrieving progress.
                frameRetrieving.setVisibility( View.GONE );

                // Show the retrieved leaderboard or the empty progress.
                FrameLayout frameLeaders     = findViewById( R.id.frameLeaders );
                FrameLayout frameEmpty       = findViewById( R.id.frameEmpty );
                boolean     zeroLimitLeaders = topLimitLeaders.size() < 1;

                frameLeaders.setVisibility( zeroLimitLeaders ? View.GONE    : View.VISIBLE );
                frameEmpty.setVisibility(   zeroLimitLeaders ? View.VISIBLE : View.GONE    );

                // Guard against setting up an empty recycler view.
                if( zeroLimitLeaders ) return;

                RecyclerView               recycleLeaders = findViewById( R.id.recycleLeaders );
                RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager( this );
                RecyclerView.Adapter       adapterLeaders = new LeaderAdapter( topLimitLeaders );

                recycleLeaders.setHasFixedSize( true );
                recycleLeaders.setLayoutManager( layoutManager );
                recycleLeaders.setAdapter( adapterLeaders );

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
