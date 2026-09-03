package com.sprinthon.focusclock.domain.model

import androidx.compose.runtime.Immutable
import com.sprinthon.focusclock.ui.clock.ClockFont

enum class ClockStyle(val displayName: String, val description: String) {
    CLEAN_DIGITAL("Clean Digital", "Stacked hours and minutes, bold and focused"),
    FLIP_CLOCK("Flip Clock", "Split-flap card style with subtle horizontal division"),
    MINIMAL_DIGITAL("Minimal Digital", "Ultra-clean pure typography without card container"),
    ANALOG("Analog Minimal", "Precision modern dial with hour, minute and second hands")
}

enum class SessionState {
    IDLE,
    STARTING,
    RUNNING,
    PAUSED,
    COMPLETED,
    CANCELLED
}

enum class TimerDisplayMode {
    COUNTDOWN,
    ELAPSED
}

enum class BackgroundType {
    SOLID_COLOR,
    SINGLE_IMAGE,
    SLIDESHOW
}

enum class SlideshowTransition {
    CROSSFADE,
    FADE,
    NONE
}

enum class SlideshowInterval(val seconds: Long, val label: String) {
    NEVER(0L, "Never"),
    FIVE_SEC(5L, "5 seconds"),
    FIFTEEN_SEC(15L, "15 seconds"),
    THIRTY_SEC(30L, "30 seconds"),
    ONE_MIN(60L, "1 minute"),
    FIVE_MIN(300L, "5 minutes"),
    TEN_MIN(600L, "10 minutes"),
    FIFTEEN_MIN(900L, "15 minutes")
}

enum class PresetDuration(val minutes: Int, val label: String) {
    MIN_25(25, "25 min"),
    MIN_45(45, "45 min"),
    MIN_60(60, "60 min"),
    MIN_90(90, "90 min"),
    MIN_120(120, "2 hours"),
    CUSTOM(-1, "Custom"),
    UNLIMITED(0, "Unlimited")
}

enum class DateFormatOption(val pattern: String, val displayName: String, val example: String) {
    SHORT_DAY_MONTH("EEE · MMM d", "Short (Thu · Aug 27)", "Thu · Aug 27"),
    FULL_DAY_MONTH("EEEE · MMM d", "Full Day (Thursday · Aug 27)", "Thursday · Aug 27"),
    DAY_MONTH_SHORT("d MMM · EEE", "Day First (27 Aug · Thu)", "27 Aug · Thu")
}

data class FocusSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val startedAt: Long = 0L,
    val targetEndAt: Long = 0L,
    val durationMinutes: Int = 25,
    val isUnlimited: Boolean = false,
    val displayMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
    val clockStyle: ClockStyle = ClockStyle.CLEAN_DIGITAL,
    val state: SessionState = SessionState.IDLE,
    val profileName: String = "Focus"
)

data class FocusProfile(
    val id: String,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val timerDisplayMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
    val clockStyle: ClockStyle = ClockStyle.CLEAN_DIGITAL,
    val backgroundType: BackgroundType = BackgroundType.SOLID_COLOR,
    val solidBackgroundColor: Long = 0xFF000000,
    val backgroundImageUri: String? = null,
    val selectedTrackId: String = "deep_focus",
    val autoPlayMusic: Boolean = false,
    val keepScreenAwake: Boolean = true,
    val isBuiltIn: Boolean = true
) {
    companion object {
        val DEEP_WORK = FocusProfile(
            id = "deep_work",
            name = "Deep Work",
            description = "50 min · Clean Digital · Pure Black · Deep Focus",
            durationMinutes = 50,
            timerDisplayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.CLEAN_DIGITAL,
            backgroundType = BackgroundType.SOLID_COLOR,
            solidBackgroundColor = 0xFF000000,
            selectedTrackId = "deep_focus",
            autoPlayMusic = true,
            keepScreenAwake = true,
            isBuiltIn = true
        )

        val STUDY = FocusProfile(
            id = "study",
            name = "Study",
            description = "45 min · Flip Clock · Forest Rain",
            durationMinutes = 45,
            timerDisplayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.FLIP_CLOCK,
            backgroundType = BackgroundType.SOLID_COLOR,
            solidBackgroundColor = 0xFF101418,
            selectedTrackId = "gentle_rain",
            autoPlayMusic = false,
            keepScreenAwake = true,
            isBuiltIn = true
        )

        val SHORT_FOCUS = FocusProfile(
            id = "short_focus",
            name = "Short Focus",
            description = "25 min · Minimal Digital · Distraction-free",
            durationMinutes = 25,
            timerDisplayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.MINIMAL_DIGITAL,
            backgroundType = BackgroundType.SOLID_COLOR,
            solidBackgroundColor = 0xFF000000,
            selectedTrackId = "deep_focus",
            autoPlayMusic = false,
            keepScreenAwake = true,
            isBuiltIn = true
        )

        val READING = FocusProfile(
            id = "reading",
            name = "Reading & Zen",
            description = "30 min · Analog Minimal · Ambient Flow",
            durationMinutes = 30,
            timerDisplayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.ANALOG,
            backgroundType = BackgroundType.SOLID_COLOR,
            solidBackgroundColor = 0xFF120E0A,
            selectedTrackId = "ambient_flow",
            autoPlayMusic = false,
            keepScreenAwake = true,
            isBuiltIn = true
        )

        val DEFAULT_PROFILES = listOf(DEEP_WORK, STUDY, SHORT_FOCUS, READING)
    }
}


enum class AnalogNumeralSize(val displayName: String, val scale: Float, val description: String) {
    STANDARD("Standard", 1.0f, "Classic timepiece proportions"),
    LARGE("Large", 1.35f, "Enhanced legibility watch face"),
    JUMBO("Jumbo", 1.70f, "Bold hero digits for executive desk view"),
    CARDINAL("12·3·6·9 Focus", 1.50f, "Prominent cardinal markers with subtle minor hours")
}

data class FocusPreferences(
    val clockStyle: ClockStyle = ClockStyle.CLEAN_DIGITAL,
    val clockFont: ClockFont = ClockFont.BEBAS_NEUE,
    val clockScale: Float = 1.15f,
    val analogNumeralSize: AnalogNumeralSize = AnalogNumeralSize.LARGE,
    val analogNumeralScale: Float = 1.35f,
    val timeFormat24Hour: Boolean = true,
    val showDate: Boolean = true,
    val showDayOfWeek: Boolean = true,
    val dateFormatOption: DateFormatOption = DateFormatOption.SHORT_DAY_MONTH,
    val showTimer: Boolean = true,
    val defaultDurationMinutes: Int = 25,
    val timerDisplayMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
    val backgroundType: BackgroundType = BackgroundType.SOLID_COLOR,
    val solidBackgroundColor: Long = 0xFF000000, // AMOLED pure black
    val backgroundImageUri: String? = null,
    val slideshowImageUris: List<String> = emptyList(),
    val slideshowInterval: SlideshowInterval = SlideshowInterval.THIRTY_SEC,
    val slideshowShuffle: Boolean = false,
    val slideshowTransition: SlideshowTransition = SlideshowTransition.CROSSFADE,
    val backgroundOverlayStrength: Float = 0.35f, // Dark overlay for readability
    val keepScreenAwake: Boolean = true,
    val autoHideControls: Boolean = true,
    val autoPlayMusicOnFocus: Boolean = false,
    val musicLoop: Boolean = true,
    val musicVolume: Float = 0.7f,
    val showWaveform: Boolean = true,
    val selectedTrackId: String = "deep_focus",
    val confirmBeforeExit: Boolean = true,
    val vibrateOnCompletion: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val activeProfileId: String = "custom",
    val immersiveFullscreenEnabled: Boolean = false,
    val batterySaverEnabled: Boolean = false,
    val notifyOnCompletion: Boolean = true,
    val soundOnCompletion: Boolean = true,
    val favoriteTrackIds: Set<String> = emptySet(),
    val activeCollectionId: String? = null,
    val collectionPlaybackMode: CollectionPlaybackMode = CollectionPlaybackMode.LOOP_COLLECTION,
    val audioSourceType: AudioSourceType = AudioSourceType.AMBIENT_SOUNDS
)

enum class AudioSourceType(val displayName: String, val description: String) {
    AMBIENT_SOUNDS("Ambient Sounds", "Built-in ambient soundscapes and local focus audio"),
    EXTERNAL_MUSIC("External Music", "Direct remote playback of Spotify, YouTube, SoundCloud & system media")
}

enum class CollectionPlaybackMode(val displayName: String, val description: String) {
    LOOP_COLLECTION("Loop All", "Cycles through all tracks in the collection continuously"),
    LOOP_SINGLE("Loop Single", "Repeats the current track continuously"),
    PLAY_ONCE("Play Once", "Plays the track once and stops"),
    PLAY_COLLECTION_ONCE("Play All Once", "Plays each track in the collection once in order, then stops")
}

@Immutable
data class TrackCollection(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val trackIds: List<String> = emptyList(),
    val playbackMode: CollectionPlaybackMode = CollectionPlaybackMode.LOOP_COLLECTION,
    val accentColorHex: Long = 0xFFF59E0B, // Amber default
    val iconName: String = "playlist",
    val createdAt: Long = System.currentTimeMillis()
)

@Immutable
data class FocusTrack(
    val id: String,
    val title: String,
    val artist: String,
    val uri: String,
    val isBuiltIn: Boolean = true,
    val isYouTube: Boolean = false
)
