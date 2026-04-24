package com.example.reportespc.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempUri != null) {
                viewModel.imageUriTemp.value = tempUri.toString()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (viewModel.imageUriTemp.value != null) {
            AsyncImage(
                model = viewModel.imageUriTemp.value,
                contentDescription = "Foto del reporte",
                modifier = Modifier.size(350.dp).padding(bottom = 20.dp)
            )
            Text("Foto lista para guardarse")
        } else {
            Text("Aún no hay fotografía", modifier = Modifier.padding(bottom = 20.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val file = crearArchivo(context)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                tempUri = uri
                cameraLauncher.launch(uri)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.imageUriTemp.value == null) "Abrir Cámara" else "Tomar Otra Foto")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.guardarReporte()
                navigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            // Solo permitir guardar si hay foto
            enabled = viewModel.imageUriTemp.value != null
        ) {
            Text("Guardar Reporte y Regresar")
        }
    }
}


fun crearArchivo(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "REPORTE_${timeStamp}_"
    // Usamos filesDir en lugar de cacheDir para que las imágenes sean persistentes
    val directory = context.filesDir
    return File.createTempFile(fileName, ".jpg", directory)
}