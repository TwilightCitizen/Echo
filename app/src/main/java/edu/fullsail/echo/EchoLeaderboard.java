/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

class EchoLeaderboard {
    // The singleton instance.
    private static EchoLeaderboard instance = null;

    // Leaderboard constants.
    private static final String            DISPLAY_NAME      = "DISPLAY_NAME";
    private static final String            FINAL_SCORE       = "FINAL_SCORE";
    private static final String            LEADERBOARD       = "LEADERBOARD";

    // Private constructor prevents instantiation.
    private EchoLeaderboard() {};

    // Return the singleton instance, instantiating as needed, also initializing the Firebase
    // instance that this will use.
    static EchoLeaderboard getInstance() {
        if( instance == null ) instance = new EchoLeaderboard();

        return instance;
    }

    void publishScoreToLeaderboard( Context context, GoogleSignInAccount googleSignInAccount, int finalScore ) {
        // Guard against publishing the score of a guest user.
        if( googleSignInAccount == null ) return;

        // Get the Google Sign In account ID.
        String googleSignInId = googleSignInAccount.getId();
        String displayName    = googleSignInAccount.getDisplayName();

        // Guard against no Google Sign In account ID.
        if( googleSignInId == null ) return;

        try {
            // Initialize Firebase and get the Cloud Firestore instance.
            FirebaseApp.initializeApp( context );

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection( LEADERBOARD )
                .document( googleSignInId )
                .get()
                .addOnCompleteListener( ( @NonNull Task< DocumentSnapshot > task ) -> {
                    if( task.isSuccessful() ) {
                        DocumentSnapshot retrievedLeaderBoardEntry = task.getResult();

                        if( retrievedLeaderBoardEntry == null ) return;

                        int retrievedFinalScore = (int) (long) retrievedLeaderBoardEntry.get( FINAL_SCORE );

                        if( retrievedFinalScore > finalScore ) return;

                        firebaseFirestore.collection( LEADERBOARD )
                            .document( googleSignInId )
                            .update( FINAL_SCORE, finalScore )
                            .addOnSuccessListener( ( Void aVoid ) -> {
                                Log.wtf( "LEADERBOARD UPDATED", "" );
                            } ).addOnFailureListener( ( @NonNull Exception e ) -> {
                                Log.wtf( "LEADERBOARD UPDATE FAILED", e.getLocalizedMessage() );
                            }
                        );
                    } else {
                        // Create a new entry for the leaderboard.
                        Map< String, Object > leaderboardEntry = new HashMap<>();

                        leaderboardEntry.put( DISPLAY_NAME, displayName );
                        leaderboardEntry.put( FINAL_SCORE,  finalScore );

                        firebaseFirestore
                            .collection( LEADERBOARD )
                            .document( googleSignInId )
                            .set( leaderboardEntry )
                            .addOnSuccessListener( ( Void aVoid ) -> {
                                Log.wtf( "LEADERBOARD WRITTEN", "" );
                            } ).addOnFailureListener( ( @NonNull Exception e ) -> {
                                Log.wtf( "LEADERBOARD WRITE FAILED", e.getLocalizedMessage() );
                            }
                        );
                    }
                }
            );
        } catch( Exception e ) {
            Log.wtf( "ERROR", e.getLocalizedMessage() );
        }
    }
}
