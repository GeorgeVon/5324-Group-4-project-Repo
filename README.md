# CSE 5324 Group 4 - AI Personal Assistant App

This guide is a step-by-step instructions on how to open, build, and run the Personal Assistant application using Android Studio.

## Pre-reqs
- **Android Studio** (Latest stable version recommended).
- **Android SDK** (API 34) installed via the SDK Manager.
- An **Android Emulator** or a physical device with **USB Debugging** enabled.

---

## Step-by-Step Instructions

### 1. Launch Android Studio
Open the Android Studio application on your computer.

### 2. Open the Project
1. On the **Welcome to Android Studio** window, click **Open**.
2. Navigate to the project directory: `.../AI_assistant_app`.
3. Select the root folder (`AI_assistant_app`) and click **OK**.

### 3. Wait for Gradle to Sync
Android Studio will automatically start syncing the project. Once a "Build Succesful" message is
received at the bottom of the screen, continue to step 4. 

### 4. Select a Run Device
In the top toolbar, locate the device dropdown menu (next to the Run button).
- Select your physical device or an emulator from the list.

### 5. Build and Run the App
1. Verify the run configuration dropdown (next to the device selector) is set to **app**.
2. Click the **Run** button (green "Play" icon) in the toolbar, or press `Shift + F10`.
3. The app will build, install, and launch automatically on your selected device.

### 6. Running Unit Tests
To execute the automated tests:
1. Open the **Project** tool window on the left.
2. Navigate to `app > src > test > java > com.Group4.personalAssistant`.
3. Right-click on `AllTestsSuite.java`.
4. Select **Run 'AllTestsSuite'**.
