package com.sprinthon.focusclock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.DateFormatOption
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsHubTest {

    private lateinit var context: Context
    private lateinit var repository: FocusPreferencesRepository

    @Before
    fun setUp() = runTest {
        context = ApplicationProvider.getApplicationContext()
        repository = FocusPreferencesRepository(context)
        repository.resetAllSettingsToDefault()
    }

    @Test
    fun testDefaultPreferencesValues() = runTest {
        val prefs = repository.preferencesFlow.first()
        assertEquals(ClockStyle.CLEAN_DIGITAL, prefs.clockStyle)
        assertTrue(prefs.timeFormat24Hour)
        assertTrue(prefs.showDate)
        assertTrue(prefs.showDayOfWeek)
        assertEquals(DateFormatOption.SHORT_DAY_MONTH, prefs.dateFormatOption)
        assertEquals(25, prefs.defaultDurationMinutes)
        assertEquals(TimerDisplayMode.COUNTDOWN, prefs.timerDisplayMode)
        assertEquals(BackgroundType.SOLID_COLOR, prefs.backgroundType)
        assertEquals(0xFF000000, prefs.solidBackgroundColor)
        assertTrue(prefs.keepScreenAwake)
        assertTrue(prefs.autoHideControls)
        assertFalse(prefs.autoPlayMusicOnFocus)
        assertTrue(prefs.musicLoop)
        assertEquals(0.7f, prefs.musicVolume, 0.01f)
        assertTrue(prefs.showWaveform)
        assertEquals("deep_focus", prefs.selectedTrackId)
        assertTrue(prefs.confirmBeforeExit)
        assertTrue(prefs.vibrateOnCompletion)
    }

    @Test
    fun testUpdateClockSettings() = runTest {
        repository.updateClockStyle(ClockStyle.FLIP_CLOCK)
        repository.updateTimeFormat(false)
        repository.updateShowDate(false)
        repository.updateDateFormatOption(DateFormatOption.FULL_DAY_MONTH)

        val updated = repository.preferencesFlow.first()
        assertEquals(ClockStyle.FLIP_CLOCK, updated.clockStyle)
        assertFalse(updated.timeFormat24Hour)
        assertFalse(updated.showDate)
        assertEquals(DateFormatOption.FULL_DAY_MONTH, updated.dateFormatOption)
    }

    @Test
    fun testUpdateFocusAndAudioSettings() = runTest {
        repository.updateDefaultDuration(45)
        repository.updateTimerDisplayMode(TimerDisplayMode.ELAPSED)
        repository.updateSelectedTrackId("gentle_rain")
        repository.updateAutoPlayMusic(true)
        repository.updateMusicVolume(0.85f)
        repository.updateAutoHideControls(false)

        val updated = repository.preferencesFlow.first()
        assertEquals(45, updated.defaultDurationMinutes)
        assertEquals(TimerDisplayMode.ELAPSED, updated.timerDisplayMode)
        assertEquals("gentle_rain", updated.selectedTrackId)
        assertTrue(updated.autoPlayMusicOnFocus)
        assertEquals(0.85f, updated.musicVolume, 0.01f)
        assertFalse(updated.autoHideControls)
    }

    @Test
    fun testResetAllSettingsToDefault() = runTest {
        repository.updateClockStyle(ClockStyle.ANALOG)
        repository.updateDefaultDuration(90)
        repository.updateSelectedTrackId("white_noise")

        var current = repository.preferencesFlow.first()
        assertEquals(ClockStyle.ANALOG, current.clockStyle)
        assertEquals(90, current.defaultDurationMinutes)
        assertEquals("white_noise", current.selectedTrackId)

        // Reset
        repository.resetAllSettingsToDefault()

        val resetPrefs = repository.preferencesFlow.first()
        assertEquals(ClockStyle.CLEAN_DIGITAL, resetPrefs.clockStyle)
        assertEquals(25, resetPrefs.defaultDurationMinutes)
        assertEquals("deep_focus", resetPrefs.selectedTrackId)
    }
}
