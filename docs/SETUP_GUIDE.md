# 🩸 Rakta-Seva Connect Setup Guide

Follow these steps to get your production-ready blood donation app running.

## 1. Firebase Project Setup
1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named **Rakta-Seva Connect**.
3. Add an **Android App**:
   - Package name: `com.example.ktrrakthaseva`
   - Download `google-services.json` and place it in the `app/` directory of your project.

## 2. Authentication Configuration
1. In Firebase Console, go to **Build > Authentication**.
2. Enable **Email/Password** provider.
3. Enable **Google** provider:
   - Copy the **Web Client ID** from the Google provider settings.
   - Open `app/src/main/java/com/example/ktrrakthaseva/ui/viewmodel/AuthViewModel.kt`.
   - Replace `"YOUR_WEB_CLIENT_ID"` with the ID you copied.

## 3. Firestore Setup
1. Go to **Build > Firestore Database** and click **Create Database**.
2. Select a location near your users.
3. Start in **Production Mode**.
4. **Rules**: Copy the content of `firestore.rules` (in project root) into the Rules tab.
5. **Indexes**: Use the Firebase CLI to deploy `firestore.indexes.json` or create them manually as listed in the file.

## 4. Storage Setup
1. Go to **Build > Storage** and click **Get Started**.
2. Set the rules to allow read/write for authenticated users.

## 5. Cloud Functions (Optional but Recommended)
1. Initialize Firebase CLI in your project root: `firebase init functions`.
2. Use the `index.js` provided in your ZIP to deploy the logic for:
   - Sending notifications when a new request is posted.
   - Awarding badges automatically.
   - Syncing the leaderboard.

## 6. Build and Run
- Sync Gradle in Android Studio.
- Run the app on an emulator or physical device.
- **Note**: Ensure you have a working internet connection on the device.

---
**Need Help?** Check the official [Firebase Documentation](https://firebase.google.com/docs/android/setup).
