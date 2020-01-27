/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends Activity {
    // Final score for the user.
    private int finalScore;

    @Override protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game_over );
        getFinalScore();
        gameOver();
    }

    private void getFinalScore() {
        // Get the final score passed in from the calling activity.
        finalScore = getIntent().getIntExtra( EchoGameActivity.FINAL_SCORE, 0 );
    }

    private void gameOver() {
        // Get handles to the game over and final score text fields.
        TextView textGameOver   = findViewById( R.id.textGameOver   );
        TextView textFinalScore = findViewById( R.id.textFinalScore );
        // Get handle to the go back button.
        Button buttonGoBack   = findViewById( R.id.buttonGoBack  );

        // Set the final score text to show the player's final score.
        textFinalScore.setText( String.format( getString( R.string.final_score ), finalScore ) );

        // Set the go back button listener to dismiss the finished game.
        buttonGoBack.setOnClickListener( ( View v ) -> finish() );
    }
}
