package com.example.escuela_escritora_marcela_paz.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.escuela_escritora_marcela_paz.ui.screens.auth.LoginScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.auth.RegistroScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.docente.DocenteHomeScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.estudiante.ActividadScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.estudiante.EstudianteHomeScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.utp.ActividadFormScreen
import com.example.escuela_escritora_marcela_paz.ui.screens.utp.UtpHomeScreen
import com.example.escuela_escritora_marcela_paz.viewmodel.ActividadViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.DocenteViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.EstudianteViewModel

@Composable
fun AppNavGraph(authVm: AuthViewModel = viewModel()) {
    val sesionLista by authVm.sesionLista.collectAsState()
    val usuario by authVm.usuario.collectAsState()

    // Mientras se revisa si había una sesión guardada, se muestra un indicador.
    if (!sesionLista) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val nav = rememberNavController()
    val actVm: ActividadViewModel = viewModel()
    val docVm: DocenteViewModel = viewModel()
    val estVm: EstudianteViewModel = viewModel()

    // La pantalla inicial se decide una sola vez: sesión guardada -> su home, si no -> login.
    val inicio = remember { usuario?.let { Rutas.home(it.rol) } ?: Rutas.LOGIN }

    NavHost(navController = nav, startDestination = inicio) {
        composable(Rutas.LOGIN) {
            LoginScreen(authVm, onIrRegistro = { nav.navigate(Rutas.REGISTRO) })
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(authVm, onVolver = { nav.popBackStack() })
        }
        composable(Rutas.UTP_HOME) {
            UtpHomeScreen(authVm, actVm, onNueva = { nav.navigate(Rutas.UTP_NUEVA) })
        }
        composable(Rutas.UTP_NUEVA) {
            ActividadFormScreen(authVm, actVm, onVolver = { nav.popBackStack() })
        }
        composable(Rutas.DOCENTE_HOME) {
            DocenteHomeScreen(authVm, docVm)
        }
        composable(Rutas.ESTUDIANTE_HOME) {
            EstudianteHomeScreen(
                authVm, estVm,
                onAbrir = { id -> nav.navigate(Rutas.estudianteActividad(id)) }
            )
        }
        composable(
            route = Rutas.ESTUDIANTE_ACTIVIDAD,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            ActividadScreen(
                actividadId = entrada.arguments?.getInt("id") ?: 0,
                authVm = authVm,
                estVm = estVm,
                onVolver = { nav.popBackStack() }
            )
        }
    }

    // Reacciona a login / logout: cambia de pantalla según el rol.
    LaunchedEffect(usuario) {
        val actual = nav.currentDestination?.route
        val u = usuario
        if (u == null) {
            if (actual != Rutas.LOGIN && actual != Rutas.REGISTRO) {
                nav.navigate(Rutas.LOGIN) {
                    popUpTo(nav.graph.id) { inclusive = true }
                }
            }
        } else if (actual == Rutas.LOGIN || actual == Rutas.REGISTRO) {
            nav.navigate(Rutas.home(u.rol)) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }
}
