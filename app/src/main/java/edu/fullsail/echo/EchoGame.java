/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
Echo Game maintains the state of games of Echo.  It receives user inputs when it is the user's turn
to repeat Echo, and it notifies its delegate/listener of events it should respond to.  These include
when Echo is presenting a sequence for the user to repeat or when the user has failed to repeat Echo
correctly or within enough time.
*/
public class EchoGame {
    // Interface that Echo Game delegates/listeners must adhere to.
    public interface EchoGameListener {
        void startPresentingSequence();
        void startFlashRedButton();
        void stopFlashRedButton();
        void startFlashGreenButton();
        void stopFlashGreenButton();
        void startFlashBlueButton();
        void stopFlashBlueButton();
        void startFlashYellowButton();
        void stopFlashYellowButton();
        void startFlashBadButton();
        void stopFlashBadButton();
        void stopPresentingSequence();
    }

    // States that Echo Game can be in.
    public enum EchoState   { presenting, comparing, waiting }

    // Buttons of particular color.
    public enum ButtonColor {
        red, green, blue, yellow;

        // Get a random button color.
        static ButtonColor getRandom() {
            Random        random = new Random();
            ButtonColor[] values = ButtonColor.values();

            return values[ random.nextInt( values.length ) ];
        }
    }

    // Delegate/Listener the Echo Game requires.
    private final EchoGameListener         echoGameListener;

    // Context for obtaining resources.  Echo game listener could be anything...
    private final Context                  context;

    // State of Echo Game.
    private       EchoState                echoState      = EchoState.presenting;

    // Sequence of buttons that Echo will present.
    private final ArrayList< ButtonColor > buttonSequence = new ArrayList<>();

    // Flags to indicate whether or not Echo should flash buttons or play sounds when presenting.
    private       boolean                  flashButtons   = true,
                                           playSounds     = true;

    // Index of button in sequence to compare against the user's tap.
    private       int                      buttonIndex    = 0;

    // Number of sequence steps the user successfully echoed.
    private       int                      playerScore    = 0;

    // Echo Game requires a context, a delegate/listener, and the aforementioned flags.
    public EchoGame(
        Context          context,
        EchoGameListener echoGameListener,
        boolean          flashButtons,
        boolean          playSounds
    ) {
        this.context          = context;
        this.echoGameListener = echoGameListener;
        this.flashButtons     = flashButtons;
        this.playSounds       = playSounds;
    }

    private void presentButtonSequence() {
        // Aggregate total delays for next iteration so all iterations do not occur at once.
        long delayNextIteration = 0;
        long delayMillisFlash;

        // Delays between button flashes and between starting and stopping a button flash.
        long delayMillisGap;

        // Total time for whole sequence.
        long delayMillisTotal   = buttonSequence.size() * (
            context.getResources().getInteger( R.integer.flash_length_milliseconds ) +
            context.getResources().getInteger( R.integer.flash_gap_milliseconds    )
        );

        // Handler to manage the start and stop of flashing buttons.
        Handler flashHandler = new Handler();

        // Start presenting sequence now.
        echoGameListener.startPresentingSequence();

        // Defer stop presenting sequence until after all have transpired.
        flashHandler.postDelayed( () -> {
            echoGameListener.stopPresentingSequence();
            echoState = EchoState.comparing;
        }, delayMillisTotal );

        for( ButtonColor buttonColor : buttonSequence ) {
            // Runnables for which button to start and stop flashing for this button in sequence.
            final Runnable startFlashButton, stopFlashButton;

            // Calculate delays for this button in sequence.
            delayMillisFlash   = context.getResources().getInteger( R.integer.flash_length_milliseconds )
                               + context.getResources().getInteger( R.integer.flash_gap_milliseconds    )
                               + delayNextIteration;

            delayMillisGap     = context.getResources().getInteger( R.integer.flash_gap_milliseconds    )
                               + delayNextIteration;

            delayNextIteration = delayMillisFlash;

            // Set runnables to start and stop flashing based on color of button in sequence.
            switch( buttonColor ) {
                case red :
                    startFlashButton = echoGameListener::startFlashRedButton;
                    stopFlashButton  = echoGameListener::stopFlashRedButton;
                    break;
                case green :
                    startFlashButton = echoGameListener::startFlashGreenButton;
                    stopFlashButton  = echoGameListener::stopFlashGreenButton;
                    break;
                case blue :
                    startFlashButton = echoGameListener::startFlashBlueButton;
                    stopFlashButton  = echoGameListener::stopFlashBlueButton;
                    break;
                case yellow :
                    startFlashButton = echoGameListener::startFlashYellowButton;
                    stopFlashButton  = echoGameListener::stopFlashYellowButton;
                    break;
                default :
                    startFlashButton = echoGameListener::startFlashBadButton;
                    stopFlashButton  = echoGameListener::stopFlashBadButton;
            }

            // Start flashing the button after barely perceptible delay.  This delay prevents
            // consecutive flashes of the same button from blending together from the user's view.
            // Then stop after a longer delay so the color being flashed is apparent.
            flashHandler.postDelayed( startFlashButton, delayMillisGap   );
            flashHandler.postDelayed( stopFlashButton,  delayMillisFlash );
        }
    }

    private void addButtonToSequence() {
        // Add a random button to the sequence.
        buttonSequence.add( ButtonColor.getRandom() );

        // Handler and runnable for presenting the new sequence after a delay.
        Handler    newSequenceHandler   = new Handler();
        Runnable   presentNewSequence   = this::presentButtonSequence;

        // Delay before presenting new sequence after increasing it.
        final int  delaySecondsSequence = context.getResources().getInteger( R.integer.sequence_delay_seconds );
        final long delayMillisSequence  = TimeUnit.SECONDS.toMillis( delaySecondsSequence );

        // Present the new sequence after a delay.
        newSequenceHandler.postDelayed( presentNewSequence, delayMillisSequence );
    }

    private void compareTapToRemainingSequence( ButtonColor buttonColor ) {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;

        // Button to compare tap against.
        ButtonColor sequenceButtonColor = buttonSequence.get( buttonIndex );

        // Compare button to one user tapped.
        if( sequenceButtonColor == buttonColor ) {
            // It matched.  Increase the compare index.
            buttonIndex++;

            // See if there are more buttons to compare.
            if( buttonIndex == buttonSequence.size() ){
                // Player must have matched the whole sequence.  Increase the score.
                playerScore++;

                // Then add to the sequence and present it, starting comparisons over.
                buttonIndex = 0;
                echoState   = EchoState.presenting;
                addButtonToSequence();
            }

            return;
        }

        // Game is over.  Publish the player's highest score, which could be from the previous round.
        echoState   = EchoState.presenting;

        Log.d( "Game Over", String.format( "Player Score: %d", playerScore ) );
    }

    void startNewGame()       { addButtonToSequence(); }

    // Process user taps on buttons.
    void redButtonTapped()    { compareTapToRemainingSequence( ButtonColor.red    ); }
    void greenButtonTapped()  { compareTapToRemainingSequence( ButtonColor.green  ); }
    void blueButtonTapped()   { compareTapToRemainingSequence( ButtonColor.blue   ); }
    void yellowButtonTapped() { compareTapToRemainingSequence( ButtonColor.yellow ); }
}
