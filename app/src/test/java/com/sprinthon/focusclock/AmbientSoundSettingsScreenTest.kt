package com.sprinthon.focusclock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.domain.model.FocusTrack
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.playback.PlayerUiState
import com.sprinthon.focusclock.ui.screens.settings.AmbientSoundSettingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Phase 8 Milestone 8.4: Automated Robolectric Compose UI test suite for Ambient Soundscape Screen.
 * Verifies filter chip switching, mini-player rendering, empty states, and quick settings sheet interactions.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmbientSoundSettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultPrefs = FocusPreferences(
        selectedTrackId = "deep_focus",
        musicVolume = 0.7f,
        musicLoop = true,
        showWaveform = true,
        autoPlayMusicOnFocus = false
    )

    private val defaultPlayerState = PlayerUiState(
        isPlaying = false,
        trackTitle = "Deep Focus"
    )

    @Test
    fun testAmbientSoundSettingsScreenRendersHeaderAndMiniPlayer() {
        composeTestRule.setContent {
            AmbientSoundSettingsScreen(
                preferences = defaultPrefs,
                playerState = defaultPlayerState,
                customTracks = emptyList(),
                collections = emptyList(),
                favoriteTrackIds = emptySet(),
                onSelectTrack = { _, _ -> },
                onAddCustomTrack = { _, _, _ -> },
                onDeleteCustomTrack = {},
                onCreateCollection = {},
                onUpdateCollection = {},
                onDeleteCollection = {},
                onAddTrackToCollection = { _, _ -> },
                onRemoveTrackFromCollection = { _, _ -> },
                onPlayCollection = { _, _, _ -> },
                onClearActiveCollection = {},
                onSetCollectionPlaybackMode = {},
                onTogglePlayPause = {},
                onToggleAutoPlay = {},
                onToggleLoop = {},
                onVolumeChange = {},
                onToggleShowWaveform = {},
                onBack = {}
            )
        }

        // Verify top-level screen containers
        composeTestRule.onNodeWithTag("ambient_sound_settings_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("audio_settings_back_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_settings_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ambient_mini_player_bar").assertIsDisplayed()

        // Verify title
        composeTestRule.onNodeWithText("Ambient Soundscapes").assertIsDisplayed()
    }

    @Test
    fun testCategoryFilterChipsSwitchTabs() {
        composeTestRule.setContent {
            AmbientSoundSettingsScreen(
                preferences = defaultPrefs,
                playerState = defaultPlayerState,
                customTracks = emptyList(),
                collections = emptyList(),
                favoriteTrackIds = emptySet(),
                onSelectTrack = { _, _ -> },
                onAddCustomTrack = { _, _, _ -> },
                onDeleteCustomTrack = {},
                onCreateCollection = {},
                onUpdateCollection = {},
                onDeleteCollection = {},
                onAddTrackToCollection = { _, _ -> },
                onRemoveTrackFromCollection = { _, _ -> },
                onPlayCollection = { _, _, _ -> },
                onClearActiveCollection = {},
                onSetCollectionPlaybackMode = {},
                onTogglePlayPause = {},
                onToggleAutoPlay = {},
                onToggleLoop = {},
                onVolumeChange = {},
                onToggleShowWaveform = {},
                onBack = {}
            )
        }

        // Verify filter chips are displayed and clickable
        composeTestRule.onNodeWithTag("tab_all").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_favorites").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_soundscapes").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_collections").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_custom").assertIsDisplayed()

        // Test switching to Favorites tab
        composeTestRule.onNodeWithTag("tab_favorites").performClick()
        composeTestRule.onNodeWithText("No favorite tracks yet").assertIsDisplayed()

        // Test switching to Collections tab
        composeTestRule.onNodeWithTag("tab_collections").performClick()
        composeTestRule.onNodeWithText("Organize Your Soundscapes").assertIsDisplayed()

        // Test switching to Custom tab
        composeTestRule.onNodeWithTag("tab_custom").performClick()
        composeTestRule.onNodeWithText("Add Your Own Sounds").assertIsDisplayed()

        // Test switching back to All tab
        composeTestRule.onNodeWithTag("tab_all").performClick()
        composeTestRule.onNodeWithTag("add_custom_track_row").assertIsDisplayed()
    }

    @Test
    fun testQuickSettingsSheetOpensOnClick() {
        composeTestRule.setContent {
            AmbientSoundSettingsScreen(
                preferences = defaultPrefs,
                playerState = defaultPlayerState,
                customTracks = emptyList(),
                collections = emptyList(),
                favoriteTrackIds = emptySet(),
                onSelectTrack = { _, _ -> },
                onAddCustomTrack = { _, _, _ -> },
                onDeleteCustomTrack = {},
                onCreateCollection = {},
                onUpdateCollection = {},
                onDeleteCollection = {},
                onAddTrackToCollection = { _, _ -> },
                onRemoveTrackFromCollection = { _, _ -> },
                onPlayCollection = { _, _, _ -> },
                onClearActiveCollection = {},
                onSetCollectionPlaybackMode = {},
                onTogglePlayPause = {},
                onToggleAutoPlay = {},
                onToggleLoop = {},
                onVolumeChange = {},
                onToggleShowWaveform = {},
                onBack = {}
            )
        }

        // Tap the quick settings gear icon
        composeTestRule.onNodeWithTag("quick_settings_button").performClick()

        // Verify Quick Settings title is displayed
        composeTestRule.onNodeWithText("Quick Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Auto-Play on Focus Start").assertIsDisplayed()
        composeTestRule.onNodeWithText("Loop Ambient Audio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Audio Waveform Visualizer").assertIsDisplayed()
    }

    @Test
    fun testBuiltInTracksRenderInTrackList() {
        composeTestRule.setContent {
            AmbientSoundSettingsScreen(
                preferences = defaultPrefs,
                playerState = defaultPlayerState,
                customTracks = emptyList(),
                collections = emptyList(),
                favoriteTrackIds = setOf("deep_focus"),
                onSelectTrack = { _, _ -> },
                onAddCustomTrack = { _, _, _ -> },
                onDeleteCustomTrack = {},
                onCreateCollection = {},
                onUpdateCollection = {},
                onDeleteCollection = {},
                onAddTrackToCollection = { _, _ -> },
                onRemoveTrackFromCollection = { _, _ -> },
                onPlayCollection = { _, _, _ -> },
                onClearActiveCollection = {},
                onSetCollectionPlaybackMode = {},
                onTogglePlayPause = {},
                onToggleAutoPlay = {},
                onToggleLoop = {},
                onVolumeChange = {},
                onToggleShowWaveform = {},
                onBack = {}
            )
        }

        // Verify built-in track rows are displayed
        composeTestRule.onNodeWithTag("track_row_deep_focus").assertIsDisplayed()
        composeTestRule.onNodeWithTag("track_row_gentle_rain").assertIsDisplayed()
        composeTestRule.onNodeWithTag("track_row_white_noise").assertIsDisplayed()
    }
}
