package com.sprinthon.focusclock.playback

import android.content.Context
import android.net.Uri
import com.sprinthon.focusclock.domain.model.FocusTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

/**
 * Generates and manages high quality, peaceful local ambient audio files for the Focus Clock.
 * Ensures 100% offline, zero-network, legal, and crash-free playback.
 */
object AudioFileHelper {

    private const val SAMPLE_RATE = 44100
    private const val DURATION_SECONDS = 12 // Seamless loopable audio block

    suspend fun getOrCreateTrackUri(context: Context, trackId: String): Uri = withContext(Dispatchers.IO) {
        try {
            val audioDir = File(context.filesDir, "focus_audio")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }

            val file = File(audioDir, "$trackId.wav")
            if (!file.exists() || file.length() < 1000L) {
                generateTrackWav(file, trackId)
            }

            Uri.fromFile(file)
        } catch (e: Exception) {
            android.util.Log.e("AudioFileHelper", "Error preparing track audio", e)
            Uri.EMPTY
        }
    }

    private fun generateTrackWav(outputFile: File, trackId: String) {
        val totalSamples = SAMPLE_RATE * DURATION_SECONDS
        val numChannels = 2
        val bitsPerSample = 16
        val bytesPerSample = (bitsPerSample / 8) * numChannels
        val dataSize = totalSamples * bytesPerSample

        FileOutputStream(outputFile).use { fos ->
            // Write placeholder WAV header
            writeWavHeader(fos, dataSize, SAMPLE_RATE, numChannels, bitsPerSample)

            val buffer = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN)
            var brownNoiseL = 0f
            var brownNoiseR = 0f
            val random = java.util.Random(42)

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                var leftSample = 0f
                var rightSample = 0f

                when (trackId) {
                    "deep_focus" -> {
                        // 432Hz harmonic warm chord (root + fifth + octave) with slow gentle LFO
                        val lfo = (1.0 + 0.15 * sin(2.0 * Math.PI * 0.2 * t)).toFloat()
                        val root = (sin(2.0 * Math.PI * 108.0 * t) * 0.45).toFloat()
                        val fifth = (sin(2.0 * Math.PI * 162.0 * t) * 0.25).toFloat()
                        val octave = (sin(2.0 * Math.PI * 216.0 * t) * 0.15).toFloat()
                        val sub = (sin(2.0 * Math.PI * 54.0 * t) * 0.35).toFloat()
                        val combined = (root + fifth + octave + sub) * 0.5f * lfo

                        // Slight stereo widening
                        leftSample = combined * 0.9f
                        rightSample = combined * 0.95f
                    }
                    "gentle_rain" -> {
                        // Soothing pink/brown noise with periodic gentle drops
                        val whiteL = (random.nextFloat() * 2f - 1f)
                        val whiteR = (random.nextFloat() * 2f - 1f)
                        brownNoiseL = (brownNoiseL * 0.96f + whiteL * 0.04f).coerceIn(-1f, 1f)
                        brownNoiseR = (brownNoiseR * 0.96f + whiteR * 0.04f).coerceIn(-1f, 1f)
                        
                        // Gentle rainfall modulation
                        val rainLfo = (1.0 + 0.2 * sin(2.0 * Math.PI * 0.4 * t)).toFloat()
                        leftSample = (brownNoiseL * 0.55f + whiteL * 0.05f) * rainLfo
                        rightSample = (brownNoiseR * 0.55f + whiteR * 0.05f) * rainLfo
                    }
                    "white_noise" -> {
                        // Smooth, gentle focus noise (filtered white/pink)
                        val whiteL = (random.nextFloat() * 2f - 1f)
                        val whiteR = (random.nextFloat() * 2f - 1f)
                        brownNoiseL = (brownNoiseL * 0.85f + whiteL * 0.15f).coerceIn(-1f, 1f)
                        brownNoiseR = (brownNoiseR * 0.85f + whiteR * 0.15f).coerceIn(-1f, 1f)
                        leftSample = brownNoiseL * 0.40f
                        rightSample = brownNoiseR * 0.40f
                    }
                    "ambient_flow" -> {
                        // Calming two-chord meditative drone with soft sine harmonics
                        val chordLfo = sin(2.0 * Math.PI * (1.0 / 6.0) * t)
                        val freq1 = if (chordLfo >= 0) 130.81 else 146.83 // C3 to D3
                        val freq2 = if (chordLfo >= 0) 196.00 else 220.00 // G3 to A3
                        val s1 = sin(2.0 * Math.PI * freq1 * t).toFloat() * 0.35f
                        val s2 = sin(2.0 * Math.PI * freq2 * t).toFloat() * 0.25f
                        val bell = (sin(2.0 * Math.PI * (freq1 * 4) * t) * (0.05 * (1.0 + sin(2.0 * Math.PI * 0.5 * t)))).toFloat()
                        leftSample = (s1 + s2 + bell) * 0.6f
                        rightSample = (s1 + s2 * 1.1f + bell * 0.9f) * 0.6f
                    }
                    "forest_waves" -> {
                        // Very slow natural oceanic swelling binaural rhythm
                        val swell = (0.5 * (1.0 + sin(2.0 * Math.PI * 0.12 * t))).toFloat()
                        val carrierL = sin(2.0 * Math.PI * 136.1 * t).toFloat() // OM frequency
                        val carrierR = sin(2.0 * Math.PI * 142.1 * t).toFloat() // 6Hz Theta binaural beat
                        leftSample = carrierL * 0.45f * (0.4f + 0.6f * swell)
                        rightSample = carrierR * 0.45f * (0.4f + 0.6f * swell)
                    }
                    "silent_dummy" -> {
                        leftSample = 0f
                        rightSample = 0f
                    }
                    else -> {
                        // Default fallback soft pure tone
                        val tone = (sin(2.0 * Math.PI * 220.0 * t) * 0.3).toFloat()
                        leftSample = tone
                        rightSample = tone
                    }
                }

                // Fade in and out at file boundaries for clickless perfect looping
                val fadeSamples = SAMPLE_RATE / 2 // 0.5 sec fade
                val envelope = when {
                    i < fadeSamples -> i.toFloat() / fadeSamples
                    i > totalSamples - fadeSamples -> (totalSamples - i).toFloat() / fadeSamples
                    else -> 1.0f
                }

                val finalL = (leftSample * envelope * 0.75f).coerceIn(-1.0f, 1.0f)
                val finalR = (rightSample * envelope * 0.75f).coerceIn(-1.0f, 1.0f)

                val shortL = (finalL * 32767f).toInt().toShort()
                val shortR = (finalR * 32767f).toInt().toShort()

                if (buffer.remaining() < 4) {
                    fos.write(buffer.array(), 0, buffer.position())
                    buffer.clear()
                }

                buffer.putShort(shortL)
                buffer.putShort(shortR)
            }

            if (buffer.position() > 0) {
                fos.write(buffer.array(), 0, buffer.position())
            }
        }
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        dataSize: Int,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val totalDataLen = dataSize + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteArray(44)
        val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        bb.put('R'.code.toByte())
        bb.put('I'.code.toByte())
        bb.put('F'.code.toByte())
        bb.put('F'.code.toByte())
        bb.putInt(totalDataLen)
        bb.put('W'.code.toByte())
        bb.put('A'.code.toByte())
        bb.put('V'.code.toByte())
        bb.put('E'.code.toByte())
        bb.put('f'.code.toByte())
        bb.put('m'.code.toByte())
        bb.put('t'.code.toByte())
        bb.put(' '.code.toByte())
        bb.putInt(16) // Subchunk1Size for PCM
        bb.putShort(1.toShort()) // AudioFormat (1 = PCM)
        bb.putShort(channels.toShort())
        bb.putInt(sampleRate)
        bb.putInt(byteRate)
        bb.putShort(blockAlign.toShort())
        bb.putShort(bitsPerSample.toShort())
        bb.put('d'.code.toByte())
        bb.put('a'.code.toByte())
        bb.put('t'.code.toByte())
        bb.put('a'.code.toByte())
        bb.putInt(dataSize)

        out.write(header)
    }
}
