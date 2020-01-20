# Echo

## About

Echo is a Wear OS 2.0 application, providing an enhanced, modern experience of the popular game Simon.

## Tested Device Emulators

Echo has been tested on and known to work on the following Wear OS emulator configurations:

- Android Wear Round, Play Store, 320x320 (HDPI), API 26, Android 8.0 Wear
- Android Wear Round, Play Store, 360x360 (280DPI), API 26, Android 8.0 Wear
- Android Wear Square, Play Store, 240x240 (HDPI), API 26, Android 8.0 Wear
- Android Wear Square, Play Store, 280x280 (HDPI), API 26, Android 8.0 Wear

## Google Sign In

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