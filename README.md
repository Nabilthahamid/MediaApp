# MediaApp

A Kotlin Android app demonstrating broadcast receivers, an internet image with pinch zoom, and local video and audio playback. Screens are accessible through a toolbar navigation drawer.

## Features

- **Custom broadcasts:** select Custom, enter a message, and view it in a separate receiving activity. Empty input displays validation; the receiver screen can send the message again.
- **Battery broadcasts:** display the device's battery percentage using the system battery broadcast.
- **Image Scale:** automatically download an image from `https://picsum.photos/800/600`, pinch between 1× and 5×, reset zoom, or retry a failed download.
- **Video:** play and pause a bundled 720p H.264 MP4 inside the app.
- **Audio:** play, pause, resume, and stop a bundled MP3. Stop resets playback; leaving the screen pauses or releases playback resources.
- **Lifecycle handling:** restore the selected screen and saved media position after activity recreation. Media stays paused until Play is tapped again. The image reloads and zoom resets after recreation.

## Build and run

1. Clone this repository and open its root folder in Android Studio.
2. Install Android SDK Platform 37 through SDK Manager and let Gradle sync finish.
3. Configure a local Android SDK if prompted. Android Studio creates `local.properties`; this machine-specific file is ignored by Git.
4. Run the `app` configuration on an emulator or Android device running Android 7.0 / API 24 or newer.

The project uses Android Gradle Plugin 9.4.1 and the included Gradle 9.6.0 wrapper. Gradle JVM provisioning is configured in `gradle/gradle-daemon-jvm.properties`. Internet access is needed for initial dependency downloads and the Image Scale screen; audio and video use bundled resources.

From PowerShell on Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest lintDebug
.\gradlew.bat installDebug
```

On macOS or Linux, use `./gradlew` instead of `.\gradlew.bat` (run `chmod +x gradlew` if necessary).

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Build outputs and APKs are not committed. Build locally to get an APK. A debug APK is intended for testing; publishing requires a signed release build.

## Main files

| File | Responsibility |
| --- | --- |
| `app/src/main/java/com/example/mediaapp/MainActivity.kt` | Toolbar, navigation drawer, and fragment selection |
| `app/src/main/java/com/example/mediaapp/BroadcastScreens.kt` | Broadcast selection, message input, custom receiver, and battery activity |
| `app/src/main/java/com/example/mediaapp/ImageScaleFragment.kt` | Background image download, loading status, retry, and reset controls |
| `app/src/main/java/com/example/mediaapp/ZoomImageView.kt` | Two-finger scale detection and centered image drawing with 1×–5× limits |
| `app/src/main/java/com/example/mediaapp/VideoFragment.kt` | Bundled video playback and lifecycle handling |
| `app/src/main/java/com/example/mediaapp/AudioFragment.kt` | MP3 playback controls and player cleanup |
| `app/src/main/java/com/example/mediaapp/UiHelpers.kt` | Shared layouts, buttons, labels, and system insets |
| `app/src/main/AndroidManifest.xml` | Activities and internet permission |
| `app/src/main/res/layout/activity_main.xml` | Main toolbar, fragment container, and drawer layout |
| `app/src/main/res/menu/drawer_menu.xml` | Drawer destinations |
| `app/src/main/res/raw/` | Bundled audio and video |

## Manual acceptance checklist

| Action | Expected result |
| --- | --- |
| Tap the toolbar menu button | Drawer opens |
| Select each menu option | Correct screen appears |
| Choose Custom → Proceed | Text entry activity opens |
| Continue with empty text | Input validation appears |
| Enter `Hello Android` → Continue | Third activity displays the broadcast message |
| Tap Send broadcast again | Message is received again |
| Choose Battery → Proceed | Battery percentage appears |
| Open Image Scale | Internet image loads |
| Pinch outward/inward | Image zooms between 1× and 5× |
| Tap Reset zoom | Image returns to normal size |
| Open Video → Play | Video plays inside the app |
| Open Audio → Play/Pause/Stop | Controls work repeatedly |
| Leave a media screen | Playback stops or pauses appropriately |
| Rotate the device | App remains usable without crashing |

## Validation

The debug build, existing unit test, and Android lint checks passed during local verification. Emulator checks covered navigation, custom and battery broadcasts, image loading, video playback, repeated audio controls, backgrounding, and rotation. Two-finger pinch behavior still needs manual gesture verification; speaker output was not verified by listening. The included test classes are starter tests, not automated coverage of the full checklist. Lint reports non-blocking warnings.
