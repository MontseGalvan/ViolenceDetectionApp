package com.example.deteccionviolencia.domain.model

/**
 * Representa una institución o línea de ayuda
 * @property id Identificador del recurso
 * @property titulo Nombre de la institución
 * @property descripcion Información sobre los servicios que ofrece
 * @property telefono Número de contacto
 * @property email Correo electrónico de contacto
 * @property orden Posición en la lista
 */
data class HelpResource(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val telefono: String? = null,
    val email: String? = null,
    val orden: Int = 0
)
