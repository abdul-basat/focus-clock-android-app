package com.sprinthon.focusclock

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.domain.model.AudioSourceType
import com.sprinthon.focusclock.domain.model.ExternalMediaSessionState
import com.sprinthon.focusclock.domain.model.FocusPreferences
import com.sprinthon.focusclock.playback.SystemMediaControllerManager
import com.sprinthon.focusclock.ui.viewmodel.FocusViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExternalMusicIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAudioSourceTypeDefaultsAndProperties() {
        val ambient = AudioSourceType.AMBIENT_SOUNDS
        assertEquals("Ambient Sounds", ambient.displayName)

        val external = AudioSourceType.EXTERNAL_MUSIC
        assertEquals("External Music", external.displayName)
        assertTrue(external.description.contains("Spotify"))
    }

    @Test
    fun testExternalMediaSessionStateDefaults() {
        val state = ExternalMediaSessionState()
        assertFalse(state.hasActiveSession)
        assertFalse(state.isPlaying)
        assertEquals("No track playing", state.displayTitle)
        assertEquals("Open Spotify, YouTube, or SoundCloud", state.displaySubtitle)
        assertTrue(state.canSkipToNext)
        assertTrue(state.canSkipToPrevious)
    }

    @Test
    fun testExternalMediaSessionStateWithMetadata() {
        val state = ExternalMediaSessionState(
            hasActiveSession = true,
            isPlaying = true,
            title = "Weightless",
            artist = "Marconi Union",
            album = "Ambient 1",
            appName = "Spotify",
            packageName = SystemMediaControllerManager.SPOTIFY_PACKAGE
        )
        assertTrue(state.hasActiveSession)
        assertTrue(state.isPlaying)
        assertEquals("Weightless", state.displayTitle)
        assertEquals("Marconi Union • Spotify", state.displaySubtitle)
    }

    @Test
    fun testViewModelAudioSourceSwitching() = runTest(testDispatcher) {
        val viewModel = FocusViewModel(application)

        // Default should be ambient sounds
        assertEquals(AudioSourceType.AMBIENT_SOUNDS, viewModel.uiState.value.preferences.audioSourceType)

        // Switch to external music
        viewModel.setAudioSourceType(AudioSourceType.EXTERNAL_MUSIC)
        testScheduler.advanceUntilIdle()

        assertEquals(AudioSourceType.EXTERNAL_MUSIC, viewModel.uiState.value.preferences.audioSourceType)

        // Switch back to ambient
        viewModel.setAudioSourceType(AudioSourceType.AMBIENT_SOUNDS)
        testScheduler.advanceUntilIdle()

        assertEquals(AudioSourceType.AMBIENT_SOUNDS, viewModel.uiState.value.preferences.audioSourceType)
    }

    @Test
    fun testSystemMediaControllerManagerInstantiation() {
        val manager = SystemMediaControllerManager(application)
        assertNotNull(manager)
        assertNotNull(manager.mediaState.value)
        assertEquals(SystemMediaControllerManager.SPOTIFY_PACKAGE, "com.spotify.music")
        assertEquals(SystemMediaControllerManager.YT_MUSIC_PACKAGE, "com.google.android.apps.youtube.music")
    }
}
