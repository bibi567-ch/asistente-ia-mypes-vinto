package com.mypes.vinto.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.dto.SyncBatchRequest
import com.mypes.vinto.network.dto.TransaccionEntrante
import com.mypes.vinto.network.dto.TransaccionPayload
import org.json.JSONObject

class OutboxWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val token = AuthSession.token(applicationContext) ?: return Result.retry()
        val db = MypesDatabase.obtener(applicationContext)
        val outboxDao = db.outboxDao()
        val pendientes = outboxDao.pendientes()
        if (pendientes.isEmpty()) return Result.success()

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
            } catch (_: Exception) {
                outboxDao.marcarErrorPorId(entry.id)
            }
        }

        if (transacciones.isEmpty()) return Result.success()

        return try {
            val response = ApiClient.syncApi.enviarLote(
                authorization = "Bearer $token",
                body = SyncBatchRequest(transacciones)
            )
            response.resultados.forEach { resultado ->
                when (resultado.estado) {
                    "ACEPTADA", "DUPLICADA" -> outboxDao.marcarSincronizada(resultado.operationId)
                    "CONFLICTO", "RECHAZADA" -> outboxDao.marcarError(resultado.operationId)
                }
            }
            outboxDao.limpiarSincronizadas()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "outbox_sync"
    }
}
