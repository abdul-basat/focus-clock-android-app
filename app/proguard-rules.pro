# ============================================================================
# FocusClock Production ProGuard & R8 Optimization Configuration
# ============================================================================

# Obfuscation & Source Map configuration for secure crash reporting
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ============================================================================
# Dynamic / OS Entry Points (Declared in AndroidManifest)
# ============================================================================
-keep public class com.sprinthon.focusclock.MainActivity extends androidx.activity.ComponentActivity
-keep public class com.sprinthon.focusclock.playback.FocusPlaybackService extends androidx.media3.session.MediaSessionService
-keep public class com.sprinthon.focusclock.playback.FocusMediaListenerService extends android.service.notification.NotificationListenerService
-keep public class com.sprinthon.focusclock.playback.FocusClockWallpaperService extends android.service.wallpaper.WallpaperService

# ============================================================================
# Coroutines & Dispatchers
# ============================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# ============================================================================
# DataStore & Serialization (Obfuscate class names, preserve required fields)
# ============================================================================
-keepclassmembers class * implements androidx.datastore.preferences.core.Preferences { *; }
-dontwarn org.json.**

# ============================================================================
# Media3 & ExoPlayer Safe Minification
# ============================================================================
-keep interface androidx.media3.common.** { *; }
-keep class androidx.media3.session.MediaSession { *; }
-keep class androidx.media3.session.MediaSessionService { *; }
-dontwarn androidx.media3.**

# ============================================================================
# Production Log Sanitization (Strip verbose, debug, info logs in release)
# ============================================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

