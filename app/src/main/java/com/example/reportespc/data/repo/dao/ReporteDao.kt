package com.example.reportespc.data.repo.dao

import com.example.reportespc.data.entity.Reporte
import kotlinx.coroutines.flow.Flow

interface ReporteDao {
    // Guardar reporte de manera suspendida devolviendo el éxito o fallo de la nube
    suspend fun guardarReporte(reporte: Reporte): Result<Unit>

    // Obtener reportes en tiempo real para pintar la lista
    fun obtenerTodosLosReportes(): Flow<List<Reporte>>
}