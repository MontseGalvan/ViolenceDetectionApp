package com.example.deteccionviolencia.data.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase encargada de la captura de señales de audio
 * @property context Contexto de la aplicación para acceder al almacenamiento
 */
class AudioRecorder(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private var audioFile: File? = null

    /** Frecuencia de muestreo estándar para reconocimiento de voz */
    private val sampleRate = 16000
    
    /** Configuración de canal único */
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    
    /** Formato de codificación lineal de 16 bits por muestra */
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    
    /** Tamaño del búfer calculado para evitar pérdida de datos durante la captura */
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

    /**
     * Inicia el proceso de grabación y crea un archivo WAV con una cabecera que se actualizará al finalizar
     */
    suspend fun startRecording() = withContext(Dispatchers.IO) {
        if (isRecording) return@withContext
        try {
            audioFile = createAudioFile()
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            audioRecord?.startRecording()
            isRecording = true
            recordingThread = Thread {
                writeAudioDataToFile()
            }.apply { start() }

        } catch (e: SecurityException) {
            throw RuntimeException("Permiso de micrófono denegado", e)
        } catch (e: Exception) {
            throw RuntimeException("Error al iniciar grabación", e)
        }
    }

    /**
     * Escribe los datos de audio en formato PCM
     * Actualiza la cabecera WAV al final del proceso
     */
    private fun writeAudioDataToFile() {
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(audioFile)
            writeWavHeader(fos, 0)
            while (isRecording) {
                bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0) {
                    fos.write(buffer, 0, bytesRead)
                }
            }
            val totalAudioLength = fos.channel.size() - 44
            updateWavHeader(fos, totalAudioLength)

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.close()
        }
    }

    /**
     * Finaliza la captura de audio y libera los recursos
     * @return El archivo WAV generado con la grabación
     */
    suspend fun stopRecording(): File = withContext(Dispatchers.IO) {
        isRecording = false
        recordingThread?.join()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        return@withContext audioFile ?: throw IllegalStateException("No hay archivo de audio")
    }

    /** Genera una ruta de archivo basada en la marca de tiempo */
    private fun createAudioFile(): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AUDIO_${timeStamp}.wav"
        return File(storageDir, fileName)
    }
    
    /**
     * Escribe la cabecera estándar de un archivo WAV
     * @param out Salida del archivo
     * @param totalDataLength Tamaño total de los datos de audio en bytes
     */
    private fun writeWavHeader(out: FileOutputStream, totalDataLength: Long) {
        val header = ByteArray(44)
        writeString(header, 0, "RIFF")
        writeInt(header, 4, (totalDataLength + 36).toInt())
        writeString(header, 8, "WAVE")
        writeString(header, 12, "fmt ")
        writeInt(header, 16, 16)
        writeShort(header, 20, 1)
        writeShort(header, 22, 1)
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, sampleRate * 2)
        writeShort(header, 32, 2.toShort())
        writeShort(header, 34, 16.toShort())
        writeString(header, 36, "data")
        writeInt(header, 40, totalDataLength.toInt())

        out.write(header)
    }

    /** Actualiza el tamaño en la cabecera WAV */
    private fun updateWavHeader(out: FileOutputStream, totalDataLength: Long) {
        out.channel.position(4)
        val fileLength = (totalDataLength + 36).toInt()
        out.write(intToByteArray(fileLength))

        out.channel.position(40)
        out.write(intToByteArray(totalDataLength.toInt()))
    }

    /** Escribe una cadena de texto en el búfer de bytes */
    private fun writeString(buffer: ByteArray, offset: Int, value: String) {
        val bytes = value.toByteArray()
        System.arraycopy(bytes, 0, buffer, offset, bytes.size)
    }
    
    /** Escribe un entero de 32 bits en formato Little Endian */
    private fun writeInt(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
        buffer[offset + 2] = ((value shr 16) and 0xFF).toByte()
        buffer[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }
    
    /** Escribe un entero de 16 bits en formato Little Endian */
    private fun writeShort(buffer: ByteArray, offset: Int, value: Short) {
        buffer[offset] = (value.toInt() and 0xFF).toByte()
        buffer[offset + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
    }
    
    /** Convierte un entero a un arreglo de 4 bytes en Little Endian */
    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

}
