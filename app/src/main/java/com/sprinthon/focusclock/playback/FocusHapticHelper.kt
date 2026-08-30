package com.sprinthon.focusclock.playback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object FocusHapticHelper {

    /**
     * Retrieves the system Vibrator, handling API level differences.
     */
    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Vibrate with a specific waveform pattern, with legacy fallback.
     */
    private fun vibratePattern(context: Context, timings: LongArray, amplitudes: IntArray, legacyDurationMs: Long) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(legacyDurationMs)
            }
        } catch (e: Exception) {
            // Ignore haptic failures silently
        }
    }

    /**
     * Session Start: Sharp double pulse — crisp, intentional kickoff feel.
     */
    fun performSessionStartHaptic(context: Context) {
        vibratePattern(
            context,
            timings = longArrayOf(0, 80, 50, 120),
            amplitudes = intArrayOf(0, 200, 0, 255),
            legacyDurationMs = 250L
        )
    }

    /**
     * Session Complete / Break Start: Smooth completion cadence — satisfying closure.
     */
    fun performCompletionHaptic(context: Context) {
        vibratePattern(
            context,
            timings = longArrayOf(0, 100, 80, 100, 80, 200),
            amplitudes = intArrayOf(0, 160, 0, 160, 0, 220),
            legacyDurationMs = 400L
        )
    }

    /**
     * Break End / Resume Focus: Gentle triple tap — subtle re-engagement nudge.
     */
    fun performResumeHaptic(context: Context) {
        vibratePattern(
            context,
            timings = longArrayOf(0, 60, 40, 60, 40, 60),
            amplitudes = intArrayOf(0, 140, 0, 140, 0, 140),
            legacyDurationMs = 200L
        )
    }
}
