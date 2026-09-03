package com.sprinthon.focusclock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusProfile
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.model.WallpaperBackgroundType
import com.sprinthon.focusclock.domain.model.WallpaperClockPosition
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.domain.session.FocusSessionManager
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.notification.FocusNotificationHelper
import com.sprinthon.focusclock.playback.FocusChimeHelper
import com.sprinthon.focusclock.playback.FocusHapticHelper
import com.sprinthon.focusclock.playback.FocusPlayerManager
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.clock.ClockFont
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FocusUiState(
    val preferences: FocusPreferences = FocusPreferences(),
    val session: SessionSnapshot = SessionSnapshot(),
    val configuredDurationMinutes: Int = 25,
    val isCustomDuration: Boolean = false,
    val configuredTimerMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
    val showCustomDurationDialog: Boolean = false,
    val showProfileSelectorSheet: Boolean = false,
    val controlsVisible: Boolean = false,
    val showExitConfirmationDialog: Boolean = false,
    val playerState: PlayerUiState = PlayerUiState(),
    val externalMediaState: com.sprinthon.focusclock.domain.model.ExternalMediaSessionState = com.sprinthon.focusclock.domain.model.ExternalMediaSessionState(),
    val allProfiles: List<FocusProfile> = FocusProfile.DEFAULT_PROFILES,
    val customTracks: List<com.sprinthon.focusclock.domain.model.FocusTrack> = emptyList(),
    val collections: List<com.sprinthon.focusclock.domain.model.TrackCollection> = emptyList(),
    val favoriteTrackIds: Set<String> = emptySet(),
    val activeCollection: com.sprinthon.focusclock.domain.model.TrackCollection? = null
)

class FocusViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val repository = FocusPreferencesRepository(application)
    private val sessionManager = FocusSessionManager(viewModelScope)
    val playerManager = FocusPlayerManager(application, viewModelScope)
    val systemMediaManager = com.sprinthon.focusclock.playback.SystemMediaControllerManager(application)

    private val _configuredDuration = MutableStateFlow(25)
    private val _isCustomDuration = MutableStateFlow(false)
    private val _configuredTimerMode = MutableStateFlow(TimerDisplayMode.COUNTDOWN)
    private val _showCustomDurationDialog = MutableStateFlow(false)
    private val _showProfileSelectorSheet = MutableStateFlow(false)
    private val _controlsVisible = MutableStateFlow(false)
    private val _showExitConfirmationDialog = MutableStateFlow(false)

    private var autoHideJob: Job? = null

    val preferencesState: StateFlow<FocusPreferences> = repository.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FocusPreferences()
    )

    val customProfilesState: StateFlow<List<FocusProfile>> = repository.customProfilesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val customTracksState: StateFlow<List<com.sprinthon.focusclock.domain.model.FocusTrack>> = repository.customTracksFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val trackCollectionsState: StateFlow<List<com.sprinthon.focusclock.domain.model.TrackCollection>> = repository.trackCollectionsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val favoriteTrackIdsState: StateFlow<Set<String>> = repository.favoriteTrackIdsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet()
    )

    val wallpaperConfigState: StateFlow<WallpaperConfig> = repository.wallpaperConfigFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = WallpaperConfig()
    )

    val sessionState: StateFlow<SessionSnapshot> = sessionManager.snapshot

    val playerState: StateFlow<PlayerUiState> = playerManager.playerUiState
    val externalMediaState: StateFlow<com.sprinthon.focusclock.domain.model.ExternalMediaSessionState> = systemMediaManager.mediaState

    val uiState: StateFlow<FocusUiState> = combine(
        preferencesState,
        sessionState,
        _configuredDuration,
        _isCustomDuration,
        _configuredTimerMode,
        _showCustomDurationDialog,
        _showProfileSelectorSheet,
        _controlsVisible,
        _showExitConfirmationDialog,
        playerState,
        externalMediaState,
        customProfilesState,
        customTracksState,
        trackCollectionsState,
        favoriteTrackIdsState
    ) { args: Array<Any> ->
        val prefs = args[0] as FocusPreferences
        val session = args[1] as SessionSnapshot
        val duration = args[2] as Int
        val isCustom = args[3] as Boolean
        val timerMode = args[4] as TimerDisplayMode
        val showCustomDuration = args[5] as Boolean
        val showProfiles = args[6] as Boolean
        val controls = args[7] as Boolean
        val exitDialog = args[8] as Boolean
        val player = args[9] as PlayerUiState
        val extMedia = args[10] as com.sprinthon.focusclock.domain.model.ExternalMediaSessionState
        @Suppress("UNCHECKED_CAST")
        val customProfiles = args[11] as List<FocusProfile>
        @Suppress("UNCHECKED_CAST")
        val customTracks = args[12] as List<com.sprinthon.focusclock.domain.model.FocusTrack>
        @Suppress("UNCHECKED_CAST")
        val collections = args[13] as List<com.sprinthon.focusclock.domain.model.TrackCollection>
        @Suppress("UNCHECKED_CAST")
        val favorites = args[14] as Set<String>

        val activeCollection = prefs.activeCollectionId?.let { activeId ->
            collections.find { it.id == activeId }
        }

        FocusUiState(
            preferences = prefs,
            session = session,
            configuredDurationMinutes = duration,
            isCustomDuration = isCustom,
            configuredTimerMode = timerMode,
            showCustomDurationDialog = showCustomDuration,
            showProfileSelectorSheet = showProfiles,
            controlsVisible = controls,
            showExitConfirmationDialog = exitDialog,
            playerState = player,
            externalMediaState = extMedia,
            allProfiles = FocusProfile.DEFAULT_PROFILES + customProfiles,
            customTracks = customTracks,
            collections = collections,
            favoriteTrackIds = favorites,
            activeCollection = activeCollection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FocusUiState()
    )

    init {
        // Initialize Media3 PlayerController connection
        playerManager.initialize()

        viewModelScope.launch {
            customTracksState.collect { tracks ->
                playerManager.setCustomTracks(tracks)
            }
        }

        viewModelScope.launch {
            repository.preferencesFlow.collect { prefs ->
                if (_configuredDuration.value == 25 && prefs.defaultDurationMinutes != 25) {
                    _configuredDuration.value = prefs.defaultDurationMinutes
                }
                _configuredTimerMode.value = prefs.timerDisplayMode

                // Keep repeat mode in sync with preferences
                val targetRepeat = if (prefs.musicLoop) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                if (playerManager.playerUiState.value.repeatMode != targetRepeat) {
                    playerManager.setRepeatMode(targetRepeat)
                }
            }
        }

        // Monitor session completion to trigger notifications, sounds, haptics, and stop music
        viewModelScope.launch {
            var lastCompletedSessionId: String? = null
            sessionState.collect { session ->
                if (session.state == SessionState.COMPLETED && session.sessionId != lastCompletedSessionId && session.sessionId.isNotEmpty()) {
                    lastCompletedSessionId = session.sessionId

                    // 1. Stop background music or pause external session
                    playerManager.stop()
                    if (preferencesState.value.audioSourceType == com.sprinthon.focusclock.domain.model.AudioSourceType.EXTERNAL_MUSIC) {
                        systemMediaManager.pause()
                    }

                    val currentPrefs = preferencesState.value

                    // 2. Play subtle completion chime if enabled
                    if (currentPrefs.soundOnCompletion) {
                        viewModelScope.launch {
                            FocusChimeHelper.playCompletionChime()
                        }
                    }

                    // 3. Trigger haptic feedback if enabled
                    if (currentPrefs.vibrateOnCompletion) {
                        FocusHapticHelper.performCompletionHaptic(application)
                    }

                    // 4. Post completion notification if enabled
                    if (currentPrefs.notifyOnCompletion) {
                        FocusNotificationHelper.postCompletionNotification(
                            context = application,
                            durationMinutes = session.durationMinutes,
                            profileName = session.profileName
                        )
                    }
                }
            }
        }
    }

    fun completeOnboarding(selectedProfile: FocusProfile? = null, customizeFirst: Boolean = false) {
        viewModelScope.launch {
            if (selectedProfile != null) {
                repository.applyProfile(selectedProfile)
                _configuredDuration.value = selectedProfile.durationMinutes
                _configuredTimerMode.value = selectedProfile.timerDisplayMode
            }
            repository.updateOnboardingCompleted(true)
        }
    }

    fun applyProfile(profile: FocusProfile) {
        _configuredDuration.value = profile.durationMinutes
        _isCustomDuration.value = false
        _configuredTimerMode.value = profile.timerDisplayMode
        viewModelScope.launch {
            repository.applyProfile(profile)
        }
    }

    fun saveCurrentAsProfile(name: String) {
        val currentPrefs = preferencesState.value
        val profile = FocusProfile(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            description = "${_configuredDuration.value} min · ${currentPrefs.clockStyle.displayName}",
            durationMinutes = _configuredDuration.value,
            timerDisplayMode = _configuredTimerMode.value,
            clockStyle = currentPrefs.clockStyle,
            backgroundType = currentPrefs.backgroundType,
            solidBackgroundColor = currentPrefs.solidBackgroundColor,
            backgroundImageUri = currentPrefs.backgroundImageUri,
            selectedTrackId = currentPrefs.selectedTrackId,
            autoPlayMusic = currentPrefs.autoPlayMusicOnFocus,
            keepScreenAwake = currentPrefs.keepScreenAwake,
            isBuiltIn = false
        )
        viewModelScope.launch {
            repository.saveCustomProfile(profile)
        }
    }

    fun deleteCustomProfile(profileId: String) {
        viewModelScope.launch {
            repository.deleteCustomProfile(profileId)
        }
    }

    fun addCustomTrack(uri: String, title: String, isYouTube: Boolean = false) {
        viewModelScope.launch {
            if (isYouTube) {
                val playlistId = com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractPlaylistId(uri)
                val videoId = com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(uri)

                // If user entered a playlist URL (or playlist with list parameter)
                if (playlistId != null && (videoId == null || uri.contains("list="))) {
                    val playlistInfo = com.sprinthon.focusclock.playback.YouTubeStreamHelper.fetchPlaylistTracks(uri)
                    if (playlistInfo != null && playlistInfo.tracks.isNotEmpty()) {
                        // Save all tracks to custom tracks
                        playlistInfo.tracks.forEach { track ->
                            repository.saveCustomTrack(track)
                        }

                        // Create and activate collection for the playlist
                        val collectionTitle = if (title.isNotBlank() && title != "YouTube Track") title else playlistInfo.title
                        val collection = com.sprinthon.focusclock.domain.model.TrackCollection(
                            id = java.util.UUID.randomUUID().toString(),
                            name = collectionTitle,
                            description = "YouTube Playlist by ${playlistInfo.author}",
                            trackIds = playlistInfo.tracks.map { it.id },
                            playbackMode = com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION,
                            accentColorHex = 0xFFF59E0B,
                            iconName = "playlist",
                            createdAt = System.currentTimeMillis()
                        )
                        repository.saveTrackCollection(collection)

                        // Immediately play the collection
                        playCollection(collection.id, playlistInfo.tracks.first().id, previewPlay = true)
                        return@launch
                    }
                }

                // Single YouTube Video
                val metadata = com.sprinthon.focusclock.playback.YouTubeStreamHelper.fetchVideoMetadata(uri)
                val resolvedTitle = if (title.isNotBlank() && title != "YouTube Track") {
                    title
                } else {
                    metadata?.title ?: "YouTube Audio"
                }
                val resolvedArtist = metadata?.author ?: "YouTube"

                val track = com.sprinthon.focusclock.domain.model.FocusTrack(
                    id = java.util.UUID.randomUUID().toString(),
                    title = resolvedTitle,
                    artist = resolvedArtist,
                    uri = uri,
                    isBuiltIn = false,
                    isYouTube = true
                )
                repository.saveCustomTrack(track)
                selectTrackInSettings(track.id, previewPlay = true)
            } else {
                val track = com.sprinthon.focusclock.domain.model.FocusTrack(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title.ifBlank { "Custom Audio" },
                    artist = "Local File",
                    uri = uri,
                    isBuiltIn = false,
                    isYouTube = false
                )
                repository.saveCustomTrack(track)
                selectTrackInSettings(track.id, previewPlay = true)
            }
        }
    }

    fun deleteCustomTrack(trackId: String) {
        viewModelScope.launch {
            repository.deleteCustomTrack(trackId)
            // If the deleted track was selected, fallback to deep_focus
            if (preferencesState.value.selectedTrackId == trackId) {
                selectTrackInSettings("deep_focus", previewPlay = false)
            }
        }
    }

    fun createCollection(
        name: String,
        description: String = "",
        trackIds: List<String> = emptyList(),
        playbackMode: com.sprinthon.focusclock.domain.model.CollectionPlaybackMode = com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION,
        accentColorHex: Long = 0xFFF59E0B,
        iconName: String = "playlist"
    ) {
        viewModelScope.launch {
            val collection = com.sprinthon.focusclock.domain.model.TrackCollection(
                id = java.util.UUID.randomUUID().toString(),
                name = name.ifBlank { "Focus Mix" },
                description = description,
                trackIds = trackIds,
                playbackMode = playbackMode,
                accentColorHex = accentColorHex,
                iconName = iconName,
                createdAt = System.currentTimeMillis()
            )
            repository.saveTrackCollection(collection)
        }
    }

    fun updateCollection(collection: com.sprinthon.focusclock.domain.model.TrackCollection) {
        viewModelScope.launch {
            repository.saveTrackCollection(collection)
            // If this is the active collection playing, update playback mode and items
            if (preferencesState.value.activeCollectionId == collection.id) {
                val allTracks = com.sprinthon.focusclock.playback.FocusAudioCatalog.BUILT_IN_TRACKS + customTracksState.value
                playerManager.playCollection(
                    collection = collection,
                    allAvailableTracks = allTracks,
                    startTrackId = playerState.value.currentTrack.id,
                    autoPlay = playerState.value.isPlaying
                )
            }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            repository.deleteTrackCollection(collectionId)
            if (preferencesState.value.activeCollectionId == collectionId) {
                val allTracks = com.sprinthon.focusclock.playback.FocusAudioCatalog.BUILT_IN_TRACKS + customTracksState.value
                playerManager.clearActiveCollection(allTracks)
            }
        }
    }

    fun toggleFavoriteTrack(trackId: String) {
        viewModelScope.launch {
            repository.toggleFavoriteTrack(trackId)
        }
    }

    fun addTrackToCollection(collectionId: String, trackId: String) {
        viewModelScope.launch {
            val collection = trackCollectionsState.value.find { it.id == collectionId } ?: return@launch
            if (!collection.trackIds.contains(trackId)) {
                val updated = collection.copy(trackIds = collection.trackIds + trackId)
                repository.saveTrackCollection(updated)
            }
        }
    }

    fun removeTrackFromCollection(collectionId: String, trackId: String) {
        viewModelScope.launch {
            val collection = trackCollectionsState.value.find { it.id == collectionId } ?: return@launch
            val updated = collection.copy(trackIds = collection.trackIds.filter { it != trackId })
            repository.saveTrackCollection(updated)
        }
    }

    fun playCollection(
        collectionId: String,
        startTrackId: String? = null,
        previewPlay: Boolean = true
    ) {
        viewModelScope.launch {
            val collection = trackCollectionsState.value.find { it.id == collectionId } ?: return@launch
            repository.updateActiveCollectionId(collectionId)
            val allTracks = com.sprinthon.focusclock.playback.FocusAudioCatalog.BUILT_IN_TRACKS + customTracksState.value
            val targetStart = startTrackId ?: collection.trackIds.firstOrNull() ?: "deep_focus"
            repository.updateSelectedTrackId(targetStart)
            playerManager.playCollection(
                collection = collection,
                allAvailableTracks = allTracks,
                startTrackId = targetStart,
                autoPlay = previewPlay
            )
        }
    }

    fun clearActiveCollection() {
        viewModelScope.launch {
            repository.updateActiveCollectionId(null)
            val allTracks = com.sprinthon.focusclock.playback.FocusAudioCatalog.BUILT_IN_TRACKS + customTracksState.value
            playerManager.clearActiveCollection(allTracks)
        }
    }

    fun setCollectionPlaybackMode(mode: com.sprinthon.focusclock.domain.model.CollectionPlaybackMode) {
        viewModelScope.launch {
            repository.updateCollectionPlaybackMode(mode)
            val repeatMode = when (mode) {
                com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION -> androidx.media3.common.Player.REPEAT_MODE_ALL
                com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_SINGLE -> androidx.media3.common.Player.REPEAT_MODE_ONE
                com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.PLAY_ONCE,
                com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.PLAY_COLLECTION_ONCE -> androidx.media3.common.Player.REPEAT_MODE_OFF
            }
            playerManager.setRepeatMode(repeatMode)
        }
    }

    fun setShowProfileSelectorSheet(show: Boolean) {
        _showProfileSelectorSheet.value = show
    }



    fun selectPresetDuration(preset: PresetDuration) {
        if (preset == PresetDuration.CUSTOM) {
            _showCustomDurationDialog.value = true
        } else {
            _isCustomDuration.value = false
            _configuredDuration.value = preset.minutes
            if (preset == PresetDuration.UNLIMITED) {
                _configuredTimerMode.value = TimerDisplayMode.ELAPSED
            }
        }
    }

    fun setCustomDuration(minutes: Int) {
        val validated = minutes.coerceIn(1, 720)
        _configuredDuration.value = validated
        _isCustomDuration.value = true
        _showCustomDurationDialog.value = false
    }

    fun setTimerDisplayMode(mode: TimerDisplayMode) {
        _configuredTimerMode.value = mode
        viewModelScope.launch {
            repository.updateTimerDisplayMode(mode)
        }
    }

    fun setClockStyle(style: ClockStyle) {
        viewModelScope.launch {
            repository.updateClockStyle(style)
        }
    }

    fun setClockFont(font: com.sprinthon.focusclock.ui.clock.ClockFont) {
        viewModelScope.launch {
            repository.updateClockFont(font)
        }
    }

    fun setClockScale(scale: Float) {
        viewModelScope.launch {
            repository.updateClockScale(scale)
        }
    }

    fun setAnalogNumeralSize(size: com.sprinthon.focusclock.domain.model.AnalogNumeralSize) {
        viewModelScope.launch {
            repository.updateAnalogNumeralSize(size)
        }
    }

    fun setAnalogNumeralScale(scale: Float) {
        viewModelScope.launch {
            repository.updateAnalogNumeralScale(scale)
        }
    }

    fun setTimeFormat(is24Hour: Boolean) {
        viewModelScope.launch {
            repository.updateTimeFormat(is24Hour)
        }
    }

    fun setShowDate(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowDate(show)
        }
    }

    fun setShowDayOfWeek(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowDayOfWeek(show)
        }
    }

    fun setDateFormatOption(option: com.sprinthon.focusclock.domain.model.DateFormatOption) {
        viewModelScope.launch {
            repository.updateDateFormatOption(option)
        }
    }

    fun setShowTimer(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowTimer(show)
        }
    }

    fun setDefaultDuration(minutes: Int) {
        _configuredDuration.value = minutes
        viewModelScope.launch {
            repository.updateDefaultDuration(minutes)
        }
    }

    fun setKeepScreenAwake(awake: Boolean) {
        viewModelScope.launch {
            repository.updateKeepScreenAwake(awake)
        }
    }

    fun setAutoHideControls(autoHide: Boolean) {
        viewModelScope.launch {
            repository.updateAutoHideControls(autoHide)
        }
    }

    fun setImmersiveFullscreen(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateImmersiveFullscreen(enabled)
        }
    }

    fun setAutoPlayMusic(autoPlay: Boolean) {
        viewModelScope.launch {
            repository.updateAutoPlayMusic(autoPlay)
        }
    }

    fun setBatterySaverEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBatterySaverEnabled(enabled)
        }
    }

    fun setMusicLoop(loop: Boolean) {
        viewModelScope.launch {
            repository.updateMusicLoop(loop)
            playerManager.setRepeatMode(if (loop) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF)
        }
    }

    fun setMusicVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        viewModelScope.launch {
            repository.updateMusicVolume(clamped)
        }
    }

    fun setShowWaveform(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowWaveform(show)
        }
    }

    fun selectTrackInSettings(trackId: String, previewPlay: Boolean = false) {
        viewModelScope.launch {
            repository.updateSelectedTrackId(trackId)
            playerManager.setCustomTracks(customTracksState.value)
            playerManager.selectTrack(trackId, autoPlay = previewPlay)
        }
    }

    fun setConfirmBeforeExit(confirm: Boolean) {
        viewModelScope.launch {
            repository.updateConfirmBeforeExit(confirm)
        }
    }

    fun setVibrateOnCompletion(vibrate: Boolean) {
        viewModelScope.launch {
            repository.updateVibrateOnCompletion(vibrate)
        }
    }

    fun setNotifyOnCompletion(notify: Boolean) {
        viewModelScope.launch {
            repository.updateNotifyOnCompletion(notify)
        }
    }

    fun setSoundOnCompletion(sound: Boolean) {
        viewModelScope.launch {
            repository.updateSoundOnCompletion(sound)
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            repository.resetAllSettingsToDefault()
            _configuredDuration.value = 25
            _isCustomDuration.value = false
            _configuredTimerMode.value = TimerDisplayMode.COUNTDOWN
        }
    }

    fun setBackgroundType(type: com.sprinthon.focusclock.domain.model.BackgroundType) {
        viewModelScope.launch {
            repository.updateBackgroundType(type)
        }
    }

    fun setSolidBackgroundColor(colorHex: Long) {
        viewModelScope.launch {
            repository.updateSolidBackgroundColor(colorHex)
        }
    }

    fun setBackgroundImageUri(uri: String?) {
        viewModelScope.launch {
            repository.updateBackgroundImageUri(uri)
        }
    }

    fun setSlideshowImageUris(uris: List<String>) {
        viewModelScope.launch {
            repository.updateSlideshowImageUris(uris)
        }
    }

    fun addSlideshowImageUris(newUris: List<String>) {
        viewModelScope.launch {
            val current = preferencesState.value.slideshowImageUris
            val combined = (current + newUris).distinct()
            repository.updateSlideshowImageUris(combined)
        }
    }

    fun removeSlideshowImageUri(uriToRemove: String) {
        viewModelScope.launch {
            val current = preferencesState.value.slideshowImageUris
            val updated = current.filter { it != uriToRemove }
            repository.updateSlideshowImageUris(updated)
            if (updated.isEmpty() && preferencesState.value.backgroundType == com.sprinthon.focusclock.domain.model.BackgroundType.SLIDESHOW) {
                repository.updateBackgroundType(com.sprinthon.focusclock.domain.model.BackgroundType.SOLID_COLOR)
            }
        }
    }

    fun setSlideshowInterval(interval: com.sprinthon.focusclock.domain.model.SlideshowInterval) {
        viewModelScope.launch {
            repository.updateSlideshowInterval(interval)
        }
    }

    fun setSlideshowShuffle(shuffle: Boolean) {
        viewModelScope.launch {
            repository.updateSlideshowShuffle(shuffle)
        }
    }

    fun setSlideshowTransition(transition: com.sprinthon.focusclock.domain.model.SlideshowTransition) {
        viewModelScope.launch {
            repository.updateSlideshowTransition(transition)
        }
    }

    fun setBackgroundOverlayStrength(strength: Float) {
        viewModelScope.launch {
            repository.updateOverlayStrength(strength.coerceIn(0f, 0.9f))
        }
    }

    // Wallpaper Configuration Methods
    fun updateWallpaperConfig(config: WallpaperConfig) {
        viewModelScope.launch {
            repository.updateWallpaperConfig(config)
        }
    }

    fun updateWallpaperClockPosition(position: WallpaperClockPosition) {
        viewModelScope.launch {
            repository.updateWallpaperClockPosition(position)
        }
    }

    fun updateWallpaperClockStyle(style: ClockStyle) {
        viewModelScope.launch {
            repository.updateWallpaperClockStyle(style)
        }
    }

    fun updateWallpaperClockFont(font: ClockFont) {
        viewModelScope.launch {
            repository.updateWallpaperClockFont(font)
        }
    }

    fun updateWallpaperClockColor(colorHex: Long) {
        viewModelScope.launch {
            repository.updateWallpaperClockColor(colorHex)
        }
    }

    fun updateWallpaperAnalogOrientation(orientation: AnalogNumeralOrientation) {
        viewModelScope.launch {
            repository.updateWallpaperAnalogOrientation(orientation)
        }
    }

    fun updateWallpaperAnalogNumeralSize(size: com.sprinthon.focusclock.domain.model.AnalogNumeralSize) {
        viewModelScope.launch {
            repository.updateWallpaperAnalogNumeralSize(size)
        }
    }

    fun updateWallpaperAnalogNumeralScale(scale: Float) {
        viewModelScope.launch {
            repository.updateWallpaperAnalogNumeralScale(scale)
        }
    }

    fun updateWallpaperBackgroundType(type: WallpaperBackgroundType) {
        viewModelScope.launch {
            repository.updateWallpaperBackgroundType(type)
        }
    }

    fun updateWallpaperBackgroundColor(colorHex: Long) {
        viewModelScope.launch {
            repository.updateWallpaperBackgroundColor(colorHex)
        }
    }

    fun updateWallpaperBackgroundImageUri(uri: String?) {
        viewModelScope.launch {
            repository.updateWallpaperBackgroundImageUri(uri)
        }
    }

    fun updateWallpaperScrimOpacity(opacity: Float) {
        viewModelScope.launch {
            repository.updateWallpaperScrimOpacity(opacity.coerceIn(0f, 0.9f))
        }
    }

    fun updateWallpaperBlurRadius(radius: Int) {
        viewModelScope.launch {
            repository.updateWallpaperBlurRadius(radius.coerceIn(0, 25))
        }
    }

    fun updateWallpaperShowDate(show: Boolean) {
        viewModelScope.launch {
            repository.updateWallpaperShowDate(show)
        }
    }

    fun updateWallpaperShowSeconds(show: Boolean) {
        viewModelScope.launch {
            repository.updateWallpaperShowSeconds(show)
        }
    }

    fun updateWallpaperMotto(show: Boolean, motto: String) {
        viewModelScope.launch {
            repository.updateWallpaperMotto(show, motto)
        }
    }

    fun updateWallpaperShowStreak(show: Boolean) {
        viewModelScope.launch {
            repository.updateWallpaperShowStreak(show)
        }
    }

    fun updateWallpaperTimeFormat(is24Hour: Boolean) {
        viewModelScope.launch {
            repository.updateWallpaperTimeFormat(is24Hour)
        }
    }


    fun setShowCustomDurationDialog(show: Boolean) {
        _showCustomDurationDialog.value = show
    }

    fun startFocusSession() {
        val currentPrefs = preferencesState.value
        val duration = _configuredDuration.value
        val mode = if (duration <= 0) TimerDisplayMode.ELAPSED else _configuredTimerMode.value

        // Resolve active profile name
        val activeProfile = (FocusProfile.DEFAULT_PROFILES + customProfilesState.value)
            .find { it.id == currentPrefs.activeProfileId }
        val profileName = activeProfile?.name ?: "Focus"

        sessionManager.startSession(
            durationMinutes = duration,
            displayMode = mode,
            clockStyle = currentPrefs.clockStyle,
            profileName = profileName
        )

        // Ensure player is up to date with custom tracks before selecting
        val allTracks = com.sprinthon.focusclock.playback.FocusAudioCatalog.BUILT_IN_TRACKS + customTracksState.value
        playerManager.setCustomTracks(customTracksState.value)

        // Handle audio autoplay preference on focus start with authoritative selected track
        val selectedTrackId = currentPrefs.selectedTrackId
        val selectedTrack = allTracks.find { it.id == selectedTrackId } ?: com.sprinthon.focusclock.playback.FocusAudioCatalog.getTrackById(selectedTrackId)
        val videoId = com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(selectedTrack.uri)
        android.util.Log.d("FocusViewModel", "[DIAGNOSTIC] Focus Session Started: selectedTrackId=$selectedTrackId, title='${selectedTrack.title}', isYouTube=${selectedTrack.isYouTube}, videoId=$videoId, originalUrl=${selectedTrack.uri}, autoPlayMusicOnFocus=${currentPrefs.autoPlayMusicOnFocus}")

        if (currentPrefs.autoPlayMusicOnFocus) {
            playerManager.selectTrack(selectedTrackId, autoPlay = true)
        } else {
            playerManager.selectTrack(selectedTrackId, autoPlay = false)
        }

        // Trigger session start haptic if vibration feedback is enabled
        if (currentPrefs.vibrateOnCompletion) {
            FocusHapticHelper.performSessionStartHaptic(application)
        }

        // Show controls temporarily on start for 4.5s
        showControlsTemporarily()
    }

    fun startFocusAgain() {
        resetSession()
        startFocusSession()
    }

    fun onScreenTapped() {
        if (!_controlsVisible.value) {
            showControlsTemporarily()
        } else {
            _controlsVisible.value = false
            autoHideJob?.cancel()
        }
    }

    fun showControlsTemporarily(durationMillis: Long = 4500L) {
        _controlsVisible.value = true
        autoHideJob?.cancel()
        autoHideJob = viewModelScope.launch {
            delay(durationMillis)
            _controlsVisible.value = false
        }
    }

    fun setControlsVisible(visible: Boolean) {
        _controlsVisible.value = visible
        if (visible) {
            showControlsTemporarily()
        } else {
            autoHideJob?.cancel()
        }
    }

    fun setExitConfirmationDialogVisible(show: Boolean) {
        _showExitConfirmationDialog.value = show
    }

    fun togglePlayerPlayPause() {
        playerManager.togglePlayPause()
        showControlsTemporarily()
    }

    fun stopPlayer() {
        playerManager.stop()
        showControlsTemporarily()
    }

    fun nextPlayerTrack() {
        playerManager.nextTrack()
        viewModelScope.launch {
            val nextTrack = playerManager.playerUiState.value.currentTrack
            repository.updateSelectedTrackId(nextTrack.id)
        }
        showControlsTemporarily()
    }

    fun previousPlayerTrack() {
        playerManager.previousTrack()
        viewModelScope.launch {
            val prevTrack = playerManager.playerUiState.value.currentTrack
            repository.updateSelectedTrackId(prevTrack.id)
        }
        showControlsTemporarily()
    }

    fun togglePlayerLoop() {
        val newMode = playerManager.cycleRepeatMode()
        viewModelScope.launch {
            repository.updateMusicLoop(newMode != Player.REPEAT_MODE_OFF)
        }
        showControlsTemporarily()
    }

    fun setAudioSourceType(type: com.sprinthon.focusclock.domain.model.AudioSourceType) {
        viewModelScope.launch {
            repository.updateAudioSourceType(type)
            if (type == com.sprinthon.focusclock.domain.model.AudioSourceType.EXTERNAL_MUSIC) {
                playerManager.stop()
                systemMediaManager.refreshActiveSession()
            }
        }
    }

    fun toggleExternalMediaPlayPause() {
        systemMediaManager.togglePlayPause()
        showControlsTemporarily()
    }

    fun playExternalMedia() {
        systemMediaManager.play()
        showControlsTemporarily()
    }

    fun pauseExternalMedia() {
        systemMediaManager.pause()
        showControlsTemporarily()
    }

    fun skipExternalMediaNext() {
        systemMediaManager.skipToNext()
        showControlsTemporarily()
    }

    fun skipExternalMediaPrevious() {
        systemMediaManager.skipToPrevious()
        showControlsTemporarily()
    }

    fun seekExternalMedia(positionMs: Long) {
        systemMediaManager.seekTo(positionMs)
        showControlsTemporarily()
    }

    fun launchExternalMusicApp(packageName: String): Boolean {
        return systemMediaManager.launchApp(packageName)
    }

    fun refreshExternalMediaSession() {
        systemMediaManager.refreshActiveSession()
    }

    fun openNotificationListenerSettings() {
        systemMediaManager.openPermissionSettings()
    }

    fun transferExternalTrackToFocusPlayer() {
        val external = externalMediaState.value
        val trackTitle = if (external.title.isNotBlank()) external.title else "YouTube Audio"
        viewModelScope.launch {
            val hasDistinctArtist = external.artist.isNotBlank() && !external.artist.equals(external.appName, ignoreCase = true)
            val query = if (hasDistinctArtist) {
                "$trackTitle ${external.artist}"
            } else {
                trackTitle
            }
            val encodedQuery = try {
                java.net.URLEncoder.encode(query, "UTF-8")
            } catch (e: Exception) {
                query
            }
            val youtubeSearchUri = "https://www.youtube.com/results?search_query=$encodedQuery"
            
            val track = com.sprinthon.focusclock.domain.model.FocusTrack(
                id = java.util.UUID.randomUUID().toString(),
                title = trackTitle,
                artist = if (external.artist.isNotBlank()) external.artist else "YouTube",
                uri = youtubeSearchUri,
                isBuiltIn = false,
                isYouTube = true
            )
            repository.saveCustomTrack(track)
            
            // Switch audio source to Ambient/Internal so it plays within the focus screen
            setAudioSourceType(com.sprinthon.focusclock.domain.model.AudioSourceType.AMBIENT_SOUNDS)
            selectTrackInSettings(track.id, previewPlay = true)
            
            // Pause the external media app
            systemMediaManager.pause()
            showControlsTemporarily()
        }
    }

    fun pauseFocusSession() {
        sessionManager.pauseSession()
        // Stop ambient music or pause external music when focus is paused
        playerManager.pause()
        if (preferencesState.value.audioSourceType == com.sprinthon.focusclock.domain.model.AudioSourceType.EXTERNAL_MUSIC) {
            systemMediaManager.pause()
        }
        showControlsTemporarily()
    }

    fun resumeFocusSession() {
        sessionManager.resumeSession()
        // Trigger gentle resume haptic if vibration feedback is enabled
        if (preferencesState.value.vibrateOnCompletion) {
            FocusHapticHelper.performResumeHaptic(application)
        }
        showControlsTemporarily()
    }

    fun cancelFocusSession() {
        // Stop music immediately when session is cancelled
        playerManager.stop()
        if (preferencesState.value.audioSourceType == com.sprinthon.focusclock.domain.model.AudioSourceType.EXTERNAL_MUSIC) {
            systemMediaManager.pause()
        }
        sessionManager.cancelSession()
    }

    fun completeFocusSession() {
        sessionManager.completeSession()
    }

    fun resetSession() {
        _showExitConfirmationDialog.value = false
        _controlsVisible.value = false
        autoHideJob?.cancel()
        // Ensure music is fully stopped when session resets
        playerManager.stop()
        sessionManager.resetToIdle()
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}

