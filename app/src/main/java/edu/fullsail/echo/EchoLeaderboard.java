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
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private EchoLeaderboard() {}

    // Return the singleton instance, instantiating as needed, also initializing the Firebase
    // instance that this will use.
    static EchoLeaderboard getInstance() {
        if( instance == null ) instance = new EchoLeaderboard();

        return instance;
    }

    public interface OnGotTopLimitLeadersListener {
        void onGotTopLimitleaders( Map< String, Integer > topLimitLeaders );
    }

    void publishScoreToLeaderboard( Context context, GoogleSignInAccount googleSignInAccount, int finalScore ) {
        // Guard against publishing the score of a guest user.
        if( googleSignInAccount == null ) return;

        // Get the Google Sign In account ID.
        String googleSignInId = googleSignInAccount.getId();
        String displayName    = googleSignInAccount.getDisplayName();

        // Guard against no Google Sign In account ID.
        if( googleSignInId == null ) return;

        // Initialize Firebase and get the Cloud Firestore instance.
        FirebaseApp.initializeApp( context );

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Get any leaderboard entry for the signed in user.
        firebaseFirestore.collection( LEADERBOARD )
            .document( googleSignInId )
            .get()

            .addOnSuccessListener( ( DocumentSnapshot documentSnapshot ) -> {
                // Create the leaderboard entry if it does not exist.
                if( !documentSnapshot.exists() ) {
                    publishNewLeaderboardEntry( firebaseFirestore, googleSignInId, displayName, finalScore );

                    return;
                }

                // If it is greater than the final score the user just got, do nothing.
                if( existingScoreBeatsNewScore( documentSnapshot, finalScore ) ) return;

                // Otherwise, update the leaderboard.
                updateExisingLeaderboardEntry( firebaseFirestore, googleSignInId, displayName, finalScore );
            } )

            .addOnFailureListener( ( @NonNull Exception e ) -> {
                // TODO: Handle Failure Here
            } );
    }

    private boolean existingScoreBeatsNewScore( DocumentSnapshot retrievedLeaderBoardEntry, int finalScore ) {
        // Evil cast the final score on the existing leaderboard entry.
        int retrievedFinalScore = (int) (long) retrievedLeaderBoardEntry.get( FINAL_SCORE );

        return retrievedFinalScore > finalScore;
    }

    private void publishNewLeaderboardEntry(
        FirebaseFirestore firebaseFirestore, String googleSignInId, String displayName, int finalScore
    ) {
        // Create a new entry for the leaderboard.
        Map< String, Object > leaderboardEntry = new HashMap<>();

        leaderboardEntry.put( DISPLAY_NAME, displayName );
        leaderboardEntry.put( FINAL_SCORE,  finalScore );

        // Write the new entry to the leaderboard.
        firebaseFirestore
            .collection( LEADERBOARD )
            .document( googleSignInId )
            .set( leaderboardEntry )
            .addOnSuccessListener( ( Void aVoid ) -> Log.wtf( "LEADERBOARD WRITTEN", "" ) )
            .addOnFailureListener( ( @NonNull Exception e ) ->  Log.wtf( "LEADERBOARD WRITE FAILED", e.getLocalizedMessage() ) );
    }

    private void updateExisingLeaderboardEntry(
        FirebaseFirestore firebaseFirestore, String googleSignInId, String displayName, int finalScore
    ) {
        // Otherwise, update the leaderboard.
        firebaseFirestore
            .collection( LEADERBOARD )
            .document( googleSignInId )
            .update( FINAL_SCORE, finalScore )
            .addOnSuccessListener( ( Void aVoid ) -> Log.wtf( "LEADERBOARD UPDATED", "" ) )
            .addOnFailureListener( ( @NonNull Exception e ) -> Log.wtf( "LEADERBOARD UPDATE FAILED", e.getLocalizedMessage() ) );
    }

    void getTopLimitLeaders( Context context, int limit, OnGotTopLimitLeadersListener onGotTopLimitLeadersListener ) {
        // Top limit leaders on the leaderboard, if any.
        Map< String, Integer > topLimitLeaders = new HashMap<>();

        // Initialize Firebase and get the Cloud Firestore instance.
        FirebaseApp.initializeApp( context );

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Get the top limit leaders, sorted by final score descending.
        firebaseFirestore
            .collection( LEADERBOARD )
            .orderBy( FINAL_SCORE, Query.Direction.DESCENDING )
            .limit( limit )
            .get()

            .addOnSuccessListener( ( QuerySnapshot queryDocumentSnapshots ) -> {
                // Put the top limit leaders in the hash map.
                for( QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots ) {
                    topLimitLeaders.put(
                        queryDocumentSnapshot.getId(),
                        (int) (long) queryDocumentSnapshot.get( FINAL_SCORE )
                    );
                }

                // Notify the caller that the top limit leaders were retrieved.
                onGotTopLimitLeadersListener.onGotTopLimitleaders( topLimitLeaders );
            } )

            .addOnFailureListener( ( @NonNull Exception e ) -> {
                // TODO: Handle Failure Here
            } );
    }
}
