package com.example.deteccionviolencia.presentation.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deteccionviolencia.data.repository.AuthRepository
import com.example.deteccionviolencia.data.repository.HistoryRepository
import com.example.deteccionviolencia.domain.model.Analysis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que contiene la lógica de la pantalla de historial
 * @property historyRepository Repositorio para acceder a los análisis guardados
 * @property authRepository Repositorio para obtener la información del usuario actual
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /**  Estado interno de la pantalla de historial */
    private val _uiState = MutableStateFlow(HistoryUiState())
    
    /** Estado público para ser observado por la interfaz de usuario */
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    /** Inicialización que carga los análisis del usuario */
    init {
        loadAnalyses()
    }

    /**
     * Se subscribe al flujo de análisis del usuario actual desde la base de datos
     */
    private fun loadAnalyses() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            historyRepository.getAnalysesForUser(userId).collect { analyses ->
                _uiState.value = _uiState.value.copy(
                    analyses = analyses,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Elimina un análisis del historial
     * @param id Identificador del análisis a borrar
     */
    fun deleteAnalysis(id: Long) {
        viewModelScope.launch {
            try {
                historyRepository.deleteAnalysis(id)
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error al eliminar el análisis: $e")
            }
        }
    }
}

/**
 * Representa el estado de la interfaz de usuario para el historial
 * @property analyses Lista de análisis realizados
 * @property isLoading Indica si los datos están siendo cargados
 */
data class HistoryUiState(
    val analyses: List<Analysis> = emptyList(),
    val isLoading: Boolean = true
)
