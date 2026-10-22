package com.example.escuela_escritora_marcela_paz.ui.screens.estudiante

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.escuela_escritora_marcela_paz.ui.components.Cabecera
import com.example.escuela_escritora_marcela_paz.ui.components.SolicitarPermisoNotificaciones
import com.example.escuela_escritora_marcela_paz.util.Fechas
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.EstudianteViewModel

@Composable
fun EstudianteHomeScreen(
    authVm: AuthViewModel,
    estVm: EstudianteViewModel,
    onAbrir: (Int) -> Unit
) {
    SolicitarPermisoNotificaciones()

    val usuario by authVm.usuario.collectAsState()
    val actividades by estVm.actividades.collectAsState()
    val conteos by estVm.conteos.collectAsState()

    LaunchedEffect(usuario?.id) { usuario?.id?.let { estVm.iniciar(it) } }

    Scaffold(
        topBar = { Cabecera("Mis actividades", onSalir = { authVm.cerrarSesion() }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "¡Hola, ${usuario?.nombre ?: ""}!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            if (actividades.isEmpty()) {
                Text(
                    "Todavía no tienes actividades. Pídele a tu profesora o profesor.",
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(actividades, key = { it.id }) { actividad ->
                        val usados = conteos[actividad.id] ?: 0
                        val vigente = Fechas.estaVigente(actividad.fechaLimite)
                        val sinIntentos = usados >= actividad.maxIntentos
                        val habilitada = vigente && !sinIntentos

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clickable(enabled = habilitada) { onAbrir(actividad.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = if (habilitada) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        actividad.titulo,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        actividad.asignatura,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        when {
                                            !vigente -> "Ya venció"
                                            sinIntentos -> "Sin intentos disponibles"
                                            else -> "Intentos: $usados de ${actividad.maxIntentos}"
                                        },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
