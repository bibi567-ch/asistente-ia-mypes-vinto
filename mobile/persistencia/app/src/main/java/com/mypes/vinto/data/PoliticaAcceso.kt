package com.mypes.vinto.data

data class EstadoAcceso(val intentosFallidos: Int = 0, val bloqueadoHasta: Long = 0)

object PoliticaAcceso {
    const val MAX_INTENTOS = 3
    const val BLOQUEO_MS = 30_000L

    fun estaBloqueado(e: EstadoAcceso, ahora: Long): Boolean = ahora < e.bloqueadoHasta

    fun segundosRestantes(e: EstadoAcceso, ahora: Long): Long =
        if (estaBloqueado(e, ahora)) (e.bloqueadoHasta - ahora + 999) / 1000 else 0

    fun registrarFallo(e: EstadoAcceso, ahora: Long): EstadoAcceso {
        if (estaBloqueado(e, ahora)) return e
        val n = e.intentosFallidos + 1
        return if (n >= MAX_INTENTOS) EstadoAcceso(0, ahora + BLOQUEO_MS) else EstadoAcceso(n, 0)
    }

    fun registrarExito(): EstadoAcceso = EstadoAcceso()
}
