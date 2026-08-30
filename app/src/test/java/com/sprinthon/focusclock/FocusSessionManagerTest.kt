package com.sprinthon.focusclock

import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import com.sprinthon.focusclock.domain.session.FocusSessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FocusSessionManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun testStartSessionCountdownAccurate() = testScope.runTest {
        val manager = FocusSessionManager(testScope)

        manager.startSession(
            durationMinutes = 25,
            displayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.CLEAN_DIGITAL
        )

        val snapshot = manager.snapshot.value
        assertEquals(SessionState.RUNNING, snapshot.state)
        assertEquals(25, snapshot.durationMinutes)
        assertEquals(TimerDisplayMode.COUNTDOWN, snapshot.displayMode)
        assertTrue(snapshot.targetEndAt > snapshot.startedAt)
        assertEquals(25 * 60 * 1000L, snapshot.remainingMillis)
        assertEquals("25:00", snapshot.formattedDisplayTime)

        manager.resetToIdle()
    }

    @Test
    fun testStartSessionUnlimitedElapsed() = testScope.runTest {
        val manager = FocusSessionManager(testScope)

        manager.startSession(
            durationMinutes = 0,
            displayMode = TimerDisplayMode.COUNTDOWN,
            clockStyle = ClockStyle.MINIMAL_DIGITAL
        )

        val snapshot = manager.snapshot.value
        assertEquals(SessionState.RUNNING, snapshot.state)
        assertTrue(snapshot.isUnlimited)
        assertEquals(TimerDisplayMode.ELAPSED, snapshot.displayMode)
        assertEquals("00:00", snapshot.formattedDisplayTime)

        manager.resetToIdle()
    }

    @Test
    fun testPauseAndResumePreservesDuration() = testScope.runTest {
        val manager = FocusSessionManager(testScope)

        manager.startSession(durationMinutes = 25)
        assertEquals(SessionState.RUNNING, manager.snapshot.value.state)

        manager.pauseSession()
        assertEquals(SessionState.PAUSED, manager.snapshot.value.state)

        manager.resumeSession()
        assertEquals(SessionState.RUNNING, manager.snapshot.value.state)

        manager.resetToIdle()
    }

    @Test
    fun testCancelAndReset() = testScope.runTest {
        val manager = FocusSessionManager(testScope)

        manager.startSession(durationMinutes = 45)
        manager.cancelSession()
        assertEquals(SessionState.CANCELLED, manager.snapshot.value.state)

        manager.resetToIdle()
        assertEquals(SessionState.IDLE, manager.snapshot.value.state)
        assertEquals(0L, manager.snapshot.value.startedAt)
    }

    @Test
    fun testManualComplete() = testScope.runTest {
        val manager = FocusSessionManager(testScope)

        manager.startSession(durationMinutes = 60)
        manager.completeSession()

        assertEquals(SessionState.COMPLETED, manager.snapshot.value.state)
        assertEquals(0L, manager.snapshot.value.remainingMillis)
        assertEquals(1f, manager.snapshot.value.progress)

        manager.resetToIdle()
    }
}
