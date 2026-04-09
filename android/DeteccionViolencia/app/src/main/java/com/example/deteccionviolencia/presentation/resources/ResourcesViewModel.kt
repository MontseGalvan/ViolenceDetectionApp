package com.example.deteccionviolencia.presentation.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deteccionviolencia.data.repository.ResourcesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsable de la lógica de la pantalla de recursos de ayuda
 * @property repository Repositorio para la obtención de recursos de ayuda
 */
@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val repository: ResourcesRepository
) : ViewModel() {

    /** Estado interno de la pantalla de recursos */
    private val _state = MutableStateFlow(ResourcesState())
    
    /** Estado público observado por la UI para mostrar la lista de centros de apoyo */
    val state: StateFlow<ResourcesState> = _state.asStateFlow()

    /** Inicialización de la carga de recursos */
    init {
        loadResources()
    }

    /** Se subscribe al flujo de recursos de ayuda desde el repositorio */
    fun loadResources() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                repository.getResourcesFlow().collect { resources ->
                    _state.value = _state.value.copy(
                        helpResources = resources,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar recursos"
                )
            }
        }
    }
}
