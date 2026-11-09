package com.mypes.vinto.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Producto
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.dto.SyncBatchRequest
import com.mypes.vinto.network.dto.TransaccionEntrante
import com.mypes.vinto.network.dto.TransaccionPayload
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class OutboxWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val token = AuthSession.token(applicationContext) ?: return Result.failure()
        val authorization = "Bearer $token"
        val db = MypesDatabase.obtener(applicationContext)
        val ventasResultado = sincronizarVentas(authorization, db)
        if (ventasResultado != null) return ventasResultado
        return sincronizarCatalogo(authorization, db)
    }

    private suspend fun sincronizarVentas(
        authorization: String,
        database: MypesDatabase
    ): Result? {
        val outboxDao = database.outboxDao()
        while (true) {
            val pendientes = outboxDao.pendientes()
            if (pendientes.isEmpty()) return null

            val transacciones = mutableListOf<TransaccionEntrante>()
            pendientes.forEach { entry ->
                try {
                    val json = JSONObject(entry.payloadJson)
                    transacciones += TransaccionEntrante(
                        operationId = entry.operationId,
                        tipo = entry.tipo,
                        payload = TransaccionPayload(
                            ventaId = json.getString("ventaId"),
                            productoId = json.getString("productoId"),
                            cantidad = json.getDouble("cantidad"),
                            precioUnitario = json.getDouble("precioUnitario"),
                            total = json.getDouble("total"),
                            formaPago = json.getString("formaPago"),
                            clienteFiado = if (json.isNull("clienteFiado")) null else json.getString("clienteFiado"),
                            creadoEn = json.getLong("creadoEn")
                        ),
                        schemaVersion = entry.schemaVersion
                    )
                } catch (error: Exception) {
                    Log.e("OutboxWorker", "No se pudo leer la operación local ${entry.operationId}.", error)
                    outboxDao.marcarErrorPorId(entry.id)
                }
            }

            try {
                if (transacciones.isEmpty()) continue
                val response = ApiClient.syncApi.enviarLote(
                    authorization = authorization,
                    body = SyncBatchRequest(transacciones)
                )
                if (response.procesadas != transacciones.size) {
                    throw IllegalStateException("La API devolvió un total de operaciones inconsistente.")
                }
                val operationIds = transacciones.mapTo(mutableSetOf()) { it.operationId }
                val resultIds = response.resultados.map { it.operationId }
                if (
                    resultIds.size != operationIds.size
                    || resultIds.toSet() != operationIds
                    || resultIds.size != resultIds.toSet().size
                ) {
                    throw IllegalStateException("La API devolvió resultados incompletos o duplicados.")
                }
                response.resultados.forEach { resultado ->
                    when (resultado.estado) {
                        "ACEPTADA", "DUPLICADA" -> outboxDao.marcarSincronizada(resultado.operationId)
                        "CONFLICTO", "RECHAZADA" -> outboxDao.marcarError(resultado.operationId)
                        else -> throw IllegalStateException(
                            "La API devolvió un estado desconocido para ${resultado.operationId}."
                        )
                    }
                }
                outboxDao.limpiarSincronizadas()
            } catch (error: HttpException) {
                if (error.code() == 401) {
                    AuthSession.clear(applicationContext)
                    Log.w("OutboxWorker", "El token de sincronización expiró; vuelve a iniciar sesión.")
                    return Result.failure()
                } else {
                    Log.e("OutboxWorker", "La API rechazó el lote con HTTP ${error.code()}.", error)
                    return Result.retry()
                }
            } catch (error: IOException) {
                Log.w("OutboxWorker", "No se pudo conectar con la API; se reintentará.", error)
                return Result.retry()
            } catch (error: Exception) {
                Log.e("OutboxWorker", "Error inesperado al sincronizar el lote.", error)
                return Result.retry()
            }
        }
        return null
    }

    private suspend fun sincronizarCatalogo(
        authorization: String,
        database: MypesDatabase
    ): Result {
        val desde = AuthSession.lastCatalogSync(applicationContext)
        var cursorActualizadoEn: Long? = null
        var cursorId: String? = null
        var timestampInicial: Long? = null

        return try {
            do {
                val response = ApiClient.syncApi.sincronizarProductos(
                    authorization = authorization,
                    desde = desde,
                    cursorActualizadoEn = cursorActualizadoEn,
                    cursorId = cursorId
                )
                if (response.total != response.items.size || response.timestamp < desde) {
                    throw IllegalStateException("Respuesta de sincronización del catálogo inconsistente.")
                }
                if (timestampInicial == null) timestampInicial = response.timestamp

                response.items.forEach { item ->
                    if (
                        item.id.isBlank()
                        || item.nombre.isBlank()
                        || !item.precio.isFinite()
                        || item.precio < 0.0
                        || !item.stock.isFinite()
                    ) {
                        throw IllegalStateException("La API devolvió un producto inválido.")
                    }
                    database.productoDao().guardarProductoSincronizado(
                        Producto(
                            id = item.id,
                            nombre = item.nombre,
                            precio = item.precio,
                            stock = item.stock,
                            actualizadoEn = item.actualizadoEn
                        )
                    )
                }

                if (response.items.size == 500) {
                    val ultimo = response.items.last()
                    val cursorAnterior = cursorActualizadoEn
                    val idAnterior = cursorId
                    if (
                        cursorAnterior != null
                        && (ultimo.actualizadoEn < cursorAnterior
                            || (ultimo.actualizadoEn == cursorAnterior && ultimo.id <= idAnterior.orEmpty()))
                    ) {
                        throw IllegalStateException("El cursor del catálogo no avanzó.")
                    }
                    cursorActualizadoEn = ultimo.actualizadoEn
                    cursorId = ultimo.id
                }
            } while (response.items.size == 500)

            AuthSession.saveLastCatalogSync(
                applicationContext,
                maxOf(desde, timestampInicial ?: desde)
            )
            Result.success()
        } catch (error: HttpException) {
            if (error.code() == 401) {
                AuthSession.clear(applicationContext)
                Log.w("OutboxWorker", "El token de sincronización expiró; vuelve a iniciar sesión.")
                Result.failure()
            } else {
                Log.e("OutboxWorker", "La API rechazó el catálogo con HTTP ${error.code()}.", error)
                Result.retry()
            }
        } catch (error: IOException) {
            Log.w("OutboxWorker", "No se pudo descargar el catálogo; se reintentará.", error)
            Result.retry()
        } catch (error: Exception) {
            Log.e("OutboxWorker", "Error inesperado al sincronizar el catálogo.", error)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "outbox_sync"
    }
}
