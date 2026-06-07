package com.example.reportespc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportespc.R
import com.example.reportespc.nav.AutenticarViewModel

@Composable
fun VistaLogin(viewModel: AutenticarViewModel, navigateToHome: () -> Unit, navigateToRegistro: () -> Unit) {

    var correoLocal by remember { mutableStateOf("") }
    var contraseniaLocal by remember { mutableStateOf("") }

    // Escuchamos de forma reactiva los estados de carga y mensajes de nuestro nuevo ViewModel
    val isLoading by viewModel.isLoading
    val statusMessage by viewModel.statusMessage

    Column(
        modifier = Modifier.fillMaxSize(), // Asegura que se centre en toda la pantalla
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.pc),
            contentDescription = "Imagen de Computadora de Escritorio",
            contentScale = ContentScale.Crop,
            alpha = 0.8F,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
        )

        Text(
            "Sistema de Reportes",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Campo: Correo Electrónico
        OutlinedTextField(
            value = correoLocal,
            onValueChange = { correoLocal = it },
            label = { Text("Correo Electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading // Se deshabilita si Firebase está cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = contraseniaLocal,
            onValueChange = { contraseniaLocal = it },
            label = { Text("Contraseña") },
            placeholder = { Text("Coloca tu contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading
        )

        // Muestra los mensajes de error/éxito dinámicos de Firebase con estilos propios
        statusMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Botón de Ingreso
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = {
                    // Invocamos función login con los parámetros y el callback reactivo de navegación
                    viewModel.login(
                        email = correoLocal,
                        contrasenia = contraseniaLocal,
                        onSuccess = { navigateToHome() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(50.dp),
                enabled = correoLocal.isNotBlank() && contraseniaLocal.isNotBlank() // Solo se activa si escribe algo
            ) {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón secundario para navegar a la Vista de Registro
        TextButton(
            onClick = {
                viewModel.limpiarMensaje() // Limpia cualquier error de la pantalla antes de cambiar de vista
                navigateToRegistro()
            },
            enabled = !isLoading
        ) {
            Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
        }
    }
}