package com.example.escuela_escritora_marcela_paz.ui.navigation

import com.example.escuela_escritora_marcela_paz.util.Roles

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val UTP_HOME = "utp/home"
    const val UTP_NUEVA = "utp/nueva"
    const val DOCENTE_HOME = "docente/home"
    const val ESTUDIANTE_HOME = "estudiante/home"
    const val ESTUDIANTE_ACTIVIDAD = "estudiante/actividad/{id}"

    fun estudianteActividad(id: Int) = "estudiante/actividad/$id"

    fun home(rol: String): String = when (rol) {
        Roles.UTP -> UTP_HOME
        Roles.DOCENTE -> DOCENTE_HOME
        else -> ESTUDIANTE_HOME
    }
}
