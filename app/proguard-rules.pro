# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep only essential attributes for crash reporting (remove line numbers for better obfuscation)
-keepattributes SourceFile
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile

# ========================================
# Compose UI Rules
# ========================================
-keep class androidx.compose.** { *; }
-keep class com.google.accompanist.** { *; }
-dontwarn androidx.compose.**
-dontwarn com.google.accompanist.**

# ========================================
# Media3 Rules (use library's own rules)
# ========================================
# Media3 library provides its own ProGuard rules, so we don't need to keep everything
# Only keep specific public interfaces if needed for reflection
-keep interface androidx.media3.common.** { *; }
-keep class androidx.media3.session.MediaSession { *; }
-keep class androidx.media3.session.MediaSessionService { *; }

# ========================================
# Kotlin Coroutines & Serialization
# ========================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# ========================================
# DataStore & JSON Serialization
# ========================================
-keepattributes *Annotation*
-keep class com.sprinthon.focusclock.data.** { *; }
-keep class com.sprinthon.focusclock.domain.model.** { *; }
-dontwarn org.json.**

# ========================================
# Playback & Audio
# ========================================
-keep class com.sprinthon.focusclock.playback.FocusPlaybackService { *; }
-keep class com.sprinthon.focusclock.playback.FocusPlayerManager { *; }
-keep class com.sprinthon.focusclock.playback.FocusAudioCatalog { *; }

# ========================================
# ViewModel & Navigation
# ========================================
-keep class com.sprinthon.focusclock.ui.viewmodel.FocusViewModel { *; }
-keep class com.sprinthon.focusclock.ui.navigation.** { *; }

# ========================================
# Keep entry points
# ========================================
-keep class com.sprinthon.focusclock.MainActivity { *; }
-keep class androidx.activity.ComponentActivity { *; }

# ========================================
# Remove logging in release builds
# ========================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
