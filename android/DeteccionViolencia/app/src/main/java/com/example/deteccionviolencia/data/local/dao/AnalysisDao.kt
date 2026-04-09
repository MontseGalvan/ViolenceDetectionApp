package com.example.deteccionviolencia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.deteccionviolencia.domain.model.Analysis
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de Acceso a Datos (DAO) para la entidad Analysis
 */
@Dao
interface AnalysisDao {
    /**
     * Almacena un nuevo resultado de análisis en la base de datos local
     * @param analysis Objeto que contiene los datos del análisis
     */
    @Insert
    suspend fun insert(analysis: Analysis)

    /**
     * Recupera todos los análisis realizados por un usuario
     * @param userId Identificador del usuario
     * @return Flujo de lista de análisis ordenados por fecha
     */
    @Query("SELECT * FROM analyses WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAnalysesForUser(userId: String): Flow<List<Analysis>>

    /**
     * Elimina un registro de análisis de la base de datos
     * @param id Identificador del análisis a borrar
     */
    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
