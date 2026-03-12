# 📦 How to Generate APK for Zee Calculator

This guide provides step-by-step instructions on how to generate an APK (Android Package) file using Android Studio.

## 1. Setting Up Android Studio
Ensure you have the latest version of **Android Studio** installed and the project is fully synced.

## 2. Generating a Debug APK (For Testing)
If you want to quickly test the app on a phone:
1. Open the project in Android Studio.
2. Click on **Build** in the top menu bar.
3. Select **Build Bundle(s) / APK(s)** > **Build APK(s)**.
4. Once completed, a notification will appear. Click **Locate** to find the `app-debug.apk` file.

## 3. Generating a Release APK (For Distribution)
To create an APK that can be shared or uploaded:
1. Go to **Build** > **Generate Signed Bundle / APK...**.
2. Select **APK** and click **Next**.
3. **Key Store Path**: If you don't have one, click **Create new...** and follow the prompts to create a digital signature.
4. Fill in the Alias and Password.
5. Select **Release** build variants.
6. Check **V1 (Jar Signature)** and **V2 (Full APK Signature)** for maximum compatibility.
7. Click **Finish**. Your APK will be generated in the `app/release/` folder.

## 4. Install on Device
- Transfer the `.apk` file to your Android device via USB or email.
- Open the file on your device.
- If prompted, allow "Install from unknown sources" in settings.
- Follow the on-screen instructions to install.

---
*For any technical support, contact the developer: Zeeshan Sarwar - 00923336003596*
