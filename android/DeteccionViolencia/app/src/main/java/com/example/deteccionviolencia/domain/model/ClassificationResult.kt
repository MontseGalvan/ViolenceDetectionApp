package com.example.deteccionviolencia.domain.model

/**
 * Resultado de la clasificación de texto
 * @property isViolence Verdadero si el texto es clasificado como violento
 */
data class ClassificationResult(
    val isViolence: Boolean
)
