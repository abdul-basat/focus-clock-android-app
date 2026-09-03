package com.sprinthon.focusclock.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.ClockAlignment
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.domain.model.SlideshowTransition
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.model.WallpaperBackgroundType
import com.sprinthon.focusclock.domain.model.WallpaperClockPosition
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.ui.clock.ClockFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.focusDataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_clock_settings")

class FocusPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val CLOCK_FONT = stringPreferencesKey("clock_font")
        val CLOCK_SCALE = floatPreferencesKey("clock_scale")
        val ANALOG_NUMERAL_SIZE = stringPreferencesKey("analog_numeral_size")
        val ANALOG_NUMERAL_SCALE = floatPreferencesKey("analog_numeral_scale")
        val TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val SHOW_DAY_OF_WEEK = booleanPreferencesKey("show_day_of_week")
        val DATE_FORMAT_OPTION = stringPreferencesKey("date_format_option")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")
        val DEFAULT_DURATION_MINUTES = intPreferencesKey("default_duration_minutes")
        val TIMER_DISPLAY_MODE = stringPreferencesKey("timer_display_mode")
        val BACKGROUND_TYPE = stringPreferencesKey("background_type")
        val SOLID_BACKGROUND_COLOR = longPreferencesKey("solid_background_color")
        val BACKGROUND_IMAGE_URI = stringPreferencesKey("background_image_uri")
        val SLIDESHOW_IMAGE_URIS = stringPreferencesKey("slideshow_image_uris")
        val SLIDESHOW_INTERVAL = stringPreferencesKey("slideshow_interval")
        val SLIDESHOW_SHUFFLE = booleanPreferencesKey("slideshow_shuffle")
        val SLIDESHOW_TRANSITION = stringPreferencesKey("slideshow_transition")
        val BACKGROUND_OVERLAY_STRENGTH = floatPreferencesKey("background_overlay_strength")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val AUTO_HIDE_CONTROLS = booleanPreferencesKey("auto_hide_controls")
        val AUTO_PLAY_MUSIC = booleanPreferencesKey("auto_play_music")
        val MUSIC_LOOP = booleanPreferencesKey("music_loop")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val SHOW_WAVEFORM = booleanPreferencesKey("show_waveform")
        val SELECTED_TRACK_ID = stringPreferencesKey("selected_track_id")
        val CONFIRM_BEFORE_EXIT = booleanPreferencesKey("confirm_before_exit")
        val VIBRATE_ON_COMPLETION = booleanPreferencesKey("vibrate_on_completion")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
        val IMMERSIVE_FULLSCREEN_ENABLED = booleanPreferencesKey("immersive_fullscreen_enabled")
        val BATTERY_SAVER_ENABLED = booleanPreferencesKey("battery_saver_enabled")
        val NOTIFY_ON_COMPLETION = booleanPreferencesKey("notify_on_completion")
        val SOUND_ON_COMPLETION = booleanPreferencesKey("sound_on_completion")
        val FAVORITE_TRACK_IDS = stringPreferencesKey("favorite_track_ids")
        val ACTIVE_COLLECTION_ID = stringPreferencesKey("active_collection_id")
        val COLLECTION_PLAYBACK_MODE = stringPreferencesKey("collection_playback_mode")
        val AUDIO_SOURCE_TYPE = stringPreferencesKey("audio_source_type")
        val CUSTOM_PROFILES_JSON = stringPreferencesKey("custom_profiles_json")
        val CUSTOM_TRACKS_JSON = stringPreferencesKey("custom_tracks_json")
        val TRACK_COLLECTIONS_JSON = stringPreferencesKey("track_collections_json")

        // Wallpaper Keys
        val WALLPAPER_CLOCK_STYLE = stringPreferencesKey("wallpaper_clock_style")
        val WALLPAPER_CLOCK_FONT = stringPreferencesKey("wallpaper_clock_font")
        val WALLPAPER_CLOCK_COLOR = longPreferencesKey("wallpaper_clock_color")
        val WALLPAPER_X_PERCENT = floatPreferencesKey("wallpaper_x_percent")
        val WALLPAPER_Y_PERCENT = floatPreferencesKey("wallpaper_y_percent")
        val WALLPAPER_CLOCK_SCALE = floatPreferencesKey("wallpaper_clock_scale")
        val WALLPAPER_ALIGNMENT = stringPreferencesKey("wallpaper_alignment")
        val WALLPAPER_ANALOG_ORIENTATION = stringPreferencesKey("wallpaper_analog_orientation")
        val WALLPAPER_ANALOG_NUMERAL_SIZE = stringPreferencesKey("wallpaper_analog_numeral_size")
        val WALLPAPER_ANALOG_NUMERAL_SCALE = floatPreferencesKey("wallpaper_analog_numeral_scale")
        val WALLPAPER_BACKGROUND_TYPE = stringPreferencesKey("wallpaper_background_type")
        val WALLPAPER_BACKGROUND_COLOR = longPreferencesKey("wallpaper_background_color")
        val WALLPAPER_BACKGROUND_IMAGE_URI = stringPreferencesKey("wallpaper_background_image_uri")
        val WALLPAPER_SCRIM_OPACITY = floatPreferencesKey("wallpaper_scrim_opacity")
        val WALLPAPER_BLUR_RADIUS = intPreferencesKey("wallpaper_blur_radius")
        val WALLPAPER_SHOW_DATE = booleanPreferencesKey("wallpaper_show_date")
        val WALLPAPER_SHOW_SECONDS = booleanPreferencesKey("wallpaper_show_seconds")
        val WALLPAPER_SHOW_MOTTO = booleanPreferencesKey("wallpaper_show_motto")
        val WALLPAPER_CUSTOM_MOTTO = stringPreferencesKey("wallpaper_custom_motto")
        val WALLPAPER_SHOW_FOCUS_STREAK = booleanPreferencesKey("wallpaper_show_focus_streak")
        val WALLPAPER_TIME_FORMAT_24H = booleanPreferencesKey("wallpaper_time_format_24h")
    }

    val preferencesFlow: Flow<FocusPreferences> = context.focusDataStore.data.map { preferences ->
        val urisRaw = preferences[PreferencesKeys.SLIDESHOW_IMAGE_URIS] ?: ""
        val urisList = if (urisRaw.isBlank()) emptyList() else urisRaw.split("|||")
        val favRaw = preferences[PreferencesKeys.FAVORITE_TRACK_IDS] ?: ""
        val favSet = if (favRaw.isBlank()) emptySet() else favRaw.split("|||").toSet()

        FocusPreferences(
            clockStyle = preferences[PreferencesKeys.CLOCK_STYLE]?.let {
                try { com.sprinthon.focusclock.domain.model.ClockStyle.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.ClockStyle.CLEAN_DIGITAL }
            } ?: com.sprinthon.focusclock.domain.model.ClockStyle.CLEAN_DIGITAL,
            clockFont = preferences[PreferencesKeys.CLOCK_FONT]?.let {
                try { com.sprinthon.focusclock.ui.clock.ClockFont.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.ui.clock.ClockFont.BEBAS_NEUE }
            } ?: com.sprinthon.focusclock.ui.clock.ClockFont.BEBAS_NEUE,
            clockScale = preferences[PreferencesKeys.CLOCK_SCALE] ?: 1.15f,
            analogNumeralSize = preferences[PreferencesKeys.ANALOG_NUMERAL_SIZE]?.let {
                try { com.sprinthon.focusclock.domain.model.AnalogNumeralSize.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE }
            } ?: com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE,
            analogNumeralScale = preferences[PreferencesKeys.ANALOG_NUMERAL_SCALE] ?: 1.35f,
            timeFormat24Hour = preferences[PreferencesKeys.TIME_FORMAT_24H] ?: true,
            showDate = preferences[PreferencesKeys.SHOW_DATE] ?: true,
            showDayOfWeek = preferences[PreferencesKeys.SHOW_DAY_OF_WEEK] ?: true,
            dateFormatOption = preferences[PreferencesKeys.DATE_FORMAT_OPTION]?.let {
                try { com.sprinthon.focusclock.domain.model.DateFormatOption.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.DateFormatOption.SHORT_DAY_MONTH }
            } ?: com.sprinthon.focusclock.domain.model.DateFormatOption.SHORT_DAY_MONTH,
            showTimer = preferences[PreferencesKeys.SHOW_TIMER] ?: true,
            defaultDurationMinutes = preferences[PreferencesKeys.DEFAULT_DURATION_MINUTES] ?: 25,
            timerDisplayMode = preferences[PreferencesKeys.TIMER_DISPLAY_MODE]?.let {
                try { TimerDisplayMode.valueOf(it) } catch (e: Exception) { TimerDisplayMode.COUNTDOWN }
            } ?: TimerDisplayMode.COUNTDOWN,
            backgroundType = preferences[PreferencesKeys.BACKGROUND_TYPE]?.let {
                try { BackgroundType.valueOf(it) } catch (e: Exception) { BackgroundType.SOLID_COLOR }
            } ?: BackgroundType.SOLID_COLOR,
            solidBackgroundColor = preferences[PreferencesKeys.SOLID_BACKGROUND_COLOR] ?: 0xFF000000,
            backgroundImageUri = preferences[PreferencesKeys.BACKGROUND_IMAGE_URI],
            slideshowImageUris = urisList,
            slideshowInterval = preferences[PreferencesKeys.SLIDESHOW_INTERVAL]?.let {
                try { SlideshowInterval.valueOf(it) } catch (e: Exception) { SlideshowInterval.THIRTY_SEC }
            } ?: SlideshowInterval.THIRTY_SEC,
            slideshowShuffle = preferences[PreferencesKeys.SLIDESHOW_SHUFFLE] ?: false,
            slideshowTransition = preferences[PreferencesKeys.SLIDESHOW_TRANSITION]?.let {
                try { SlideshowTransition.valueOf(it) } catch (e: Exception) { SlideshowTransition.CROSSFADE }
            } ?: SlideshowTransition.CROSSFADE,
            backgroundOverlayStrength = preferences[PreferencesKeys.BACKGROUND_OVERLAY_STRENGTH] ?: 0.35f,
            keepScreenAwake = preferences[PreferencesKeys.KEEP_SCREEN_AWAKE] ?: true,
            autoHideControls = preferences[PreferencesKeys.AUTO_HIDE_CONTROLS] ?: true,
            autoPlayMusicOnFocus = preferences[PreferencesKeys.AUTO_PLAY_MUSIC] ?: false,
            musicLoop = preferences[PreferencesKeys.MUSIC_LOOP] ?: true,
            musicVolume = preferences[PreferencesKeys.MUSIC_VOLUME] ?: 0.7f,
            showWaveform = preferences[PreferencesKeys.SHOW_WAVEFORM] ?: true,
            selectedTrackId = preferences[PreferencesKeys.SELECTED_TRACK_ID] ?: "deep_focus",
            confirmBeforeExit = preferences[PreferencesKeys.CONFIRM_BEFORE_EXIT] ?: true,
            vibrateOnCompletion = preferences[PreferencesKeys.VIBRATE_ON_COMPLETION] ?: true,
            onboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
            activeProfileId = preferences[PreferencesKeys.ACTIVE_PROFILE_ID] ?: "custom",
            immersiveFullscreenEnabled = preferences[PreferencesKeys.IMMERSIVE_FULLSCREEN_ENABLED] ?: false,
            batterySaverEnabled = preferences[PreferencesKeys.BATTERY_SAVER_ENABLED] ?: false,
            notifyOnCompletion = preferences[PreferencesKeys.NOTIFY_ON_COMPLETION] ?: true,
            soundOnCompletion = preferences[PreferencesKeys.SOUND_ON_COMPLETION] ?: true,
            favoriteTrackIds = favSet,
            activeCollectionId = preferences[PreferencesKeys.ACTIVE_COLLECTION_ID],
            collectionPlaybackMode = preferences[PreferencesKeys.COLLECTION_PLAYBACK_MODE]?.let {
                try { com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION }
            } ?: com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION,
            audioSourceType = preferences[PreferencesKeys.AUDIO_SOURCE_TYPE]?.let {
                try { com.sprinthon.focusclock.domain.model.AudioSourceType.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.AudioSourceType.AMBIENT_SOUNDS }
            } ?: com.sprinthon.focusclock.domain.model.AudioSourceType.AMBIENT_SOUNDS
        )
    }

    val trackCollectionsFlow: Flow<List<com.sprinthon.focusclock.domain.model.TrackCollection>> = context.focusDataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.TRACK_COLLECTIONS_JSON] ?: "[]"
        deserializeTrackCollections(rawJson)
    }

    val favoriteTrackIdsFlow: Flow<Set<String>> = context.focusDataStore.data.map { preferences ->
        val favRaw = preferences[PreferencesKeys.FAVORITE_TRACK_IDS] ?: ""
        if (favRaw.isBlank()) emptySet() else favRaw.split("|||").toSet()
    }

    val customProfilesFlow: Flow<List<com.sprinthon.focusclock.domain.model.FocusProfile>> = context.focusDataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.CUSTOM_PROFILES_JSON] ?: "[]"
        deserializeCustomProfiles(rawJson)
    }



    val customTracksFlow: Flow<List<com.sprinthon.focusclock.domain.model.FocusTrack>> = context.focusDataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.CUSTOM_TRACKS_JSON] ?: "[]"
        deserializeCustomTracks(rawJson)
    }

    val wallpaperConfigFlow: Flow<WallpaperConfig> = context.focusDataStore.data.map { preferences ->
        val alignment = preferences[PreferencesKeys.WALLPAPER_ALIGNMENT]?.let {
            try { ClockAlignment.valueOf(it) } catch (e: Exception) { ClockAlignment.TOP_CENTER }
        } ?: ClockAlignment.TOP_CENTER

        val position = WallpaperClockPosition(
            xPercent = preferences[PreferencesKeys.WALLPAPER_X_PERCENT] ?: 0.0f,
            yPercent = preferences[PreferencesKeys.WALLPAPER_Y_PERCENT] ?: -0.35f,
            scale = preferences[PreferencesKeys.WALLPAPER_CLOCK_SCALE] ?: 1.0f,
            alignment = alignment
        )

        val clockStyle = preferences[PreferencesKeys.WALLPAPER_CLOCK_STYLE]?.let {
            try { ClockStyle.valueOf(it) } catch (e: Exception) { ClockStyle.ANALOG }
        } ?: ClockStyle.ANALOG

        val clockFont = preferences[PreferencesKeys.WALLPAPER_CLOCK_FONT]?.let {
            try { ClockFont.valueOf(it) } catch (e: Exception) { ClockFont.BEBAS_NEUE }
        } ?: ClockFont.BEBAS_NEUE

        val numeralOrientation = preferences[PreferencesKeys.WALLPAPER_ANALOG_ORIENTATION]?.let {
            try { AnalogNumeralOrientation.valueOf(it) } catch (e: Exception) { AnalogNumeralOrientation.HORIZONTAL_UPRIGHT }
        } ?: AnalogNumeralOrientation.HORIZONTAL_UPRIGHT

        val numeralSize = preferences[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SIZE]?.let {
            try { com.sprinthon.focusclock.domain.model.AnalogNumeralSize.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE }
        } ?: com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE

        val numeralScale = preferences[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SCALE] ?: 1.35f

        val bgType = preferences[PreferencesKeys.WALLPAPER_BACKGROUND_TYPE]?.let {
            try { WallpaperBackgroundType.valueOf(it) } catch (e: Exception) { WallpaperBackgroundType.SOLID_COLOR }
        } ?: WallpaperBackgroundType.SOLID_COLOR

        WallpaperConfig(
            clockStyle = clockStyle,
            clockFont = clockFont,
            clockColorHex = preferences[PreferencesKeys.WALLPAPER_CLOCK_COLOR] ?: 0xFFFFFFFFL,
            position = position,
            analogNumeralOrientation = numeralOrientation,
            analogNumeralSize = numeralSize,
            analogNumeralScale = numeralScale,
            backgroundType = bgType,
            backgroundColorHex = preferences[PreferencesKeys.WALLPAPER_BACKGROUND_COLOR] ?: 0xFF000000L,
            backgroundImageUri = preferences[PreferencesKeys.WALLPAPER_BACKGROUND_IMAGE_URI],
            scrimOpacity = preferences[PreferencesKeys.WALLPAPER_SCRIM_OPACITY] ?: 0.25f,
            blurRadius = preferences[PreferencesKeys.WALLPAPER_BLUR_RADIUS] ?: 0,
            showDate = preferences[PreferencesKeys.WALLPAPER_SHOW_DATE] ?: true,
            showSeconds = preferences[PreferencesKeys.WALLPAPER_SHOW_SECONDS] ?: true,
            showMotto = preferences[PreferencesKeys.WALLPAPER_SHOW_MOTTO] ?: false,
            customMotto = preferences[PreferencesKeys.WALLPAPER_CUSTOM_MOTTO] ?: "Stay in the Flow",
            showFocusStreak = preferences[PreferencesKeys.WALLPAPER_SHOW_FOCUS_STREAK] ?: false,
            timeFormat24Hour = preferences[PreferencesKeys.WALLPAPER_TIME_FORMAT_24H] ?: true
        )
    }

    suspend fun updateWallpaperConfig(config: WallpaperConfig) {
        context.focusDataStore.edit { prefs ->
            prefs[PreferencesKeys.WALLPAPER_CLOCK_STYLE] = config.clockStyle.name
            prefs[PreferencesKeys.WALLPAPER_CLOCK_FONT] = config.clockFont.name
            prefs[PreferencesKeys.WALLPAPER_CLOCK_COLOR] = config.clockColorHex
            prefs[PreferencesKeys.WALLPAPER_X_PERCENT] = config.position.xPercent
            prefs[PreferencesKeys.WALLPAPER_Y_PERCENT] = config.position.yPercent
            prefs[PreferencesKeys.WALLPAPER_CLOCK_SCALE] = config.position.scale
            prefs[PreferencesKeys.WALLPAPER_ALIGNMENT] = config.position.alignment.name
            prefs[PreferencesKeys.WALLPAPER_ANALOG_ORIENTATION] = config.analogNumeralOrientation.name
            prefs[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SIZE] = config.analogNumeralSize.name
            prefs[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SCALE] = config.analogNumeralScale
            prefs[PreferencesKeys.WALLPAPER_BACKGROUND_TYPE] = config.backgroundType.name
            prefs[PreferencesKeys.WALLPAPER_BACKGROUND_COLOR] = config.backgroundColorHex
            if (config.backgroundImageUri != null) {
                prefs[PreferencesKeys.WALLPAPER_BACKGROUND_IMAGE_URI] = config.backgroundImageUri
            } else {
                prefs.remove(PreferencesKeys.WALLPAPER_BACKGROUND_IMAGE_URI)
            }
            prefs[PreferencesKeys.WALLPAPER_SCRIM_OPACITY] = config.scrimOpacity
            prefs[PreferencesKeys.WALLPAPER_BLUR_RADIUS] = config.blurRadius
            prefs[PreferencesKeys.WALLPAPER_SHOW_DATE] = config.showDate
            prefs[PreferencesKeys.WALLPAPER_SHOW_SECONDS] = config.showSeconds
            prefs[PreferencesKeys.WALLPAPER_SHOW_MOTTO] = config.showMotto
            prefs[PreferencesKeys.WALLPAPER_CUSTOM_MOTTO] = config.customMotto
            prefs[PreferencesKeys.WALLPAPER_SHOW_FOCUS_STREAK] = config.showFocusStreak
            prefs[PreferencesKeys.WALLPAPER_TIME_FORMAT_24H] = config.timeFormat24Hour
        }
    }

    suspend fun updateWallpaperClockPosition(position: WallpaperClockPosition) {
        context.focusDataStore.edit { prefs ->
            prefs[PreferencesKeys.WALLPAPER_X_PERCENT] = position.xPercent
            prefs[PreferencesKeys.WALLPAPER_Y_PERCENT] = position.yPercent
            prefs[PreferencesKeys.WALLPAPER_CLOCK_SCALE] = position.scale
            prefs[PreferencesKeys.WALLPAPER_ALIGNMENT] = position.alignment.name
        }
    }

    suspend fun updateWallpaperClockStyle(style: ClockStyle) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_CLOCK_STYLE] = style.name }
    }

    suspend fun updateWallpaperClockFont(font: ClockFont) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_CLOCK_FONT] = font.name }
    }

    suspend fun updateWallpaperClockColor(colorHex: Long) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_CLOCK_COLOR] = colorHex }
    }

    suspend fun updateWallpaperAnalogOrientation(orientation: AnalogNumeralOrientation) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_ANALOG_ORIENTATION] = orientation.name }
    }

    suspend fun updateWallpaperAnalogNumeralSize(size: com.sprinthon.focusclock.domain.model.AnalogNumeralSize) {
        context.focusDataStore.edit {
            it[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SIZE] = size.name
            it[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SCALE] = size.scale
        }
    }

    suspend fun updateWallpaperAnalogNumeralScale(scale: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_ANALOG_NUMERAL_SCALE] = scale }
    }

    suspend fun updateWallpaperBackgroundType(type: WallpaperBackgroundType) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_BACKGROUND_TYPE] = type.name }
    }

    suspend fun updateWallpaperBackgroundColor(colorHex: Long) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_BACKGROUND_COLOR] = colorHex }
    }

    suspend fun updateWallpaperBackgroundImageUri(uri: String?) {
        context.focusDataStore.edit { prefs ->
            if (uri != null) {
                prefs[PreferencesKeys.WALLPAPER_BACKGROUND_IMAGE_URI] = uri
            } else {
                prefs.remove(PreferencesKeys.WALLPAPER_BACKGROUND_IMAGE_URI)
            }
        }
    }

    suspend fun updateWallpaperScrimOpacity(opacity: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_SCRIM_OPACITY] = opacity }
    }

    suspend fun updateWallpaperBlurRadius(radius: Int) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_BLUR_RADIUS] = radius }
    }

    suspend fun updateWallpaperShowDate(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_SHOW_DATE] = show }
    }

    suspend fun updateWallpaperShowSeconds(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_SHOW_SECONDS] = show }
    }

    suspend fun updateWallpaperMotto(show: Boolean, motto: String) {
        context.focusDataStore.edit { prefs ->
            prefs[PreferencesKeys.WALLPAPER_SHOW_MOTTO] = show
            prefs[PreferencesKeys.WALLPAPER_CUSTOM_MOTTO] = motto
        }
    }

    suspend fun updateWallpaperShowStreak(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_SHOW_FOCUS_STREAK] = show }
    }

    suspend fun updateWallpaperTimeFormat(is24Hour: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.WALLPAPER_TIME_FORMAT_24H] = is24Hour }
    }

    suspend fun saveCustomTrack(track: com.sprinthon.focusclock.domain.model.FocusTrack) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.CUSTOM_TRACKS_JSON] ?: "[]"
            val currentList = deserializeCustomTracks(rawJson).toMutableList()
            val existingIndex = currentList.indexOfFirst { it.id == track.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = track
            } else {
                currentList.add(track)
            }
            prefs[PreferencesKeys.CUSTOM_TRACKS_JSON] = serializeCustomTracks(currentList)
        }
    }

    suspend fun deleteCustomTrack(trackId: String) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.CUSTOM_TRACKS_JSON] ?: "[]"
            val currentList = deserializeCustomTracks(rawJson).filter { it.id != trackId }
            prefs[PreferencesKeys.CUSTOM_TRACKS_JSON] = serializeCustomTracks(currentList)
        }
    }

    suspend fun updateClockStyle(style: com.sprinthon.focusclock.domain.model.ClockStyle) {
        context.focusDataStore.edit { it[PreferencesKeys.CLOCK_STYLE] = style.name }
    }

    suspend fun updateClockFont(font: com.sprinthon.focusclock.ui.clock.ClockFont) {
        context.focusDataStore.edit { it[PreferencesKeys.CLOCK_FONT] = font.name }
    }

    suspend fun updateClockScale(scale: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.CLOCK_SCALE] = scale }
    }

    suspend fun updateAnalogNumeralSize(size: com.sprinthon.focusclock.domain.model.AnalogNumeralSize) {
        context.focusDataStore.edit {
            it[PreferencesKeys.ANALOG_NUMERAL_SIZE] = size.name
            it[PreferencesKeys.ANALOG_NUMERAL_SCALE] = size.scale
        }
    }

    suspend fun updateAnalogNumeralScale(scale: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.ANALOG_NUMERAL_SCALE] = scale }
    }

    suspend fun updateTimeFormat(is24Hour: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.TIME_FORMAT_24H] = is24Hour }
    }

    suspend fun updateShowDate(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SHOW_DATE] = show }
    }

    suspend fun updateShowDayOfWeek(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SHOW_DAY_OF_WEEK] = show }
    }

    suspend fun updateDateFormatOption(option: com.sprinthon.focusclock.domain.model.DateFormatOption) {
        context.focusDataStore.edit { it[PreferencesKeys.DATE_FORMAT_OPTION] = option.name }
    }

    suspend fun updateShowTimer(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SHOW_TIMER] = show }
    }

    suspend fun updateDefaultDuration(minutes: Int) {
        context.focusDataStore.edit { it[PreferencesKeys.DEFAULT_DURATION_MINUTES] = minutes }
    }

    suspend fun updateTimerDisplayMode(mode: TimerDisplayMode) {
        context.focusDataStore.edit { it[PreferencesKeys.TIMER_DISPLAY_MODE] = mode.name }
    }

    suspend fun updateBackgroundType(type: BackgroundType) {
        context.focusDataStore.edit { it[PreferencesKeys.BACKGROUND_TYPE] = type.name }
    }

    suspend fun updateSolidBackgroundColor(colorHex: Long) {
        context.focusDataStore.edit { it[PreferencesKeys.SOLID_BACKGROUND_COLOR] = colorHex }
    }

    suspend fun updateBackgroundImageUri(uri: String?) {
        context.focusDataStore.edit {
            if (uri != null) {
                it[PreferencesKeys.BACKGROUND_IMAGE_URI] = uri
            } else {
                it.remove(PreferencesKeys.BACKGROUND_IMAGE_URI)
            }
        }
    }

    suspend fun updateSlideshowImageUris(uris: List<String>) {
        context.focusDataStore.edit {
            it[PreferencesKeys.SLIDESHOW_IMAGE_URIS] = uris.joinToString("|||")
        }
    }

    suspend fun updateSlideshowInterval(interval: SlideshowInterval) {
        context.focusDataStore.edit { it[PreferencesKeys.SLIDESHOW_INTERVAL] = interval.name }
    }

    suspend fun updateSlideshowShuffle(shuffle: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SLIDESHOW_SHUFFLE] = shuffle }
    }

    suspend fun updateSlideshowTransition(transition: SlideshowTransition) {
        context.focusDataStore.edit { it[PreferencesKeys.SLIDESHOW_TRANSITION] = transition.name }
    }

    suspend fun updateOverlayStrength(strength: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.BACKGROUND_OVERLAY_STRENGTH] = strength }
    }

    suspend fun updateKeepScreenAwake(awake: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.KEEP_SCREEN_AWAKE] = awake }
    }

    suspend fun updateAutoHideControls(autoHide: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.AUTO_HIDE_CONTROLS] = autoHide }
    }

    suspend fun updateImmersiveFullscreen(enabled: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.IMMERSIVE_FULLSCREEN_ENABLED] = enabled }
    }

    suspend fun updateBatterySaverEnabled(enabled: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.BATTERY_SAVER_ENABLED] = enabled }
    }

    suspend fun updateAutoPlayMusic(autoPlay: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.AUTO_PLAY_MUSIC] = autoPlay }
    }

    suspend fun updateMusicLoop(loop: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.MUSIC_LOOP] = loop }
    }

    suspend fun updateMusicVolume(volume: Float) {
        context.focusDataStore.edit { it[PreferencesKeys.MUSIC_VOLUME] = volume }
    }

    suspend fun updateShowWaveform(show: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SHOW_WAVEFORM] = show }
    }

    suspend fun updateSelectedTrackId(trackId: String) {
        context.focusDataStore.edit { it[PreferencesKeys.SELECTED_TRACK_ID] = trackId }
    }

    suspend fun updateConfirmBeforeExit(confirm: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.CONFIRM_BEFORE_EXIT] = confirm }
    }

    suspend fun updateVibrateOnCompletion(vibrate: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.VIBRATE_ON_COMPLETION] = vibrate }
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateActiveProfileId(profileId: String) {
        context.focusDataStore.edit { it[PreferencesKeys.ACTIVE_PROFILE_ID] = profileId }
    }

    suspend fun updateNotifyOnCompletion(notify: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.NOTIFY_ON_COMPLETION] = notify }
    }

    suspend fun updateSoundOnCompletion(sound: Boolean) {
        context.focusDataStore.edit { it[PreferencesKeys.SOUND_ON_COMPLETION] = sound }
    }

    suspend fun applyProfile(profile: com.sprinthon.focusclock.domain.model.FocusProfile) {
        context.focusDataStore.edit { prefs ->
            prefs[PreferencesKeys.ACTIVE_PROFILE_ID] = profile.id
            prefs[PreferencesKeys.DEFAULT_DURATION_MINUTES] = profile.durationMinutes
            prefs[PreferencesKeys.TIMER_DISPLAY_MODE] = profile.timerDisplayMode.name
            prefs[PreferencesKeys.CLOCK_STYLE] = profile.clockStyle.name
            prefs[PreferencesKeys.BACKGROUND_TYPE] = profile.backgroundType.name
            prefs[PreferencesKeys.SOLID_BACKGROUND_COLOR] = profile.solidBackgroundColor
            if (profile.backgroundImageUri != null) {
                prefs[PreferencesKeys.BACKGROUND_IMAGE_URI] = profile.backgroundImageUri
            } else {
                prefs.remove(PreferencesKeys.BACKGROUND_IMAGE_URI)
            }
            prefs[PreferencesKeys.SELECTED_TRACK_ID] = profile.selectedTrackId
            prefs[PreferencesKeys.AUTO_PLAY_MUSIC] = profile.autoPlayMusic
            prefs[PreferencesKeys.KEEP_SCREEN_AWAKE] = profile.keepScreenAwake
        }
    }

    suspend fun saveCustomProfile(profile: com.sprinthon.focusclock.domain.model.FocusProfile) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.CUSTOM_PROFILES_JSON] ?: "[]"
            val currentList = deserializeCustomProfiles(rawJson).toMutableList()
            val existingIndex = currentList.indexOfFirst { it.id == profile.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = profile
            } else {
                currentList.add(profile)
            }
            prefs[PreferencesKeys.CUSTOM_PROFILES_JSON] = serializeCustomProfiles(currentList)
            prefs[PreferencesKeys.ACTIVE_PROFILE_ID] = profile.id
        }
    }

    suspend fun deleteCustomProfile(profileId: String) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.CUSTOM_PROFILES_JSON] ?: "[]"
            val currentList = deserializeCustomProfiles(rawJson).filter { it.id != profileId }
            prefs[PreferencesKeys.CUSTOM_PROFILES_JSON] = serializeCustomProfiles(currentList)
            if (prefs[PreferencesKeys.ACTIVE_PROFILE_ID] == profileId) {
                prefs[PreferencesKeys.ACTIVE_PROFILE_ID] = "custom"
            }
        }
    }



    suspend fun updateAudioSourceType(type: com.sprinthon.focusclock.domain.model.AudioSourceType) {
        context.focusDataStore.edit { it[PreferencesKeys.AUDIO_SOURCE_TYPE] = type.name }
    }

    suspend fun resetAllSettingsToDefault() {
        context.focusDataStore.edit { it.clear() }
    }

    private fun serializeCustomProfiles(profiles: List<com.sprinthon.focusclock.domain.model.FocusProfile>): String {
        val array = org.json.JSONArray()
        for (p in profiles) {
            val obj = org.json.JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("description", p.description)
            obj.put("durationMinutes", p.durationMinutes)
            obj.put("timerDisplayMode", p.timerDisplayMode.name)
            obj.put("clockStyle", p.clockStyle.name)
            obj.put("backgroundType", p.backgroundType.name)
            obj.put("solidBackgroundColor", p.solidBackgroundColor)
            if (p.backgroundImageUri != null) obj.put("backgroundImageUri", p.backgroundImageUri)
            obj.put("selectedTrackId", p.selectedTrackId)
            obj.put("autoPlayMusic", p.autoPlayMusic)
            obj.put("keepScreenAwake", p.keepScreenAwake)
            obj.put("isBuiltIn", false)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeCustomProfiles(rawJson: String): List<com.sprinthon.focusclock.domain.model.FocusProfile> {
        val list = mutableListOf<com.sprinthon.focusclock.domain.model.FocusProfile>()
        try {
            val array = org.json.JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val profile = com.sprinthon.focusclock.domain.model.FocusProfile(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    name = obj.optString("name", "Custom Profile"),
                    description = obj.optString("description", "Custom preset"),
                    durationMinutes = obj.optInt("durationMinutes", 25),
                    timerDisplayMode = try {
                        TimerDisplayMode.valueOf(obj.optString("timerDisplayMode", "COUNTDOWN"))
                    } catch (e: Exception) {
                        TimerDisplayMode.COUNTDOWN
                    },
                    clockStyle = try {
                        com.sprinthon.focusclock.domain.model.ClockStyle.valueOf(obj.optString("clockStyle", "CLEAN_DIGITAL"))
                    } catch (e: Exception) {
                        com.sprinthon.focusclock.domain.model.ClockStyle.CLEAN_DIGITAL
                    },
                    backgroundType = try {
                        BackgroundType.valueOf(obj.optString("backgroundType", "SOLID_COLOR"))
                    } catch (e: Exception) {
                        BackgroundType.SOLID_COLOR
                    },
                    solidBackgroundColor = obj.optLong("solidBackgroundColor", 0xFF000000),
                    backgroundImageUri = if (obj.has("backgroundImageUri")) obj.getString("backgroundImageUri") else null,
                    selectedTrackId = obj.optString("selectedTrackId", "deep_focus"),
                    autoPlayMusic = obj.optBoolean("autoPlayMusic", false),
                    keepScreenAwake = obj.optBoolean("keepScreenAwake", true),
                    isBuiltIn = false
                )
                list.add(profile)
            }
        } catch (e: Exception) {
            // Fail gracefully
        }
        return list
    }



    suspend fun saveTrackCollection(collection: com.sprinthon.focusclock.domain.model.TrackCollection) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.TRACK_COLLECTIONS_JSON] ?: "[]"
            val currentList = deserializeTrackCollections(rawJson).toMutableList()
            val existingIndex = currentList.indexOfFirst { it.id == collection.id }
            if (existingIndex >= 0) {
                currentList[existingIndex] = collection
            } else {
                currentList.add(collection)
            }
            prefs[PreferencesKeys.TRACK_COLLECTIONS_JSON] = serializeTrackCollections(currentList)
        }
    }

    suspend fun deleteTrackCollection(collectionId: String) {
        context.focusDataStore.edit { prefs ->
            val rawJson = prefs[PreferencesKeys.TRACK_COLLECTIONS_JSON] ?: "[]"
            val currentList = deserializeTrackCollections(rawJson).filter { it.id != collectionId }
            prefs[PreferencesKeys.TRACK_COLLECTIONS_JSON] = serializeTrackCollections(currentList)
            if (prefs[PreferencesKeys.ACTIVE_COLLECTION_ID] == collectionId) {
                prefs.remove(PreferencesKeys.ACTIVE_COLLECTION_ID)
            }
        }
    }

    suspend fun toggleFavoriteTrack(trackId: String) {
        context.focusDataStore.edit { prefs ->
            val rawFav = prefs[PreferencesKeys.FAVORITE_TRACK_IDS] ?: ""
            val favSet = if (rawFav.isBlank()) mutableSetOf() else rawFav.split("|||").toMutableSet()
            if (favSet.contains(trackId)) {
                favSet.remove(trackId)
            } else {
                favSet.add(trackId)
            }
            prefs[PreferencesKeys.FAVORITE_TRACK_IDS] = favSet.joinToString("|||")
        }
    }

    suspend fun updateActiveCollectionId(collectionId: String?) {
        context.focusDataStore.edit { prefs ->
            if (collectionId != null) {
                prefs[PreferencesKeys.ACTIVE_COLLECTION_ID] = collectionId
            } else {
                prefs.remove(PreferencesKeys.ACTIVE_COLLECTION_ID)
            }
        }
    }

    suspend fun updateCollectionPlaybackMode(mode: com.sprinthon.focusclock.domain.model.CollectionPlaybackMode) {
        context.focusDataStore.edit { prefs ->
            prefs[PreferencesKeys.COLLECTION_PLAYBACK_MODE] = mode.name
        }
    }

    private fun serializeTrackCollections(collections: List<com.sprinthon.focusclock.domain.model.TrackCollection>): String {
        val array = org.json.JSONArray()
        for (c in collections) {
            val obj = org.json.JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("description", c.description)
            val trackArray = org.json.JSONArray()
            c.trackIds.forEach { trackArray.put(it) }
            obj.put("trackIds", trackArray)
            obj.put("playbackMode", c.playbackMode.name)
            obj.put("accentColorHex", c.accentColorHex)
            obj.put("iconName", c.iconName)
            obj.put("createdAt", c.createdAt)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeTrackCollections(rawJson: String): List<com.sprinthon.focusclock.domain.model.TrackCollection> {
        val list = mutableListOf<com.sprinthon.focusclock.domain.model.TrackCollection>()
        try {
            val array = org.json.JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val trackIdsList = mutableListOf<String>()
                val trackArray = obj.optJSONArray("trackIds")
                if (trackArray != null) {
                    for (j in 0 until trackArray.length()) {
                        trackIdsList.add(trackArray.getString(j))
                    }
                }
                val collection = com.sprinthon.focusclock.domain.model.TrackCollection(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    name = obj.optString("name", "My Collection"),
                    description = obj.optString("description", ""),
                    trackIds = trackIdsList,
                    playbackMode = try {
                        com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.valueOf(obj.optString("playbackMode", "LOOP_COLLECTION"))
                    } catch (e: Exception) {
                        com.sprinthon.focusclock.domain.model.CollectionPlaybackMode.LOOP_COLLECTION
                    },
                    accentColorHex = obj.optLong("accentColorHex", 0xFFF59E0B),
                    iconName = obj.optString("iconName", "playlist"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
                list.add(collection)
            }
        } catch (e: Exception) {
            // Fail gracefully
        }
        return list
    }

    private fun serializeCustomTracks(tracks: List<com.sprinthon.focusclock.domain.model.FocusTrack>): String {
        val array = org.json.JSONArray()
        for (t in tracks) {
            val obj = org.json.JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("artist", t.artist)
            obj.put("uri", t.uri)
            obj.put("isBuiltIn", t.isBuiltIn)
            if (t.isYouTube) obj.put("isYouTube", t.isYouTube)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeCustomTracks(rawJson: String): List<com.sprinthon.focusclock.domain.model.FocusTrack> {
        val list = mutableListOf<com.sprinthon.focusclock.domain.model.FocusTrack>()
        try {
            val array = org.json.JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val track = com.sprinthon.focusclock.domain.model.FocusTrack(
                    id = obj.optString("id"),
                    title = obj.optString("title", "Custom Track"),
                    artist = obj.optString("artist", "Local"),
                    uri = obj.optString("uri"),
                    isBuiltIn = obj.optBoolean("isBuiltIn", false),
                    isYouTube = obj.optBoolean("isYouTube", false)
                )
                if (track.id.isNotEmpty() && track.uri.isNotEmpty()) {
                    list.add(track)
                }
            }
        } catch (e: Exception) {
            // Fail gracefully
        }
        return list
    }
}
