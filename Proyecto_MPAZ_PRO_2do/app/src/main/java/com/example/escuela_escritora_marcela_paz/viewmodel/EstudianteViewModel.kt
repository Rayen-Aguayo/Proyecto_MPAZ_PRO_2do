package com.example.escuela_escritora_marcela_paz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_escritora_marcela_paz.data.local.AppDatabase
import com.example.escuela_escritora_marcela_paz.data.local.entity.Actividad
import com.example.escuela_escritora_marcela_paz.data.repository.ActividadRepository
import com.example.escuela_escritora_marcela_paz.data.repository.ResultadoRespuesta
import com.example.escuela_escritora_marcela_paz.util.Notificaciones
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class EstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AppDatabase.getInstance(application).let {
        ActividadRepository(it.actividadDao(), it.intentoDao())
    }

    private val _estudianteId = MutableStateFlow<Int?>(null)

    val actividades: StateFlow<List<Actividad>> = repositorio.publicadas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** actividadId -> intentos usados por el estudiante actual */
    val conteos: StateFlow<Map<Int, Int>> = _estudianteId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repositorio.conteos(id)
        }
        .map { lista -> lista.associate { it.actividadId to it.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _actividad = MutableStateFlow<Actividad?>(null)
    val actividad: StateFlow<Actividad?> = _actividad.asStateFlow()

    private val _resultado = MutableStateFlow<ResultadoRespuesta?>(null)
    val resultado: StateFlow<ResultadoRespuesta?> = _resultado.asStateFlow()

    fun iniciar(estudianteId: Int) {
        _estudianteId.value = estudianteId
    }

    fun cargarActividad(id: Int) {
        viewModelScope.launch {
            _resultado.value = null
            _actividad.value = repositorio.buscar(id)
        }
    }

    fun responder(estudianteId: Int, elegida: Int) {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            val resultado = repositorio.responder(actividad, estudianteId, elegida)
            _resultado.value = resultado
            if (resultado is ResultadoRespuesta.Respondida && resultado.correcta) {
                Notificaciones.mostrar(
                    getApplication(),
                    "¡Muy bien!",
                    "Completaste la actividad: ${actividad.titulo}"
                )
            }
        }
    }

    fun limpiarResultado() {
        _resultado.value = null
    }
}
