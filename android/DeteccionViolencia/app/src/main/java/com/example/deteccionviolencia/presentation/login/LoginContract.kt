package com.example.deteccionviolencia.presentation.login

/**
 * Representa el estado de la interfaz de usuario para el inicio de sesión
 * 
 * @property email Correo electrónico introducido por el usuario
 * @property password Contraseña introducida por el usuario
 * @property isLoading Indica si se está realizando la validación con Firebase
 * @property isLoginSuccess Indica si el acceso fue autorizado correctamente
 * @property errorMessage Mensaje de error a mostrar en caso de fallo en las credenciales
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Eventos que pueden ser disparados desde la pantalla de login hacia su ViewModel
 */
sealed class LoginEvent {
    /** Actualiza el correo en el estado conforme el usuario escribe */
    data class EmailChanged(val email: String) : LoginEvent()
    
    /** Actualiza la contraseña en el estado conforme el usuario escribe */
    data class PasswordChanged(val password: String) : LoginEvent()
    
    /** Intenta realizar el inicio de sesión con los datos actuales */
    data object LoginClicked : LoginEvent()
}
