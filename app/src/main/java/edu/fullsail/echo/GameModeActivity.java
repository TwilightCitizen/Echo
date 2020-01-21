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
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/*
Game Mode activity allows the user to configure how the subsequent game of Echo will be played.
Options are selectable via a discrete seek bar.  See & Hear means Echo will visibly flash the buttons
and play an audible tone, while See Only and Hear Only exclude one of those side effects.  The
default option is See & Hear.  A single navigation button takes the user to the Echo Game activity,
which receives any Google Account passed to this activity along with the selected Game Mode.
*/
public class GameModeActivity extends WearableActivity {
    // Game Mode tag for passing the selected Game Mode to the Echo Game activity.
    public static final String GAME_MODE = "GAME_MODE";

    // Game Mode provides discrete options with Game Mode seek bar positions and text labels.
    public enum GameMode {
        seeAndHear( 0, R.string.see_hear, true,  true  ),
        seeOnly(    1, R.string.see_only,   true,  false ),
        hearOnly(   2, R.string.hear_only,  false, true  );

        // Position of Game Mode seek bar and text label associated with the option.
        private final int     seekBarPosition;
        private final int     gameModeLabel;
        private final boolean flashButtons;
        private final boolean playSounds;

        // Associate Game Mode seek bar and text label with the option.
        GameMode(
            final int seekBarPosition,
            final int gameModeLabel,
            final boolean flashButtons,
            final boolean playSounds
        ) {
            this.seekBarPosition = seekBarPosition;
            this.gameModeLabel   = gameModeLabel;
            this.flashButtons    = flashButtons;
            this.playSounds      = playSounds;
        }

        static GameMode getGameMode( int forSeekBarPosition ) {
            // Return the first Game Mode whose Seek Bar position matches.
            for( GameMode gameMode : GameMode.values() )
                if( gameMode.seekBarPosition == forSeekBarPosition ) return gameMode;

            // Provide sane default in the case of a faulty seek bar input.
            return seeAndHear;
        }

        // Return the Text Label associated with the Game Mode.
        int getGameModeLabel() { return gameModeLabel; }

        // Return whether or not the Game Mode flashes buttons or plays sounds.
        public boolean getFlashesButtons() { return flashButtons; }
        public boolean getPlaysSounds()    { return playSounds;   }
    }

    private GameMode            gameMode            = GameMode.seeAndHear;
    private GoogleSignInAccount googleSignInAccount = null;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_mode );
        setAmbientEnabled();
        getGoogleSignInAccount();
        setupSeekBarGameMode();
        setupButtonStartGame();
    }

    private void getGoogleSignInAccount() {
        // Get any authenticated Google Account passed from the calling activity.
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void setupSeekBarGameMode() {
        // Game mode seek bar and text label
        SeekBar  seekBarGameMode = findViewById( R.id.seekBarGameMode );
        TextView textGameMode    = findViewById( R.id.textGameMode );

        // Capture user changes to the Game Mode seek bar.
        seekBarGameMode.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                // Set the Game Mode according to the seek bar position and update the text label
                // so the user knows which mode is selected.
                gameMode = GameMode.getGameMode( progress );

                textGameMode.setText( gameMode.getGameModeLabel() );
            }

            // Unimplemented interface functionality.
            @Override public void onStartTrackingTouch( SeekBar seekBar ) {}
            @Override public void onStopTrackingTouch(  SeekBar seekBar ) {}
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
            // Pass Game Mode AFTER click.  Google Account cannot change, but Game Mode can.
            echoGameIntent.putExtra( GAME_MODE, gameMode.ordinal() );
            startActivity( echoGameIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
        } );
    }
}
