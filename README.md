# KTR RAKTHA SEVA (GIFT OF LIFE) 🩸

**Rakta Seva** is a high-tech, real-time blood donation matching platform designed to connect voluntary blood donors with recipients instantly and securely. The app leverages Firebase for its backend and AI-inspired matching algorithms to prioritize life-saving connections based on compatibility, proximity, and urgency.

---

## 🚀 Key Features

### 🔐 Authentication & Identity
*   **Synchronized Splash:** Intelligent session check during the splash screen ensures users are automatically navigated to the Home screen if already logged in, eliminating race conditions.
*   **Multi-step Registration:** Secure 3-step onboarding capturing biometric data (Weight, DOB), medical history, and blood type with full input sanitization and validation.
*   **Persistent Session:** User profiles are cached and synchronized across the app using shared ViewModel scoping within the Navigation Graph.
*   **Digital Donor Card:** A generated ID card with a unique identifier, blood group, and verification status for donors.

### 🩸 Life-Saving Network
*   **Live Network Map:** Real-time visualization of nearby compatible donors and urgent requests using Google Maps SATELLITE view.
*   **AI Smart Match Logic:** Automated filtering and search based on medical blood type compatibility (e.g., prioritizing O- universal donors for emergency needs).
*   **Emergency Broadcasts:** Live tracking and display of urgent blood requirements in the user's vicinity, powered by Cloud Messaging.

### 🤖 Automated Intelligence (Firebase Functions)
*   **Proximity Notifications:** Automatic push alerts sent to compatible donors when a new request is posted in their region.
*   **Smart Availability:** Automated 90-day cooldown management for donors to ensure medical safety after a donation.
*   **Self-Maintaining DB:** Background logic that automatically cancels stale requests after 48 hours to keep the network accurate.
*   **Badge Engine:** Server-side logic to award "Life Saver" and "Elite Hero" badges upon verified donation completion.

### 📊 Community & Gamification
*   **Global Rankings:** A points-based leaderboard to recognize and reward frequent donors, synchronized in real-time via Firestore listeners.
*   **Impact Analytics:** Personal statistics showing "Lives Saved" and donation frequency trends.
*   **Achievement Dossier:** Unlockable badges and achievements to encourage community engagement.

### 🛡️ Secure Communication
*   **Encrypted Chat:** Direct communication channel between requesters and donors with real-time message synchronization and proper sender/receiver bubble alignment.
*   **Admin Command Center:** A specialized dashboard for managing high-level network statistics, system health, and emergency oversight.

---

## 🛠 Tech Stack

*   **Language:** Kotlin 2.0.21
*   **UI Framework:** Jetpack Compose (Modern Declarative UI)
*   **Dependency Injection:** Hilt (using high-performance **KSP**)
*   **Backend:** Firebase
    *   **Authentication:** Email/Password with robust error mapping and input sanitization.
    *   **Firestore:** NoSQL real-time database with optimized indexing and manual fallback deserialization.
    *   **Cloud Messaging:** Push notifications for urgent alerts.
    *   **Cloud Functions:** Node.js background triggers for automation (Notifications, Badges, Cleanup).
*   **Maps:** Google Maps Compose SDK.
*   **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles.

---

## 🚦 Project Status & Technical Audit

The project has been thoroughly optimized to resolve build conflicts, session bugs, and data synchronization issues found during development.

### ✅ Fixed & Verified Features
*   **Modern Build System:** Cleaned `gradle.properties` (removed obsolete flags) and migrated Hilt to **KSP** for faster, error-free builds.
*   **Auth Session Sync:** Splash screen synchronization fixed to prevent unwanted logouts on app start.
*   **Reactive Chat Stream:** Fixed message loading using `flatMapLatest` in `ChatViewModel`, ensuring chat updates instantly when switching between requests.
*   **Chat UI Logic:** Fixed alignment issues; message bubbles now correctly identify the sender and align (Right for Sent, Left for Received).
*   **Firestore Resilience:** Added manual fallback deserializers in `FirebaseRepository` to ensure user profiles load correctly even if Firestore enum mapping fails.
*   **Release Stability:** Integrated comprehensive ProGuard rules to protect Hilt and Firebase classes in release builds.
*   **Live Map Implementation:** Fully functional real-time Google Maps integration for tracking donors and requests.

### ⚠️ Final Steps to "Go Live"
*   **Firestore API:** (CRITICAL) You must manually enable the **Cloud Firestore API** in your Google Cloud Console for the database features to activate.
*   **Firestore Indexes:** Deploy the `firestore.indexes.json` using the Firebase CLI (`firebase deploy --only firestore:indexes`) to prevent crashes on complex filtered queries.
*   **Firebase Functions:** Deploy the provided background functions (`firebase deploy --only functions`) to enable automated notifications and badge rewards.
*   **Google Sign-In:** The UI button is ready; the `AuthViewModel` needs the Google Identity SDK integration for the final login logic.
*   **Background Location:** For real-time proximity alerts, a `Foreground Service` should be implemented to track locations when the app is minimized.

---

## 🛠 Installation & Setup

1.  **Clone the project.**
2.  **Firebase Setup:**
    *   Enable **Cloud Firestore** and **Authentication** in the Firebase Console.
    *   Download your `google-services.json` and place it in the `app/` directory.
    *   Add your machine's **SHA-1 fingerprint** to Firebase settings.
3.  **API Keys:** Add your `MAPS_API_KEY` to the `local.properties` file:
    ```properties
    MAPS_API_KEY=your_key_here
    ```
4.  **Sync & Run:** Perform a Gradle Sync and run on an emulator or physical device (API 24+).

