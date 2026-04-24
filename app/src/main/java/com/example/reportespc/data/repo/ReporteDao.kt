package com.example.reportespc.data.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reportespc.data.entity.Reporte
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {
    @Insert
    fun insertarReporte(reporte: Reporte)
    @Query("SELECT * FROM reportes ORDER BY id DESC")
    fun obtenerTodosLosReportes(): Flow<List<Reporte>>
}