/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/*
Game Options activity allows the user to configure how the subsequent game of Echo will be played.
Options are selectable via custom toggle buttons, both defaulting to true.  A single navigation
button takes the user to the Echo Game activity, which receives any Google Account passed to this
activity along with the configured options.
*/
public class GameOptionsActivity extends WearableActivity {
    // Tags for passing the game options to the Echo Game activity.
    public static final String       FLASH_BUTTONS = "FLASH_BUTTONS";
    public static final String       PLAY_SOUNDS   = "PLAY_SOUNDS";

    // Toggles for game options.
    private             ToggleButton toggleFlashButtons;
    private             ToggleButton togglePlaySounds;

    // Google Sign In account from calling activity.
    private GoogleSignInAccount googleSignInAccount = null;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_options );
        getGoogleSignInAccount();
        setupToggleButtons();
        setupButtonStartGame();
    }

    private void getGoogleSignInAccount() {
        // Get any authenticated Google Account passed from the calling activity.
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void setupToggleButtons() {
        // Toggle Buttons for flashing buttons and/or playing tunes.
        toggleFlashButtons = findViewById( R.id.toggleFlashButtons );
        togglePlaySounds  = findViewById( R.id.togglePlayTunes    );

        // Flashing buttons and playing tunes cannot both be disabled.

        // Setup the check change listener for flashing buttons.
        toggleFlashButtons.setOnCheckedChangeListener( ( CompoundButton buttonView, boolean isChecked ) -> {
            if( !isChecked && !togglePlaySounds.isChecked()   ) togglePlaySounds.setChecked(   true );
        } );

        // Setup the check change listener for playing tunes.
        togglePlaySounds.setOnCheckedChangeListener( ( CompoundButton buttonView, boolean isChecked ) -> {
            if( !isChecked && !toggleFlashButtons.isChecked() ) toggleFlashButtons.setChecked( true );
        } );
    }

    private void setupButtonStartGame() {
        // Button and intent to start the Echo Game activity.
        Button startGameButton = findViewById( R.id.buttonStartGame );
        Intent echoGameIntent  = new Intent( this, EchoGameActivity.class );

        // Pass any authenticated Google Account to the Echo Game activity.
        echoGameIntent.putExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount );

        // Set the button on click listener to navigate to the activity.
        startGameButton.setOnClickListener( ( View v ) -> {
            // Pass game options AFTER click.  Google Account cannot change, but game options can.
            echoGameIntent.putExtra( FLASH_BUTTONS, toggleFlashButtons.isChecked() );
            echoGameIntent.putExtra( PLAY_SOUNDS,   togglePlaySounds.isChecked()   );
            // Start the Echo Game activity with a custom transition.  Finish after to remove from
            // back navigation.
            startActivityForResult( echoGameIntent, 1 );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            finish();
        } );
    }
}
