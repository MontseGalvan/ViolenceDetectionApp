package com.example.deteccionviolencia.presentation.home

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deteccionviolencia.data.audio.AudioRecorder
import com.example.deteccionviolencia.domain.model.Analysis
import com.example.deteccionviolencia.data.repository.AuthRepository
import com.example.deteccionviolencia.data.repository.ClassificationRepository
import com.example.deteccionviolencia.data.repository.HistoryRepository
import com.example.deteccionviolencia.data.repository.ProfileRepository
import com.example.deteccionviolencia.data.repository.TranscriptionRepository
import com.example.deteccionviolencia.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * ViewModel encargado de la lógica de la pantalla de inicio, desde el proceso de
 * grabación a clasificación
 * 
 * @property audioRecorder Componente para capturar audio del micrófono
 * @property transcriptionRepository Repositorio para convertir audio a texto
 * @property classificationRepository Repositorio para detectar violencia en el texto
 * @property historyRepository Repositorio para persistir los resultados
 * @property profileRepository Repositorio para gestionar el perfil del usuario
 * @property authRepository Repositorio para el control de autenticación
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val transcriptionRepository: TranscriptionRepository,
    private val classificationRepository: ClassificationRepository,
    private val historyRepository: HistoryRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Estado interno de la pantalla de inicio */
    private val _state = MutableStateFlow(HomeState())
    
    /** Estado público de la pantalla de inicio para la UI */
    val state: StateFlow<HomeState> = _state.asStateFlow()

    /** Estado interno del perfil del usuario */
    private val _userProfile = MutableStateFlow(UserProfile())
    
    /** Perfil del usuario para mostrar en la interfaz */
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    /** Inicialización del ViewModel que carga los datos del usuario */
    init {
        loadUserProfile()
    }

    /** Carga la información del perfil del usuario desde el repositorio */
    private fun loadUserProfile() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val profile = profileRepository.getProfile(userId)
                if (profile != null) {
                    _userProfile.value = profile
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al cargar el nombre del usuario", e)
            }
        }
    }

    /**
     * Gestiona los eventos disparados desde la pantalla de inicio
     * @param event El evento a procesar
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.StartRecording -> startRecording()
            HomeEvent.StopRecording -> stopRecording()
            is HomeEvent.TranscribedTextChanged -> {
                _state.value = _state.value.copy(transcribedText = event.text)
            }
            HomeEvent.AnalyzeText -> analyzeText()
            HomeEvent.SaveAnalysis -> saveCurrentAnalysis()
        }
    }

    /** Inicia el proceso de captura de audio */
    private fun startRecording() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isRecording = true,
                    errorMessage = null,
                    transcribedText = "",
                    classificationResult = null,
                    isSaved = false
                )
                audioRecorder.startRecording()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRecording = false,
                    errorMessage = "Error al iniciar: ${e.message}"
                )
            }
        }
    }

    /** Finaliza la captura de audio y comienza el flujo de procesamiento */
    private fun stopRecording() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isRecording = false, isProcessing = true)
                val file = audioRecorder.stopRecording()
                val duration = getAudioDuration(file)
                _state.value = _state.value.copy(
                    audioFile = file,
                    durationMs = duration,
                    isProcessing = false
                )
                transcribeAudio()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isProcessing = false,
                    errorMessage = "Error al detener audio: ${e.message}"
                )
            }
        }
    }

    /** Envía el archivo de audio al repositorio de transcripción para convertirlo a texto */
    private fun transcribeAudio() {
        val file = _state.value.audioFile
        if (file == null) {
            _state.value = _state.value.copy(
                errorMessage = "No hay archivo de audio para transcribir",
                isProcessing = false
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true)
            try {
                val text = transcriptionRepository.transcribe(file)
                _state.value = _state.value.copy(
                    transcribedText = text.trim(),
                    isProcessing = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e("WhisperCpp", "Error al transcribir", e)
                _state.value = _state.value.copy(
                    errorMessage = "Error al transcribir: ${e.message}",
                    isProcessing = false
                )
            }
        }
    }

    /** Analiza el texto transcrito para clasificarlo */
    private fun analyzeText() {
        val currentText = _state.value.transcribedText
        if (currentText.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isProcessing = true,
                classificationResult = null,
                errorMessage = null
            )
            try {
                val result = classificationRepository.classify(currentText.trim())
                _state.value = _state.value.copy(
                    classificationResult = result,
                    isProcessing = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Error al analizar: ${e.message}",
                    isProcessing = false
                )
            }
        }
    }

    /** Almacena el análisis actual en el historial local del usuario */
    private fun saveCurrentAnalysis() {
        if (_state.value.isSaved) return
        
        val currentResult = _state.value.classificationResult ?: return
        val currentText = _state.value.transcribedText
        if (currentText.isBlank()) return

        viewModelScope.launch {
            try {
                val userId = authRepository.currentUser?.uid ?: return@launch
                val analysis = Analysis(
                    userId = userId,
                    transcribedText = currentText,
                    isViolence = currentResult.isViolence,
                    timestamp = System.currentTimeMillis()
                )
                historyRepository.saveAnalysis(analysis)
                _state.value = _state.value.copy(
                    isSaved = true,
                    errorMessage = "Guardado en historial"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Error al guardar: ${e.message}")
            }
        }
    }

    /**
     * Obtiene la duración del archivo de audio en milisegundos
     * @param file El archivo de audio a analizar
     * @return Duración en milisegundos
     */
    private suspend fun getAudioDuration(file: File): Long = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLong() ?: 0
        } catch (e: Exception) {
            0
        } finally {
            try { retriever.release() } catch (e: Exception) {}
        }
    }

    /** Reinicia el estado de la pantalla a sus valores por defecto */
    fun clearState() {
        _state.value = HomeState()
    }
}
