package com.example.escuela_escritora_marcela_paz.ui.screens.utp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.escuela_escritora_marcela_paz.ui.components.BotonAnimado
import com.example.escuela_escritora_marcela_paz.ui.components.Cabecera
import com.example.escuela_escritora_marcela_paz.ui.components.CampoTexto
import com.example.escuela_escritora_marcela_paz.ui.components.MensajeError
import com.example.escuela_escritora_marcela_paz.ui.components.OpcionesRadio
import com.example.escuela_escritora_marcela_paz.util.Asignaturas
import com.example.escuela_escritora_marcela_paz.util.Fechas
import com.example.escuela_escritora_marcela_paz.util.Notificaciones
import com.example.escuela_escritora_marcela_paz.viewmodel.ActividadViewModel
import com.example.escuela_escritora_marcela_paz.viewmodel.AuthViewModel

/** Formulario 2: crear una actividad (texto, número, fecha, checkbox, switch y botón). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadFormScreen(
    authVm: AuthViewModel,
    actVm: ActividadViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val usuario by authVm.usuario.collectAsState()
    val form by actVm.formulario.collectAsState()
    val errores by actVm.errores.collectAsState()
    val guardadoOk by actVm.guardadoOk.collectAsState()

    var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { actVm.nuevoFormulario() }

    LaunchedEffect(guardadoOk) {
        if (guardadoOk) {
            Notificaciones.mostrar(
                context,
                "Actividad creada",
                "La actividad \"${form.titulo.trim()}\" se guardó correctamente."
            )
            actVm.consumirGuardado()
            onVolver()
        }
    }

    Scaffold(topBar = { Cabecera("Nueva actividad", onVolver = onVolver) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTexto(
                form.titulo, { v -> actVm.actualizar { it.copy(titulo = v) } },
                "Título", errores.titulo
            )

            OpcionesRadio(
                titulo = "Asignatura",
                opciones = Asignaturas.todas,
                seleccion = Asignaturas.todas.indexOf(form.asignatura),
                onSeleccion = { i -> actVm.actualizar { it.copy(asignatura = Asignaturas.todas[i]) } },
                error = errores.asignatura
            )

            CampoTexto(
                form.pregunta, { v -> actVm.actualizar { it.copy(pregunta = v) } },
                "Pregunta (frase corta)", errores.pregunta,
                singleLine = false, minLines = 2
            )
            CampoTexto(
                form.alternativaA, { v -> actVm.actualizar { it.copy(alternativaA = v) } },
                "Alternativa A", errores.alternativaA
            )
            CampoTexto(
                form.alternativaB, { v -> actVm.actualizar { it.copy(alternativaB = v) } },
                "Alternativa B", errores.alternativaB
            )
            CampoTexto(
                form.alternativaC, { v -> actVm.actualizar { it.copy(alternativaC = v) } },
                "Alternativa C", errores.alternativaC
            )

            OpcionesRadio(
                titulo = "Respuesta correcta",
                opciones = listOf("A", "B", "C"),
                seleccion = form.respuestaCorrecta,
                onSeleccion = { i -> actVm.actualizar { it.copy(respuestaCorrecta = i) } },
                error = errores.respuestaCorrecta
            )

            // Campo numérico (Int)
            CampoTexto(
                form.maxIntentos, { v -> actVm.actualizarIntentos(v) },
                "Intentos máximos (1 a 5)", errores.maxIntentos,
                teclado = KeyboardType.Number
            )

            // Fecha (Date)
            OutlinedButton(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    form.fechaLimite?.let { "Disponible hasta: ${Fechas.formatear(it)}" }
                        ?: "Elegir fecha límite"
                )
            }
            if (errores.fechaLimite != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        errores.fechaLimite ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = form.conAudio,
                    onCheckedChange = { v -> actVm.actualizar { it.copy(conAudio = v) } }
                )
                Text("Incluir lectura en voz alta")
            }

            // Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Publicar ahora")
                Switch(
                    checked = form.publicar,
                    onCheckedChange = { v -> actVm.actualizar { it.copy(publicar = v) } }
                )
            }

            MensajeError(errores.general)
            Spacer(Modifier.height(4.dp))

            BotonAnimado(
                texto = "Guardar actividad",
                onClick = { actVm.guardar(usuario?.id ?: 0) }
            )
        }
    }

    if (mostrarDatePicker) {
        val estado = rememberDatePickerState(initialSelectedDateMillis = form.fechaLimite)
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    actVm.actualizar { it.copy(fechaLimite = estado.selectedDateMillis) }
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}
