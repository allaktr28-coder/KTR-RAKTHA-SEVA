# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

# Keep all data models for Firestore serialization
-keep class com.example.ktrrakthaseva.data.model.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep Kotlin coroutines
-keepclassmembernames class kotlinx.** { volatile <fields>; }

# Keep enums (BloodType, Urgency, etc.)
-keepclassmembers enum * { *; }

# Keep source line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
