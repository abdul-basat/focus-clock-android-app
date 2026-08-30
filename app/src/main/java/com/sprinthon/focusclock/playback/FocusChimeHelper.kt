package com.sprinthon.focusclock.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object FocusChimeHelper {

    /**
     * Plays a soft, harmonious two-tone chime (528 Hz and 660 Hz) on completion.
     * Generates a smooth sine wave with fade-in and fade-out envelope to avoid clipping.
     */
    suspend fun playCompletionChime() = withContext(Dispatchers.IO) {
        try {
            val sampleRate = 44100
            val durationSec = 0.6
            val numSamples = (sampleRate * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            val freq1 = 528.0 // Solfeggio "transformation" / calm frequency
            val freq2 = 660.0 // Major third harmony

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                // Harmonic blend
                val sample1 = sin(2.0 * Math.PI * freq1 * time)
                val sample2 = sin(2.0 * Math.PI * freq2 * time) * 0.5
                val combined = (sample1 + sample2) / 1.5

                // Envelope: 20ms attack, exponential decay
                val envelope = when {
                    time < 0.02 -> (time / 0.02)
                    else -> Math.exp(-4.5 * (time - 0.02))
                }

                val value = (combined * envelope * 0.4 * Short.MAX_VALUE).toInt()
                buffer[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build()

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Release after playback finishes
            kotlinx.coroutines.delay((durationSec * 1000).toLong() + 100L)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            // Ignore audio generation failures gracefully
        }
    }
}
