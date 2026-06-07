package com.example.reportespc.data.repo.dao

import com.google.firebase.auth.FirebaseUser

interface AutenticarRepo {
    // Registra un nuevo usuario con correo y contraseña
    suspend fun registrarConCorreo(email: String, contrasenia: String): Result<FirebaseUser?>

    // Inicia sesión con un usuario existente
    suspend fun iniciarSesionConCorreo(email: String, contrasenia: String): Result<FirebaseUser?>

    // Obtener el ID del usuario que tiene la sesión iniciada actualmente
    fun obtenerUsuarioActualId(): String?
}