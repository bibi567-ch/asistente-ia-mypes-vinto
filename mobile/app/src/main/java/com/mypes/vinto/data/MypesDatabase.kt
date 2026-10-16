package com.mypes.vinto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Producto::class,
        Usuario::class,
        Venta::class,
        DetalleVenta::class,
        MovimientoCaja::class,
        OutboxEntry::class
    ],
    version = 2,
    exportSchema = true
)
abstract class MypesDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ventaDao(): VentaDao
    abstract fun outboxDao(): OutboxDao

    companion object {
        @Volatile
        private var instancia: MypesDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS ventas (
                        id TEXT NOT NULL PRIMARY KEY,
                        total REAL NOT NULL,
                        formaPago TEXT NOT NULL,
                        clienteFiado TEXT,
                        creadoEn INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS detalle_ventas (
                        id TEXT NOT NULL PRIMARY KEY,
                        ventaId TEXT NOT NULL,
                        productoId TEXT NOT NULL,
                        cantidad REAL NOT NULL,
                        precioUnitario REAL NOT NULL,
                        FOREIGN KEY (ventaId) REFERENCES ventas(id) ON DELETE CASCADE,
                        FOREIGN KEY (productoId) REFERENCES productos(id) ON DELETE RESTRICT
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_detalle_ventas_ventaId ON detalle_ventas(ventaId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_detalle_ventas_productoId ON detalle_ventas(productoId)")
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS movimientos_caja (
                        id TEXT NOT NULL PRIMARY KEY,
                        tipo TEXT NOT NULL,
                        monto REAL NOT NULL,
                        concepto TEXT NOT NULL,
                        ventaId TEXT,
                        creadoEn INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS outbox (
                        id TEXT NOT NULL PRIMARY KEY,
                        tipo TEXT NOT NULL,
                        operationId TEXT NOT NULL,
                        schemaVersion INTEGER NOT NULL,
                        payloadJson TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        intentos INTEGER NOT NULL,
                        creadoEn INTEGER NOT NULL
                    )"""
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_outbox_operationId ON outbox(operationId)")
            }
        }

        fun obtener(context: Context): MypesDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    MypesDatabase::class.java,
                    "mypes-local.db"
                ).addMigrations(MIGRATION_1_2).build().also { instancia = it }
            }
    }
}
