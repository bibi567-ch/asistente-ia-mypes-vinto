package com.mypes.vinto.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.OutboxEntry
import com.mypes.vinto.data.VintoDatabase
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.nlu.PhoneActionExecutor
import com.mypes.vinto.sync.OutboxWorker
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface EstadoAccion {
    data object Inactivo : EstadoAccion

    data class EsperandoConfirmacion(
        val intencion: IntentClassifier.Intencion,
        val parametros: EntityExtractor.Entidades,
        val textoOriginal: String,
        val entradaId: String
    ) : EstadoAccion
}

data class DashboardEstado(
    val escuchando: Boolean = false,
    val textoParcial: String = "",
    val ultimoResultado: AsistenteVozFacade.Resultado? = null,
    val estadoAccion: EstadoAccion = EstadoAccion.Inactivo,
    val historial: List<OutboxEntry> = emptyList(),
    val respuesta: String? = null,
    val mensajeError: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VintoDatabase.obtener(application)
    private val asistente = AsistenteVozFacade(application)
    private val actionExecutor = PhoneActionExecutor(application)
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private val _estado = MutableStateFlow(DashboardEstado())
    val estado: StateFlow<DashboardEstado> = _estado

    init {
        textToSpeech = TextToSpeech(application) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                val result = textToSpeech?.setLanguage(Locale("es", "BO"))
                if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.LANG_MISSING_DATA
                ) {
                    textToSpeech?.setLanguage(Locale("es", "ES"))
                }
            }
        }
        asistente.inicializar()
        cargarHistorial()
        OutboxWorker.programar(application)
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(historial = db.outboxDao().recientes())
        }
    }

    fun iniciarEscucha() {
        _estado.value = _estado.value.copy(
            escuchando = true,
            textoParcial = "",
            mensajeError = null,
            respuesta = null
        )
        asistente.escuchar(
            onParcial = { texto -> _estado.value = _estado.value.copy(textoParcial = texto) },
            onResultado = { resultado ->
                val entrada = OutboxEntry(
                    textoComando = resultado.textoOriginal,
                    intencion = resultado.intencion.name
                )
                val respuesta = respuestaInformativa(resultado.intencion)
                val error = errorInformativo(resultado.intencion)
                val requiereConfirmacion = actionExecutor.requiresConfirmation(resultado.intencion)
                val estadoAccion = if (requiereConfirmacion) {
                    EstadoAccion.EsperandoConfirmacion(
                        resultado.intencion,
                        resultado.entidades,
                        resultado.textoOriginal,
                        entrada.id
                    )
                } else {
                    EstadoAccion.Inactivo
                }
                _estado.value = _estado.value.copy(
                    escuchando = false,
                    textoParcial = "",
                    ultimoResultado = resultado,
                    estadoAccion = estadoAccion,
                    respuesta = respuesta,
                    mensajeError = error
                )
                if (requiereConfirmacion) {
                    hablar("¿Quieres que ejecute ${nombreAccion(resultado.intencion)}?")
                } else if (respuesta != null) {
                    hablar(respuesta)
                } else if (error != null) {
                    hablar(error)
                }

                viewModelScope.launch {
                    db.outboxDao().insertar(entrada)
                    if (!requiereConfirmacion) {
                        db.outboxDao().actualizarResultado(
                            entrada.id,
                            confirmado = false,
                            ejecutado = respuesta != null
                        )
                        OutboxWorker.programar(getApplication())
                    }
                    cargarHistorial()
                }
            },
            onError = { mensaje ->
                _estado.value = _estado.value.copy(
                    escuchando = false,
                    textoParcial = "",
                    mensajeError = mensaje
                )
                hablar(mensaje)
            }
        )
    }

    fun confirmarAccion() {
        val pendiente = _estado.value.estadoAccion as? EstadoAccion.EsperandoConfirmacion ?: return
        val textoALeer = pendiente.parametros.textoALeer
        val ejecutado = if (pendiente.intencion == IntentClassifier.Intencion.LEER_TEXTO) {
            !textoALeer.isNullOrBlank()
        } else {
            actionExecutor.ejecutar(
                pendiente.intencion,
                pendiente.parametros,
                confirmar = true
            )
        }
        val resultado = if (ejecutado) {
            if (pendiente.intencion == IntentClassifier.Intencion.LEER_TEXTO) {
                textoALeer.orEmpty()
            } else {
                respuestaDeAccion(pendiente.intencion)
            }
        } else {
            "No pude completar la acción. Revisa los datos e inténtalo otra vez."
        }
        _estado.value = _estado.value.copy(
            estadoAccion = EstadoAccion.Inactivo,
            respuesta = if (ejecutado && pendiente.intencion != IntentClassifier.Intencion.LEER_TEXTO) {
                resultado
            } else {
                null
            },
            mensajeError = resultado.takeUnless { ejecutado }
        )
        hablar(resultado)
        viewModelScope.launch {
            db.outboxDao().actualizarResultado(
                pendiente.entradaId,
                confirmado = true,
                ejecutado = ejecutado
            )
            OutboxWorker.programar(getApplication())
            cargarHistorial()
        }
    }

    fun cancelarAccion() {
        val pendiente = _estado.value.estadoAccion as? EstadoAccion.EsperandoConfirmacion ?: return
        _estado.value = _estado.value.copy(estadoAccion = EstadoAccion.Inactivo)
        hablar("Acción cancelada.")
        viewModelScope.launch {
            db.outboxDao().actualizarResultado(
                pendiente.entradaId,
                confirmado = false,
                ejecutado = false
            )
            OutboxWorker.programar(getApplication())
            cargarHistorial()
        }
    }

    fun detenerEscucha() {
        asistente.detener()
        _estado.value = _estado.value.copy(escuchando = false, textoParcial = "")
    }

    fun nombreAccion(intencion: IntentClassifier.Intencion): String = when (intencion) {
        IntentClassifier.Intencion.LLAMAR_CONTACTO -> "llamar"
        IntentClassifier.Intencion.ENVIAR_MENSAJE -> "enviar un mensaje"
        IntentClassifier.Intencion.CREAR_ALARMA -> "crear una alarma"
        IntentClassifier.Intencion.ABRIR_APP -> "abrir una aplicación"
        IntentClassifier.Intencion.LINTERNA -> "cambiar el estado de la linterna"
        IntentClassifier.Intencion.AJUSTES_RED -> "abrir los ajustes de red"
        IntentClassifier.Intencion.LEER_TEXTO -> "leer el texto"
        IntentClassifier.Intencion.CONSULTAR_HORA -> "consultar la hora"
        IntentClassifier.Intencion.GUIA_TELEFONO -> "mostrar la guía"
        IntentClassifier.Intencion.AYUDA -> "mostrar la ayuda"
        IntentClassifier.Intencion.DESCONOCIDO -> "procesar el comando"
    }

    private fun respuestaDeAccion(intencion: IntentClassifier.Intencion): String = when (intencion) {
        IntentClassifier.Intencion.LLAMAR_CONTACTO -> "Abriendo el marcador."
        IntentClassifier.Intencion.ENVIAR_MENSAJE -> "Abriendo mensajes. Confirma el envío en la aplicación."
        IntentClassifier.Intencion.CREAR_ALARMA -> "Abriendo el reloj para programar la alarma."
        IntentClassifier.Intencion.ABRIR_APP -> "Abriendo la aplicación."
        IntentClassifier.Intencion.LINTERNA -> "Linterna actualizada."
        IntentClassifier.Intencion.AJUSTES_RED -> "Abriendo los ajustes de red."
        else -> "Acción completada."
    }

    private fun respuestaInformativa(intencion: IntentClassifier.Intencion): String? =
        when (intencion) {
            IntentClassifier.Intencion.CONSULTAR_HORA ->
                "Son las ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}."
            IntentClassifier.Intencion.GUIA_TELEFONO ->
                "Toca el micrófono y di un comando, por ejemplo: llama a Juan o pon una alarma."
            IntentClassifier.Intencion.AYUDA ->
                "Puedes pedir una llamada, un mensaje, una alarma, abrir una app, consultar la hora o usar la linterna."
            else -> null
        }

    private fun errorInformativo(intencion: IntentClassifier.Intencion): String? =
        when (intencion) {
            IntentClassifier.Intencion.DESCONOCIDO ->
                "No reconocí ese comando. Inténtalo otra vez."
            else -> null
        }

    private fun hablar(texto: String) {
        if (ttsReady) {
            textToSpeech?.speak(
                texto,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "vinto-${System.currentTimeMillis()}"
            )
        }
    }

    override fun onCleared() {
        asistente.close()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onCleared()
    }
}
