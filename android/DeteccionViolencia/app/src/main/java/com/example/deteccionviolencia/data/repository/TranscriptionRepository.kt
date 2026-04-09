package com.example.deteccionviolencia.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Interfaz que define el contrato para la conversión de audio a texto
 */
interface TranscriptionRepository {
    /**
     * Convierte un archivo de audio WAV en una cadena de texto
     * 
     * @param audioFile Archivo de audio grabado previamente
     * @return Texto transcrito
     */
    suspend fun transcribe(audioFile: File): String
}

/**
 * Implementación del repositorio de transcripción utilizando el motor Whisper C++
 * Utiliza JNI para comunicarse con la librería nativa de reconocimiento de voz
 * 
 * @property context Contexto de la aplicación necesario para acceder a assets y archivos internos
 */
class WhisperTranscriptionRepository(private val context: Context) : TranscriptionRepository {

    companion object {
        private const val TAG = "WhisperCpp"
        
        /** Inicialización de la librería nativa al cargar la clase */
        init {
            try {
                System.loadLibrary("whispercpp")
                Log.d(TAG, "Librería whispercpp cargada con éxito")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar la librería: ${e.message}")
            }
        }
    }

    /** Función definida en C++ para procesar el audio con el modelo Whisper */
    private external fun transcribeNative(modelPath: String, audioData: FloatArray): String

    /**
     * Ejecuta la transcripción en un hilo de cómputo
     * Prepara el modelo y normaliza el audio antes de llamar a la función nativa
     */
    override suspend fun transcribe(audioFile: File): String = withContext(Dispatchers.Default) {
        try {
            Log.d(TAG, "Iniciando transcripción de: ${audioFile.name}")
            val modelFile = ensureModelFile()
            Log.d(TAG, "Modelo listo en: ${modelFile.absolutePath}")

            val audioFloatArray = readWavToFloatArray(audioFile)
            Log.d(TAG, "Audio procesado: ${audioFloatArray.size} samples")

            if (audioFloatArray.isEmpty()) return@withContext "Error: el archivo de audio está vacío"

            val result = transcribeNative(modelFile.absolutePath, audioFloatArray)
            Log.d(TAG, "Resultado de la transcripción: $result")
            return@withContext result
        } catch (e: Exception) {
            Log.e(TAG, "Error en el proceso de transcripción: ${e.message}", e)
            return@withContext "Error: ${e.message}"
        }
    }

    /**
     * Asegura que el archivo del modelo esté en el almacenamiento interno
     * 
     * @return El archivo del modelo listo para ser usado por Whisper
     */
    private fun ensureModelFile(): File {
        val modelFileName = "ggml-base-q5_1.bin"
        val modelFile = File(context.filesDir, modelFileName)

        if (!modelFile.exists()) {
            Log.d(TAG, "Copiando modelo de assets a almacenamiento interno")
            context.assets.open("models/$modelFileName").use { inputStream ->
                FileOutputStream(modelFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d(TAG, "Modelo copiado con éxito")
        }
        return modelFile
    }

    /**
     * Convierte el archivo WAV en un arreglo de flotantes
     * 
     * @param wavFile Archivo de entrada en formato WAV
     * @return Arreglo de flotantes con la señal de audio procesada
     */
    private fun readWavToFloatArray(wavFile: File): FloatArray {
        return try {
            FileInputStream(wavFile).use { fis ->
                val pcmBytes = fis.readBytes()
                val dataOffset = if (pcmBytes.size > 44) 44 else 0
                val dataSize = pcmBytes.size - dataOffset

                if (dataSize <= 0) return floatArrayOf()

                val shortBuffer = ByteBuffer.wrap(pcmBytes, dataOffset, dataSize)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()

                val shorts = ShortArray(shortBuffer.remaining())
                shortBuffer.get(shorts)

                FloatArray(shorts.size) { i ->
                    shorts[i].toFloat() / 32768.0f
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al leer WAV: ${e.message}")
            floatArrayOf()
        }
    }
}
