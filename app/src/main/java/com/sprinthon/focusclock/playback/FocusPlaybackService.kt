package com.sprinthon.focusclock.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.sprinthon.focusclock.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground MediaSessionService for background ambient audio playback in Focus Clock.
 * Complies strictly with Media3 standards and Android 14+ foreground service requirements.
 */
class FocusPlaybackService : MediaSessionService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    companion object {
        const val CHANNEL_ID = "focus_playback_channel"
        const val NOTIFICATION_ID = 1001
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("com.google.android.youtube/19.29.35 (Linux; U; Android 11; Pixel 5)")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)

        val dataSourceFactory = DefaultDataSource.Factory(this, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(dataSourceFactory)

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        var httpCode: Int? = null
                        var cause: Throwable? = error.cause
                        while (cause != null) {
                            if (cause is androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException) {
                                httpCode = cause.responseCode
                                break
                            }
                            cause = cause.cause
                        }
                        android.util.Log.e("FocusPlaybackService", "[DIAGNOSTIC SERVICE ERROR] ExoPlayer error: code=${error.errorCode}, name=${error.errorCodeName}, cause=${error.cause?.javaClass?.simpleName}, httpCode=$httpCode, message=${error.message}")
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        val stateStr = when(state) {
                            Player.STATE_IDLE -> "IDLE"
                            Player.STATE_BUFFERING -> "BUFFERING"
                            Player.STATE_READY -> "READY"
                            Player.STATE_ENDED -> "ENDED"
                            else -> "UNKNOWN($state)"
                        }
                        android.util.Log.d("FocusPlaybackService", "[DIAGNOSTIC SERVICE STATE] state=$stateStr, playWhenReady=${this@apply.playWhenReady}")
                    }
                })
            }

        exoPlayer = player

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCallback(MediaSessionCallback())
            .build()

        // Prepare initial built-in media items
        serviceScope.launch {
            try {
                val mediaItems = FocusAudioCatalog.getAllMediaItems(applicationContext)
                if (mediaItems.isNotEmpty() && player.mediaItemCount == 0) {
                    player.setMediaItems(mediaItems)
                    player.prepare()
                }
            } catch (e: Exception) {
                android.util.Log.e("FocusPlaybackService", "Error preparing initial media items", e)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = exoPlayer
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        exoPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Clock Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media controls and ambient sound playback during focus sessions"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): com.google.common.util.concurrent.ListenableFuture<MutableList<MediaItem>> {
            val updatedMediaItems = mediaItems.map { 
                val uri = it.requestMetadata.mediaUri ?: it.localConfiguration?.uri
                it.buildUpon().setUri(uri).build() 
            }.toMutableList()
            return com.google.common.util.concurrent.Futures.immediateFuture(updatedMediaItems)
        }
    }
}
