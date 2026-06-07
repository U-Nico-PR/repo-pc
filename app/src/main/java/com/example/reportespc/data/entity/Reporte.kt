package com.example.reportespc.data.entity

data class Reporte(
    val id: String = "",        // Firebase maneja los IDs como Strings Alfanumeriocos
    val idEquipo: String = "",
    val falla: String = "",
    val imageUri: String? = "",
    val fecha: String = ""
) {
    // Constructor requerido por Firebase para convertir los documentos de la BD a objetos Kotlin
    constructor() : this("", "", "", "", "")
}