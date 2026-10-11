package com.mypes.vinto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [Usuario::class, Producto::class, Venta::class, DetalleVenta::class, MovimientoCaja::class, OutboxEntry::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun cajaDao(): CajaDao
    abstract fun outboxDao(): OutboxDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            System.loadLibrary("sqlcipher")
            val factory = SupportOpenHelperFactory(ClaveDb.obtener(context))
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mypes.db")
                .openHelperFactory(factory)
                .build()
                .also { instancia = it }
        }
    }
}
