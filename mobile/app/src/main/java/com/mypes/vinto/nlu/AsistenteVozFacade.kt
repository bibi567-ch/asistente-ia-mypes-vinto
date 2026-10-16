package com.mypes.vinto.nlu

import android.content.Context

class AsistenteVozFacade(context: Context) : AutoCloseable {
    private val asr = AsrEngine(context)
    private val clasificador = IntentClassifier()
    private val extractor = EntityExtractor()

    data class Resultado(
        val intencion: IntentClassifier.Intencion,
        val entidades: EntityExtractor.Entidades,
        val textoOriginal: String
    )

    fun inicializar(): Boolean = asr.inicializar()

    fun procesar(texto: String): Resultado = Resultado(
        intencion = clasificador.clasificar(texto),
        entidades = extractor.extraer(texto),
        textoOriginal = texto
    )

    fun escuchar(
        onParcial: (String) -> Unit,
        onResultado: (Resultado) -> Unit,
        onError: (String) -> Unit
    ) {
        asr.escuchar(object : AsrEngine.Resultado {
            override fun onTextoParcial(texto: String) = onParcial(texto)
            override fun onTextoFinal(texto: String) = onResultado(procesar(texto))
            override fun onError(mensaje: String) = onError(mensaje)
        })
    }

    fun detener() = asr.detener()
    override fun close() = asr.close()
}
