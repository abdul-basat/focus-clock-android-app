package com.sprinthon.focusclock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.AnalogNumeralOrientation
import com.sprinthon.focusclock.domain.model.ClockAlignment
import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.WallpaperBackgroundType
import com.sprinthon.focusclock.domain.model.WallpaperClockPosition
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.ui.clock.ClockFont
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WallpaperClockPersistenceTest {

    private lateinit var context: Context
    private lateinit var repository: FocusPreferencesRepository

    @Before
    fun setup() = runTest {
        context = ApplicationProvider.getApplicationContext()
        repository = FocusPreferencesRepository(context)
        repository.updateWallpaperConfig(WallpaperConfig())
    }

    @Test
    fun testDefaultWallpaperConfigValues() = runTest {
        val config = repository.wallpaperConfigFlow.first()
        assertNotNull(config)
        assertEquals(ClockStyle.ANALOG, config.clockStyle)
        assertEquals(ClockFont.BEBAS_NEUE, config.clockFont)
        assertEquals(AnalogNumeralOrientation.HORIZONTAL_UPRIGHT, config.analogNumeralOrientation)
        assertEquals(WallpaperBackgroundType.SOLID_COLOR, config.backgroundType)
        assertEquals(0xFF000000L, config.backgroundColorHex)
        assertEquals(0.25f, config.scrimOpacity, 0.01f)
        assertEquals(true, config.showDate)
        assertEquals(true, config.showSeconds)
    }

    @Test
    fun testWallpaperClockPositionPresets() {
        val topCenter = ClockAlignment.TOP_CENTER
        assertEquals(0.0f, topCenter.xPercent, 0.001f)
        assertEquals(-0.65f, topCenter.yPercent, 0.001f)

        val bottomCenter = ClockAlignment.BOTTOM_CENTER
        assertEquals(0.0f, bottomCenter.xPercent, 0.001f)
        assertEquals(0.55f, bottomCenter.yPercent, 0.001f)

        val customPos = WallpaperClockPosition(
            xPercent = 0.25f,
            yPercent = -0.15f,
            scale = 1.3f,
            alignment = ClockAlignment.CUSTOM
        )
        assertEquals(0.25f, customPos.xPercent, 0.001f)
        assertEquals(-0.15f, customPos.yPercent, 0.001f)
        assertEquals(1.3f, customPos.scale, 0.001f)
    }

    @Test
    fun testAnalogNumeralOrientationEnumValues() {
        val values = AnalogNumeralOrientation.values()
        assertEquals(4, values.size)
        assertTrue(values.contains(AnalogNumeralOrientation.HORIZONTAL_UPRIGHT))
        assertTrue(values.contains(AnalogNumeralOrientation.RADIAL_ROTATED))
        assertTrue(values.contains(AnalogNumeralOrientation.MINIMAL_TICKS))
        assertTrue(values.contains(AnalogNumeralOrientation.MINIMAL_PIPS))
    }

    @Test
    fun testUpdateWallpaperPositionAndConfig() = runTest {
        val customPos = WallpaperClockPosition(
            xPercent = -0.3f,
            yPercent = 0.2f,
            scale = 1.25f,
            alignment = ClockAlignment.CUSTOM
        )

        repository.updateWallpaperClockPosition(customPos)
        repository.updateWallpaperAnalogOrientation(AnalogNumeralOrientation.RADIAL_ROTATED)
        repository.updateWallpaperClockStyle(ClockStyle.CLEAN_DIGITAL)
        repository.updateWallpaperClockFont(ClockFont.STAATLICHES)
        repository.updateWallpaperClockColor(0xFFE2E8F0L)
        repository.updateWallpaperMotto(show = true, motto = "Flow State")
        repository.updateWallpaperScrimOpacity(0.45f)
        repository.updateWallpaperBlurRadius(12)

        val updated = repository.wallpaperConfigFlow.first()
        assertEquals(-0.3f, updated.position.xPercent, 0.001f)
        assertEquals(0.2f, updated.position.yPercent, 0.001f)
        assertEquals(1.25f, updated.position.scale, 0.001f)
        assertEquals(AnalogNumeralOrientation.RADIAL_ROTATED, updated.analogNumeralOrientation)
        assertEquals(ClockStyle.CLEAN_DIGITAL, updated.clockStyle)
        assertEquals(ClockFont.STAATLICHES, updated.clockFont)
        assertEquals(0xFFE2E8F0L, updated.clockColorHex)
        assertEquals(true, updated.showMotto)
        assertEquals("Flow State", updated.customMotto)
        assertEquals(0.45f, updated.scrimOpacity, 0.01f)
        assertEquals(12, updated.blurRadius)
    }

    @Test
    fun testClockScaleAndAnalogNumeralSizePersistence() = runTest {
        // Test default values
        val initial = repository.preferencesFlow.first()
        assertEquals(1.15f, initial.clockScale, 0.01f)
        assertEquals(com.sprinthon.focusclock.domain.model.AnalogNumeralSize.LARGE, initial.analogNumeralSize)
        assertEquals(1.35f, initial.analogNumeralScale, 0.01f)

        // Test updating to Jumbo
        repository.updateClockScale(1.50f)
        repository.updateAnalogNumeralSize(com.sprinthon.focusclock.domain.model.AnalogNumeralSize.JUMBO)

        val updatedPrefs = repository.preferencesFlow.first()
        assertEquals(1.50f, updatedPrefs.clockScale, 0.01f)
        assertEquals(com.sprinthon.focusclock.domain.model.AnalogNumeralSize.JUMBO, updatedPrefs.analogNumeralSize)
        assertEquals(1.70f, updatedPrefs.analogNumeralScale, 0.01f)

        // Test Wallpaper numeral size updates
        repository.updateWallpaperAnalogNumeralSize(com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL)
        val updatedWallpaper = repository.wallpaperConfigFlow.first()
        assertEquals(com.sprinthon.focusclock.domain.model.AnalogNumeralSize.CARDINAL, updatedWallpaper.analogNumeralSize)
        assertEquals(1.50f, updatedWallpaper.analogNumeralScale, 0.01f)
    }

    @Test
    fun testWallpaperBitmapRendererWithEnlargedNumerals() = runTest {
        val timeData = com.sprinthon.focusclock.ui.clock.ClockTimeData(
            hourString = "10",
            minuteString = "10",
            secondString = "30",
            dayOfWeek = "Wednesday",
            dateString = "Sep 2",
            fullDateString = "Wednesday, Sep 2",
            hourInt = 10,
            minuteInt = 10,
            secondInt = 30,
            is24Hour = true,
            amPm = "AM"
        )

        for (size in com.sprinthon.focusclock.domain.model.AnalogNumeralSize.values()) {
            val config = WallpaperConfig(
                clockStyle = ClockStyle.ANALOG,
                analogNumeralOrientation = AnalogNumeralOrientation.HORIZONTAL_UPRIGHT,
                analogNumeralSize = size,
                analogNumeralScale = size.scale
            )

            val bitmap = com.sprinthon.focusclock.playback.WallpaperBitmapRenderer.renderWallpaperBitmap(
                context = context,
                config = config,
                timeData = timeData,
                width = 540,
                height = 960
            )

            assertNotNull(bitmap)
            assertEquals(540, bitmap.width)
            assertEquals(960, bitmap.height)
        }
    }
}
