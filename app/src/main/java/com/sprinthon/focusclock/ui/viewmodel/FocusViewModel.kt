package com.sprinthon.focusclock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusProfile
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.session.FocusSessionManager
import com.sprinthon.focusclock.domain.session.SessionSnapshot
import com.sprinthon.focusclock.notification.FocusNotificationHelper
import com.sprinthon.focusclock.playback.FocusChimeHelper
import com.sprinthon.focusclock.playback.FocusHapticHelper
import com.sprinthon.focusclock.playback.FocusPlayerManager
import com.sprinthon.focusclock.playback.PlayerUiState
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
    val allProfiles: List<FocusProfile> = FocusProfile.DEFAULT_PROFILES,
    val customTracks: List<com.sprinthon.focusclock.domain.model.FocusTrack> = emptyList()
)

class FocusViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val repository = FocusPreferencesRepository(application)
    private val sessionManager = FocusSessionManager(viewModelScope)
    val playerManager = FocusPlayerManager(application, viewModelScope)

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



    val sessionState: StateFlow<SessionSnapshot> = sessionManager.snapshot

    val playerState: StateFlow<PlayerUiState> = playerManager.playerUiState

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
        customProfilesState,
        customTracksState
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
        @Suppress("UNCHECKED_CAST")
        val customProfiles = args[10] as List<FocusProfile>
        @Suppress("UNCHECKED_CAST")
        val customTracks = args[11] as List<com.sprinthon.focusclock.domain.model.FocusTrack>

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
            allProfiles = FocusProfile.DEFAULT_PROFILES + customProfiles,
            customTracks = customTracks
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

                    // 1. Stop background music
                    playerManager.stop()

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
            val track = com.sprinthon.focusclock.domain.model.FocusTrack(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                artist = if (isYouTube) "YouTube Link" else "Custom Audio",
                uri = uri,
                isBuiltIn = false,
                isYouTube = isYouTube
            )
            repository.saveCustomTrack(track)
            selectTrackInSettings(track.id, previewPlay = true)
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

        // Handle audio autoplay preference on focus start
        if (currentPrefs.autoPlayMusicOnFocus) {
            playerManager.selectTrack(currentPrefs.selectedTrackId, autoPlay = true)
        } else {
            playerManager.selectTrack(currentPrefs.selectedTrackId, autoPlay = false)
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

    fun pauseFocusSession() {
        sessionManager.pauseSession()
        // Stop music when focus is paused
        playerManager.pause()
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

