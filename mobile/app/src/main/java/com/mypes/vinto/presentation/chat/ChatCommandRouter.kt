package com.mypes.vinto.presentation.chat

import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.nlu.AsistenteVozFacade
import java.text.Normalizer

sealed class ChatDestination {
    data class Venta(val producto: String?, val cantidad: Double?) : ChatDestination()
    data class Inventario(val filtro: String?) : ChatDestination()
    object Fiados : ChatDestination()
}

internal fun resolverDestinoChat(
    texto: String,
    intencion: IntentClassifier.Intencion,
    entidades: EntityExtractor.Entidades,
    puedePrepararProducto: Boolean
): ChatDestination? {
    return when (intencion) {
        IntentClassifier.Intencion.REGISTRAR_VENTA ->
            ChatDestination.Venta(entidades.producto.limpiar(), entidades.cantidad)
        IntentClassifier.Intencion.CONSULTAR_STOCK ->
            ChatDestination.Inventario(entidades.producto.limpiar() ?: extraerFiltroStock(texto))
        IntentClassifier.Intencion.AGREGAR_PRODUCTO ->
            if (puedePrepararProducto) null
            else ChatDestination.Inventario(extraerFiltroProducto(texto, entidades.producto))
        else -> if (esConsultaFiados(texto)) ChatDestination.Fiados else null
    }
}

internal fun errorTranscripcionParaChat(resultado: AsistenteVozFacade.Resultado): String? {
    val texto = resultado.textoOriginal.trim()
    val textoNormalizado = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return when {
        texto.isBlank() || textoNormalizado == "de" ->
            "No te escuché bien. Mantén presionado el micrófono, habla y suelta para enviar."
        resultado.requiereTranscripcionNube && !resultado.transcripcionNube ->
            "El modo quechua no produjo una transcripción cloud. Revisa la conexión y vuelve a intentarlo; no ejecuté ninguna acción."
        resultado.confianzaVosk < 0.70f && !resultado.transcripcionNube ->
            "La confianza de la transcripción es baja. No ejecutaré ninguna acción; mantén presionado para repetir."
        resultado.transcripcionNube && resultado.clasificacionRemota &&
            resultado.intencionRemota == null ->
            "Transcribí el audio, pero no reconocí una intención con suficiente confianza. No ejecuté ninguna acción."
        resultado.requiereTranscripcionNube && !resultado.clasificacionRemota ->
            "Transcribí el audio, pero el servicio no confirmó una intención. No ejecuté ninguna acción."
        else -> null
    }
}

internal fun mostrarTranscripcionEnChat(resultado: AsistenteVozFacade.Resultado): Boolean {
    val texto = resultado.textoOriginal.trim()
    val normalizado = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return texto.isNotBlank() && normalizado != "de" &&
        (!resultado.requiereTranscripcionNube || resultado.transcripcionNube)
}

private fun extraerFiltroStock(texto: String): String? =
    Regex("""(?i)(?:stock|inventario|existencias|yupay)(?:\s+de)?\s+([\p{L}\d]+)""")
        .find(texto)
        ?.groupValues
        ?.get(1)
        ?.limpiar()

private fun extraerFiltroProducto(texto: String, producto: String?): String? {
    producto.limpiar()?.let { return it }
    val match = Regex(
        """(?i)(?:agrega|agregar|añade|anade|nuevo\s+producto)\s+(?:el\s+)?(?:producto\s+)?([\p{L}\d][\p{L}\d\s'-]*)"""
    ).find(texto) ?: return null
    return match.groupValues[1]
        .replace(Regex("""\s+por\s+.+$""", RegexOption.IGNORE_CASE), "")
        .limpiar()
}

private fun esConsultaFiados(texto: String): Boolean {
    val normalizado = Normalizer.normalize(texto.lowercase().trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return listOf("fiado", "fiados", "deuda", "deudas", "cuentas por cobrar")
        .any(normalizado::contains)
}

private fun String?.limpiar(): String? =
    this?.trim()?.trim(',', '.', ';', ':', '!', '?')?.takeIf(String::isNotBlank)
