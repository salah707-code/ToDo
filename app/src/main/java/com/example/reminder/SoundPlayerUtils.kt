package com.example.reminder

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.example.data.preferences.NotificationTone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundPlayerUtils {

    private const val SAMPLE_RATE = 44100

    fun previewTone(context: Context, tone: NotificationTone, volume: Float = 0.85f) {
        if (tone == NotificationTone.MUTE) return
        CoroutineScope(Dispatchers.Default).launch {
            playTone(context, tone, volume)
        }
    }

    fun playTone(context: Context, tone: NotificationTone, volume: Float = 0.85f) {
        if (tone == NotificationTone.MUTE) return

        try {
            when (tone) {
                NotificationTone.CHIME_ALERT -> {
                    // Modern 3-tone harmonic chime: 659Hz (E5), 880Hz (A5), 1318Hz (E6)
                    val notes = listOf(659.25 to 120, 880.00 to 120, 1318.51 to 300)
                    playFrequencySequence(notes, volume)
                }
                NotificationTone.GENTLE_BELL -> {
                    // Soft Bell Chord: 523Hz (C5), 659Hz (E5), 783Hz (G5)
                    val notes = listOf(523.25 to 140, 659.25 to 140, 783.99 to 350)
                    playFrequencySequence(notes, volume)
                }
                NotificationTone.DIGITAL_PULSE -> {
                    // Modern dual electronic beep
                    val notes = listOf(1046.50 to 90, 0.0 to 40, 1567.98 to 160)
                    playFrequencySequence(notes, volume)
                }
                NotificationTone.CRYSTAL_DROP -> {
                    // Gentle raindrop / crystal ding: 1760Hz with decay
                    val notes = listOf(1760.00 to 70, 2093.00 to 220)
                    playFrequencySequence(notes, volume)
                }
                NotificationTone.SYSTEM_DEFAULT -> {
                    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, (volume * 100).toInt().coerceIn(1, 100))
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
                }
                NotificationTone.MUTE -> { /* No-op */ }
            }
        } catch (e: Exception) {
            Log.e("SoundPlayerUtils", "Error playing tone: ${e.message}")
        }
    }

    private fun playFrequencySequence(frequencies: List<Pair<Double, Int>>, volume: Float) {
        try {
            var totalDurationMs = 0
            for ((_, duration) in frequencies) {
                totalDurationMs += duration
            }

            val totalSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
            val audioData = ShortArray(totalSamples)
            var currentSampleIndex = 0

            val gain = (Short.MAX_VALUE * volume.coerceIn(0.1f, 1f) * 0.7f)

            for ((freq, durationMs) in frequencies) {
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                if (freq <= 0) {
                    // Silence
                    for (i in 0 until numSamples) {
                        if (currentSampleIndex < audioData.size) {
                            audioData[currentSampleIndex++] = 0
                        }
                    }
                } else {
                    for (i in 0 until numSamples) {
                        if (currentSampleIndex < audioData.size) {
                            // Apply smooth attack and decay envelope
                            val progress = i.toDouble() / numSamples
                            val envelope = when {
                                progress < 0.1 -> progress / 0.1
                                progress > 0.6 -> (1.0 - progress) / 0.4
                                else -> 1.0
                            }
                            val angle = 2.0 * Math.PI * i / (SAMPLE_RATE / freq)
                            val sample = (sin(angle) * gain * envelope).toInt().toShort()
                            audioData[currentSampleIndex++] = sample
                        }
                    }
                }
            }

            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(maxOf(minBufferSize, audioData.size * 2))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(audioData, 0, audioData.size)
            track.play()
            
            // Release track after playback completes
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(totalDurationMs.toLong() + 200)
                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            }
        } catch (e: Exception) {
            Log.e("SoundPlayerUtils", "AudioTrack synthesis error: ${e.message}")
        }
    }
}
