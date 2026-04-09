package com.example.deteccionviolencia.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deteccionviolencia.data.repository.AuthRepository
import com.example.deteccionviolencia.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsable de la lógica de la pantalla de perfil
 * @property profileRepository Repositorio para la persistencia de datos del perfil
 * @property authRepository Repositorio para obtener la identidad del usuario actual
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Estado interno de la pantalla de perfil */
    private val _state = MutableStateFlow(ProfileState())
    
    /** Estado público observado por la UI para mostrar datos y manejar el modo edición */
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    /** Gestiona los eventos en la pantalla de perfil
     * @param event El evento disparado por el usuario
     */
    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.ToggleEditMode -> {
                _state.value = _state.value.copy(
                    isEditing = !_state.value.isEditing,
                    profile = if (!_state.value.isEditing) _state.value.profile else _state.value.originalProfile
                )
            }
            ProfileEvent.SaveProfile -> saveProfile()
            ProfileEvent.CancelEdit -> cancelEdit()
            is ProfileEvent.UpdateField -> updateField(event.field, event.value)
            ProfileEvent.Logout -> logout()
            ProfileEvent.DismissError -> dismissError()
        }
    }

    /** Recupera la información del perfil de Firestore usando el ID  */
    private fun loadProfile() {
        val user = authRepository.currentUser
        val userId = user?.uid ?: return
        val userEmail = user.email ?: ""
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, email = userEmail)
            try {
                val profile = profileRepository.getProfile(userId)
                if (profile != null) {
                    _state.value = _state.value.copy(
                        profile = profile,
                        originalProfile = profile,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isEditing = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar perfil: Verifica tu conexión a internet"
                )
            }
        }
    }

    /** Valida y guarda los cambios en el perfil */
    private fun saveProfile() {
        val currentProfile = _state.value.profile

        val ageNum = currentProfile.edad.toIntOrNull()
        if (currentProfile.edad.isNotBlank() && (ageNum == null || ageNum !in 0..120)) {
            _state.value = _state.value.copy(errorMessage = "Por favor ingrese una edad válida (0-120)")
            return
        }

        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                profileRepository.saveProfile(userId, currentProfile)
                _state.value = _state.value.copy(
                    isLoading = false,
                    isEditing = false,
                    originalProfile = currentProfile,
                    saveSuccess = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar: No se pudo actualizar la información"
                )
            }
        }
    }

    /** Descarta los cambios realizados y vuelve al estado original del perfil */
    private fun cancelEdit() {
        _state.value = _state.value.copy(
            isEditing = false,
            profile = _state.value.originalProfile
        )
    }

    /**
     * Actualiza un campo específico del perfil en el estado temporal
     * @param field Nombre del campo a actualizar
     * @param value Nuevo valor para el campo
     */
    private fun updateField(field: String, value: String) {
        val currentProfile = _state.value.profile
        val updatedProfile = when (field) {
            "name" -> currentProfile.copy(nombre = value)
            "age" -> {
                val digitsOnly = value.filter { it.isDigit() }.take(3)
                currentProfile.copy(edad = digitsOnly)
            }
            "sex" -> currentProfile.copy(sexo = value)
            "gender" -> currentProfile.copy(genero = value)
            "uamRelationship" -> currentProfile.copy(relacionUAM = value)
            else -> currentProfile
        }
        _state.value = _state.value.copy(profile = updatedProfile)
    }

    /** Cierra la sesión del usuario actual */
    private fun logout() {
        authRepository.logout()
    }

    /** Limpia los mensajes de error o éxito del estado */
    private fun dismissError() {
        _state.value = _state.value.copy(errorMessage = null, saveSuccess = false)
    }
}
