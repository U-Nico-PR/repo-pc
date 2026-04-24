package com.example.reportespc.ui

import com.example.reportespc.nav.ReporteViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.net.toUri
import coil.compose.AsyncImage

@Composable
fun VistaListaReportes(viewModel: ReporteViewModel, navigateBack: () -> Unit) {

    val listaReportes by viewModel.reportes.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reportes Guardados", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {

            items(listaReportes) { reporte ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "ID: ${reporte.idEquipo}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Falla: ${reporte.falla}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        AsyncImage(

                            model = reporte.imageUri.toUri(),
                            contentDescription = "Foto de falla",
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Button(onClick = { navigateBack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar")
        }
    }
}