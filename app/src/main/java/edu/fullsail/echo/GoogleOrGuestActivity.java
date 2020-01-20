/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

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

public class GoogleOrGuestActivity extends WearableActivity {
    private static final int    REQUEST_GOOGLE_SIGN_IN = 1;
    public  static final String GOOGLE_SIGN_IN_ACCOUNT = "GOOGLE_SIGN_IN_ACCOUNT";

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_google_or_guest );
        setAmbientEnabled();
        setupButtonUseGoogleAccount();
        setupButtonUseGuestAccount();
    }

    private void setupButtonUseGoogleAccount() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail().build();

        GoogleSignInClient  googleSignInClient     = GoogleSignIn.getClient( this, googleSignInOptions );
        Intent              googleSignInIntent     = googleSignInClient.getSignInIntent();
        Button              useGoogleAccountButton = findViewById( R.id.buttonUseGoogleAccount );

        useGoogleAccountButton.setOnClickListener( ( View v ) ->
            startActivityForResult( googleSignInIntent, REQUEST_GOOGLE_SIGN_IN )
        );
    }

    private void setupButtonUseGuestAccount() {
        Button useGuestAccountButton = findViewById( R.id.buttonUseGuestAccount );

        Intent echoGameIntent = new Intent( this, EchoGameActivity.class );

        useGuestAccountButton.setOnClickListener( ( View v ) -> {
            startActivity( echoGameIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
        } );
    }

    @Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if( requestCode != REQUEST_GOOGLE_SIGN_IN ) return;

        Task< GoogleSignInAccount > googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent( data );

        handleGoogleSignInResult( googleSignInAccountTask );
    }

    private void handleGoogleSignInResult( Task< GoogleSignInAccount > googleSignInAccountTask ) {
        try {
            GoogleSignInAccount googleSignInAccount    = googleSignInAccountTask.getResult( ApiException.class );
            Intent              echoGameIntent         = new Intent( this, EchoGameActivity.class );

            Runnable            transitionNotification = () -> {
                startActivity( echoGameIntent );
                overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            };

            Handler             transitionHandler      = new Handler();
            final int           delaySeconds           = getResources().getInteger( R.integer.notification_delay_seconds );
            final long          delayMillis            = TimeUnit.SECONDS.toMillis( delaySeconds );

            notifyGoogleSignInSuccess( ConfirmationActivity.SUCCESS_ANIMATION, R.string.successful_google_sign_in );
            echoGameIntent.putExtra( GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount );
            transitionHandler.postDelayed( transitionNotification, delayMillis );

        } catch( ApiException e ) {
            notifyGoogleSignInSuccess( ConfirmationActivity.FAILURE_ANIMATION, R.string.failed_google_sign_in );
        }
    }

    private void notifyGoogleSignInSuccess( int successAnimation, int messageId ) {
        Intent googleSignInSuccessIntent = new Intent( this, ConfirmationActivity.class );

        googleSignInSuccessIntent.putExtra( ConfirmationActivity.EXTRA_ANIMATION_TYPE, successAnimation );
        googleSignInSuccessIntent.putExtra( ConfirmationActivity.EXTRA_MESSAGE, getString( messageId ) );
        startActivity( googleSignInSuccessIntent );
    }
}
