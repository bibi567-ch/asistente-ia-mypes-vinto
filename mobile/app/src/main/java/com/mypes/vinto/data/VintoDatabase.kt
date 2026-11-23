package com.mypes.vinto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Usuario::class, OutboxEntry::class], version = 3, exportSchema = true)
abstract class VintoDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun outboxDao(): OutboxDao

    companion object {
        @Volatile
        private var instancia: VintoDatabase? = null

        fun obtener(context: Context): VintoDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    VintoDatabase::class.java,
                    "mypes-local.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { instancia = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `outbox_entries` (" +
                        "`id` TEXT NOT NULL, `textoComando` TEXT NOT NULL, `intencion` TEXT NOT NULL, " +
                        "`timestamp` INTEGER NOT NULL, `confirmado` INTEGER NOT NULL, `ejecutado` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `outbox_entries` ADD COLUMN `sincronizado` INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
