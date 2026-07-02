package com.brunogp.minasdossons.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object GeneratedSoundFallback {
    const val DURATION_MS = 1_800
    private const val SAMPLE_RATE = 22_050

    fun ensureFile(
        directory: File,
        sound: ReferenceSound,
    ): File {
        directory.mkdirs()
        val file = File(directory, "fallback_${sound.id}.wav")
        if (!file.exists() || file.length() == 0L) {
            file.writeBytes(generateWav(sound))
        }
        return file
    }

    private fun generateWav(sound: ReferenceSound): ByteArray {
        val sampleCount = SAMPLE_RATE * DURATION_MS / 1_000
        val random = Random(sound.id.hashCode())
        val pcm = ShortArray(sampleCount)
        var lastNoise = 0.0
        repeat(sampleCount) { index ->
            val t = index.toDouble() / SAMPLE_RATE
            val fadeIn = (index / (SAMPLE_RATE * 0.05)).coerceIn(0.0, 1.0)
            val fadeOut = ((sampleCount - index) / (SAMPLE_RATE * 0.08)).coerceIn(0.0, 1.0)
            val fade = fadeIn * fadeOut
            val rawNoise = random.nextDouble(-1.0, 1.0)
            val filter = if (sound == ReferenceSound.S || sound == ReferenceSound.Z) 0.62 else 0.38
            lastNoise = filter * rawNoise + (1.0 - filter) * lastNoise
            val voice =
                when (sound) {
                    ReferenceSound.Z -> sin(2.0 * PI * 145.0 * t) * 0.34
                    ReferenceSound.J -> sin(2.0 * PI * 170.0 * t) * 0.34
                    else -> 0.0
                }
            val noiseLevel =
                when (sound) {
                    ReferenceSound.S -> 0.42
                    ReferenceSound.CH -> 0.34
                    ReferenceSound.Z -> 0.25
                    ReferenceSound.J -> 0.22
                }
            val sample =
                ((lastNoise * noiseLevel + voice) * fade * Short.MAX_VALUE * 0.55)
                    .coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())
            pcm[index] = sample.toInt().toShort()
        }
        return wavFromPcm(pcm)
    }

    private fun wavFromPcm(samples: ShortArray): ByteArray {
        val dataSize = samples.size * 2
        val out = ByteArrayOutputStream(44 + dataSize)

        fun writeAscii(value: String) = out.write(value.toByteArray(Charsets.US_ASCII))

        fun writeInt(value: Int) = out.write(
            ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value)
                .array(),
        )

        fun writeShort(value: Int) = out.write(
            ByteBuffer
                .allocate(2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(value.toShort())
                .array(),
        )
        writeAscii("RIFF")
        writeInt(36 + dataSize)
        writeAscii("WAVE")
        writeAscii("fmt ")
        writeInt(16)
        writeShort(1)
        writeShort(1)
        writeInt(SAMPLE_RATE)
        writeInt(SAMPLE_RATE * 2)
        writeShort(2)
        writeShort(16)
        writeAscii("data")
        writeInt(dataSize)
        samples.forEach { writeShort(it.toInt()) }
        return out.toByteArray()
    }
}
