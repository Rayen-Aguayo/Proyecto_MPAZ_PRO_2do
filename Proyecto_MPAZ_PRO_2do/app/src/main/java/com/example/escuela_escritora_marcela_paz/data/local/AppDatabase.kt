package com.example.escuela_escritora_marcela_paz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.escuela_escritora_marcela_paz.data.local.dao.ActividadDao
import com.example.escuela_escritora_marcela_paz.data.local.dao.IntentoDao
import com.example.escuela_escritora_marcela_paz.data.local.dao.UsuarioDao
import com.example.escuela_escritora_marcela_paz.data.local.entity.Actividad
import com.example.escuela_escritora_marcela_paz.data.local.entity.Intento
import com.example.escuela_escritora_marcela_paz.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, Actividad::class, Intento::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun actividadDao(): ActividadDao
    abstract fun intentoDao(): IntentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_database"
                )
                    .fallbackToDestructiveMigration() // MVP: si cambias tablas, se recrea la BD
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
