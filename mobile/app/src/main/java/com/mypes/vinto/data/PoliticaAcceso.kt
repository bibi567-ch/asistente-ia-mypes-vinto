package com.mypes.vinto.data

object PoliticaAcceso {
    private const val MAX_INTENTOS = 5
    private const val DURACION_BLOQUEO_MS = 60_000L

    data class EstadoAcceso(val intentosFallidos: Int, val bloqueadoHasta: Long)

    fun estaBloqueado(estado: EstadoAcceso, ahora: Long): Boolean =
        estado.bloqueadoHasta > ahora

    fun segundosRestantes(estado: EstadoAcceso, ahora: Long): Long =
        ((estado.bloqueadoHasta - ahora).coerceAtLeast(0) + 999) / 1000

    fun registrarFallo(estado: EstadoAcceso, ahora: Long): EstadoAcceso {
        val intentos = estado.intentosFallidos + 1
        return if (intentos >= MAX_INTENTOS) {
            EstadoAcceso(0, ahora + DURACION_BLOQUEO_MS)
        } else {
            EstadoAcceso(intentos, 0)
        }
    }
}
