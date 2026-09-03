package com.sprinthon.focusclock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.sprinthon.focusclock.domain.model.WallpaperConfig
import com.sprinthon.focusclock.ui.clock.ClockTimeData
import com.sprinthon.focusclock.ui.screens.ClockWallpaperCustomizationScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WallpaperStudioUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWallpaperCustomizerRendersAndSwitchesTabs() {
        val testConfig = WallpaperConfig()
        val timeData = ClockTimeData(
            hourString = "10",
            minuteString = "10",
            secondString = "30",
            dayOfWeek = "Tuesday",
            dateString = "Sep 1",
            fullDateString = "Tuesday, Sep 1",
            hourInt = 10,
            minuteInt = 10,
            secondInt = 30,
            is24Hour = false,
            amPm = "AM"
        )

        composeTestRule.setContent {
            ClockWallpaperCustomizationScreen(
                wallpaperConfig = testConfig,
                currentTimeData = timeData,
                onUpdatePosition = {},
                onUpdateStyle = {},
                onUpdateFont = {},
                onUpdateColor = {},
                onUpdateAnalogOrientation = {},
                onUpdateBackgroundType = {},
                onUpdateBackgroundColor = {},
                onUpdateBackgroundImageUri = {},
                onUpdateScrimOpacity = {},
                onUpdateBlurRadius = {},
                onUpdateShowDate = {},
                onUpdateShowSeconds = {},
                onUpdateMotto = { _, _ -> },
                onUpdateShowStreak = {},
                onUpdate24Hour = {},
                onBack = {}
            )
        }

        // Verify Canvas and Controls are displayed
        composeTestRule.onNodeWithTag("wallpaper_drag_canvas").assertIsDisplayed()
        composeTestRule.onNodeWithTag("apply_wallpaper_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("simulation_mode_selector").assertIsDisplayed()

        // Test Simulation Mode Buttons
        composeTestRule.onNodeWithTag("sim_mode_home").performClick()
        composeTestRule.onNodeWithTag("sim_mode_lock").performClick()
        composeTestRule.onNodeWithTag("sim_mode_clean").performClick()

        // Test Deck Tabs Switching
        composeTestRule.onNodeWithTag("wallpaper_tab_style").performClick()
        composeTestRule.onNodeWithTag("wallpaper_tab_background").performClick()
        composeTestRule.onNodeWithTag("wallpaper_tab_content").performClick()
        composeTestRule.onNodeWithTag("wallpaper_tab_position").performClick()

        // Test Visual 3x3 Alignment Matrix click
        composeTestRule.onNodeWithTag("alignment_center").performClick()
        composeTestRule.onNodeWithTag("reset_position_button").performClick()

        // Test Minimizing and Immersion Mode
        composeTestRule.onNodeWithTag("wallpaper_deck_minimize_button").performClick()
        composeTestRule.onNodeWithTag("wallpaper_deck_expand_pill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("wallpaper_deck_expand_pill").performClick()
        composeTestRule.onNodeWithTag("wallpaper_tab_position").assertIsDisplayed()
    }
}
