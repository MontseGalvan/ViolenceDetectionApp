package com.example.deteccionviolencia.data.repository

import android.util.Log
import com.example.deteccionviolencia.domain.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Interfaz para la gestión de perfiles de usuario */
interface ProfileRepository {
    /**
     * Obtiene los datos del perfil
     * @param userId Identificador único del usuario
     * @return Perfil del usuario o null si no existe o hay error
     */
    suspend fun getProfile(userId: String): UserProfile?
    
    /**
     * Almacena o actualiza la información del perfil
     * @param userId Identificador único del usuario
     * @param profile Objeto con los datos actualizados del perfil
     */
    suspend fun saveProfile(userId: String, profile: UserProfile)
}

/**
 * Implementación del repositorio utilizando Firestore
 * @property firestore Instancia de la base de datos Firestore
 */
class FirestoreProfileRepository(
    private val firestore: FirebaseFirestore
) : ProfileRepository {
    /** Referencia a la colección de usuarios en Firestore */
    private val collection = firestore.collection("usuarios")

    override suspend fun getProfile(userId: String): UserProfile? {
        return try {
            val document = collection.document(userId).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error al obtener perfil", e)
            null
        }
    }

    override suspend fun saveProfile(userId: String, profile: UserProfile) {
        try {
            collection.document(userId).set(profile).await()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error al guardar perfil", e)
            throw e
        }
    }
}
