package com.example.deteccionviolencia.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

/**
 * Modelo de datos para la petición de clasificación de texto
 * @property texto El texto que será analizado
 */
data class ClassificationRequest(val texto: String)

/**
 * Modelo de datos para la respuesta del servicio de clasificación
 * @property isViolence Indica si se detectaron indicadores de violencia
 */
data class ClassificationResponse(val isViolence: Boolean)

/**
 * Interfaz de Retrofit que define los endpoints del servicio web de clasificación
 */
interface ClassificationApi {
    /**
     * Envía un texto al servidor para su análisis de riesgo
     * @param request Objeto que contiene el texto a clasificar
     * @return Respuesta del servidor con el resultado del análisis
     */
    @Headers("Cache-Control: no-cache")
    @POST("classify")
    suspend fun classify(@Body request: ClassificationRequest): ClassificationResponse
}
