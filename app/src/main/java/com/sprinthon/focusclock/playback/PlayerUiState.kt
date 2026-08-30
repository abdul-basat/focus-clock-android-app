package com.sprinthon.focusclock.playback

import androidx.media3.common.Player
import com.sprinthon.focusclock.domain.model.FocusTrack

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    READY,
    PLAYING,
    ENDED,
    ERROR
}

data class PlayerUiState(
    val isConnected: Boolean = false,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val isPlaying: Boolean = false,
    val isStopped: Boolean = true,
    val currentTrack: FocusTrack = FocusAudioCatalog.BUILT_IN_TRACKS.first(),
    val currentMediaIndex: Int = 0,
    val playlist: List<FocusTrack> = FocusAudioCatalog.BUILT_IN_TRACKS,
    val repeatMode: Int = Player.REPEAT_MODE_ALL,
    val durationMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val hasNextTrack: Boolean = true,
    val hasPreviousTrack: Boolean = true,
    val errorMessage: String? = null
) {
    val trackTitle: String get() = currentTrack.title
    val artistName: String get() = currentTrack.artist
    val isLooping: Boolean get() = repeatMode != Player.REPEAT_MODE_OFF
    val isRepeatOne: Boolean get() = repeatMode == Player.REPEAT_MODE_ONE
}
