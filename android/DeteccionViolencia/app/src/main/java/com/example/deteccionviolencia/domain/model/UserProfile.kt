package com.example.deteccionviolencia.domain.model

/**
 * Información del perfil personal del usuario
 * @property nombre Nombre o alias
 * @property edad Edad del usuario
 * @property sexo Sexo biológico
 * @property genero Identidad de género
 * @property relacionUAM Vínculo con la UAM
 */
data class UserProfile(
    val nombre: String = "",
    val edad: String = "",
    val sexo: String = "",
    val genero: String = "",
    val relacionUAM: String = ""
)
