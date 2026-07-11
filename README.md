# KTR RAKTHA SEVA (GIFT OF LIFE)

**Rakta Seva** is a high-tech, real-time blood donation matching platform designed to connect voluntary blood donors with recipients instantly and securely. The app leverages Firebase for its backend and AI-inspired matching algorithms to prioritize life-saving connections based on compatibility and proximity.

## 🚀 Key Features

### 🔐 Authentication & Identity
*   **Multi-step Registration:** Secure onboarding capturing biometric data, medical history, and blood type.
*   **Persistent Session:** Synchronized splash screen to handle auto-login for existing users.
*   **Digital Donor Card:** A generated ID card with a unique QR code for verified donors.

### 🩸 Life-Saving Network
*   **Live Network Map:** Real-time visualization of nearby compatible donors and urgent requests using Google Maps SATELLITE view.
*   **AI Smart Match:** Intelligent search that filters donors by blood compatibility (e.g., O- can see all, but AB+ is prioritized for specific needs).
*   **Emergency Broadcasts:** Push notifications for urgent blood requirements in the user's vicinity.

### 📊 Community & Gamification
*   **Global Rankings:** A points-based leaderboard to recognize and reward frequent donors.
*   **Impact Analytics:** Personal statistics showing lives saved and donation frequency trends.
*   **Achievement Dossier:** Unlockable badges like "Life Saver" and "Elite Hero" (Gamification).

### 🛡️ Secure Communication
*   **Encrypted Chat:** Direct communication channel between requesters and donors to coordinate logistics without exposing personal contact details until necessary.
*   **Admin Command Center:** A specialized dashboard for managing high-level network statistics and emergency oversight.

## 🛠 Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Modern Declarative UI)
*   **Dependency Injection:** Hilt (Dagger)
*   **Backend:** Firebase
    *   **Authentication:** Email/Password & Google Sign-In support.
    *   **Firestore:** NoSQL real-time database with optimized indexing.
    *   **Cloud Messaging:** Push notifications for alerts.
    *   **Analytics & Crashlytics:** Stability and engagement tracking.
*   **Maps:** Google Maps Compose SDK.
*   **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles.

## 🛠 Installation & Setup

1.  **Clone the project.**
2.  **Firebase Setup:**
    *   Enable **Cloud Firestore** and **Authentication** in the Firebase Console.
    *   Download your `google-services.json` and place it in the `app/` directory.
    *   Add your machine's **SHA-1 fingerprint** to Firebase settings for Google Services to work.
3.  **API Keys:** Add your `MAPS_API_KEY` to the `local.properties` file:
    ```properties
    MAPS_API_KEY=your_key_here
    ```
4.  **Sync & Run:** Perform a Gradle Sync and run on an emulator or physical device (API 24+).

---

## 🚦 Project Status & Feature Evaluation

I have performed a comprehensive audit of the current codebase. Here is the evaluation of what is working and what requires updates:

### ✅ Working Features
*   **Core Navigation:** The `NavGraph` is fully implemented with smooth transitions between all 15+ screens.
*   **Synchronized Authentication:** Fixed the race condition where users were logged out during splash. Login and Register are now robust.
*   **Multi-step Registration:** The 3-step biometric and medical data collection is fully functional.
*   **Live Map Implementation:** Google Maps integration is working, displaying donor and request markers.
*   **Shared State Management:** `AuthViewModel` is correctly scoped to ensure user profiles are consistent across the entire app.
*   **Database Integration:** Firestore repositories are set up for Users, Requests, and Leaderboards.
*   **Dependency Injection:** Hilt is correctly configured for all ViewModels and Repositories.
*   **Testing Infrastructure:** Hilt-supported instrumented tests are ready for UI automation.

### ⚠️ Needs Update / Attention
*   **Firestore API Activation:** (CRITICAL) You must manually enable the Firestore API in your Google Cloud Console for the database to start working.
*   **Google Sign-In:** The UI button exists, but the backend logic for `signInWithGoogle` needs to be linked in `AuthViewModel`.
*   **Achievement System:** The "Achievement Badges" logic is currently a UI placeholder ("Restricted Access"). It needs a Firestore Cloud Function to trigger badge awards when donation status changes.
*   **Real-time Location:** While permissions are handled, active background location tracking (Foreground Service) should be added to keep the "Live Network" markers accurate while the app is in the background.
*   **Admin Dashboard:** UI components are ready, but require "Admin" boolean to be set to `true` in a user's Firestore document to access.
