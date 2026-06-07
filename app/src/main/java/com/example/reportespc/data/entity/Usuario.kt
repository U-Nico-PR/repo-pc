package com.example.reportespc.data.entity

/*
* Clase que representa la estructura de un Usuario dentro de nuestra base de datos (Firestore)
* */
data class Usuario(
    val id: String = "",       // El UID único que nos dará Firebase Auth
    val name: String = "",     // Nombre completo del usuario o técnico
    val email: String = ""     // Correo electrónico de contacto
)