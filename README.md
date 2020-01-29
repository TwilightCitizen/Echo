# Echo

## About

Echo is a Wear OS 2.0 application, providing an enhanced, modern experience of the popular game Simon.

## Repository Link

[https://github.com/daclark1/Echo](https://github.com/daclark1/Echo)

## Image and Audio Resources

All image and audio resources were created by me.

The Echo logo was rendered in Adobe XD in [Technique font by Brian Kent, available on Free Font, ](https://www.fontsquirrel.com/fonts/Technique-BRK) then exported to PNG.

All other images were either rendered in Adobe XD, exported as SVG, and vectorized in Android Studio, or were built as vectors directly in Android Studio.

All audio files are MIDI files that were sequenced, generated, and exported from [Online Sequencer.](https://onlinesequencer.net)

## Tested Device Emulators

Echo has been tested on and known to work on the following Wear OS emulator configurations:

- Android Wear Round, Play Store, 320x320 (HDPI), API 26, Android 8.0 Wear
- Android Wear Round, Play Store, 360x360 (280DPI), API 26, Android 8.0 Wear
- Android Wear Square, Play Store, 240x240 (HDPI), API 26, Android 8.0 Wear
- Android Wear Square, Play Store, 280x280 (HDPI), API 26, Android 8.0 Wear

## Getting Google Sign In to Work

For Google sign in to work, the emulator must be paired with a phone connected via USB with USB Debugging enabled and communication ports forwarded:

1. On the phone, enable Developer Options and USB Debugging.
1. Connect the phone to your computer through USB.
1. Forward the AVD's communication port to the connected handheld device (each time the phone is connected):
    adb -d forward tcp:5601 tcp:5601
1. On the phone, in the Android Wear app, begin the standard pairing process. For example, on the Welcome screen, tap the Set It Up button. Alternatively, if an existing watch already is paired, in the upper-left drop-down, tap Add a New Watch.
1. On the phone, in the Android Wear app, tap the Overflow button, and then tap Pair with Emulator.
1. Tap the Settings icon.
1. Under Device Settings, tap Emulator.
1. Tap Accounts and select a Google Account, and follow the steps in the wizard to sync the account with the emulator. If necessary, type the screen-lock device password, and Google Account password, to start the account sync.

Preceding instructions [provided by Teyam on Stack Overflow](https://stackoverflow.com/questions/41637552/how-to-add-a-google-account-in-android-wear-2-0-emulator), derived from [Google's Developer Resources.](https://developer.android.com/wear/preview/downloads.html)

In order to follow the preceding instructions, ABD must be installed.  There are several methods to do do this, including using Home Brew or [downloading directly from Android sources.](https://developer.android.com/studio/releases/platform-tools.html)

Additional considerations on installing ABD can be found on [Stack Overflow.](https://stackoverflow.com/questions/31374085/installing-adb-on-macos)

Google sign in also requires the configuration of a Google API project with Echo's package name and the SHA-1 has of the signing certificate for the app.

Details of how this was configured can be found in [Google's Developer Resources.](https://developers.google.com/identity/sign-in/android/start)

Obtaining the SHA-1 hash proved difficult according to the provided instructions, but this [resource on Stack Overflow](https://stackoverflow.com/questions/15727912/sha-1-fingerprint-of-keystore-certificate) helped simplify the process.

## Status of Key Features

- Memory Game with 3 Modes of Play (See & Hear, See Only, and Hear Only)
    - 99.9% Complete
    - Hear Only is seriously difficult!  I can hardly get 1 or 2 correct.
- Guest Game Play or Authenticated Game Play with Score Persistence to Leaderboard
    - 75% Complete
    - Authenticated Game Play Records New Entries to or Updates Existing Entries on Leaderboard.
    - Retrieving Top XXX Leaders from Leaderboard Works.
    - Next Major Lift is Displaying Retrieved Leaders.
- Intelligent Pause, Resume, and/or Reset Based on Time Spent in Orientation
    - 99.9% Complete
    - Players have no more than 3 seconds between taps before Echo call's game over.
    - No need for Ambient Mode.
    
## Known Issues and Bugs
- The yellow back button that appears after game over appears beneath the bad button tap overlay.  Nothing seems to work to fix this.  Changes in elevation, order of declaration in the layout, or even calling sendToFront in code do nothing.  It still works, but it looks off.