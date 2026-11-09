package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.sync.OutboxWorker
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SincronizacionViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MypesDatabase.obtener(application)
    private val workManager = WorkManager.getInstance(application)

    val pendientes = database.outboxDao().observarPendientes()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val errores = database.outboxDao().observarErrores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val trabajo = workManager.getWorkInfosForUniqueWorkFlow(OutboxWorker.WORK_NAME)
        .map { it.lastOrNull()?.state }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun sincronizar() {
        SyncScheduler.programarSync(getApplication())
    }

    fun textoEstado(state: WorkInfo.State?): String = when (state) {
        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> "En espera de conexión"
        WorkInfo.State.RUNNING -> "Enviando operaciones pendientes..."
        WorkInfo.State.SUCCEEDED -> "Último intento completado"
        WorkInfo.State.FAILED -> "El intento requiere atención"
        WorkInfo.State.CANCELLED -> "Sincronización cancelada"
        null -> "Sin actividad de sincronización"
    }
}
