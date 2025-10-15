# VibeDex Meal Planner

An offline-first Android application for generating balanced breakfast, lunch, snack, and dinner plans without repeating recent meals. The app lets you maintain candidate dishes for each meal slot, then produces a daily plan that respects your history so the menu stays fresh.

## Features

- 💾 **Private by design** – all preferences and history are stored locally using DataStore.
- 🥗 **Customisable meal candidates** – add, edit, or remove options for breakfast, lunch, snacks, and dinner.
- 🍛 **South Indian & diabetes-friendly focus** – default dishes highlight traditional millet- and lentil-forward plates that stay low on the glycaemic index while providing fibre and protein-rich variety.
- 📅 **Unique daily plans** – generate multi-day menus that avoid repeating recent dishes.
- 🧾 **Plan history** – review previously generated plans to keep track of what you have eaten.

## Getting started

1. Install [Android Studio Giraffe or newer](https://developer.android.com/studio).
2. Clone this repository and open it in Android Studio.
3. Sync Gradle and run the `app` configuration on your Android device or emulator.

> **Note:** To keep the repository free of binary artifacts, the Gradle wrapper JAR is stored as a Base64 text file (`gradle/wrapper/gradle-wrapper.jar.base64`). When you run any `./gradlew` command the script reconstructs the JAR automatically using the system `base64` utility. Ensure that tool is available on your PATH (it ships with macOS, Linux, and Git Bash on Windows).

When launched, the app loads with a curated set of South Indian, diabetes-friendly meal ideas. Each candidate carries dietary tags (South Indian, Low GI, fibre-rich, etc.) that the planner uses to keep recommendations wholesome. Add your own dishes—optionally adjusting the dietary tags—and tap **Generate Plan** to produce a new schedule. Each plan is saved so future generations can avoid repeats while still respecting the health focus.

## Previewing and testing the app

### Run on a physical device
1. Enable *Developer options* and *USB debugging* on your Android phone.
2. Connect the device to your computer and verify that it appears with `adb devices`.
3. From Android Studio, click **Run > Run 'app'** (or run `./gradlew :app:installDebug` followed by `adb shell am start -n com.example.vibedex/.MainActivity`) to install the debug build.
4. Interact with the app, add meal candidates, and generate meal plans directly on the device.

### Run on an Android emulator
1. In Android Studio, open **Tools > Device Manager** and create a device image (API 26+ matches the app's minimum SDK; an API 33 or newer image aligns with the target SDK).
2. Start the emulator, then press **Run 'app'** in Android Studio to deploy the debug build.
3. Use the emulator to exercise the candidate management flow and ensure plans are generated without repeats.

### Preview the UI without installing
1. Open `MealPlannerApp.kt` in Android Studio.
2. Use the **Split** view and select the **Design** tab to render the `MealPlannerContentPreview()` Compose preview.
3. Updates to layouts, colors, or typography will refresh in the preview without needing to run the entire application.

### Automated checks
- `./gradlew help` ensures the Gradle wrapper works in your environment (it also rebuilds the wrapper JAR on first run).
- `./gradlew :app:assembleDebug` builds the APK so you can share it with other personal devices if needed.
