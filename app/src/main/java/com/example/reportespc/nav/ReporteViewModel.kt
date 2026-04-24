package com.example.reportespc.nav

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportespc.data.ReporteDatabase
import com.example.reportespc.data.entity.Reporte
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReporteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ReporteDatabase.getDatabase(application).reporteDao()

    // Lista reactiva de reportes
    val reportes = dao.obtenerTodosLosReportes()

    // Estado temporal para el reporte que se está creando
    var idEquipoTemp = mutableStateOf("")
    var fallaTemp = mutableStateOf("")
    var imageUriTemp = mutableStateOf<String?>(null)

    fun guardarReporte() {
        viewModelScope.launch {
            val uri = imageUriTemp.value
            if (idEquipoTemp.value.isNotBlank() && fallaTemp.value.isNotBlank() && uri != null) {
                val nuevoReporte = Reporte(
                    idEquipo = idEquipoTemp.value,
                    falla = fallaTemp.value,
                    imageUri = uri
                )
                withContext(Dispatchers.IO) {
                    dao.insertarReporte(nuevoReporte)
                }
                limpiarFormulario()
            }
        }
    }

    fun limpiarFormulario() {
        idEquipoTemp.value = ""
        fallaTemp.value = ""
        imageUriTemp.value = null
    }
}