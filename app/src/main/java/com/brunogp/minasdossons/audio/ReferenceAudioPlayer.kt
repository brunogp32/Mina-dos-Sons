package com.brunogp.minasdossons.audio

import android.content.Context
import android.media.MediaPlayer

class ReferenceAudioPlayer(
    private val context: Context,
    private val repository: ReferenceAudioRepository,
) {
    private var player: MediaPlayer? = null

    fun playReferenceSound(sound: ReferenceSound): Boolean {
        stop()
        return runCatching {
            player =
                when (val source = repository.resolve(sound)) {
                    is ReferenceAudioSource.GeneratedFallback -> MediaPlayer().apply { setDataSource(source.file.absolutePath) }
                    is ReferenceAudioSource.RawResource -> MediaPlayer.create(context, source.resId)
                }?.apply {
                    setOnCompletionListener { stop() }
                    if (!isPlaying) {
                        prepareIfNeeded()
                        start()
                    }
                }
        }.isSuccess
    }

    fun stop() {
        player?.release()
        player = null
    }

    private fun MediaPlayer.prepareIfNeeded() {
        runCatching { prepare() }
    }
}
