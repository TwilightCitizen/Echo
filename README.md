# Echo

## About

Echo is a Wear OS 2.0 application, providing an enhanced, modern experience of the popular game Simon.

## Tested Device Emulators

- TODO: List Device Emulators

## Google Sign In

For Google sign in to work, the emulator must be paired with a phone connected via USB with USB Debugging enabled and communication ports forwarded.  Instructions to set this up are here:

https://stackoverflow.com/questions/41637552/how-to-add-a-google-account-in-android-wear-2-0-emulator

Those instructions are derived from this resource:

https://developer.android.com/wear/preview/downloads.html

In order to follow the preceding instructions, ABD must be installed.  There are several methods to do do this, including using brew or downloading directly from Android sources, here:

https://developer.android.com/studio/releases/platform-tools.html

Additional considerations on installing ABD can be found here:

https://stackoverflow.com/questions/31374085/installing-adb-on-macos

Google sign in also required configuration of a Google API project with Echo's package name and the SHA-1 has of the signing certificate for the app.

Details of how this was configured can be found here:

https://developers.google.com/identity/sign-in/android/start

Obtaining the SHA-1 hash proved difficult according to the provided instructions, but this resource helped:

https://stackoverflow.com/questions/15727912/sha-1-fingerprint-of-keystore-certificate