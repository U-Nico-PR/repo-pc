package com.example.reportespc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportespc.nav.ReporteViewModel

@Composable
fun VistaHome(viewModel: ReporteViewModel, navigateToCamara: () -> Unit, navigateToLista: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Generar Reporte", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = viewModel.idEquipoTemp.value,
            onValueChange = { viewModel.idEquipoTemp.value = it },
            label = { Text("ID de la Computadora") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.fallaTemp.value,
            onValueChange = { viewModel.fallaTemp.value = it },
            label = { Text("Falla detectada") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navigateToCamara() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Tomar Foto del Equipo")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón para ver la lista de reportes guardados
        OutlinedButton(
            onClick = { navigateToLista() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Ver Reportes Guardados")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}