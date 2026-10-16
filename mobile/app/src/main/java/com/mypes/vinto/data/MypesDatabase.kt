package com.mypes.vinto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Producto::class], version = 1, exportSchema = true)
abstract class MypesDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var instancia: MypesDatabase? = null

        fun obtener(context: Context): MypesDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    MypesDatabase::class.java,
                    "mypes-local.db"
                ).build().also { instancia = it }
            }
    }
}
