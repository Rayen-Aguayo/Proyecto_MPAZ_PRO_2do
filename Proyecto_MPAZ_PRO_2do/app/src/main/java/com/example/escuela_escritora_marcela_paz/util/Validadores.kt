package com.example.escuela_escritora_marcela_paz.util

/** Reglas de validación. Cada función devuelve el mensaje de error o null si está bien. */
object Validadores {

    // ---------- Registro / login ----------
    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun username(valor: String): String? = when {
        valor.isBlank() -> "El usuario es obligatorio"
        valor.trim().length < 4 -> "Debe tener al menos 4 caracteres"
        valor.any { it.isWhitespace() } -> "No puede tener espacios"
        else -> null
    }

    fun clave(valor: String): String? = when {
        valor.isBlank() -> "La clave es obligatoria"
        valor.length < 6 -> "Debe tener al menos 6 caracteres"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun confirmacion(clave: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la clave"
        clave != confirmacion -> "Las claves no coinciden"
        else -> null
    }

    fun rol(valor: String): String? =
        if (valor !in Roles.todos) "Selecciona un perfil" else null

    // ---------- Actividad ----------
    fun titulo(valor: String): String? = when {
        valor.isBlank() -> "El título es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        valor.trim().length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun asignatura(valor: String): String? =
        if (valor !in Asignaturas.todas) "Selecciona una asignatura" else null

    fun pregunta(valor: String): String? = when {
        valor.isBlank() -> "La pregunta es obligatoria"
        valor.trim().length < 5 -> "Debe tener al menos 5 caracteres"
        valor.trim().length > 150 -> "Máximo 150 caracteres (frases cortas)"
        else -> null
    }

    fun alternativa(valor: String): String? =
        if (valor.isBlank()) "La alternativa es obligatoria" else null

    fun respuestaCorrecta(indice: Int): String? =
        if (indice !in 0..2) "Indica cuál es la respuesta correcta" else null

    fun intentos(valor: String): String? {
        val n = valor.toIntOrNull() ?: return "Debe ingresar un número"
        return if (n !in 1..5) "Debe estar entre 1 y 5" else null
    }

    fun fechaLimite(valor: Long?): String? = when {
        valor == null -> "Debe seleccionar una fecha"
        valor < Fechas.inicioHoyUtc() -> "La fecha no puede ser anterior a hoy"
        else -> null
    }
}
