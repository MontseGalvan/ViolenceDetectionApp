package com.example.deteccionviolencia.presentation.resources

import com.example.deteccionviolencia.domain.model.HelpResource

/**
 * Representa el estado de la interfaz de usuario para la pantalla de recursos de ayuda
 * @property helpResources Lista de instituciones y líneas de ayuda obtenidas de Firestore
 * @property isLoading Indica si se está realizando la carga de datos
 * @property error Mensaje de error en caso de que la descarga de recursos falle
 */
data class ResourcesState(
    val helpResources: List<HelpResource> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
