package com.example.deteccionviolencia.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Resultado de un análisis de detección de violencia
 * @property id Identificador único autogenerado
 * @property userId ID del usuario propietario del registro
 * @property transcribedText Texto transcrito
 * @property isViolence Indica si se detectó violencia
 * @property timestamp Fecha y hora de creación
 */
@Parcelize
@Entity(tableName = "analyses")
data class Analysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val transcribedText: String,
    val isViolence: Boolean,
    val timestamp: Long = System.currentTimeMillis()
): Parcelable
