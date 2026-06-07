package com.example.reportespc.nav

import android.app.Application
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportespc.data.entity.Reporte
import com.example.reportespc.data.repo.dao.ReporteDao
import com.example.reportespc.data.repo.ReporteRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

// Usamos AndroidViewModel para poder seguir teniendo acceso al contexto de la app (necesario para el PDF)
class ReporteViewModel(application: Application) : AndroidViewModel(application) {
    // Estado temporal para el reporte que se esta haciendo
    var idEquipoTemp = mutableStateOf("")
    var fallaTemp = mutableStateOf("")
    var imagenUriTemp = mutableStateOf<String?>(null)
    var fechaTemp = mutableStateOf("")

    // Conexión con Firebase
    private val reporteRepoCloud: ReporteDao = ReporteRepositoryImpl()
    val reportes: Flow<List<Reporte>> = reporteRepoCloud.obtenerTodosLosReportes()

    // Estados de carga encapsulados
    private val _isUploading = mutableStateOf(false)
    val isUploading: State<Boolean> = _isUploading
    private val _uploadMessage = mutableStateOf<String?>(null)
    val uploadMessage: State<String?> = _uploadMessage
    private val _guardadoExitoso = mutableStateOf(false)
    val guardadoExitoso: State<Boolean> = _guardadoExitoso

    fun enviarReporteAFirebase(fallaTexto: String, fechaTexto: String, uriTexto: String?) {

        // Validación de campos obligatorios
        if (fallaTexto.isBlank() || fechaTexto.isBlank()) {
            _uploadMessage.value = "Por favor, completa todos los campos obligatorios."
            return
        }

        viewModelScope.launch {
            _isUploading.value = true
            _guardadoExitoso.value = false
            _uploadMessage.value = "Subiendo reporte a la nube..."

            val nuevoReporte = Reporte(
                id = "", // Firebase le asignará su ID real al recibirlo
                idEquipo = "",
                falla = fallaTexto,
                fecha = fechaTexto,
                imageUri = uriTexto,
            )

            val resultado = reporteRepoCloud.guardarReporte(nuevoReporte)

            _isUploading.value = false
            resultado.onSuccess {
                _uploadMessage.value = "¡Reporte subido a Firestore con éxito!"
                _guardadoExitoso.value = true
                limpiarCamposFormulario()
            }.onFailure { error ->
                _guardadoExitoso.value = false
                _uploadMessage.value = "Error al subir: ${error.localizedMessage}"
            }
        }
    }

    // Función para activa el escáner flotante de Google Play Services
    fun escanearCodigo() {
        // Configuración para el escáner
        val opciones = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAutoZoom()
            .build()

        // Inicializamos el cliente del escáner usando el contexto
        val scanner = GmsBarcodeScanning.getClient(getApplication(), opciones)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val coidgoDetectado = barcode.rawValue
                if (!coidgoDetectado.isNullOrBlank()) {
                    // Colocamos el valor directamente en la variable reactiva
                    idEquipoTemp.value = coidgoDetectado
                }
            }
            .addOnFailureListener { exception ->
                // Aquí se puede manejar errores en caso de ocurrir.
            }
    }

    fun limpiarCamposFormulario() {
        idEquipoTemp.value = ""
        fallaTemp.value = ""
        imagenUriTemp.value = null
        fechaTemp.value = ""
    }

    fun exportarAPdf(listaReportes: List<Reporte>) {
        if (listaReportes.isEmpty()) {
            Toast.makeText(getApplication(), "No hay reportes para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            // ---- DISEÑO EJECUTIVO ----
            val azulPrimario = Color.rgb(26, 54, 93)
            val grisFondo = Color.rgb(247, 250, 252)
            val grisBorde = Color.rgb(226, 232, 240)
            val textoOscuro = Color.rgb(45, 55, 72)
            val textoMutado = Color.rgb(113, 128, 150)

            // Encabezado
            paint.color = azulPrimario
            canvas.drawRect(0f, 0f, 595f, 15f, paint)

            paint.textSize = 20f
            paint.isFakeBoldText = true
            paint.color = azulPrimario
            paint.isAntiAlias = true
            canvas.drawText("REPORTE DE INCIDENCIAS TECNOLÓGICAS", 45f, 55f, paint)

            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.color = textoMutado
            canvas.drawText("Resumen automatizado de equipos de cómputo", 45f, 72f, paint)

            paint.color = grisBorde
            paint.strokeWidth = 1.5f
            canvas.drawLine(45f, 85f, 550f, 85f, paint)

            // Cuerpo (Tarjetas)
            var yPosition = 110f

            for (reporte in listaReportes) {
                if (yPosition > 720f) {
                    paint.textSize = 10f
                    paint.color = textoMutado
                    canvas.drawText("[... Lista truncada por espacio de página ...]", 45f, 780f, paint)
                    break
                }

                paint.color = grisFondo
                paint.style = Paint.Style.FILL
                val tarjetaRect = RectF(45f, yPosition, 550f, yPosition + 75f)
                canvas.drawRoundRect(tarjetaRect, 8f, 8f, paint)

                paint.color = grisBorde
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1f
                canvas.drawRoundRect(tarjetaRect, 8f, 8f, paint)

                paint.style = Paint.Style.FILL

                // Datos del Reporte
                paint.textSize = 12f
                paint.isFakeBoldText = true
                paint.color = azulPrimario
                canvas.drawText("ID EQUIPO: ${reporte.idEquipo}", 60f, yPosition + 25f, paint)

                paint.textSize = 10f
                paint.isFakeBoldText = false
                paint.color = textoMutado
                canvas.drawText("Fecha: ${reporte.fecha}", 420f, yPosition + 23f, paint)

                paint.color = grisBorde
                canvas.drawLine(60f, yPosition + 35f, 535f, yPosition + 35f, paint)

                paint.textSize = 11f
                paint.color = textoOscuro
                val fallaCorta = if (reporte.falla.length > 75) reporte.falla.take(72) + "..." else reporte.falla
                canvas.drawText("Descripción: $fallaCorta", 60f, yPosition + 55f, paint)

                yPosition += 90f
            }

            // Pie de Página
            paint.color = grisBorde
            canvas.drawLine(45f, 795f, 550f, 795f, paint)
            paint.textSize = 9f
            paint.color = textoMutado
            canvas.drawText("Generado desde ReportesPC App", 45f, 812f, paint)

            pdfDocument.finishPage(page)

            // ---- ESCRITURA SEGURA EN CACHÉ DE LA APP ----
            val context = getApplication<Application>().applicationContext
            val carpetaPdf = File(context.cacheDir, "documentos")
            if (!carpetaPdf.exists()) carpetaPdf.mkdirs()

            val archivoPdf = File(carpetaPdf, "Reporte_Dispositivos.pdf")

            val outputStream = FileOutputStream(archivoPdf)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // ---- APERTURA INMEDIATA ----
            val uriPdf = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // 👈 Vinculado exactamente a tu XML con .provider
                archivoPdf
            )

            val intentVisualizar = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uriPdf, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intentVisualizar)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                getApplication(),
                "Error controlado al generar el PDF: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}