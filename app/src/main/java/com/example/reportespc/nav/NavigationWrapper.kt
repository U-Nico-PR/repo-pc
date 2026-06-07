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
import com.example.reportespc.ui.VistaRegistro

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel de Reportes
    val reporteViewModel: ReporteViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    )

    // val loginViewModel: LoginViewModel = viewModel()
    val autenticarViewModel: AutenticarViewModel = viewModel()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            VistaLogin(
                viewModel = autenticarViewModel,
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                navigateToRegistro = {
                    navController.navigate(Registro)
                }
            )
        }

        composable<Registro> {
            VistaRegistro(
                viewModel = autenticarViewModel,
                onRegistroSuccess = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                navigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<Home> {
            VistaHome(
                viewModel = reporteViewModel,
                navigateToCamara = {
                    navController.navigate(Camara) {}
                },
                navigateToLista = {
                    navController.navigate(ListaReportes) {}
                },
                navToLogout = {
                    autenticarViewModel.limpiarMensaje()
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Camara> {
            VistaCamara(
                viewModel = reporteViewModel,
                navigateBack = { navController.popBackStack() }
            )
        }


        composable<ListaReportes> {
            VistaListaReportes(
                viewModel = reporteViewModel,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}