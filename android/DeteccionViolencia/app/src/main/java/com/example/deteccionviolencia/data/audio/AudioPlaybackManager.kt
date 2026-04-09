package com.example.deteccionviolencia.data.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Clase encargada de la reproducción de archivos de audio
 * @property context Contexto de la aplicación necesario para inicializar el reproductor
 */
class AudioPlaybackManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    
    /** Estado interno que indica si el audio se está reproduciendo */
    private val _isPlaying = MutableStateFlow(false)
    
    /** Estado observable para la interfaz de usuario */
    val isPlaying: StateFlow<Boolean> = _isPlaying

    /**
     * Prepara el reproductor con el archivo de audio
     * Configura los escuchadores de eventos para actualizar el estado
     * @param file El archivo de audio WAV a reproducir
     */
    fun prepare(file: File) {
        release()
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
            setMediaItem(mediaItem)
            prepare()
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        this@apply.pause()
                        seekTo(0)
                    }
                }
            })
        }
    }

    /** Alterna entre los estados de reproducción y pausa */
    fun playPause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    /** Libera los recursos del reproductor cuando ya no son necesarios */
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
