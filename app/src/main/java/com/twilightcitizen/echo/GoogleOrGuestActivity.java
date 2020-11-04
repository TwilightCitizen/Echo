/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package com.twilightcitizen.echo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

/*
Google or Guest activity presents binary navigation actions to the user for the Game Mode activity.
In either case, the user will navigate to the Game Mode activity.  Guest selection simply navigates
directly to it, while Google Account selection first attempts to authenticate the user via Google
sign in.  Google sign in is enabled via a corresponding Google APIs project for Echo, and for it to
work, the user must first transfer their valid Google account(s) from a phone synced with the watch
using the Wear app on the phone.  Google Account selection notifies the user upon success or failure,
and upon failure, navigation to the Game Mode activity is halted.
*/
@SuppressWarnings("deprecation")
public class GoogleOrGuestActivity extends WearableActivity {
    // Google Sign In request code.
    private static final int    REQUEST_GOOGLE_SIGN_IN = 10;
    // Google Account tag for passing authenticated account to Game Mode activity.
    public  static final String GOOGLE_SIGN_IN_ACCOUNT = "GOOGLE_SIGN_IN_ACCOUNT";

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_google_or_guest );
        setupButtonUseGoogleAccount();
        setupButtonUseGuestAccount();
    }

    private void setupButtonUseGoogleAccount() {
        // Request email and default options during Google sign in attempts.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail().build();

        // Client, intent, and button for Google Sign In.
        GoogleSignInClient  googleSignInClient     = GoogleSignIn.getClient( this, googleSignInOptions );
        Intent              googleSignInIntent     = googleSignInClient.getSignInIntent();
        Button              useGoogleAccountButton = findViewById( R.id.buttonUseGoogleAccount );

        useGoogleAccountButton.setOnClickListener( ( View v ) ->
            // Start built-in Google Sign In activity.
            startActivityForResult( googleSignInIntent, REQUEST_GOOGLE_SIGN_IN )
        );
    }

    private void setupButtonUseGuestAccount() {
        // Button and intent for Game Mode activity navigation.
        Button useGuestAccountButton = findViewById( R.id.buttonUseGuestAccount );
        Intent gameModeIntent        = new Intent( this, GameOptionsActivity.class );

        useGuestAccountButton.setOnClickListener( ( View v ) -> {
            // Start the Game Options activity with a custom transition.  Finish afterward so Google
            // or Guest Activity is removed from back stack.
            startActivity( gameModeIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            finish();
        } );
    }

    @Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        // Guard against non-Google Sign In request.
        if( requestCode != REQUEST_GOOGLE_SIGN_IN ) return;

        // Obtain asynchronous task in which Google Sign In activity authenticates user.
        Task< GoogleSignInAccount > googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent( data );

        // Process results of Google Sign In task.
        handleGoogleSignInResult( googleSignInAccountTask );
    }

    private void handleGoogleSignInResult( Task< GoogleSignInAccount > googleSignInAccountTask ) {
        try {
            // Attempt to obtain the authenticated account from the completed Google Sign In task.
            GoogleSignInAccount googleSignInAccount    = googleSignInAccountTask.getResult( ApiException.class );
            // Intent for Game Options activity.
            Intent              gameOptionsIntent      = new Intent( this, GameOptionsActivity.class );

            // Runnable to transition to Game Options activity after delay so that notification of
            // successful Google Sign In remains on screen long enough for the user to see it.
            Runnable            transitionNotification = () -> {
                // Start the Game Options activity with a custom transition.  Finish afterward so Google
                // or Guest Activity is removed from back stack.
                startActivity( gameOptionsIntent );
                overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
                finish();
            };

            // Handler and delay for Game Mode activity transition.
            Handler             transitionHandler      = new Handler();
            final int           delaySeconds           = getResources().getInteger( R.integer.notification_delay_seconds );
            final long          delayMillis            = TimeUnit.SECONDS.toMillis( delaySeconds );

            // Notify the user of successful Google Sign in, pass the authenticated Google account
            // to the Game Mode activity, and navigate to it after a brief delay.
            notifyGoogleSignInSuccess( ConfirmationActivity.SUCCESS_ANIMATION, R.string.successful_google_sign_in );
            gameOptionsIntent.putExtra( GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount );
            transitionHandler.postDelayed( transitionNotification, delayMillis );

        } catch( ApiException e ) {
            // Error occurred in corresponding Google API project for echo.  Notify the user of
            // unsuccessful Google Sign In.
            notifyGoogleSignInSuccess( ConfirmationActivity.FAILURE_ANIMATION, R.string.failed_google_sign_in );
        }
    }

    private void notifyGoogleSignInSuccess( int successAnimation, int messageId ) {
        // Confirmation activity to show user success or failure of Google Sign In.
        Intent googleSignInSuccessIntent = new Intent( this, ConfirmationActivity.class );

        // Start the confirmation notification with the passed in success animation and message.
        googleSignInSuccessIntent.putExtra( ConfirmationActivity.EXTRA_ANIMATION_TYPE, successAnimation );
        googleSignInSuccessIntent.putExtra( ConfirmationActivity.EXTRA_MESSAGE, getString( messageId ) );
        startActivity( googleSignInSuccessIntent );
    }
}
