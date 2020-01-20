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

public class GameModeActivity extends WearableActivity {
    public static final String GAME_MODE = "GAME_MODE";

    public enum GameMode {
        seeAndHear( 0, R.string.see_hear  ),
        seeOnly(    1, R.string.see_only  ),
        hearOnly(   2, R.string.hear_only );

        private final int seekBarPosition;
        private final int gameModeLabel;

        GameMode( final int seekBarPosition, final int gameModeLabel ) {
            this.seekBarPosition = seekBarPosition;
            this.gameModeLabel   = gameModeLabel;
        }

        public static GameMode getGameMode( int forSeekBarPosition ) {
            for( GameMode gameMode : GameMode.values() )
                if( gameMode.seekBarPosition == forSeekBarPosition ) return gameMode;

            return null;
        }

        public int getGameModeLabel() { return gameModeLabel; }
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
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void setupSeekBarGameMode() {
        SeekBar seekBarGameMode = findViewById( R.id.seekBarGameMode );

        seekBarGameMode.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                TextView textGameMode = findViewById( R.id.textGameMode );

                gameMode = GameMode.getGameMode( progress );

                if( gameMode == null ) return;

                int      gameModeLabelString = gameMode.getGameModeLabel();

                textGameMode.setText( gameModeLabelString );
            }

            @Override public void onStartTrackingTouch( SeekBar seekBar ) {}
            @Override public void onStopTrackingTouch(  SeekBar seekBar ) {}
        } );
    }

    private void setupButtonStartGame() {
        Button startGameButton = findViewById( R.id.buttonStartGame );

        Intent echoGameIntent  = new Intent( this, EchoGameActivity.class );

        echoGameIntent.putExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT, googleSignInAccount );

        startGameButton.setOnClickListener( ( View v ) -> {
            echoGameIntent.putExtra( GAME_MODE, gameMode.ordinal() );
            startActivity( echoGameIntent );
            overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
        } );
    }
}
