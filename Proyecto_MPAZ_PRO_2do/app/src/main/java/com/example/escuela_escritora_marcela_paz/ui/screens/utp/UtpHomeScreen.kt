package com.example.escuela_escritora_marcela_paz.ui.screens.utp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.escuela_escritora_marcela_paz.data.local.entity.Actividad
import com.example.escuela_escritora_marcela_paz.ui.components.Cabecera
import com.example.escuela_escritora_marcela_paz.util.Fechas
import com.example.escuela_escritora_marcela_paz.viewmodel.ActividadViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel

@Composable
fun UtpHomeScreen(
    authVm: AuthViewModel,
    actVm: ActividadViewModel,
    onNueva: () -> Unit
) {
    val usuario by authVm.usuario.collectAsState()
    val actividades by actVm.actividades.collectAsState()

    Scaffold(
        topBar = { Cabecera("Panel UTP", onSalir = { authVm.cerrarSesion() }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNueva,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva actividad") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Hola, ${usuario?.nombre ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Actividades creadas: ${actividades.size}")

            if (actividades.isEmpty()) {
                Text("Aún no hay actividades. Crea la primera con el botón de abajo.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(actividades, key = { it.id }) { actividad ->
                        TarjetaActividad(
                            actividad = actividad,
                            onPublicada = { actVm.cambiarPublicada(actividad, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaActividad(actividad: Actividad, onPublicada: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                actividad.titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(actividad.asignatura, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Intentos: ${actividad.maxIntentos} · Hasta: ${Fechas.formatear(actividad.fechaLimite)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (actividad.publicada) "Publicada" else "Borrador")
                Switch(checked = actividad.publicada, onCheckedChange = onPublicada)
            }
        }
    }
}
