package com.example.reportespc.data.repo

import com.example.reportespc.data.entity.Reporte
import com.example.reportespc.data.repo.dao.ReporteDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Segun se tiene que actualizar este para la conexión con Firebase
class ReporteRepositoryImpl(
    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
): ReporteDao {
    override suspend fun guardarReporte(reporte: Reporte): Result<Unit> {
        return try {
            // 1. Generamos referencia de documento vacía para obtener su ID aleatorio único de Firebase
            val documentoRef = firebase.collection("reportes").document()

            // 2. Inyectamos ese ID real de la nube dentro de las propiedades del reporte
            val reporteConIdReal = reporte.copy(id = documentoRef.id)

            // 3. Subimos el objeto completo y esperamos con .await() la respuesta de red
            documentoRef.set(reporteConIdReal).await()
            //documentoRef.set(reporte).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e) // Si falla el internet o las reglas de Firebase, devuelve el error de forma segura
        }
    }

    override fun obtenerTodosLosReportes(): Flow<List<Reporte>> {
        return firebase.collection("reportes")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Reporte::class.java)
            }
    }
}