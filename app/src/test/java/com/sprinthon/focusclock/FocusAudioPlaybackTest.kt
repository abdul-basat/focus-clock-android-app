package com.sprinthon.focusclock

import android.app.Application
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.playback.PlaybackStatus
import com.sprinthon.focusclock.playback.PlayerUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
@Config(sdk = [36])
class FocusAudioPlaybackTest {

    private lateinit var application: Application

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testAudioCatalogContainsBuiltInTracks() {
        val tracks = FocusAudioCatalog.BUILT_IN_TRACKS
        assertTrue(tracks.isNotEmpty())
        assertEquals(5, tracks.size)

        val deepFocus = FocusAudioCatalog.getTrackById("deep_focus")
        assertEquals("Deep Focus", deepFocus.title)

        val rain = FocusAudioCatalog.getTrackById("gentle_rain")
        assertEquals("Gentle Rain", rain.title)

        val whiteNoise = FocusAudioCatalog.getTrackById("white_noise")
        assertEquals("Smooth Noise", whiteNoise.title)
    }

    @Test
    fun testMediaItemCreation() = runTest {
        val track = FocusAudioCatalog.BUILT_IN_TRACKS.first()
        val mediaItem = FocusAudioCatalog.createMediaItem(application, track)

        assertNotNull(mediaItem)
        assertEquals("deep_focus", mediaItem.mediaId)
        assertEquals("Deep Focus", mediaItem.mediaMetadata.title.toString())
        assertNotNull(mediaItem.requestMetadata.mediaUri)
    }

    @Test
    fun testPlayerUiStateDefaults() {
        val defaultState = PlayerUiState()
        assertFalse(defaultState.isPlaying)
        assertTrue(defaultState.isStopped)
        assertEquals("Deep Focus", defaultState.trackTitle)
        assertEquals(Player.REPEAT_MODE_ALL, defaultState.repeatMode)
        assertTrue(defaultState.isLooping)
        assertFalse(defaultState.isRepeatOne)
        assertEquals(PlaybackStatus.IDLE, defaultState.status)
    }

    @Test
    fun testPlayerUiStateRepeatModes() {
        val repeatOneState = PlayerUiState(repeatMode = Player.REPEAT_MODE_ONE)
        assertTrue(repeatOneState.isLooping)
        assertTrue(repeatOneState.isRepeatOne)

        val repeatOffState = PlayerUiState(repeatMode = Player.REPEAT_MODE_OFF)
        assertFalse(repeatOffState.isLooping)
        assertFalse(repeatOffState.isRepeatOne)
    }
}
