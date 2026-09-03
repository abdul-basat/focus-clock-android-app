package com.sprinthon.focusclock.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.util.Log
import com.sprinthon.focusclock.domain.model.ExternalMediaSessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Service to connect to Android system-wide MediaSessions (Spotify, YouTube, SoundCloud, Podcasts)
 * and expose real-time playback state and control capabilities to Focus Clock.
 */
class FocusMediaListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var activeController: MediaController? = null

    private val callback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updatePlaybackState()
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updatePlaybackState()
        }

        override fun onSessionDestroyed() {
            activeController = null
            findAndAttachActiveController()
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Companion.isNotificationListenerConnected = true
        findAndAttachActiveController()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Companion.isNotificationListenerConnected = false
        detachController()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        detachController()
        if (instance == this) {
            instance = null
        }
    }

    fun findAndAttachActiveController() {
        try {
            val sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
            val componentName = ComponentName(this, FocusMediaListenerService::class.java)
            val controllers = sessionManager?.getActiveSessions(componentName) ?: emptyList()

            // Find best controller: prefer one that is currently playing or not our own app
            val ourPackage = packageName
            val candidate = controllers.firstOrNull {
                it.packageName != ourPackage && (it.playbackState?.state == PlaybackState.STATE_PLAYING)
            } ?: controllers.firstOrNull { it.packageName != ourPackage }

            if (candidate != null && candidate.sessionToken != activeController?.sessionToken) {
                detachController()
                activeController = candidate
                candidate.registerCallback(callback)
            }
            updatePlaybackState()
        } catch (e: SecurityException) {
            Log.w(TAG, "Notification listener permission not granted: ${e.message}")
            _mediaState.value = ExternalMediaSessionState(hasNotificationPermission = false)
        } catch (e: Exception) {
            Log.e(TAG, "Error finding active media controller: ${e.message}", e)
        }
    }

    private fun detachController() {
        activeController?.let {
            try {
                it.unregisterCallback(callback)
            } catch (_: Exception) {}
        }
        activeController = null
    }

    fun play() {
        activeController?.transportControls?.play()
    }

    fun pause() {
        activeController?.transportControls?.pause()
    }

    fun togglePlayPause() {
        val state = activeController?.playbackState?.state
        if (state == PlaybackState.STATE_PLAYING) {
            pause()
        } else {
            play()
        }
    }

    fun skipToNext() {
        activeController?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        activeController?.transportControls?.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        activeController?.transportControls?.seekTo(positionMs)
    }

    private fun updatePlaybackState() {
        val controller = activeController
        if (controller == null) {
            _mediaState.value = ExternalMediaSessionState(
                hasActiveSession = false,
                hasNotificationPermission = isNotificationListenerConnected
            )
            return
        }

        val metadata = controller.metadata
        val playbackState = controller.playbackState
        val pm = packageManager
        val appName = try {
            val appInfo = pm.getApplicationInfo(controller.packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            controller.packageName
        }

        val isPlaying = playbackState?.state == PlaybackState.STATE_PLAYING
        val actions = playbackState?.actions ?: 0L

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            ?: ""

        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_AUTHOR)
            ?: ""

        val album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""

        val albumArt = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)

        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val position = playbackState?.position ?: 0L

        val canSkipNext = (actions and PlaybackState.ACTION_SKIP_TO_NEXT) != 0L || actions == 0L
        val canSkipPrev = (actions and PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0L || actions == 0L
        val canSeek = (actions and PlaybackState.ACTION_SEEK_TO) != 0L

        _mediaState.value = ExternalMediaSessionState(
            hasActiveSession = title.isNotBlank() || isPlaying,
            isPlaying = isPlaying,
            title = title,
            artist = artist,
            album = album,
            albumArt = albumArt,
            packageName = controller.packageName,
            appName = appName,
            currentPositionMs = position,
            durationMs = duration,
            canSkipToNext = canSkipNext,
            canSkipToPrevious = canSkipPrev,
            canSeek = canSeek,
            hasNotificationPermission = isNotificationListenerConnected
        )
    }

    companion object {
        private const val TAG = "FocusMediaListener"
        var isNotificationListenerConnected: Boolean = false
            private set
        var instance: FocusMediaListenerService? = null
            private set

        private val _mediaState = MutableStateFlow(ExternalMediaSessionState())
        val mediaState: StateFlow<ExternalMediaSessionState> = _mediaState.asStateFlow()

        fun isPermissionGranted(context: Context): Boolean {
            val packageName = context.packageName
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            return flat.contains(packageName)
        }

        fun getPermissionIntent(): Intent {
            return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        }
    }
}
