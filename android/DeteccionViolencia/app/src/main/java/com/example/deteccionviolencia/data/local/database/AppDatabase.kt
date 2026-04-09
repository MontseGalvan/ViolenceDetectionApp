package com.example.deteccionviolencia.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.deteccionviolencia.data.local.dao.AnalysisDao
import com.example.deteccionviolencia.domain.model.Analysis

/**
 * Punto de acceso a la base de datos local mediante Room
 */
@Database(entities = [Analysis::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Proporciona el objeto de acceso a datos para el análisis
     * @return Instancia del DAO de análisis
     */
    abstract fun analysisDao(): AnalysisDao
}
