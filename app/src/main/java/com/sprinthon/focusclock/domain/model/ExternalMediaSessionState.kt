package com.sprinthon.focusclock.domain.model

import android.graphics.Bitmap

/**
 * Represents the real-time state of an external active media session (e.g. Spotify, YouTube, SoundCloud, Apple Music).
 */
data class ExternalMediaSessionState(
    val hasActiveSession: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumArt: Bitmap? = null,
    val packageName: String = "",
    val appName: String = "",
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val canSkipToNext: Boolean = true,
    val canSkipToPrevious: Boolean = true,
    val canSeek: Boolean = true,
    val hasNotificationPermission: Boolean = false
) {
    val displayTitle: String
        get() = title.ifBlank { "No track playing" }

    val displaySubtitle: String
        get() = when {
            artist.isNotBlank() && appName.isNotBlank() -> "$artist • $appName"
            artist.isNotBlank() -> artist
            appName.isNotBlank() -> appName
            else -> "Open Spotify, YouTube, or SoundCloud"
        }
}
