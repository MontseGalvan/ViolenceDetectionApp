package com.example.deteccionviolencia.presentation.home

import com.example.deteccionviolencia.domain.model.ClassificationResult
import java.io.File

/**
 * Representa el estado de la interfaz de usuario para la pantalla de inicio
 * @property isRecording Indica si el micrófono está capturando audio actualmente
 * @property audioFile Archivo generado tras la grabación
 * @property durationMs Duración del audio en milisegundos
 * @property transcribedText Texto transcrito
 * @property isProcessing Indica si se está realizando transcripción o análisis
 * @property classificationResult Resultado final del análisis de violencia
 * @property isSaved Indica si el análisis actual ya fue guardado en la base de datos
 * @property errorMessage Mensaje de error a mostrar en caso de fallo
 */
data class HomeState(
    val isRecording: Boolean = false,
    val audioFile: File? = null,
    val durationMs: Long = 0,
    val transcribedText: String = "",
    val isProcessing: Boolean = false,
    val classificationResult: ClassificationResult? = null,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Eventos que pueden ser lanzados desde la vista hacia el ViewModel de la pantalla de inicio
 */
sealed class HomeEvent {
    /** Inicia el proceso de grabación */
    data object StartRecording : HomeEvent()
    
    /** Detiene la grabación e inicia el procesamiento */
    data object StopRecording : HomeEvent()
    
    /** Actualiza el texto transcrito si el usuario lo edita manualmente */
    data class TranscribedTextChanged(val text: String) : HomeEvent()
    
    /** Envía el texto para su clasificación */
    data object AnalyzeText : HomeEvent()
    
    /** Guarda el resultado del análisis en el historial */
    data object SaveAnalysis : HomeEvent()
}
