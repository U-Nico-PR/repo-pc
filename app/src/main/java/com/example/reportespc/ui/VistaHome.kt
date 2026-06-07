package com.example.reportespc.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportespc.nav.ReporteViewModel
import com.google.firebase.firestore.util.Util
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaHome(viewModel: ReporteViewModel, navigateToCamara: () -> Unit, navigateToLista: () -> Unit) {

    // Control de validación local de errores en la vista
    var fallaError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }

    // Escucha de estados encapsulados del ViewModel
    val isUploading by viewModel.isUploading
    val uploadMessage by viewModel.uploadMessage

    // Configuración para el componente DatePicker (Calendario)
    val datePickerState = rememberDatePickerState()
    var mostrarDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Generar Reporte", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(30.dp))

        // Campo 1: Descripción detallada
        OutlinedTextField(
            value = viewModel.fallaTemp.value,
            onValueChange = {
                viewModel.fallaTemp.value = it
                if (it.isNotBlank()) fallaError = false
            },
            label = { Text("Descripción detallada de falla") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            enabled = !isUploading,
            isError = fallaError,
            supportingText = {
                if (fallaError) {
                    Text("La descripción de la falla no debe estar vacía", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo 2: Campo de texto fecha seleccionable
        OutlinedTextField(
            value = viewModel.fechaTemp.value,
            onValueChange = { },
            label = { Text("Fecha del Reporte") },
            placeholder = { Text("Selecciona la fecha del incidente") },
            readOnly = true,
            enabled = !isUploading,
            isError = fechaError,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { if (!isUploading) mostrarDatePicker = true} ) {
                    Text("📅", fontSize = 18.sp)
                }
            },
            supportingText = {
                if (fechaError) {
                    Text("Debes seleccionar una fecha obligatoriamente", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // Feedback visual de Firebase: Círculo de progreso
        if (isUploading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Mensaje dinámico (Subiendo / Éxito / Error)
        uploadMessage?.let { mensaje ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mensaje,
                color = if (mensaje.contains("¡Reporte")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Boton Cámara
        Button(
            onClick = { navigateToCamara() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isUploading
        ) {
            Text("Tomar Foto del Equipo")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Boton subir a Firestore con paso de parámetros reales
        Button(
            onClick = {
                val fallaActual = viewModel.fallaTemp.value
                val fechaActual = viewModel.fechaTemp.value

                // Validación de los campos antes de envíar
                if (fallaActual.isBlank()) fallaError = true
                if (fechaActual.isBlank()) fechaError = true

                if (fechaActual.isNotBlank() && fallaActual.isNotBlank()) {
                    viewModel.enviarReporteAFirebase(
                        fallaTexto = fallaActual,
                        fechaTexto = fechaActual,
                        uriTexto = viewModel.imagenUriTemp.value
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isUploading
        ) {
            Text("Subir Reporte a la Nube")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para navegar al Listado de Reportes
        OutlinedButton(
            onClick = { navigateToLista() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isUploading
        ) {
            Text("Ver Reportes guardados")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Dialogo flotante del calendario
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val fechaSeleccionada = datePickerState.selectedDateMillis
                    if (fechaSeleccionada != null) {
                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            timeZone = java.util.TimeZone.getTimeZone("UTC")
                        }
                        viewModel.fechaTemp.value = formato.format(Date(fechaSeleccionada))
                        fechaError = false
                    }
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
