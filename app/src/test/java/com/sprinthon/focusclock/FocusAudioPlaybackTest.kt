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
@Config(sdk = [34])
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
        assertNotNull(mediaItem.localConfiguration?.uri)
    }

    @Test
    fun testYouTubeIdExtraction() {
        val watchUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        assertEquals("dQw4w9WgXcQ", com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(watchUrl))

        val shortUrl = "https://youtu.be/dQw4w9WgXcQ"
        assertEquals("dQw4w9WgXcQ", com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(shortUrl))

        val embedUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        assertEquals("dQw4w9WgXcQ", com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(embedUrl))

        val shortsUrl = "https://www.youtube.com/shorts/dQw4w9WgXcQ"
        assertEquals("dQw4w9WgXcQ", com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractVideoId(shortsUrl))

        val playlistUrl = "https://www.youtube.com/playlist?list=PLrAXtmErZgOdP_8GztsuKi9upL79By424"
        assertEquals("PLrAXtmErZgOdP_8GztsuKi9upL79By424", com.sprinthon.focusclock.playback.YouTubeStreamHelper.extractPlaylistId(playlistUrl))
    }

    @Test
    fun testYouTubeMediaItemNoSilentFallback() = runTest {
        val ytTrack = com.sprinthon.focusclock.domain.model.FocusTrack(
            id = "yt_test",
            title = "Test YouTube Track",
            artist = "Test Artist",
            uri = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            isBuiltIn = false,
            isYouTube = true
        )
        // In offline unit test environment, stream resolution throws YouTubeStreamResolutionException
        // and NEVER falls back silently to ambient audio or deep_focus.
        var caughtException = false
        try {
            FocusAudioCatalog.createMediaItem(application, ytTrack)
        } catch (e: com.sprinthon.focusclock.playback.YouTubeStreamResolutionException) {
            caughtException = true
            assertTrue(e.message?.contains("Unable to resolve audio stream") == true)
        }
        assertTrue("Expected YouTubeStreamResolutionException on unresolved stream", caughtException)

        // createMediaItemOrNull safely returns null without returning a fallback track
        val nullItem = FocusAudioCatalog.createMediaItemOrNull(application, ytTrack)
        org.junit.Assert.assertNull(nullItem)
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
