/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class GameOverActivity extends Activity {
    // Final score for the user.
    private int finalScore;

    // Google Account obtained from calling activity.
    private GoogleSignInAccount googleSignInAccount = null;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_over );
        getFinalScore();
        getGoogleSignInAccount();
        gameOver();
        publishScoreToLeaderboard();
    }

    private void getFinalScore() {
        // Get the final score passed in from the calling activity.
        finalScore = getIntent().getIntExtra( EchoGameActivity.FINAL_SCORE, 0 );
    }

    private void getGoogleSignInAccount() {
        // Get any authenticated Google Account passed from the calling activity.
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void gameOver() {
        // Get handles to the game over and final score text fields.
        TextView textFinalScore = findViewById( R.id.textFinalScore );
        // Get handle to the go back button.
        Button buttonGoBack   = findViewById( R.id.buttonGoBack  );

        // Set the final score text to show the player's final score.
        textFinalScore.setText( String.format( getString( R.string.final_score ), finalScore ) );

        // Set the go back button listener to dismiss the finished game.
        buttonGoBack.setOnClickListener( ( View v ) -> finish() );
    }

    private void publishScoreToLeaderboard() {
        // Guard against publishing the score of a guest user.
        if( googleSignInAccount == null ) return;

        try {
            // Initialize Firebase and get Cloud Firestore instance.
            FirebaseApp.initializeApp( this );

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            // Create a new entry for the leaderboard.
            Map< String, Object > leaderboardEntry = new HashMap<>();

            leaderboardEntry.put( "DISPLAY_NAME", googleSignInAccount.getDisplayName() );
            leaderboardEntry.put( "FINAL_SCORE",  finalScore );

            firebaseFirestore
                .collection( "LEADERBOARD" )
                .document( googleSignInAccount.getId() )
                .set( leaderboardEntry )
                .addOnSuccessListener( ( Void aVoid ) -> {
                    Log.wtf( "LEADERBOARD ENTRY WRITTEN", "" );
                } ).addOnFailureListener( ( @NonNull Exception e ) -> {
                    Log.wtf( "LEADERBOARD ENTRY FAILED", e.getLocalizedMessage() );
                }
            );

            firebaseFirestore.collection( "LEADERBOARD" ).get()
                .addOnCompleteListener( ( @NonNull Task< QuerySnapshot > task ) -> {
                    if ( task.isSuccessful() ) {
                        for ( QueryDocumentSnapshot document : task.getResult() ) {
                            Log.wtf( "LEADERBOARD", document.getId() + " => " + document.getData() );
                        }
                    } else {
                        Log.wtf( "ERROR", "Error getting documents.", task.getException() );
                    }
                } );
        } catch( Exception e ) {
            Log.wtf( "ERROR", e.getLocalizedMessage() );
        }
    }
}
