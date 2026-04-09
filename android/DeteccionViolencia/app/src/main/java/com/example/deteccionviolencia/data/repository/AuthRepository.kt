package com.example.deteccionviolencia.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Interfaz que define el contrato para los servicios de autenticación
 */
interface AuthRepository {
    /** Usuario actualmente autenticado en Firebase */
    val currentUser: FirebaseUser?
    
    /**
     * Inicia sesión con correo y contraseña
     * 
     * @param email Correo electrónico del usuario
     * @param password Contraseña de la cuenta
     * @return Resultado con el usuario de Firebase o una excepción en caso de fallo
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    
    /**
     * Registra un nuevo usuario en Firebase Auth
     * 
     * @param email Correo electrónico para la nueva cuenta
     * @param password Contraseña elegida
     * @return Resultado con el nuevo usuario o una excepción
     */
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    
    /** Cierra la sesión activa del usuario */
    fun logout()
}

/**
 * Implementación del repositorio de autenticación utilizando Firebase Auth
 * 
 * @property auth Instancia de FirebaseAuth inyectada mediante Hilt
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser? 
        get() = auth.currentUser

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Por favor, completa todos los campos"))
        }
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e)))
        }
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Por favor, completa todos los campos"))
        }
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e)))
        }
    }

    override fun logout() {
        auth.signOut()
    }

    /**
     * Traduce los códigos de error técnicos de Firebase a mensajes en español
     * 
     * @param e Excepción capturada durante el proceso de autenticación
     * @return Cadena de texto con el mensaje de error comprensible para el usuario
     */
    private fun mapFirebaseError(e: Exception): String {
        if (e !is FirebaseAuthException) {
            return e.localizedMessage ?: "Ocurrió un error inesperado"
        }

        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL", "invalid-email" -> "El formato del correo electrónico no es válido"
            "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL", "invalid-credential" -> "La contraseña es incorrecta"
            "ERROR_USER_NOT_FOUND", "user-not-found" -> "No existe una cuenta con este correo"
            "ERROR_USER_DISABLED", "user-disabled" -> "Esta cuenta ha sido deshabilitada"
            "ERROR_TOO_MANY_REQUESTS", "too-many-requests" -> "Demasiados intentos. Inténtalo más tarde"
            "ERROR_EMAIL_ALREADY_IN_USE", "email-already-in-use" -> "Este correo ya está registrado por otro usuario"
            "ERROR_WEAK_PASSWORD", "weak-password" -> "La contraseña es muy débil (mínimo 6 caracteres)"
            "ERROR_NETWORK_REQUEST_FAILED", "network-request-failed" -> "Error de red. Verifica tu conexión"
            "ERROR_USER_MISMATCH" -> "Las credenciales no coinciden con el usuario"
            "ERROR_INVALID_USER_TOKEN" -> "La sesión ha expirado. Inicia sesión de nuevo"
            else -> "Error de autenticación: ${e.localizedMessage}"
        }
    }
}
