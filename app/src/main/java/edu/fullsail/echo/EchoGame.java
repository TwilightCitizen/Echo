/*
David Clark
CE03 - Android Wear
MDV359-O
C20200101
*/

package edu.fullsail.echo;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
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
        void stopPresentingSequence();

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
        void startFlashGoodButton();
        void stopFlashGoodButton();

        void startPlayRedTune();
        void startPlayGreenTune();
        void startPlayBlueTune();
        void startPlayYellowTune();
        void startPlayBadTune();
        void startPlayGoodTune();

        void gameOver( int finalScore );
    }

    // States that Echo Game can be in.
    public enum EchoState   { presenting, comparing }

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
    private final EchoGameListener    echoGameListener;

    // Context for obtaining resources.  Echo game listener could be anything...
    private final Context             context;

    // State of Echo Game.
    private       EchoState           echoState      = EchoState.presenting;

    // Sequence of buttons that Echo will present.
    private final List< ButtonColor > buttonSequence = new ArrayList<>();

    // Flags to indicate whether or not Echo should flash buttons or play sounds when presenting.
    private       boolean             flashButtons,
                                      playSounds;

    // Index of button in sequence to compare against the user's tap.
    private       int                 buttonIndex    = 0;

    // Number of sequence steps the user successfully echoed.
    private       int                 playerScore    = 0;

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
            final Runnable startFlashButton = getStartFlashButton( buttonColor ),
                           stopFlashButton  = getStopFlashButton(  buttonColor ),
                           startPlayTune    = getStartPlayTune(    buttonColor );

            // Calculate delays for this button in sequence.
            delayMillisGap     = context.getResources().getInteger( R.integer.flash_gap_milliseconds    )
                               + delayNextIteration;

            delayMillisFlash   = context.getResources().getInteger( R.integer.flash_length_milliseconds )
                               + delayMillisGap;

            delayNextIteration = delayMillisFlash;

            // Start flashing the button after barely perceptible delay.  This delay prevents
            // consecutive flashes of the same button from blending together from the user's view.
            // Then stop after a longer delay so the color being flashed is apparent.
            if( flashButtons ) {
                flashHandler.postDelayed( startFlashButton, delayMillisGap   );
                flashHandler.postDelayed( stopFlashButton,  delayMillisFlash );
            }

            // Use the same timings for the sounds to keep things matched up, and only flash buttons
            // or play sounds if the game mode is configured for it.
            if( playSounds ) flashHandler.postDelayed( startPlayTune, delayMillisGap   );
        }
    }

    private Runnable getStartFlashButton( ButtonColor buttonColor ) {
        // Return appropriate start flash button for button color.
        switch( buttonColor ) {
            case red   : return echoGameListener::startFlashRedButton;
            case green : return echoGameListener::startFlashGreenButton;
            case blue  : return echoGameListener::startFlashBlueButton;
            default    : return echoGameListener::startFlashYellowButton;
        }
    }

    private Runnable getStopFlashButton( ButtonColor buttonColor ) {
        // Return appropriate stop flash button for button color.
        switch( buttonColor ) {
            case red   : return echoGameListener::stopFlashRedButton;
            case green : return echoGameListener::stopFlashGreenButton;
            case blue  : return echoGameListener::stopFlashBlueButton;
            default    : return echoGameListener::stopFlashYellowButton;
        }
    }

    private Runnable getStartPlayTune( ButtonColor buttonColor ) {
        // Return appropriate start play tune for button color.
        switch( buttonColor ) {
            case red   : return echoGameListener::startPlayRedTune;
            case green : return echoGameListener::startPlayGreenTune;
            case blue  : return echoGameListener::startPlayBlueTune;
            default    : return echoGameListener::startPlayYellowTune;
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

        // Remove the good button flash and present the new sequence after a delay.
        newSequenceHandler.postDelayed( echoGameListener::stopFlashGoodButton, delayMillisSequence );
        newSequenceHandler.postDelayed( presentNewSequence, delayMillisSequence );
    }

    private void compareTapToRemainingSequence( ButtonColor buttonColor ) {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;

        // Button to compare tap against.
        ButtonColor sequenceButtonColor = buttonSequence.get( buttonIndex );

        // Compare button to one user tapped.
        if( sequenceButtonColor != buttonColor ) {
            // It did not match.  Make player failure known.
            flashAndPlayBadButton();
            notifyGameOver();

            return;
        }

        // It matched.  Increase the compare index.
        buttonIndex++;

        // See if there are more buttons to compare.
        if( buttonIndex == buttonSequence.size() ){
            // Player must have matched the whole sequence.  Increase the score.
            playerScore++;

            // Make player success known.
            flashAndPlayGoodButton();

            // Then add to the sequence and present it, starting comparisons over.
            buttonIndex = 0;
            echoState   = EchoState.presenting;
            addButtonToSequence();
        }
    }

    private void notifyGameOver() {
        // Game is over.  Notify listener with player's final score.
        echoState  = EchoState.presenting;

        echoGameListener.gameOver( playerScore );
    }

    private void flashAndPlayGoodButton() {
        // Flash good button and play good button tune if configured to do so.
        if( flashButtons ) echoGameListener.startFlashGoodButton();
        if( playSounds   ) echoGameListener.startPlayGoodTune();
    }

    private void flashAndPlayBadButton() {
        // Flash bad button and play bad button tune if configured to do so.
        if( flashButtons ) echoGameListener.startFlashBadButton();
        if( playSounds   ) echoGameListener.startPlayBadTune();
    }

    void startNewGame()       { addButtonToSequence(); }

    // Process user taps on buttons.
    void redButtonTapped()    { compareTapToRemainingSequence( ButtonColor.red    ); }
    void greenButtonTapped()  { compareTapToRemainingSequence( ButtonColor.green  ); }
    void blueButtonTapped()   { compareTapToRemainingSequence( ButtonColor.blue   ); }
    void yellowButtonTapped() { compareTapToRemainingSequence( ButtonColor.yellow ); }
}
