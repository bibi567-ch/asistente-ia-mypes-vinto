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

    fun procesarPinDictado(
        onPinDetectado: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        asr.escuchar(object : AsrEngine.Resultado {
            override fun onTextoParcial(texto: String) = Unit
            override fun onTextoFinal(texto: String) {
                val pin = extractor.extraerPin(texto)
                    ?: texto.lowercase()
                        .replace("cero", "0")
                        .replace("uno", "1")
                        .replace("un", "1")
                        .replace("una", "1")
                        .replace("dos", "2")
                        .replace("tres", "3")
                        .replace("cuatro", "4")
                        .replace("cinco", "5")
                        .replace("seis", "6")
                        .replace("siete", "7")
                        .replace("ocho", "8")
                        .replace("nueve", "9")
                        .filter { it.isDigit() }

                if (pin.length == 4 && pin.all(Char::isDigit)) {
                    onPinDetectado(pin, texto)
                } else {
                    onError("No se entendió el PIN. Di los 4 números claramente.")
                }
            }

            override fun onError(mensaje: String) = onError(mensaje)
        })
    }

    fun detener() = asr.detener()
    override fun close() = asr.close()
}
