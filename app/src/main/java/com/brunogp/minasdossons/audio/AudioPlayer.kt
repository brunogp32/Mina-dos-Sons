package com.brunogp.minasdossons.audio

import android.media.MediaPlayer
import java.io.File

class AudioPlayer : RecordingPlaybackOutput {
    private var player: MediaPlayer? = null

    fun play(file: File?): Boolean {
        if (file == null || !file.exists()) return false
        stop()
        return runCatching {
            player =
                MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    setOnCompletionListener { stop() }
                    prepare()
                    start()
                }
        }.isSuccess
    }

    override fun stop() {
        player?.release()
        player = null
    }
}
