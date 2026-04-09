package com.example.deteccionviolencia.data.repository

import com.example.deteccionviolencia.data.local.dao.AnalysisDao
import com.example.deteccionviolencia.domain.model.Analysis
import kotlinx.coroutines.flow.Flow

/** Interfaz que define las operaciones para el historial de análisis */
interface HistoryRepository {
    /**
     * Almacena un nuevo análisis en la base de datos
     * @param analysis Objeto con los datos del análisis realizado
     */
    suspend fun saveAnalysis(analysis: Analysis)
    
    /**
     * Obtiene el flujo de análisis relacionados a un usuario
     * @param userId ID del usuario actual
     * @return Flow con la lista de análisis encontrados
     */
    fun getAnalysesForUser(userId: String): Flow<List<Analysis>>
    
    /**
     * Elimina un registro del historial
     * @param id Identificador del análisis
     */
    suspend fun deleteAnalysis(id: Long)
}

/**
 * Implementación del repositorio de historial utilizando Room
 * @property dao Objeto de acceso a datos para el análisis
 */
class RoomHistoryRepository(
    private val dao: AnalysisDao
) : HistoryRepository {
    override suspend fun saveAnalysis(analysis: Analysis) {
        dao.insert(analysis)
    }

    override fun getAnalysesForUser(userId: String): Flow<List<Analysis>> {
        return dao.getAnalysesForUser(userId)
    }

    override suspend fun deleteAnalysis(id: Long) {
        dao.deleteById(id)
    }
}
