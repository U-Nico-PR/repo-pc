package com.example.reportespc.ui

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportespc.R

@Composable
fun VistaLogin(navigateToHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.pc),
            contentDescription = "Imagen de Computadora de Escritoro", // Para TalkBack que proporciona descripción para accesibilidad
            contentScale = ContentScale.Crop,    // Para ajustar la imagen a la pantalla
            alpha = 0.8F, // Para la opacidad de la imagen
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
        )
        Text(
            "Sistema de Reportes",
            fontSize = 28.sp
        )
        Spacer(
            modifier = Modifier.height(40.dp)
        )
        Button(
            onClick = {
                navigateToHome()
            }) {
            Text("Ingresar")
        }
    }
}