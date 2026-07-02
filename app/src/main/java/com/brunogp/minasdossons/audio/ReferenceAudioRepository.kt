package com.brunogp.minasdossons.audio

import android.content.Context
import java.io.File

sealed class ReferenceAudioSource {
    data class RawResource(val resId: Int) : ReferenceAudioSource()
    data class GeneratedFallback(val file: File) : ReferenceAudioSource()
}

class ReferenceAudioRepository(
    private val context: Context,
    private val modelDir: File = File(context.filesDir, "reference_models"),
    private val fallbackDir: File = File(context.filesDir, "reference_fallbacks"),
) {
    fun customModelFile(sound: ReferenceSound): File = File(modelDir.apply { mkdirs() }, "${sound.id}.m4a")

    fun hasCustomModel(sound: ReferenceSound): Boolean = false

    fun resolve(sound: ReferenceSound): ReferenceAudioSource {
        val resId = context.resources.getIdentifier(sound.bundledRawResourceName, "raw", context.packageName)
        if (resId != 0) return ReferenceAudioSource.RawResource(resId)

        return ReferenceAudioSource.GeneratedFallback(GeneratedSoundFallback.ensureFile(fallbackDir, sound))
    }

    companion object {
        fun preferredSourceKind(hasCustomModel: Boolean, rawResourceId: Int): String = when {
            rawResourceId != 0 -> "raw"
            else -> "fallback"
        }
    }
}
