package com.example.deteccionviolencia.presentation.login

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
 * ViewModel encargado de validar las credenciales y gestionar el estado de la sesión activa del usuario
 * 
 * @property authRepository Repositorio para la gestión de autenticación con Firebase
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Estado interno de la pantalla de inicio de sesión
     */
    private val _state = MutableStateFlow(LoginState())
    
    /**
     * Estado público observado por la UI para reaccionar a cambios en el formulario o errores
     */
    val state: StateFlow<LoginState> = _state.asStateFlow()

    /**
     * Gestiona las interacciones del usuario en la pantalla de login
     * 
     * @param event El evento disparado desde la interfaz de usuario
     */
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            LoginEvent.LoginClicked -> {
                login()
            }
        }
    }

    /**
     * Ejecuta el proceso de autenticación de forma asíncrona
     */
    private fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, isLoginSuccess = false)
            val result = authRepository.login(_state.value.email, _state.value.password)
            _state.value = if (result.isSuccess) {
                _state.value.copy(isLoading = false, isLoginSuccess = true)
            } else {
                _state.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }
}
