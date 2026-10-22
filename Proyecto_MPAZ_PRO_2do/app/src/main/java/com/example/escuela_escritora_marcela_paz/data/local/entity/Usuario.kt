package com.example.escuela_escritora_marcela_paz.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["username"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val username: String,
    val sal: String,
    val claveHash: String,
    val rol: String
)
