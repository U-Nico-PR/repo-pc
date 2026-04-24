package com.example.reportespc.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reportes")
data class Reporte(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idEquipo: String,
    val falla: String,
    val imageUri: String
)
