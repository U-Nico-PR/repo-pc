package com.example.reportespc.nav

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportespc.data.entity.Usuario
import com.example.reportespc.data.repo.dao.AutenticarRepo
import com.example.reportespc.data.repo.AutenticarRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/*
* Clase ViewModel encargada de gestionar los estados y lógica de negocio para el inicio de sesión y registro.
* */
class AutenticarViewModel (
    // Instanciando la lógica del negocio con inyección por defecto
    private val authRepository: AutenticarRepo = AutenticarRepositoryImpl()
) : ViewModel() {

    // Instancia de Firestore para guardar los datos de la entidad Usuario
    private val firestore = FirebaseFirestore.getInstance()
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _statusMessage = mutableStateOf<String?>(null)
    val statusMessage: State<String?> = _statusMessage

    /**
     * Función para iniciar sesión con un usuario existente
     */
    fun login(email: String, contrasenia: String, onSuccess: () -> Unit) {
        if (email.isBlank() || contrasenia.isBlank()) {
            _statusMessage.value = "Por favor, llena todos los campos."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Iniciando sesión..."

            val result = authRepository.iniciarSesionConCorreo(email, contrasenia)

            _isLoading.value = false
            result.onSuccess { firebaseUser ->
                if (firebaseUser != null) {
                    _statusMessage.value = "¡Bienvenido!"
                    onSuccess() // Ejecuta la navegación reactiva a la siguiente pantalla
                } else {
                    _statusMessage.value = "Error desconocido."
                }
            }.onFailure { exception ->
                _statusMessage.value = "Credenciales Incorrectas. Verifica la cuenta y contraseña"
            }
        }
    }

    /**
     * Función para registrar un nuevo usuario y guardar su perfil en Firestore
     */
    fun registrarUsuario(nombre: String, email: String, contrasenia: String, onSuccess: () -> Unit) {
        if (nombre.isBlank() || email.isBlank() || contrasenia.isBlank()) {
            _statusMessage.value = "Por favor, completa todos los campos obligatorios."
            return
        }

        if (contrasenia.length < 6) {
            _statusMessage.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Creando cuenta en el servidor..."

            // Crear la credencial de acceso en Firebase Auth
            val resultAuth = authRepository.registrarConCorreo(email, contrasenia)

            resultAuth.onSuccess { firebaseUser ->
                if (firebaseUser != null) {
                    _statusMessage.value = "Cuenta creada. Guardando datos de perfil..."

                    // Construimos la entidad Usuario con el ID único asignado por Firebase
                    val nuevoUsuario = Usuario(
                        id = firebaseUser.uid,
                        name = nombre,
                        email = email
                    )

                    // Intentamos persistir el objeto Usuario en la colección de Firestore
                    try {
                        firestore.collection("usuarios")
                            .document(nuevoUsuario.id)
                            .set(nuevoUsuario)
                            .await() // Espera asíncronamente a que termine la subida

                        _statusMessage.value = "¡Registro completado con éxito!"
                        onSuccess() // Ejecuta la navegación al Home de forma segura tras guardar
                    } catch (e: Exception) {
                        // Si Firestore falla, la cuenta en Auth ya se creó, le avisamos al usuario
                        _statusMessage.value = "Cuenta creada, pero falló el registro en la base de datos."
                        onSuccess() // Permitimos pasar ya que la cuenta existe
                    }
                } else {
                    _statusMessage.value = "Error inesperado al generar el perfil."
                }
            }.onFailure { exception ->
                _statusMessage.value = "Error al registrar: ${exception.localizedMessage ?: "Datos inválidos"}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Permite limpiar el mensaje de estado desde la vista cuando sea necesario
     */
    fun limpiarMensaje() {
        _statusMessage.value = null
    }

    /**
     * Verifica de forma rápida si hay un usuario con sesión activa
     */
    fun estaUsuarioLogueado(): Boolean {
        return authRepository.obtenerUsuarioActualId() != null
    }
}