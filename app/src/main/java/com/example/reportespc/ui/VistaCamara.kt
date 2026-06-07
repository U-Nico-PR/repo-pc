package com.example.reportespc.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.reportespc.nav.ReporteViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VistaCamara(viewModel: ReporteViewModel, navigateBack: () -> Unit) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Obtenemos la uri
    val imagenSeleccionada = viewModel.imagenUriTemp.value.toString()

    // Laucher de camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            viewModel.imagenUriTemp.value = tempUri.toString()
        }
    }

    // Laucher de galeria
    val galleryLuncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
           try {
               context.contentResolver.takePersistableUriPermission(
                   it,
                   Intent.FLAG_GRANT_READ_URI_PERMISSION
               )
           } catch (e: Exception) {

           }
        viewModel.imagenUriTemp.value = it.toString()
       }
    }

    // Permiso de camara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = crearArchivo(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            println("Permiso de cámara denegado")
        }
    }

    // UI Optimización con scroll vertical automático
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        if (imagenSeleccionada.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(imagenSeleccionada),
                contentDescription = "Foto del reporte",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 20.dp)
            )
            Text("Imagen lista para el reporte")
        } else {
            Text("Aún no hay imagen asignada", modifier = Modifier.padding(bottom = 20.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Boton Cámara
        Button(
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA)},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (imagenSeleccionada.isBlank()) "Tomar Foto" else "Tomar Otra Foto")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón galería
        Button(
            onClick = { galleryLuncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar de la Galería")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para confirmar y regresar al formulario
        Button(
            onClick = {
                navigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            // Solo permitir guardar si hay foto
            enabled = imagenSeleccionada.isNotBlank()
        ) {
            Text("Confirmar Imagen y Regresar")
        }
    }
}

fun crearArchivo(context: Context): File {
    val tiempo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val nombreArchivo = "REPORTE_${tiempo}"
    val directorio = context.filesDir
    return File.createTempFile(nombreArchivo,".jpg", directorio)
}