package com.sprinthon.focusclock.playback

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.sprinthon.focusclock.domain.model.ExternalMediaSessionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Controller class to manage interactions with the system media session and launch third-party music apps.
 */
class SystemMediaControllerManager(private val context: Context) {

    val mediaState: StateFlow<ExternalMediaSessionState>
        get() = FocusMediaListenerService.mediaState

    fun isPermissionGranted(): Boolean {
        return FocusMediaListenerService.isPermissionGranted(context)
    }

    fun openPermissionSettings() {
        val intent = FocusMediaListenerService.getPermissionIntent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun refreshActiveSession() {
        FocusMediaListenerService.instance?.findAndAttachActiveController()
    }

    fun play() {
        FocusMediaListenerService.instance?.play()
    }

    fun pause() {
        FocusMediaListenerService.instance?.pause()
    }

    fun togglePlayPause() {
        FocusMediaListenerService.instance?.togglePlayPause()
    }

    fun skipToNext() {
        FocusMediaListenerService.instance?.skipToNext()
    }

    fun skipToPrevious() {
        FocusMediaListenerService.instance?.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        FocusMediaListenerService.instance?.seekTo(positionMs)
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun launchSpotify(): Boolean = launchApp(SPOTIFY_PACKAGE)
    fun launchYouTube(): Boolean = launchApp(YOUTUBE_PACKAGE) || launchApp(YT_MUSIC_PACKAGE)
    fun launchSoundCloud(): Boolean = launchApp(SOUNDCLOUD_PACKAGE)

    companion object {
        const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val YT_MUSIC_PACKAGE = "com.google.android.apps.youtube.music"
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val SOUNDCLOUD_PACKAGE = "com.soundcloud.android"
    }
}
