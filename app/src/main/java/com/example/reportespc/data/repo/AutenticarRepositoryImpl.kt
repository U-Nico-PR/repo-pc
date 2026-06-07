package com.example.reportespc.data.repo

import android.os.Bundle
import com.example.reportespc.data.repo.dao.AutenticarRepo
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AutenticarRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
): AutenticarRepo {

    // Instancia oficial de Firebase Analytics para registrar eventos en la nube
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    // Método que registra usuarios nuevos
    override suspend fun registrarConCorreo(email: String, contrasenia: String): Result<FirebaseUser?> {
        return try {
            // Con AWAIT hace que espere a que se conecte con internet antes de pasar a la siguiente línea
            val result = firebaseAuth.createUserWithEmailAndPassword(email, contrasenia).await()
            val usuarioLogueado = result.user

            // ANALYTICS: Registramos de forma métrica que un usuario se creó con éxito
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, "email")
                putString("usuario_id", usuarioLogueado?.uid)
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

            Result.success(usuarioLogueado) // Si se creó correctamente devuelve un estado de éxito
        } catch (e: Exception) {
            // 📈 ANALYTICS: Registramos si el registro falló (ej. correo duplicado o sin internet)
            registrarErrorEnAnalytics("error_registro_usuario", e.localizedMessage ?: "Error desconocido")
            Result.failure(e)
        }
    }

    // Autentica si un usuario ya existe (Login)
    override suspend fun iniciarSesionConCorreo(email: String, contrasenia: String): Result<FirebaseUser?> {
        return try {
            // Con AWAIT hace que espere a que se conecte a internet antes de pasar a la siguiente línea
            val result = firebaseAuth.signInWithEmailAndPassword(email, contrasenia).await()
            val usuarioLogueado = result.user

            // 📈 ANALYTICS: Registramos el inicio de sesión exitoso
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, "email")
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

            Result.success(usuarioLogueado) // Si la contraseña con el correo coincide devuelve el usuario logueado
        } catch (e: Exception) {
            // 📈 ANALYTICS: Registramos si el login falló (ej. contraseña incorrecta)
            registrarErrorEnAnalytics("error_login_usuario", e.localizedMessage ?: "Error desconocido")
            Result.failure(e)
        }
    }

    // Obtener usuario con la ID actual
    override fun obtenerUsuarioActualId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    /**
     * Función interna optimizada para registrar fallas en la consola de Firebase Analytics
     */
    private fun registrarErrorEnAnalytics(nombreEvento: String, mensajeError: String) {
        val bundle = Bundle().apply {
            putString("detalles_error", mensajeError)
        }
        firebaseAnalytics.logEvent(nombreEvento, bundle)
    }
}