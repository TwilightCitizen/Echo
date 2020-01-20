/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class EchoGameActivity extends WearableActivity {
    private GameModeActivity.GameMode gameMode            = GameModeActivity.GameMode.seeAndHear;
    private GoogleSignInAccount       googleSignInAccount = null;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_echo_game );
        setAmbientEnabled();
        getGoogleSignInAccount();
        getGameMode();
    }

    private void getGoogleSignInAccount() {
        googleSignInAccount = getIntent().getParcelableExtra( GoogleOrGuestActivity.GOOGLE_SIGN_IN_ACCOUNT );
    }

    private void getGameMode() {
        gameMode = getIntent().getParcelableExtra( GameModeActivity.GAME_MODE );
    }
}