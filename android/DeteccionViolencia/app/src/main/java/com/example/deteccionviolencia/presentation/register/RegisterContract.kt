package com.example.deteccionviolencia.presentation.register

/**
 * Representa el estado de la interfaz de usuario para el registro de nuevos usuarios
 * 
 * @property email Correo electrónico proporcionado por el usuario
 * @property password Contraseña elegida por el usuario
 * @property confirmPassword Confirmación de la contraseña para validación
 * @property isLoading Indica si el proceso de creación de cuenta está activo
 * @property errorMessage Mensaje de error en caso de fallo en el registro
 * @property isRegisterSuccess Indica si la cuenta fue creada exitosamente en Firebase
 */
data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)

/**
 * Eventos que pueden ser disparados desde la pantalla de registro hacia su ViewModel
 */
sealed class RegisterEvent {
    /** Actualiza el correo en el estado conforme el usuario escribe */
    data class EmailChanged(val email: String) : RegisterEvent()
    
    /** Actualiza la contraseña en el estado conforme el usuario escribe */
    data class PasswordChanged(val password: String) : RegisterEvent()
    
    /** Actualiza la confirmación de contraseña en el estado conforme el usuario escribe */
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    
    /** Intenta realizar el registro de la nueva cuenta con los datos actuales */
    data object RegisterClicked : RegisterEvent()
}
