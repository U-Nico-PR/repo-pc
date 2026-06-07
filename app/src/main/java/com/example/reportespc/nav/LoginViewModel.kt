package com.example.reportespc.nav

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var correoTemp = mutableStateOf("")
    var contrasenaTemp = mutableStateOf("")
    var mensajeError = mutableStateOf<String?>(null)
    var estaCargando = mutableStateOf(false)

    fun iniciarSesion(onLoginExitoso: () -> Unit) {
        val correo = correoTemp.value.trim()
        val password = contrasenaTemp.value.trim()

        if (correo.isBlank() || password.isBlank()) {
            mensajeError.value = "Por favor, completa todos los campos."
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mensajeError.value = "Por favor, ingresa un correo válido."
            return
        }

        estaCargando.value = true
        mensajeError.value = null

        // Simulación de respuesta de red (Firebase de preparación)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            estaCargando.value = false

            // Cuando integres Firebase real, este bloque cambiará a:
            // if (task.isSuccessful) { onLoginExitoso() } else { mensajeError.value = ... }
            onLoginExitoso()

        }, 1200)
    }
}