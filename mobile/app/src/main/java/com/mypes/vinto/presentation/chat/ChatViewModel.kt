package com.mypes.vinto.presentation.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.nlu.MensajeChat
import com.mypes.vinto.nlu.YapayAgent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoChatScreen(
    val textoEntrada: String = "",
    val procesando: Boolean = false,
    val destinoPendiente: ChatDestination? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val agente = YapayAgent(application)
    private val clasificador = IntentClassifier()
    private val extractor = EntityExtractor()

    val mensajes: StateFlow<List<MensajeChat>> = agente.estadoChat

    private val _estado = MutableStateFlow(EstadoChatScreen())
    val estado: StateFlow<EstadoChatScreen> = _estado.asStateFlow()

    fun actualizarTexto(texto: String) {
        _estado.update { it.copy(textoEntrada = texto) }
    }

    fun enviarMensaje() {
        val texto = _estado.value.textoEntrada.trim()
        if (texto.isBlank() || _estado.value.procesando) return

        _estado.update { it.copy(textoEntrada = "") }
        procesarMensaje(texto)
    }

    fun procesarTranscripcionVoz(resultado: AsistenteVozFacade.Resultado) {
        val error = errorTranscripcionParaChat(resultado)
        if (error != null) {
            val texto = if (mostrarTranscripcionEnChat(resultado)) resultado.textoOriginal.trim() else ""
            agente.registrarInteraccion(texto, error)
            return
        }
        procesarMensaje(
            texto = resultado.textoOriginal.trim(),
            intencionForzada = resultado.intencionRemota ?: resultado.intencion
        )
    }

    private fun procesarMensaje(
        texto: String,
        intencionForzada: IntentClassifier.Intencion? = null
    ) {
        if (texto.isBlank() || _estado.value.procesando) return
        _estado.update { it.copy(procesando = true, destinoPendiente = null) }
        viewModelScope.launch {
            try {
                val intencion = intencionForzada ?: clasificador.clasificar(texto)
                val destino = if (agente.esperandoConfirmacionProducto) {
                    null
                } else {
                    resolverDestinoChat(
                        texto = texto,
                        intencion = intencion,
                        entidades = extractor.extraer(texto),
                        puedePrepararProducto = agente.puedePrepararProducto(texto)
                    )
                }

                if (destino == null) {
                    agente.procesarComando(texto, intencionForzada)
                } else {
                    agente.registrarInteraccion(texto, respuestaPara(destino))
                    _estado.update { it.copy(destinoPendiente = destino) }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                agente.registrarInteraccion(
                    "",
                    "No pude procesar el mensaje: ${error.message ?: "error inesperado"}."
                )
            } finally {
                _estado.update { it.copy(procesando = false) }
            }
        }
    }

    fun consumirDestinoPendiente() {
        _estado.update { it.copy(destinoPendiente = null) }
    }

    private fun respuestaPara(destino: ChatDestination): String = when (destino) {
        is ChatDestination.Venta -> "Abro el formulario de venta. Revisa los datos y confirma antes de guardar."
        is ChatDestination.Inventario -> "Abro el inventario para revisar los productos."
        ChatDestination.Fiados -> "Abro las cuentas por cobrar."
    }
}
