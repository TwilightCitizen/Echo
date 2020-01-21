/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

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

    // Colored game buttons.
    private Button[]                  allButtons;

    // Overlays that fake highlighting a button.
    private ImageView overlayFlashRed;
    private ImageView overlayFlashGreen;
    private ImageView overlayFlashBlue;
    private ImageView overlayFlashYellow;
    private ImageView overlaySubdueAll;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_echo_game );
        setAmbientEnabled();
        getGoogleSignInAccount();
        getGameMode();
        setupButtons();
        setupOverlays();
        setupEchoGame();
    }

    private void getGoogleSignInAccount() {
        // Get any authenticated Google Account passed from the calling activity.
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void getGameMode() {
        // Get the Game Mode passed from the calling activity.
        gameMode = GameModeActivity.GameMode.values()[ getIntent().getIntExtra( GameModeActivity.GAME_MODE, 0 ) ];
    }

    private void setupButtons() {
        // Obtain handles to all the game buttons.
        Button buttonRed    = findViewById( R.id.buttonRed    );
        Button buttonGreen  = findViewById( R.id.buttonGreen  );
        Button buttonBlue   = findViewById( R.id.buttonBlue   );
        Button buttonYellow = findViewById( R.id.buttonYellow );

        allButtons   = new Button[] { buttonRed, buttonGreen, buttonBlue, buttonYellow };

        // Forward their taps to the Echo Game's handlers.
        buttonRed.setOnClickListener(    ( View v ) -> echoGame.redButtonTapped()    );
        buttonGreen.setOnClickListener(  ( View v ) -> echoGame.greenButtonTapped()  );
        buttonBlue.setOnClickListener(   ( View v ) -> echoGame.blueButtonTapped()   );
        buttonYellow.setOnClickListener( ( View V ) -> echoGame.yellowButtonTapped() );
    }

    private void setupOverlays() {
        // Obtain handles to all the flashing button overlays.
        overlayFlashRed    = findViewById( R.id.imageRedFlash    );
        overlayFlashGreen  = findViewById( R.id.imageGreenFlash  );
        overlayFlashBlue   = findViewById( R.id.imageBlueFlash   );
        overlayFlashYellow = findViewById( R.id.imageYellowFlash );
        overlaySubdueAll   = findViewById( R.id.imageSubdueAll   );
    }

    private void setupEchoGame() {
        // Setup and start a game of Echo.
        echoGame = new EchoGame( this, this, gameMode.getFlashesButtons(), gameMode.getPlaysSounds() );

        echoGame.startNewGame();
    }

    private void hideAllButtons() { for( Button button : allButtons ) button.setVisibility( View.GONE    ); }
    private void showAllButtons() { for( Button button : allButtons ) button.setVisibility( View.VISIBLE ); }

    @Override public void startPresentingSequence() {
        hideAllButtons();
        overlaySubdueAll.setVisibility( View.VISIBLE );
    }

    @Override public void startFlashRedButton() {
        overlayFlashRed.setVisibility( View.VISIBLE );
    }

    @Override public void stopFlashRedButton() {
        overlayFlashRed.setVisibility( View.GONE );
    }

    @Override public void startFlashGreenButton() {
        overlayFlashGreen.setVisibility( View.VISIBLE );
    }

    @Override public void stopFlashGreenButton() {
        overlayFlashGreen.setVisibility( View.GONE );
    }

    @Override public void startFlashBlueButton() {
        overlayFlashBlue.setVisibility( View.VISIBLE );
    }

    @Override public void stopFlashBlueButton() {
        overlayFlashBlue.setVisibility( View.GONE );
    }

    @Override public void startFlashYellowButton() {
        overlayFlashYellow.setVisibility( View.VISIBLE );
    }

    @Override public void stopFlashYellowButton() {
        overlayFlashYellow.setVisibility( View.GONE );
    }

    @Override public void startFlashBadButton() {
    }

    @Override public void stopFlashBadButton() {
    }

    @Override public void stopPresentingSequence() {
        showAllButtons();
        overlaySubdueAll.setVisibility( View.GONE );
    }
}