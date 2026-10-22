package com.example.escuela_escritora_marcela_paz.ui.screens.docente

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.escuela_escritora_marcela_paz.data.local.entity.ResumenActividad
import com.example.escuela_escritora_marcela_paz.ui.components.Cabecera
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.DocenteViewModel

/** Informe de rendimiento: solo visible para el perfil docente. */
@Composable
fun DocenteHomeScreen(authVm: AuthViewModel, docVm: DocenteViewModel) {
    val usuario by authVm.usuario.collectAsState()
    val resumen by docVm.resumen.collectAsState()

    Scaffold(
        topBar = { Cabecera("Panel docente", onSalir = { authVm.cerrarSesion() }) }
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
            Text("Rendimiento por actividad (informe reservado al docente)")

            if (resumen.isEmpty()) {
                Text("Todavía no hay actividades creadas por UTP.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(resumen, key = { it.titulo + it.asignatura }) { item ->
                        TarjetaResumen(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaResumen(item: ResumenActividad) {
    val porcentaje = if (item.total == 0) 0f else item.aciertos.toFloat() / item.total
    val animado by animateFloatAsState(targetValue = porcentaje, label = "progresoAciertos")

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                item.titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(item.asignatura, style = MaterialTheme.typography.bodyMedium)
            Text("Intentos realizados: ${item.total}")
            Text("Aciertos: ${item.aciertos} (${(porcentaje * 100).toInt()}%)")
            LinearProgressIndicator(
                progress = { animado },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
