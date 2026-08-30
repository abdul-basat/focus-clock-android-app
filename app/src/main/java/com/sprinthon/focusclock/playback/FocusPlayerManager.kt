package com.sprinthon.focusclock.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sprinthon.focusclock.domain.model.FocusTrack
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * High-level manager connecting the UI layer (ViewModel) to the Media3 FocusPlaybackService
 * via MediaController. Handles state mapping, playlist control, repeat cycles, and error recovery.
 */
class FocusPlayerManager(
    private val context: Context,
    private val scope: CoroutineScope
) {

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState: StateFlow<PlayerUiState> = _playerUiState.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var progressPollingJob: Job? = null

    private var currentPlaylist: List<FocusTrack> = FocusAudioCatalog.BUILT_IN_TRACKS

    fun setCustomTracks(customTracks: List<FocusTrack>) {
        currentPlaylist = FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
        // Optionally update the player's media items here, but usually it's fine 
        // to just let it update on the next play or track selection.
        updateStateFromController()
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updateStateFromController()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateStateFromController()
            if (isPlaying) {
                startProgressPolling()
            } else {
                stopProgressPolling()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateStateFromController()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateStateFromController()
        }

        override fun onPlayerError(error: PlaybackException) {
            _playerUiState.update {
                it.copy(
                    status = PlaybackStatus.ERROR,
                    errorMessage = error.localizedMessage ?: "Audio playback error"
                )
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            updateStateFromController()
        }
    }

    fun initialize(onConnected: (() -> Unit)? = null) {
        if (mediaController != null || controllerFuture != null) return

        try {
            val sessionToken = SessionToken(
                context,
                ComponentName(context, FocusPlaybackService::class.java)
            )

            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture?.addListener(
                {
                    try {
                        val controller = controllerFuture?.get()
                        if (controller != null) {
                            mediaController = controller
                            controller.addListener(playerListener)
                            updateStateFromController()
                            onConnected?.invoke()
                        }
                    } catch (e: Exception) {
                        _playerUiState.update {
                            it.copy(
                                status = PlaybackStatus.ERROR,
                                errorMessage = e.message ?: "Failed to connect to audio service"
                            )
                        }
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        } catch (e: Exception) {
            _playerUiState.update {
                it.copy(
                    status = PlaybackStatus.ERROR,
                    errorMessage = e.message ?: "Failed to initialize player"
                )
            }
        }
    }

    fun togglePlayPause() {
        val controller = mediaController ?: run {
            initialize { togglePlayPause() }
            return
        }

        val isYouTube = _playerUiState.value.currentTrack?.isYouTube == true
        if (isYouTube) {
            val currentlyPlaying = _playerUiState.value.isPlaying
            _playerUiState.update { it.copy(isPlaying = !currentlyPlaying) }
            return
        }

        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_IDLE || controller.mediaItemCount == 0) {
                scope.launch(Dispatchers.Main) {
                    val mediaItems = currentPlaylist.map { FocusAudioCatalog.createMediaItem(context, it) }
                    controller.setMediaItems(mediaItems)
                    controller.prepare()
                    controller.play()
                }
            } else {
                controller.play()
            }
        }
        updateStateFromController()
    }

    fun play() {
        val controller = mediaController ?: run {
            initialize { play() }
            return
        }

        val isYouTube = _playerUiState.value.currentTrack?.isYouTube == true
        if (isYouTube) {
            _playerUiState.update { it.copy(isPlaying = true) }
            return
        }

        if (controller.playbackState == Player.STATE_IDLE || controller.mediaItemCount == 0) {
            scope.launch(Dispatchers.Main) {
                val mediaItems = currentPlaylist.map { FocusAudioCatalog.createMediaItem(context, it) }
                controller.setMediaItems(mediaItems)
                controller.prepare()
                controller.play()
            }
        } else {
            controller.play()
        }
        updateStateFromController()
    }

    fun pause() {
        val isYouTube = _playerUiState.value.currentTrack?.isYouTube == true
        if (isYouTube) {
            _playerUiState.update { it.copy(isPlaying = false) }
            return
        }

        mediaController?.pause()
        updateStateFromController()
    }

    /**
     * Stop playback, reset current position to beginning (0ms), and update state.
     * Note: Stop music does NOT terminate the Focus session.
     */
    fun stop() {
        val controller = mediaController ?: return
        controller.pause()
        controller.seekTo(0L)
        _playerUiState.update {
            it.copy(
                isPlaying = false,
                isStopped = true,
                currentPositionMs = 0L,
                status = PlaybackStatus.READY
            )
        }
    }

    fun nextTrack() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        } else if (controller.mediaItemCount > 0) {
            // Wrap to first if at end of playlist
            controller.seekToDefaultPosition(0)
        }
        updateStateFromController()
    }

    /**
     * Previous track: If track played > 3 seconds, restarts current track; otherwise goes to previous.
     */
    fun previousTrack() {
        val controller = mediaController ?: return
        val currentPosition = controller.currentPosition
        if (currentPosition > 3000L) {
            controller.seekTo(0L)
        } else if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else if (controller.mediaItemCount > 0) {
            // Wrap to last track
            controller.seekToDefaultPosition(controller.mediaItemCount - 1)
        }
        updateStateFromController()
    }

    fun cycleRepeatMode(): Int {
        val controller = mediaController ?: return Player.REPEAT_MODE_ALL
        val nextMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_ALL
        }
        controller.repeatMode = nextMode
        updateStateFromController()
        return nextMode
    }

    fun setRepeatMode(repeatMode: Int) {
        mediaController?.repeatMode = repeatMode
        updateStateFromController()
    }

    fun selectTrack(trackId: String, autoPlay: Boolean = false) {
        val controller = mediaController ?: run {
            initialize { selectTrack(trackId, autoPlay) }
            return
        }

        val targetIndex = currentPlaylist.indexOfFirst { it.id == trackId }
        if (targetIndex >= 0) {
            val isYouTube = currentPlaylist[targetIndex].isYouTube
            if (isYouTube) {
                // If it's a YouTube track, we must pause the Media3 player.
                // YouTube playback is handled visually on ActiveFocusScreen.
                controller.pause()
                if (autoPlay) {
                    _playerUiState.update { it.copy(isPlaying = true) }
                }
                updateStateFromController(forceTrackId = trackId)
                return
            }

            if (controller.mediaItemCount > targetIndex) {
                if (controller.currentMediaItemIndex != targetIndex) {
                    controller.seekToDefaultPosition(targetIndex)
                }
                if (autoPlay) {
                    controller.play()
                }
            } else {
                scope.launch(Dispatchers.Main) {
                    val mediaItems = currentPlaylist.map { FocusAudioCatalog.createMediaItem(context, it) }
                    controller.setMediaItems(mediaItems, targetIndex, 0L)
                    controller.prepare()
                    if (autoPlay) {
                        controller.play()
                    }
                }
            }
        }
        updateStateFromController()
    }

    private fun updateStateFromController(forceTrackId: String? = null) {
        val controller = mediaController
        if (controller == null) {
            _playerUiState.update { it.copy(isConnected = false) }
            return
        }

        val currentIndex = controller.currentMediaItemIndex.coerceAtLeast(0)
        val currentMediaItem = controller.currentMediaItem
        val trackId = forceTrackId ?: currentMediaItem?.mediaId ?: currentPlaylist.getOrNull(currentIndex)?.id ?: "deep_focus"
        val currentTrack = currentPlaylist.find { it.id == trackId } ?: FocusAudioCatalog.getTrackById(trackId)

        val isYouTube = currentTrack.isYouTube
        val isPlaying = if (isYouTube) _playerUiState.value.isPlaying else controller.isPlaying
        val playbackState = controller.playbackState

        val status = when (playbackState) {
            Player.STATE_IDLE -> PlaybackStatus.IDLE
            Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            Player.STATE_READY -> if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.READY
            Player.STATE_ENDED -> PlaybackStatus.ENDED
            else -> PlaybackStatus.IDLE
        }

        _playerUiState.update {
            it.copy(
                isConnected = true,
                status = status,
                isPlaying = isPlaying,
                isStopped = !isPlaying && controller.currentPosition == 0L,
                currentTrack = currentTrack,
                currentMediaIndex = currentIndex,
                playlist = currentPlaylist,
                repeatMode = controller.repeatMode,
                durationMs = controller.duration.coerceAtLeast(0L),
                currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                hasNextTrack = controller.hasNextMediaItem() || controller.mediaItemCount > 1,
                hasPreviousTrack = controller.hasPreviousMediaItem() || controller.mediaItemCount > 1,
                errorMessage = null
            )
        }
    }

    private fun startProgressPolling() {
        stopProgressPolling()
        progressPollingJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                val controller = mediaController
                if (controller != null && controller.isPlaying) {
                    _playerUiState.update {
                        it.copy(
                            currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                            durationMs = controller.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(1000L) // 1 second intervals for low CPU usage
            }
        }
    }

    private fun stopProgressPolling() {
        progressPollingJob?.cancel()
        progressPollingJob = null
    }

    fun release() {
        stopProgressPolling()
        mediaController?.let { controller ->
            controller.removeListener(playerListener)
            controller.release()
            mediaController = null
        }
        controllerFuture?.let {
            MediaController.releaseFuture(it)
            controllerFuture = null
        }
        _playerUiState.update { it.copy(isConnected = false) }
    }
}
