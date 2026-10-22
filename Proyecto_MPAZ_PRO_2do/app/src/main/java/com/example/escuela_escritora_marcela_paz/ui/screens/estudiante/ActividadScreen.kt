package com.example.escuela_escritora_marcela_paz.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.escuela_escritora_marcela_paz.data.repository.ResultadoRespuesta
import com.example.escuela_escritora_marcela_paz.ui.components.BotonAnimado
import com.example.escuela_escritora_marcela_paz.ui.components.Cabecera
import com.example.escuela_escritora_marcela_paz.util.Fechas
import com.example.escuela_escritora_marcela_paz.util.LectorVoz
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.EstudianteViewModel

@Composable
fun ActividadScreen(
    actividadId: Int,
    authVm: AuthViewModel,
    estVm: EstudianteViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val usuario by authVm.usuario.collectAsState()
    val actividad by estVm.actividad.collectAsState()
    val conteos by estVm.conteos.collectAsState()
    val resultado by estVm.resultado.collectAsState()

    var elegida by rememberSaveable { mutableStateOf(-1) }

    val lector = remember { LectorVoz(context) }
    DisposableEffect(Unit) { onDispose { lector.liberar() } }

    LaunchedEffect(actividadId) { estVm.cargarActividad(actividadId) }
    LaunchedEffect(usuario?.id) { usuario?.id?.let { estVm.iniciar(it) } }

    Scaffold(
        topBar = { Cabecera(actividad?.titulo ?: "Actividad", onVolver = onVolver) }
    ) { padding ->
        val a = actividad
        if (a == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val letras = listOf("A", "B", "C")
        val usados = conteos[a.id] ?: 0
        val restantes = a.maxIntentos - usados
        val vigente = Fechas.estaVigente(a.fechaLimite)
        val r = resultado
        val completada = r is ResultadoRespuesta.Respondida && r.correcta
        val bloqueada = completada || restantes <= 0 || !vigente

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                a.pregunta,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (a.conAudio) {
                OutlinedButton(
                    onClick = {
                        val texto = buildString {
                            append(a.pregunta)
                            a.alternativas().forEachIndexed { i, alt ->
                                append(". Opción ${letras[i]}: $alt")
                            }
                        }
                        lector.hablar(texto)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Escuchar la pregunta")
                }
            }

            a.alternativas().forEachIndexed { indice, texto ->
                val fondo by animateColorAsState(
                    targetValue = if (elegida == indice) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                    label = "fondoAlternativa"
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = fondo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !bloqueada) {
                            elegida = indice
                            estVm.limpiarResultado()
                        }
                ) {
                    Text(
                        "${letras[indice]}) $texto",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            Text(
                if (!vigente) "Esta actividad ya venció"
                else "Intentos disponibles: ${restantes.coerceAtLeast(0)}",
                style = MaterialTheme.typography.bodyLarge
            )

            // Retroalimentación animada
            AnimatedVisibility(
                visible = r != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val bien = r is ResultadoRespuesta.Respondida && r.correcta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (bien) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (bien) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            when (r) {
                                is ResultadoRespuesta.Respondida ->
                                    if (r.correcta) "¡Muy bien! Respuesta correcta."
                                    else "Casi. Intenta otra vez."
                                is ResultadoRespuesta.SinIntentos -> "Ya usaste todos tus intentos."
                                is ResultadoRespuesta.Vencida -> "Esta actividad ya venció."
                                null -> ""
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            BotonAnimado(
                texto = "Responder",
                onClick = { usuario?.id?.let { estVm.responder(it, elegida) } },
                enabled = elegida >= 0 && !bloqueada
            )
        }
    }
}
