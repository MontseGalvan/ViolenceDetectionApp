package com.example.deteccionviolencia.data.repository

import com.example.deteccionviolencia.data.remote.api.ClassificationApi
import com.example.deteccionviolencia.data.remote.api.ClassificationRequest
import com.example.deteccionviolencia.domain.model.ClassificationResult

/**
 * Interfaz para la detección de violencia
 */
interface ClassificationRepository {
    /**
     * Envía un texto al motor de clasificación
     * @param text El texto a analizar
     * @return Resultado booleano de la clasificación
     */
    suspend fun classify(text: String): ClassificationResult
}

/**
 * Implementación del repositorio de clasificación
 * @property api Interfaz de Retrofit para realizar peticiones HTTP
 */
class RemoteClassificationRepository(
    private val api: ClassificationApi
) : ClassificationRepository {
    override suspend fun classify(text: String): ClassificationResult {
        /** Realiza la llamada a la API externa enviando el texto envuelto en un objeto de petición */
        val response = api.classify(ClassificationRequest(text))
        /** Transforma la respuesta DTO de la API en un ClassificationResult */
        return ClassificationResult(response.isViolence)
    }
}
