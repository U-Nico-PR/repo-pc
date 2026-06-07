package com.example.reportespc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun VistaRegistro(
    viewModel: AutenticarViewModel,
    onRegistroSuccess: () -> Unit,   // Callback que te lleva al Home tras un registro exitoso
    navigateBackToLogin: () -> Unit // Callback para regresar a la pantalla de Login
) {
    // Estados locales para capturar los datos del formulario de registro
    var nombreLocal by remember { mutableStateOf("") }
    var correoLocal by remember { mutableStateOf("") }
    var contraseniaLocal by remember { mutableStateOf("") }

    // Escuchamos los estados reactivos del ViewModel unificado
    val isLoading by viewModel.isLoading
    val statusMessage by viewModel.statusMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), // Soporte de scroll por si el teclado ocupa espacio
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Conservamos tu diseño idéntico de imagen para la identidad de la app
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
            "Crear Cuenta",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Campo Nuevo: Nombre Completo (Necesario para tu Entidad Usuario en Firestore)
        OutlinedTextField(
            value = nombreLocal,
            onValueChange = { nombreLocal = it },
            label = { Text("Nombre Completo") },
            placeholder = { Text("Tu nombre y apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Correo Electrónico
        OutlinedTextField(
            value = correoLocal,
            onValueChange = { correoLocal = it },
            label = { Text("Correo Electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = contraseniaLocal,
            onValueChange = { contraseniaLocal = it },
            label = { Text("Contraseña (Mínimo 6 caracteres)") },
            placeholder = { Text("Crea una contraseña segura") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading
        )

        // Mensajes dinámicos de error o éxito de Firebase con tus estilos
        statusMessage?.let { mensaje ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mensaje,
                color = if (mensaje.contains("éxito")) Color.Green else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Botón de Registro / Indicador de Progreso
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = {
                    // Llamamos al método que registra en Auth y luego sube tu modelo a Firestore
                    viewModel.registrarUsuario(
                        nombre = nombreLocal,
                        email = correoLocal,
                        contrasenia = contraseniaLocal,
                        onSuccess = { onRegistroSuccess() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(50.dp),
                // Se habilita solo si el formulario está lleno
                enabled = nombreLocal.isNotBlank() && correoLocal.isNotBlank() && contraseniaLocal.isNotBlank()
            ) {
                Text("Registrar y Guardar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón secundario para regresar al inicio de sesión
        TextButton(
            onClick = {
                viewModel.limpiarMensaje() // Limpiamos mensajes previos antes de cambiar de pantalla
                navigateBackToLogin()
            },
            enabled = !isLoading
        ) {
            Text("¿Ya tienes una cuenta? Inicia sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}