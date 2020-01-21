/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class EchoGameActivity extends WearableActivity implements EchoGame.EchoGameListener {
    // Game Mode and Google Account obtained from calling activity.
    private GameModeActivity.GameMode gameMode            = GameModeActivity.GameMode.seeAndHear;
    private GoogleSignInAccount       googleSignInAccount = null;

    // Echo Game maintains the game logic to which this activity responds.
    private EchoGame                  echoGame;

    // Overlays that fake highlighting a button.
    private ImageView                 overlayFlashRed,    overlayFlashGreen, overlayFlashBlue,
                                      overlayFlashYellow, overlaySubdueAll,  overlayFlashGood,
                                      overlayFlashBad;

    // Media players for MIDI sound bites.
    private MediaPlayer               mediaButtonPressBad,   mediaButtonPressGood, mediaButtonPressRed,
                                      mediaButtonPressGreen, mediaButtonPressBlue, mediaButtonPressYellow;

    // All media players aggregated for batch changes.
    private MediaPlayer[]             mediaPlayers;

    // Volume for media players.
    private final static float        MEDIA_PLAYER_VOLUME = 1.0f;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_echo_game );
        setAmbientEnabled();
        getGoogleSignInAccount();
        getGameMode();
        setupButtons();
        setupMediaPlayers();
        setupOverlays();
        setupEchoGame();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayers();
    }

    private void getGoogleSignInAccount() {
        // Get any authenticated Google Account passed from the calling activity.
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void getGameMode() {
        // Get the Game Mode passed from the calling activity.
        gameMode = GameModeActivity.GameMode.values()[ getIntent().getIntExtra( GameModeActivity.GAME_MODE, 0 ) ];
    }

    private void setupMediaPlayers() {
        // Setup media players for each button press (or flash).
        mediaButtonPressBad    = MediaPlayer.create( this, R.raw.button_press_bad    );
        mediaButtonPressGood   = MediaPlayer.create( this, R.raw.button_press_good   );
        mediaButtonPressRed    = MediaPlayer.create( this, R.raw.button_press_red    );
        mediaButtonPressGreen  = MediaPlayer.create( this, R.raw.button_press_green  );
        mediaButtonPressBlue   = MediaPlayer.create( this, R.raw.button_press_blue   );
        mediaButtonPressYellow = MediaPlayer.create( this, R.raw.button_press_yellow );

        // Collect all the media players.
        mediaPlayers = new MediaPlayer[] {
                mediaButtonPressBad,   mediaButtonPressGood, mediaButtonPressRed,
                mediaButtonPressGreen, mediaButtonPressBlue, mediaButtonPressYellow
        };

        // False start all the media players so they can play from start over each other.
        // Also set their volume to a nice medium.
        for( MediaPlayer mediaPlayer : mediaPlayers ){
            falseStartMedia( mediaPlayer );
            mediaPlayer.setVolume( MEDIA_PLAYER_VOLUME, MEDIA_PLAYER_VOLUME );
        }
    }

    private void releaseMediaPlayers() {
        // Release them to avoid choking resources.
        for( MediaPlayer mediaPlayer : mediaPlayers ) mediaPlayer.release();
    }

    private void falseStartMedia( MediaPlayer mediaPlayer ) {
        mediaPlayer.start();
        mediaPlayer.pause();
    }

    private void playMediaFromStart( MediaPlayer mediaPlayer ) {
        mediaPlayer.pause();
        mediaPlayer.seekTo( 0 );
        mediaPlayer.start();
    }

    private void setupButtons() {
        // Obtain handles to all the game buttons.
        Button buttonRed    = findViewById( R.id.buttonRed    );
        Button buttonGreen  = findViewById( R.id.buttonGreen  );
        Button buttonBlue   = findViewById( R.id.buttonBlue   );
        Button buttonYellow = findViewById( R.id.buttonYellow );

        // Forward their taps to the Echo Game's handlers.
        buttonRed.setOnClickListener(    ( View v ) -> {
            playMediaFromStart( mediaButtonPressRed );
            echoGame.redButtonTapped();
        } );

        buttonGreen.setOnClickListener(  ( View v ) -> {
            playMediaFromStart( mediaButtonPressGreen );
            echoGame.greenButtonTapped();
        } );

        buttonBlue.setOnClickListener(   ( View v ) -> {
            playMediaFromStart( mediaButtonPressBlue );
            echoGame.blueButtonTapped();
        } );

        buttonYellow.setOnClickListener( ( View V ) -> {
            playMediaFromStart( mediaButtonPressYellow );
            echoGame.yellowButtonTapped();
        } );
    }

    private void setupOverlays() {
        // Obtain handles to all the flashing button overlays.
        overlayFlashRed    = findViewById( R.id.imageRedFlash    );
        overlayFlashGreen  = findViewById( R.id.imageGreenFlash  );
        overlayFlashBlue   = findViewById( R.id.imageBlueFlash   );
        overlayFlashYellow = findViewById( R.id.imageYellowFlash );
        overlaySubdueAll   = findViewById( R.id.imageSubdueAll   );
        overlayFlashGood   = findViewById( R.id.imageGoodFlash   );
        overlayFlashBad    = findViewById( R.id.imageBadFlash    );
    }

    private void setupEchoGame() {
        // Setup and start a game of Echo.
        echoGame = new EchoGame( this, this, gameMode.getFlashesButtons(), gameMode.getPlaysSounds() );

        echoGame.startNewGame();
    }

    // Respond to Echo Game listener events.
    @Override public void startPresentingSequence() { overlaySubdueAll.setVisibility(   View.VISIBLE ); }
    @Override public void stopPresentingSequence()  { overlaySubdueAll.setVisibility(   View.GONE    ); }

    @Override public void startFlashRedButton()     { overlayFlashRed.setVisibility(    View.VISIBLE ); }
    @Override public void stopFlashRedButton()      { overlayFlashRed.setVisibility(    View.GONE    ); }
    @Override public void startFlashGreenButton()   { overlayFlashGreen.setVisibility(  View.VISIBLE ); }
    @Override public void stopFlashGreenButton()    { overlayFlashGreen.setVisibility(  View.GONE    ); }
    @Override public void startFlashBlueButton()    { overlayFlashBlue.setVisibility(   View.VISIBLE ); }
    @Override public void stopFlashBlueButton()     { overlayFlashBlue.setVisibility(   View.GONE    ); }
    @Override public void startFlashYellowButton()  { overlayFlashYellow.setVisibility( View.VISIBLE ); }
    @Override public void stopFlashYellowButton()   { overlayFlashYellow.setVisibility( View.GONE    ); }
    @Override public void startFlashBadButton()     { overlayFlashBad.setVisibility(    View.VISIBLE ); }
    @Override public void stopFlashBadButton()      { overlayFlashBad.setVisibility(    View.GONE    ); }
    @Override public void startFlashGoodButton()    { overlayFlashGood.setVisibility(   View.VISIBLE ); }
    @Override public void stopFlashGoodButton()     { overlayFlashGood.setVisibility(   View.GONE    ); }

    @Override public void startPlayRedTune()        { playMediaFromStart( mediaButtonPressRed        ); }
    @Override public void startPlayGreenTune()      { playMediaFromStart( mediaButtonPressGreen      ); }
    @Override public void startPlayBlueTune()       { playMediaFromStart( mediaButtonPressBlue       ); }
    @Override public void startPlayYellowTune()     { playMediaFromStart( mediaButtonPressYellow     ); }
    @Override public void startPlayBadTune()        { playMediaFromStart( mediaButtonPressBad        ); }
    @Override public void startPlayGoodTune()       { playMediaFromStart( mediaButtonPressGood       ); }
}