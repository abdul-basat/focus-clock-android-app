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
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.domain.model.SlideshowTransition
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.focusDataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_clock_settings")

class FocusPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val CLOCK_FONT = stringPreferencesKey("clock_font")
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
        val CUSTOM_PROFILES_JSON = stringPreferencesKey("custom_profiles_json")
        val CUSTOM_TRACKS_JSON = stringPreferencesKey("custom_tracks_json")
    }

    val preferencesFlow: Flow<FocusPreferences> = context.focusDataStore.data.map { preferences ->
        val urisRaw = preferences[PreferencesKeys.SLIDESHOW_IMAGE_URIS] ?: ""
        val urisList = if (urisRaw.isBlank()) emptyList() else urisRaw.split("|||")

        FocusPreferences(
            clockStyle = preferences[PreferencesKeys.CLOCK_STYLE]?.let {
                try { com.sprinthon.focusclock.domain.model.ClockStyle.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.domain.model.ClockStyle.CLEAN_DIGITAL }
            } ?: com.sprinthon.focusclock.domain.model.ClockStyle.CLEAN_DIGITAL,
            clockFont = preferences[PreferencesKeys.CLOCK_FONT]?.let {
                try { com.sprinthon.focusclock.ui.clock.ClockFont.valueOf(it) } catch (e: Exception) { com.sprinthon.focusclock.ui.clock.ClockFont.BEBAS_NEUE }
            } ?: com.sprinthon.focusclock.ui.clock.ClockFont.BEBAS_NEUE,
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
            soundOnCompletion = preferences[PreferencesKeys.SOUND_ON_COMPLETION] ?: true
        )
    }

    val customProfilesFlow: Flow<List<com.sprinthon.focusclock.domain.model.FocusProfile>> = context.focusDataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.CUSTOM_PROFILES_JSON] ?: "[]"
        deserializeCustomProfiles(rawJson)
    }



    val customTracksFlow: Flow<List<com.sprinthon.focusclock.domain.model.FocusTrack>> = context.focusDataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.CUSTOM_TRACKS_JSON] ?: "[]"
        deserializeCustomTracks(rawJson)
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
