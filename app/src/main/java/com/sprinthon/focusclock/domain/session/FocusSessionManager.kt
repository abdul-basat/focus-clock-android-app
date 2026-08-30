package com.sprinthon.focusclock.domain.session

import com.sprinthon.focusclock.domain.model.ClockStyle
import com.sprinthon.focusclock.domain.model.SessionState
import com.sprinthon.focusclock.domain.model.TimerDisplayMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

data class SessionSnapshot(
    val sessionId: String = "",
    val state: SessionState = SessionState.IDLE,
    val durationMinutes: Int = 25,
    val isUnlimited: Boolean = false,
    val displayMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
    val clockStyle: ClockStyle = ClockStyle.CLEAN_DIGITAL,
    val startedAt: Long = 0L,
    val targetEndAt: Long = 0L,
    val remainingMillis: Long = 0L,
    val elapsedMillis: Long = 0L,
    val progress: Float = 0f,
    val profileName: String = "Focus"
) {
    val formattedDisplayTime: String
        get() {
            return if (isUnlimited || displayMode == TimerDisplayMode.ELAPSED) {
                formatDuration(elapsedMillis)
            } else {
                formatDuration(remainingMillis)
            }
        }

    val displayModeLabel: String
        get() {
            return if (isUnlimited) "Elapsed" else when (displayMode) {
                TimerDisplayMode.COUNTDOWN -> "Remaining"
                TimerDisplayMode.ELAPSED -> "Elapsed"
            }
        }

    private fun formatDuration(millis: Long): String {
        val totalSeconds = (millis / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
}

class FocusSessionManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _snapshot = MutableStateFlow(SessionSnapshot())
    val snapshot: StateFlow<SessionSnapshot> = _snapshot.asStateFlow()

    private var tickerJob: Job? = null
    private var pausedAt: Long = 0L
    private var totalPausedDuration: Long = 0L

    fun startSession(
        durationMinutes: Int,
        displayMode: TimerDisplayMode = TimerDisplayMode.COUNTDOWN,
        clockStyle: ClockStyle = ClockStyle.CLEAN_DIGITAL,
        profileName: String = "Focus"
    ) {
        val now = System.currentTimeMillis()
        val isUnlimited = durationMinutes <= 0
        val durationMillis = if (isUnlimited) 0L else durationMinutes * 60 * 1000L
        val targetEnd = if (isUnlimited) 0L else now + durationMillis

        pausedAt = 0L
        totalPausedDuration = 0L

        _snapshot.value = SessionSnapshot(
            sessionId = UUID.randomUUID().toString(),
            state = SessionState.RUNNING,
            durationMinutes = durationMinutes,
            isUnlimited = isUnlimited,
            displayMode = if (isUnlimited) TimerDisplayMode.ELAPSED else displayMode,
            clockStyle = clockStyle,
            startedAt = now,
            targetEndAt = targetEnd,
            remainingMillis = durationMillis,
            elapsedMillis = 0L,
            progress = 0f,
            profileName = profileName
        )

        startTicker()
    }

    fun pauseSession() {
        if (_snapshot.value.state != SessionState.RUNNING) return
        tickerJob?.cancel()
        pausedAt = System.currentTimeMillis()
        _snapshot.value = _snapshot.value.copy(state = SessionState.PAUSED)
    }

    fun resumeSession() {
        if (_snapshot.value.state != SessionState.PAUSED) return
        val now = System.currentTimeMillis()
        if (pausedAt > 0L) {
            val pauseDelta = now - pausedAt
            totalPausedDuration += pauseDelta
            if (!_snapshot.value.isUnlimited) {
                val newTarget = _snapshot.value.targetEndAt + pauseDelta
                _snapshot.value = _snapshot.value.copy(targetEndAt = newTarget)
            }
            pausedAt = 0L
        }
        _snapshot.value = _snapshot.value.copy(state = SessionState.RUNNING)
        startTicker()
    }

    fun completeSession() {
        tickerJob?.cancel()
        val current = _snapshot.value
        _snapshot.value = current.copy(
            state = SessionState.COMPLETED,
            remainingMillis = 0L,
            progress = 1f
        )
    }

    fun cancelSession() {
        tickerJob?.cancel()
        _snapshot.value = _snapshot.value.copy(state = SessionState.CANCELLED)
    }

    fun resetToIdle() {
        tickerJob?.cancel()
        _snapshot.value = SessionSnapshot(state = SessionState.IDLE)
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                delay(250L)
                updateTime()
            }
        }
    }

    private fun updateTime() {
        val current = _snapshot.value
        if (current.state != SessionState.RUNNING) return

        val now = System.currentTimeMillis()
        val effectiveElapsed = now - current.startedAt - totalPausedDuration

        if (current.isUnlimited) {
            _snapshot.value = current.copy(
                elapsedMillis = effectiveElapsed.coerceAtLeast(0L),
                remainingMillis = 0L,
                progress = 0f
            )
        } else {
            val totalDuration = current.durationMinutes * 60 * 1000L
            val remaining = (current.targetEndAt - now).coerceAtLeast(0L)
            val progress = if (totalDuration > 0) {
                ((totalDuration - remaining).toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
            } else 0f

            if (remaining <= 0L) {
                tickerJob?.cancel()
                _snapshot.value = current.copy(
                    state = SessionState.COMPLETED,
                    remainingMillis = 0L,
                    elapsedMillis = totalDuration,
                    progress = 1f
                )
            } else {
                _snapshot.value = current.copy(
                    remainingMillis = remaining,
                    elapsedMillis = effectiveElapsed.coerceAtLeast(0L),
                    progress = progress
                )
            }
        }
    }
}
