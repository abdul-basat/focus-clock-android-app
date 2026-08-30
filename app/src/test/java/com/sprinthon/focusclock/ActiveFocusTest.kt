package com.sprinthon.focusclock

import android.app.Application
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.domain.model.PresetDuration
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.playback.FocusAudioCatalog
import com.sprinthon.focusclock.ui.viewmodel.FocusViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ActiveFocusTest {

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
    fun testActiveFocusSessionStartAndControlsAutoHide() = runTest(testDispatcher) {
        val viewModel = FocusViewModel(application)
        
        viewModel.selectPresetDuration(PresetDuration.MIN_25)
        viewModel.startFocusSession()

        // Verify session started
        val session = viewModel.uiState.value.session
        assertEquals(SessionState.RUNNING, session.state)
        assertEquals(25, session.durationMinutes)

        // Controls are visible initially
        assertTrue(viewModel.uiState.value.controlsVisible)

        // Advance 5 seconds -> controls should auto-hide
        advanceTimeBy(5000L)
        assertFalse(viewModel.uiState.value.controlsVisible)

        // Tap screen -> controls reveal
        viewModel.onScreenTapped()
        assertTrue(viewModel.uiState.value.controlsVisible)

        // Tap again -> controls toggle off
        viewModel.onScreenTapped()
        assertFalse(viewModel.uiState.value.controlsVisible)

        viewModel.resetSession()
    }

    @Test
    fun testExitConfirmationDialogFlow() = runTest(testDispatcher) {
        val viewModel = FocusViewModel(application)
        viewModel.startFocusSession()

        // User requests exit
        viewModel.setExitConfirmationDialogVisible(true)
        assertTrue(viewModel.uiState.value.showExitConfirmationDialog)

        // User decides to keep focusing
        viewModel.setExitConfirmationDialogVisible(false)
        assertFalse(viewModel.uiState.value.showExitConfirmationDialog)
        assertEquals(SessionState.RUNNING, viewModel.uiState.value.session.state)

        // User confirms end session
        viewModel.cancelFocusSession()
        viewModel.resetSession()
        assertEquals(SessionState.IDLE, viewModel.uiState.value.session.state)
    }

    @Test
    fun testMediaPlaybackIndependenceFromFocusSession() = runTest(testDispatcher) {
        val viewModel = FocusViewModel(application)
        viewModel.startFocusSession()

        assertEquals(SessionState.RUNNING, viewModel.uiState.value.session.state)

        // Stopping audio does not cancel focus session
        viewModel.stopPlayer()
        assertEquals(SessionState.RUNNING, viewModel.uiState.value.session.state)

        // Pausing focus session does not terminate player manager
        viewModel.pauseFocusSession()
        assertEquals(SessionState.PAUSED, viewModel.uiState.value.session.state)

        viewModel.resumeFocusSession()
        assertEquals(SessionState.RUNNING, viewModel.uiState.value.session.state)

        viewModel.resetSession()
    }

    @Test
    fun testTrackNavigationAndRepeatMode() = runTest(testDispatcher) {
        val viewModel = FocusViewModel(application)

        // Initial default state
        assertEquals("Deep Focus", viewModel.playerState.value.trackTitle)
        assertTrue(viewModel.playerState.value.isLooping)

        // Toggle loop cycles modes
        viewModel.togglePlayerLoop()
        // Controls are revealed upon action
        assertTrue(viewModel.uiState.value.controlsVisible)
    }
}
