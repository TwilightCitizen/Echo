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

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class EchoGameActivity extends WearableActivity implements EchoGame.EchoGameListener {
    // Game Mode and Google Account obtained from calling activity.
    private GameModeActivity.GameMode gameMode            = GameModeActivity.GameMode.seeAndHear;
    private GoogleSignInAccount       googleSignInAccount = null;

    // Echo Game maintains the game logic to which this activity responds.
    private EchoGame                  echoGame;

    // Colored game buttons.
    Button buttonRed;
    Button buttonGreen;
    Button buttonBlue;
    Button buttonYellow;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_echo_game );
        setAmbientEnabled();
        getGoogleSignInAccount();
        getGameMode();
        setupButtons();
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
        buttonRed    = findViewById(  R.id.buttonRed   );
        buttonGreen  = findViewById(  R.id.buttonGreen );
        buttonBlue   = findViewById(  R.id.buttonBlue  );
        buttonYellow = findViewById( R.id.buttonYellow );

        // Forward their taps to the Echo Game's handlers.
        buttonRed.setOnClickListener(    ( View v ) -> echoGame.redButtonTapped()    );
        buttonGreen.setOnClickListener(  ( View v ) -> echoGame.greenButtonTapped()  );
        buttonBlue.setOnClickListener(   ( View v ) -> echoGame.blueButtonTapped()   );
        buttonYellow.setOnClickListener( ( View V ) -> echoGame.yellowButtonTapped() );
    }

    private void setupEchoGame() {
        // Setup and start a game of Echo.
        echoGame = new EchoGame( this, this, gameMode.getFlashesButtons(), gameMode.getPlaysSounds() );

        echoGame.startNewGame();
    }

    private void subdueAllButtons() {
        // Subdue all the buttons so whichever one is flashed will stand out more.
        buttonRed.setBackgroundColor(    getColor( R.color.red_subdue    ) );
        buttonGreen.setBackgroundColor(  getColor( R.color.green_subdue  ) );
        buttonBlue.setBackgroundColor(   getColor( R.color.blue_subdue   ) );
        buttonYellow.setBackgroundColor( getColor( R.color.yellow_subdue ) );
    }

    private void normalizeAllButtons() {
        // Return all buttons to their normal shade of color.
        buttonRed.setBackgroundColor(    getColor( R.color.red_normal    ) );
        buttonGreen.setBackgroundColor(  getColor( R.color.green_normal  ) );
        buttonBlue.setBackgroundColor(   getColor( R.color.blue_normal   ) );
        buttonYellow.setBackgroundColor( getColor( R.color.yellow_normal ) );
    }

    @Override public void startFlashRedButton() {
        subdueAllButtons();
        buttonRed.setBackgroundColor( getColor( R.color.red_flash ) );
    }

    @Override public void stopFlashRedButton() {
        normalizeAllButtons();
    }

    @Override public void startFlashGreenButton() {
        subdueAllButtons();
        buttonGreen.setBackgroundColor( getColor( R.color.green_flash ) );
    }

    @Override public void stopFlashGreenButton() {
        normalizeAllButtons();
    }

    @Override public void startFlashBlueButton() {
        subdueAllButtons();
        buttonBlue.setBackgroundColor( getColor( R.color.blue_flash ) );
    }

    @Override public void stopFlashBlueButton() {
        normalizeAllButtons();
    }

    @Override public void startFlashYellowButton() {
        subdueAllButtons();
        buttonYellow.setBackgroundColor( getColor( R.color.yellow_flash ) );
    }

    @Override public void stopFlashYellowButton() {
        normalizeAllButtons();
    }

    @Override public void startFlashBadButton() {

    }

    @Override public void stopFlashBadButton() {

    }
}