package com.example.reportespc.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reportespc.ui.VistaCamara
import com.example.reportespc.ui.VistaHome
import com.example.reportespc.ui.VistaLogin


import android.app.Application

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.reportespc.ui.VistaListaReportes

@Composable
fun NavigationWrapper() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: ReporteViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    )

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            VistaLogin(
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            VistaHome(
                viewModel = viewModel,
                navigateToCamara = { navController.navigate(Camara) },
                navigateToLista = { navController.navigate(ListaReportes) } // 3. Pasamos la navegación
            )
        }

        composable<Camara> {
            VistaCamara(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }


        composable<ListaReportes> {
            VistaListaReportes(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}