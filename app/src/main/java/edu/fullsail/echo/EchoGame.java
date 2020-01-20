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
        void startFlashRedButton();
        void stopFlashRedButton();
        void startFlashGreenButton();
        void stopFlashGreenButton();
        void startFlashBlueButton();
        void stopFlashBlueButton();
        void startFlashYellowButton();
        void stopFlashYellowButton();
    }

    // States that Echo Game can be in.
    public enum EchoState   { presenting, comparing, waiting }

    // Buttons of particular color.
    public enum ButtonColor {
        red, green, blue, yellow;

        // Get a random button color.
        public static ButtonColor getRandom() {
            Random        random = new Random();
            ButtonColor[] values = ButtonColor.values();

            return values[ random.nextInt( values.length ) ];
        }
    }

    // Delegate/Listener the Echo Game requires.
    private EchoGameListener         echoGameListener;

    // Context for obtaining resources.  Echo game listener could be anything...
    private Context                  context;

    // State of Echo Game.
    private EchoState                echoState        = EchoState.presenting;

    // Sequence of buttons that Echo will present.
    private ArrayList< ButtonColor > buttonSequence   = new ArrayList<>();
    // private int                      buttonIndex      = 0;

    // Flags to indicate whether or not Echo should flash buttons or play sounds when presenting.
    private boolean                  flashButtons     = true,
                                     playSounds       = true;

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
        for( ButtonColor buttonColor : buttonSequence ) {
            // Runnables for which button to start and stop flashing.
            Runnable startFlashButton, stopFlashButton;

            // Handler to manage the start and stop of flashing buttons.
            Handler flashHandler = new Handler();

            // Delays between button flashes and between starting and stopping a button flash.
            final int  delaySecondsFlash = context.getResources().getInteger( R.integer.flash_length_seconds );
            final long delayMillisFlash  = TimeUnit.SECONDS.toMillis( delaySecondsFlash );
            final long delayMillisGap    = context.getResources().getInteger( R.integer.flash_gap_milliseconds );

            // Set runnables to start and stop flashing based on color of button in sequence.
            switch( buttonColor ) {
                case red :
                    startFlashButton = () -> echoGameListener.startFlashRedButton();
                    stopFlashButton  = () -> echoGameListener.stopFlashRedButton();
                    break;
                case green :
                    startFlashButton = () -> echoGameListener.startFlashGreenButton();
                    stopFlashButton  = () -> echoGameListener.stopFlashGreenButton();
                    break;
                case blue :
                    startFlashButton = () -> echoGameListener.startFlashBlueButton();
                    stopFlashButton  = () -> echoGameListener.stopFlashBlueButton();
                case yellow :
                default :
                    startFlashButton = () -> echoGameListener.startFlashYellowButton();
                    stopFlashButton  = () -> echoGameListener.stopFlashYellowButton();
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

    private void startNewGame() {
        buttonSequence.clear();
        addButtonToSequence();
    }

    // Process user taps on buttons.

    public void redTapped() {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;
    }

    public void greenTapped() {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;
    }

    public void blueTapped() {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;
    }

    public void yellowTapped() {
        // Guard against acting on user inputs while presenting.
        if( echoState == EchoState.presenting ) return;
    }
}
