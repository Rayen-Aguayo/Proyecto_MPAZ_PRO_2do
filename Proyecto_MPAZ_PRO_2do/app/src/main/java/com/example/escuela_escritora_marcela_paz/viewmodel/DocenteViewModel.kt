package com.example.escuela_escritora_marcela_paz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_escritora_marcela_paz.data.local.AppDatabase
import com.example.escuela_escritora_marcela_paz.data.local.entity.ResumenActividad
import com.example.escuela_escritora_marcela_paz.data.repository.ActividadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** ViewModel del perfil docente: resumen de rendimiento por actividad. */
class DocenteViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AppDatabase.getInstance(application).let {
        ActividadRepository(it.actividadDao(), it.intentoDao())
    }

    val resumen: StateFlow<List<ResumenActividad>> = repositorio.resumen
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
