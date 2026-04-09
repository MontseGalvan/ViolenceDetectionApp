package com.example.deteccionviolencia.presentation.profile

import com.example.deteccionviolencia.domain.model.UserProfile

/**
 * Representa el estado de la interfaz de usuario para la pantalla de perfil
 * @property isLoading Indica si se están cargando los datos desde Firestore
 * @property isEditing Indica si el usuario está en modo edición
 * @property profile Información del perfil que se muestra en los campos
 * @property originalProfile Respaldo de la información original para permitir cancelar cambios
 * @property email Correo electrónico del usuario
 * @property errorMessage Mensaje de error a mostrar
 * @property saveSuccess Indica si los cambios se guardaron correctamente
 */
data class ProfileState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val profile: UserProfile = UserProfile(),
    val originalProfile: UserProfile = UserProfile(),
    val email: String = "",
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

/** Eventos que pueden ser disparados desde la pantalla hacia su ViewModel */
sealed class ProfileEvent {
    /** Solicita la carga inicial de los datos del perfil */
    data object LoadProfile : ProfileEvent()
    
    /** Cambia entre el modo visualización y el modo edición */
    data object ToggleEditMode : ProfileEvent()
    
    /** Envía los cambios realizados a Firestore */
    data object SaveProfile : ProfileEvent()
    
    /** Descarta los cambios y vuelve a la información original */
    data object CancelEdit : ProfileEvent()
    
    /** Actualiza un campo específico del perfil en el estado temporal */
    data class UpdateField(val field: String, val value: String) : ProfileEvent()
    
    /** Cierra la sesión */
    data object Logout : ProfileEvent()
    
    /** Limpia los mensajes de error o éxito */
    data object DismissError : ProfileEvent()
}
