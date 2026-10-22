package com.example.escuela_escritora_marcela_paz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_escritora_marcela_paz.data.local.AppDatabase
import com.example.escuela_escritora_marcela_paz.data.local.entity.Actividad
import com.example.escuela_escritora_marcela_paz.data.repository.ActividadRepository
import com.example.escuela_escritora_marcela_paz.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActividadForm(
    val titulo: String = "",
    val asignatura: String = "",
    val pregunta: String = "",
    val alternativaA: String = "",
    val alternativaB: String = "",
    val alternativaC: String = "",
    val respuestaCorrecta: Int = -1,
    val maxIntentos: String = "",
    val fechaLimite: Long? = null,
    val conAudio: Boolean = false,
    val publicar: Boolean = false
)

data class ActividadErrores(
    val titulo: String? = null,
    val asignatura: String? = null,
    val pregunta: String? = null,
    val alternativaA: String? = null,
    val alternativaB: String? = null,
    val alternativaC: String? = null,
    val respuestaCorrecta: String? = null,
    val maxIntentos: String? = null,
    val fechaLimite: String? = null,
    val general: String? = null
) {
    fun hayErrores() = listOf(
        titulo, asignatura, pregunta, alternativaA, alternativaB, alternativaC,
        respuestaCorrecta, maxIntentos, fechaLimite, general
    ).any { it != null }
}

/** ViewModel del perfil UTP: lista de actividades y formulario de creación. */
class ActividadViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AppDatabase.getInstance(application).let {
        ActividadRepository(it.actividadDao(), it.intentoDao())
    }

    val actividades: StateFlow<List<Actividad>> = repositorio.todas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formulario = MutableStateFlow(ActividadForm())
    val formulario: StateFlow<ActividadForm> = _formulario.asStateFlow()

    private val _errores = MutableStateFlow(ActividadErrores())
    val errores: StateFlow<ActividadErrores> = _errores.asStateFlow()

    private val _guardadoOk = MutableStateFlow(false)
    val guardadoOk: StateFlow<Boolean> = _guardadoOk.asStateFlow()

    fun actualizar(cambio: (ActividadForm) -> ActividadForm) {
        _formulario.value = cambio(_formulario.value)
    }

    /** Solo acepta un dígito (el máximo permitido es 5). */
    fun actualizarIntentos(valor: String) {
        if (valor.length <= 1 && valor.all { it.isDigit() }) {
            actualizar { it.copy(maxIntentos = valor) }
        }
    }

    fun guardar(creadorId: Int) {
        val f = _formulario.value
        val errores = ActividadErrores(
            titulo = Validadores.titulo(f.titulo),
            asignatura = Validadores.asignatura(f.asignatura),
            pregunta = Validadores.pregunta(f.pregunta),
            alternativaA = Validadores.alternativa(f.alternativaA),
            alternativaB = Validadores.alternativa(f.alternativaB),
            alternativaC = Validadores.alternativa(f.alternativaC),
            respuestaCorrecta = Validadores.respuestaCorrecta(f.respuestaCorrecta),
            maxIntentos = Validadores.intentos(f.maxIntentos),
            fechaLimite = Validadores.fechaLimite(f.fechaLimite)
        )
        _errores.value = errores
        if (errores.hayErrores()) return

        viewModelScope.launch {
            try {
                repositorio.crear(
                    Actividad(
                        titulo = f.titulo.trim(),
                        asignatura = f.asignatura,
                        pregunta = f.pregunta.trim(),
                        alternativaA = f.alternativaA.trim(),
                        alternativaB = f.alternativaB.trim(),
                        alternativaC = f.alternativaC.trim(),
                        respuestaCorrecta = f.respuestaCorrecta,
                        maxIntentos = f.maxIntentos.toInt(),
                        fechaLimite = f.fechaLimite!!,
                        conAudio = f.conAudio,
                        publicada = f.publicar,
                        creadorId = creadorId
                    )
                )
                _guardadoOk.value = true
            } catch (e: Exception) {
                _errores.value = errores.copy(general = "No se pudo guardar la actividad")
            }
        }
    }

    fun cambiarPublicada(actividad: Actividad, publicada: Boolean) {
        viewModelScope.launch { repositorio.cambiarPublicada(actividad.id, publicada) }
    }

    fun nuevoFormulario() {
        _formulario.value = ActividadForm()
        _errores.value = ActividadErrores()
        _guardadoOk.value = false
    }

    fun consumirGuardado() {
        _guardadoOk.value = false
    }
}
