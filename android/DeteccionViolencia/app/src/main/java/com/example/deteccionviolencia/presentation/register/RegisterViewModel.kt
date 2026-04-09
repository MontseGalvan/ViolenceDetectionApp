package com.example.deteccionviolencia.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deteccionviolencia.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado de la lógica de registro de nuevos usuarios
 * @property authRepository Repositorio para la gestión de autenticación con Firebase
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Estado interno de la pantalla de registro
     */
    private val _state = MutableStateFlow(RegisterState())
    
    /**
     * Estado público observado por la UI para reaccionar a cambios en el formulario o errores
     */
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    /**
     * Gestiona las interacciones del usuario en la pantalla de registro
     * @param event El evento disparado desde la interfaz de usuario
     */
    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is RegisterEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword = event.confirmPassword)
            }
            RegisterEvent.RegisterClicked -> {
                register()
            }
        }
    }

    /**
     * Ejecuta el proceso de registro validando que las contraseñas coincidan
     */
    private fun register() {
        val currentState = _state.value
        if (currentState.password != currentState.confirmPassword) {
            _state.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, errorMessage = null, isRegisterSuccess = false)

            val result = authRepository.register(currentState.email, currentState.password)

            if (result.isSuccess) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRegisterSuccess = true
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al registrarse"
                )
            }
        }
    }
}
