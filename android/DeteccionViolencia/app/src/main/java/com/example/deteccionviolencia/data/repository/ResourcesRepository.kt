package com.example.deteccionviolencia.data.repository

import com.example.deteccionviolencia.domain.model.HelpResource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Interfaz para la obtención de información de apoyo
 */
interface ResourcesRepository {
    /**
     * Proporciona un flujo de datos con la lista de recursos de ayuda actualizados
     * @return Flow con la lista de HelpResource ordenados por el campo orden
     */
    fun getResourcesFlow(): Flow<List<HelpResource>>
}

/**
 * Implementación del repositorio de recursos utilizando Firestore
 * @property firestore Instancia de la base de datos Firestore
 */
class FirestoreResourcesRepository(
    private val firestore: FirebaseFirestore
) : ResourcesRepository {

    /** Referencia a la colección de recursos en Firestore */
    private val collection = firestore.collection("Recursos_ayuda")

    override fun getResourcesFlow(): Flow<List<HelpResource>> {
        return collection.orderBy("orden").snapshots().map { snapshot ->
            snapshot.documents.map { mapToHelpResource(it) }
        }
    }

    /**
     * Mapea un documento de Firestore a un objeto HelpResource
     * @param doc Documento de Firestore
     * @return Objeto HelpResource
     */
    private fun mapToHelpResource(doc: DocumentSnapshot): HelpResource {
        return HelpResource(
            id = doc.id,
            titulo = doc.getString("titulo") ?: "",
            descripcion = doc.getString("descripcion") ?: "",
            telefono = doc.getString("telefono"),
            email = doc.getString("email"),
            orden = when (val value = doc.get("orden")) {
                is Number -> value.toInt()
                is String -> value.toIntOrNull() ?: 0
                else -> 0
            }
        )
    }
}
