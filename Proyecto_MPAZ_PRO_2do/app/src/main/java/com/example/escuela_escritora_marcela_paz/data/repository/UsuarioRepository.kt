package com.example.escuela_escritora_marcela_paz.data.repository

import com.example.escuela_escritora_marcela_paz.data.local.dao.UsuarioDao
import com.example.escuela_escritora_marcela_paz.data.local.entity.Usuario
import com.example.escuela_escritora_marcela_paz.util.PasswordHasher

class UsuarioRepository(private val dao: UsuarioDao) {

    /** Devuelve false si el usuario ya existe. */
    suspend fun registrar(nombre: String, username: String, clave: String, rol: String): Boolean {
        val usernameLimpio = username.trim().lowercase()
        if (dao.buscarPorUsername(usernameLimpio) != null) return false

        val sal = PasswordHasher.nuevaSal()
        dao.insertar(
            Usuario(
                nombre = nombre.trim(),
                username = usernameLimpio,
                sal = sal,
                claveHash = PasswordHasher.hashear(clave, sal),
                rol = rol
            )
        )
        return true
    }

    /** Devuelve el usuario si las credenciales son correctas, o null. */
    suspend fun login(username: String, clave: String): Usuario? {
        val usuario = dao.buscarPorUsername(username.trim().lowercase()) ?: return null
        return if (PasswordHasher.verificar(clave, usuario.sal, usuario.claveHash)) usuario else null
    }

    suspend fun buscarPorId(id: Int): Usuario? = dao.buscarPorId(id)
}
