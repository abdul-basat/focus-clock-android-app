package com.sprinthon.focusclock.playback

import android.content.ComponentName
import android.content.Context
import android.util.Log
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

import kotlinx.coroutines.withContext

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
    private var selectTrackJob: Job? = null

    private var currentPlaylist: List<FocusTrack> = FocusAudioCatalog.BUILT_IN_TRACKS
    private var activeCollectionId: String? = null
    private var activeCollectionName: String? = null
    private var currentCollectionMode: com.sprinthon.focusclock.domain.model.CollectionPlaybackMode = com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION

    fun setCustomTracks(customTracks: List<FocusTrack>) {
        if (activeCollectionId == null) {
            currentPlaylist = FocusAudioCatalog.BUILT_IN_TRACKS + customTracks
        }
        updateStateFromController()
    }

    fun playCollection(
        collection: com.sprinthon.focusclock.domain.model.TrackCollection,
        allAvailableTracks: List<FocusTrack>,
        startTrackId: String? = null,
        autoPlay: Boolean = true
    ) {
        val tracksInCollection = collection.trackIds.mapNotNull { id ->
            allAvailableTracks.find { it.id == id } ?: FocusAudioCatalog.getTrackById(id)
        }.ifEmpty {
            allAvailableTracks.take(1)
        }

        activeCollectionId = collection.id
        activeCollectionName = collection.name
        currentCollectionMode = collection.playbackMode
        currentPlaylist = tracksInCollection

        val repeatMode = when (collection.playbackMode) {
            com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION -> Player.REPEAT_MODE_ALL
            com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_SINGLE -> Player.REPEAT_MODE_ONE
            com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.PLAY_ONCE,
            com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.PLAY_COLLECTION_ONCE -> Player.REPEAT_MODE_OFF
        }

        val controller = mediaController ?: run {
            initialize {
                playCollection(collection, allAvailableTracks, startTrackId, autoPlay)
            }
            return
        }

        controller.repeatMode = repeatMode

        val targetIndex = if (startTrackId != null) {
            tracksInCollection.indexOfFirst { it.id == startTrackId }.coerceAtLeast(0)
        } else {
            0
        }

        scope.launch(Dispatchers.Main) {
            val targetTrack = tracksInCollection.getOrNull(targetIndex) ?: tracksInCollection.first()
            if (targetTrack.isYouTube) {
                _playerUiState.update {
                    it.copy(
                        status = PlaybackStatus.RESOLVING,
                        currentTrack = targetTrack,
                        errorMessage = null,
                        isPlaying = false,
                        isStopped = false
                    )
                }
            }
            try {
                val targetMediaItem = withContext(Dispatchers.IO) {
                    FocusAudioCatalog.createMediaItem(context, targetTrack)
                }
                controller.setMediaItem(targetMediaItem)
                controller.prepare()
                if (autoPlay) {
                    controller.play()
                }
                updateStateFromController(forceTrackId = targetTrack.id)
            } catch (e: Exception) {
                controller.pause()
                controller.clearMediaItems()
                _playerUiState.update {
                    it.copy(
                        status = PlaybackStatus.ERROR,
                        currentTrack = targetTrack,
                        errorMessage = e.localizedMessage ?: "Failed to play collection track. Tap to retry.",
                        isPlaying = false,
                        isStopped = true
                    )
                }
            }
        }
    }

    fun clearActiveCollection(allAvailableTracks: List<FocusTrack>) {
        activeCollectionId = null
        activeCollectionName = null
        currentPlaylist = allAvailableTracks
        updateStateFromController()
    }

    private val playerListener = object : Player.Listener {
        private var hasRetriedError = false

        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateName = when (playbackState) {
                Player.STATE_IDLE -> "IDLE"
                Player.STATE_BUFFERING -> "BUFFERING"
                Player.STATE_READY -> "READY"
                Player.STATE_ENDED -> "ENDED"
                else -> "UNKNOWN($playbackState)"
            }
            Log.d("FocusPlayerManager", "[DIAGNOSTIC] Playback State Changed: $stateName (isPlaying=${mediaController?.isPlaying})")
            if (playbackState == Player.STATE_READY) {
                hasRetriedError = false
            }
            updateStateFromController()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d("FocusPlayerManager", "[DIAGNOSTIC] isPlaying Changed: $isPlaying (playbackState=${mediaController?.playbackState})")
            updateStateFromController()
            if (isPlaying) {
                startProgressPolling()
            } else {
                stopProgressPolling()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val uri = mediaItem?.requestMetadata?.mediaUri ?: mediaItem?.localConfiguration?.uri
            val scheme = uri?.scheme ?: "none"
            val host = uri?.host ?: "none"
            Log.d("FocusPlayerManager", "[DIAGNOSTIC] onMediaItemTransition: mediaId=${mediaItem?.mediaId}, title='${mediaItem?.mediaMetadata?.title}', scheme=$scheme, host=$host, reason=$reason")
            updateStateFromController()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateStateFromController()
        }

        override fun onPlayerError(error: PlaybackException) {
            val currentTrack = _playerUiState.value.currentTrack
            val mediaItem = mediaController?.currentMediaItem
            val uri = mediaItem?.requestMetadata?.mediaUri ?: mediaItem?.localConfiguration?.uri
            val scheme = uri?.scheme ?: "unknown"
            val host = uri?.host ?: "unknown"
            val mimeType = uri?.getQueryParameter("mime") ?: "unspecified"
            val itag = uri?.getQueryParameter("itag") ?: "unspecified"

            // Traverse exception causes to extract deep HTTP / Codec info safely
            var httpResponseCode: Int? = null
            var httpResponseMessage: String? = null
            val httpHeaders = mutableMapOf<String, String>()
            var currentCause: Throwable? = error.cause
            var depth = 0
            while (currentCause != null && depth < 5) {
                if (currentCause is androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException) {
                    httpResponseCode = currentCause.responseCode
                    httpResponseMessage = currentCause.responseMessage
                    currentCause.headerFields.forEach { (k, v) ->
                        if (k != null) {
                            httpHeaders[k] = v.joinToString(", ")
                        }
                    }
                    break
                }
                currentCause = currentCause.cause
                depth++
            }

            Log.e(
                "FocusPlayerManager",
                """
                |==================== [EXOPLAYER DIAGNOSTIC ERROR] ====================
                | Error Class: ${error.javaClass.name}
                | Error Code: ${error.errorCode}
                | Error Name: ${error.errorCodeName}
                | Message: ${error.message}
                | Cause Class: ${error.cause?.javaClass?.name ?: "None"}
                | Cause Message: ${error.cause?.message ?: "None"}
                | HTTP Response Code: ${httpResponseCode ?: "N/A"}
                | HTTP Response Message: ${httpResponseMessage ?: "N/A"}
                | HTTP Response Headers: ${if (httpHeaders.isNotEmpty()) httpHeaders.toString() else "N/A"}
                | MediaItem Track ID: ${currentTrack.id}
                | MediaItem Track Title: ${currentTrack.title}
                | MediaItem isYouTube: ${currentTrack.isYouTube}
                | Media URI Scheme: $scheme
                | Media URI Host: $host
                | Extracted MIME: $mimeType
                | Extracted itag: $itag
                |======================================================================
                """.trimMargin()
            )

            if (currentTrack.isYouTube && !hasRetriedError) {
                hasRetriedError = true
                Log.d("FocusPlayerManager", "[DIAGNOSTIC] Triggering one-time automatic stream refresh retry for ${currentTrack.title}")
                scope.launch(Dispatchers.Main) {
                    val controller = mediaController ?: return@launch
                    try {
                        val freshMediaItem = withContext(Dispatchers.IO) {
                            FocusAudioCatalog.createMediaItem(context, currentTrack, forceRefreshStream = true)
                        }
                        controller.setMediaItem(freshMediaItem)
                        controller.prepare()
                        controller.play()
                        return@launch
                    } catch (e: Exception) {
                        Log.e("FocusPlayerManager", "[DIAGNOSTIC] Automatic retry failed for ${currentTrack.title}: ${e.message}")
                        _playerUiState.update {
                            it.copy(
                                status = PlaybackStatus.ERROR,
                                errorMessage = e.localizedMessage ?: "Failed to stream YouTube audio. Tap to retry.",
                                isPlaying = false,
                                isStopped = true
                            )
                        }
                    }
                }
                return
            }
            hasRetriedError = false
            _playerUiState.update {
                it.copy(
                    status = PlaybackStatus.ERROR,
                    errorMessage = error.localizedMessage ?: "Audio playback error. Tap to retry.",
                    isPlaying = false,
                    isStopped = true
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

        if (_playerUiState.value.status == PlaybackStatus.RESOLVING) {
            Log.d("FocusPlayerManager", "[DIAGNOSTIC] togglePlayPause ignored while track is currently RESOLVING")
            return
        }

        if (_playerUiState.value.status == PlaybackStatus.ERROR || controller.mediaItemCount == 0) {
            retryCurrentTrack(autoPlay = true)
            return
        }

        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_IDLE) {
                selectTrack(_playerUiState.value.currentTrack.id, autoPlay = true)
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

        if (_playerUiState.value.status == PlaybackStatus.RESOLVING) {
            Log.d("FocusPlayerManager", "[DIAGNOSTIC] play ignored while track is currently RESOLVING")
            return
        }

        if (_playerUiState.value.status == PlaybackStatus.ERROR || controller.mediaItemCount == 0) {
            retryCurrentTrack(autoPlay = true)
            return
        }

        if (controller.playbackState == Player.STATE_IDLE) {
            selectTrack(_playerUiState.value.currentTrack.id, autoPlay = true)
        } else {
            controller.play()
        }
        updateStateFromController()
    }

    fun pause() {
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
        if (currentPlaylist.isEmpty()) return
        val currentIndex = currentPlaylist.indexOfFirst { it.id == _playerUiState.value.currentTrack.id }.coerceAtLeast(0)
        val nextIndex = (currentIndex + 1) % currentPlaylist.size
        val nextTrack = currentPlaylist[nextIndex]
        val shouldPlay = _playerUiState.value.isPlaying
        selectTrack(nextTrack.id, autoPlay = shouldPlay)
    }

    /**
     * Previous track: If track played > 3 seconds, restarts current track; otherwise goes to previous.
     */
    fun previousTrack() {
        if (currentPlaylist.isEmpty()) return
        val controller = mediaController
        val currentPosition = controller?.currentPosition ?: 0L
        if (currentPosition > 3000L) {
            controller?.seekTo(0L)
            updateStateFromController()
            return
        }
        val currentIndex = currentPlaylist.indexOfFirst { it.id == _playerUiState.value.currentTrack.id }.coerceAtLeast(0)
        val prevIndex = if (currentIndex - 1 < 0) currentPlaylist.size - 1 else currentIndex - 1
        val prevTrack = currentPlaylist[prevIndex]
        val shouldPlay = _playerUiState.value.isPlaying
        selectTrack(prevTrack.id, autoPlay = shouldPlay)
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

    fun selectTrack(trackId: String, autoPlay: Boolean = false, forceRefresh: Boolean = false) {
        val targetTrack = currentPlaylist.find { it.id == trackId } ?: FocusAudioCatalog.getTrackById(trackId)
        
        // Ensure the track is present in current playlist
        if (currentPlaylist.none { it.id == targetTrack.id }) {
            currentPlaylist = currentPlaylist + targetTrack
        }

        val targetIndex = currentPlaylist.indexOfFirst { it.id == targetTrack.id }.coerceAtLeast(0)

        val videoId = YouTubeStreamHelper.extractVideoId(targetTrack.uri)
        Log.d("FocusPlayerManager", "[DIAGNOSTIC] selectTrack called: trackId=${targetTrack.id}, title='${targetTrack.title}', isYouTube=${targetTrack.isYouTube}, videoId=$videoId, originalUrl=${targetTrack.uri}, autoPlay=$autoPlay, forceRefresh=$forceRefresh")

        _playerUiState.update {
            it.copy(
                status = if (targetTrack.isYouTube) PlaybackStatus.RESOLVING else it.status,
                currentTrack = targetTrack,
                currentMediaIndex = targetIndex,
                errorMessage = null,
                isPlaying = false,
                isStopped = false
            )
        }

        val controller = mediaController ?: run {
            initialize { selectTrack(trackId, autoPlay, forceRefresh) }
            return
        }

        // Cancel any pending resolution job to prevent race conditions on rapid selection
        selectTrackJob?.cancel()
        selectTrackJob = scope.launch(Dispatchers.Main) {
            if (!isActive) return@launch
            try {
                // Asynchronously create the MediaItem for the target track on IO dispatcher
                val targetMediaItem = withContext(Dispatchers.IO) {
                    FocusAudioCatalog.createMediaItem(context, targetTrack, forceRefreshStream = forceRefresh)
                }

                if (!isActive || _playerUiState.value.currentTrack.id != targetTrack.id) {
                    Log.d("FocusPlayerManager", "[DIAGNOSTIC] Resolution completed but active track changed, discarding item for ${targetTrack.title}")
                    return@launch
                }

                controller.setMediaItem(targetMediaItem)
                controller.prepare()
                if (autoPlay) {
                    controller.play()
                }
                updateStateFromController(forceTrackId = targetTrack.id)
            } catch (e: Exception) {
                if (!isActive || _playerUiState.value.currentTrack.id != targetTrack.id) {
                    return@launch
                }
                Log.e("FocusPlayerManager", "[DIAGNOSTIC] Stream resolution error for ${targetTrack.title}: ${e.message}")
                controller.pause()
                controller.clearMediaItems()
                _playerUiState.update {
                    it.copy(
                        status = PlaybackStatus.ERROR,
                        currentTrack = targetTrack,
                        errorMessage = e.localizedMessage ?: "Failed to resolve YouTube audio stream. Tap to retry.",
                        isPlaying = false,
                        isStopped = true
                    )
                }
            }
        }
    }

    fun retryCurrentTrack(autoPlay: Boolean = true) {
        val track = _playerUiState.value.currentTrack
        Log.d("FocusPlayerManager", "[DIAGNOSTIC] Retry triggered for track: id=${track.id}, title='${track.title}'")
        selectTrack(track.id, autoPlay = autoPlay, forceRefresh = true)
    }

    private fun updateStateFromController(forceTrackId: String? = null) {
        val controller = mediaController
        if (controller == null) {
            _playerUiState.update { it.copy(isConnected = false) }
            return
        }

        val currentIndex = controller.currentMediaItemIndex.coerceAtLeast(0)
        val currentMediaItem = controller.currentMediaItem
        val previousTrackId = _playerUiState.value.currentTrack.id
        val trackId = forceTrackId 
            ?: currentMediaItem?.mediaId 
            ?: previousTrackId.takeIf { it.isNotEmpty() } 
            ?: currentPlaylist.getOrNull(currentIndex)?.id 
            ?: "deep_focus"
        val currentTrack = currentPlaylist.find { it.id == trackId } ?: FocusAudioCatalog.getTrackById(trackId)

        val isPlaying = controller.isPlaying
        val playbackState = controller.playbackState

        val currentStatus = _playerUiState.value.status
        val status = when {
            currentStatus == PlaybackStatus.RESOLVING && !isPlaying -> PlaybackStatus.RESOLVING
            currentStatus == PlaybackStatus.ERROR && !isPlaying && playbackState == Player.STATE_IDLE -> PlaybackStatus.ERROR
            playbackState == Player.STATE_IDLE -> PlaybackStatus.IDLE
            playbackState == Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            playbackState == Player.STATE_READY -> if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.READY
            playbackState == Player.STATE_ENDED -> PlaybackStatus.ENDED
            else -> PlaybackStatus.IDLE
        }

        _playerUiState.update {
            it.copy(
                isConnected = true,
                status = status,
                isPlaying = isPlaying,
                isStopped = !isPlaying && controller.currentPosition == 0L && status != PlaybackStatus.RESOLVING,
                currentTrack = currentTrack,
                currentMediaIndex = currentIndex,
                playlist = currentPlaylist,
                repeatMode = controller.repeatMode,
                durationMs = controller.duration.coerceAtLeast(0L),
                currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                hasNextTrack = controller.hasNextMediaItem() || controller.mediaItemCount > 1,
                hasPreviousTrack = controller.hasPreviousMediaItem() || controller.mediaItemCount > 1,
                errorMessage = if (status == PlaybackStatus.ERROR) it.errorMessage else null,
                activeCollectionId = activeCollectionId,
                activeCollectionName = activeCollectionName,
                collectionPlaybackMode = currentCollectionMode
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
