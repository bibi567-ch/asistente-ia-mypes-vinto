package com.mypes.vinto.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.VoiceLog
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.AssistantQueryRequest
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.nlu.PhoneActionExecutor
import com.mypes.vinto.sync.AuthSession
import com.mypes.vinto.sync.SyncScheduler
import java.io.IOException
import java.text.Normalizer
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class MensajeAsistente(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val esUsuario: Boolean,
    val corregible: Boolean = false
)

sealed interface EstadoAccion {
    data object Inactivo : EstadoAccion

    data class EsperandoConfirmacion(
        val intencion: IntentClassifier.Intencion,
        val parametros: EntityExtractor.Entidades,
        val textoOriginal: String
    ) : EstadoAccion
}

data class DashboardEstado(
    val escuchando: Boolean = false,
    val modeloDisponible: Boolean? = null,
    val textoParcial: String = "",
    val mensajes: List<MensajeAsistente> = listOf(
        MensajeAsistente(
            texto = "Soy Yapay, tu asistente. Puedo ayudarte con acciones del teléfono y consultas de negocio. " +
                "Las consultas en línea usan una guía de demostración.",
            esUsuario = false
        )
    ),
    val estadoAccion: EstadoAccion = EstadoAccion.Inactivo,
    val mensajeError: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)
    private val asistente = AsistenteVozFacade(application)
    private val actionExecutor = PhoneActionExecutor(application)
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private val _estado = MutableStateFlow(DashboardEstado())
    val estado: StateFlow<DashboardEstado> = _estado

    init {
        _estado.value = _estado.value.copy(modeloDisponible = asistente.inicializar())
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
    }

    fun iniciarEscucha() {
        if (_estado.value.escuchando) return
        _estado.value = _estado.value.copy(
            escuchando = true,
            textoParcial = "",
            mensajeError = null
        )
        asistente.escuchar(
            onParcial = { texto ->
                _estado.value = _estado.value.copy(textoParcial = texto)
            },
            onResultado = { resultado ->
                viewModelScope.launch { procesarResultado(resultado) }
            },
            onError = { mensaje ->
                viewModelScope.launch {
                    _estado.value = _estado.value.copy(
                        escuchando = false,
                        textoParcial = ""
                    )
                    agregarMensaje(mensaje, esUsuario = false)
                }
            }
        )
    }

    fun detenerEscucha() {
        asistente.detener()
        _estado.value = _estado.value.copy(escuchando = false, textoParcial = "")
    }

    fun confirmarAccion() {
        val pendiente = _estado.value.estadoAccion as? EstadoAccion.EsperandoConfirmacion ?: return
        val ejecutado = if (pendiente.intencion == IntentClassifier.Intencion.LEER_TEXTO) {
            !pendiente.parametros.textoALeer.isNullOrBlank()
        } else {
            actionExecutor.ejecutar(pendiente.intencion, pendiente.parametros, confirmar = true)
        }
        val texto = when {
            !ejecutado -> "No pude completar la acción. Revisa los datos, permisos y aplicaciones disponibles."
            pendiente.intencion == IntentClassifier.Intencion.LEER_TEXTO ->
                pendiente.parametros.textoALeer.orEmpty()
            else -> respuestaDeAccion(pendiente.intencion)
        }
        _estado.value = _estado.value.copy(estadoAccion = EstadoAccion.Inactivo)
        agregarMensaje(texto, esUsuario = false)
        if (ejecutado) hablar(texto)
    }

    fun cancelarAccion() {
        if (_estado.value.estadoAccion !is EstadoAccion.EsperandoConfirmacion) return
        _estado.value = _estado.value.copy(estadoAccion = EstadoAccion.Inactivo)
        responder("Acción cancelada. No se realizó ningún cambio.")
    }

    fun registrarCorreccion(textoOriginal: String, textoCorregido: String, compartir: Boolean) {
        val original = textoOriginal.trim()
        val corregido = textoCorregido.trim()
        if (original.isEmpty() || corregido.isEmpty()) {
            _estado.value = _estado.value.copy(mensajeError = "La corrección no puede estar vacía.")
            return
        }

        viewModelScope.launch {
            try {
                val intent = IntentClassifier().clasificar(corregido)
                db.voiceLogDao().insertar(
                    VoiceLog(
                        textoOriginal = original,
                        textoCorregido = corregido,
                        intencion = intent.name,
                        consentToUpload = compartir
                    )
                )
                agregarMensaje(
                    if (compartir) {
                        "Corrección guardada. Autorizaste su envío para mejorar el reconocimiento."
                    } else {
                        "Corrección guardada solo en este dispositivo."
                    },
                    esUsuario = false
                )
                if (compartir) SyncScheduler.programarSync(getApplication())
            } catch (error: Exception) {
                _estado.value = _estado.value.copy(
                    mensajeError = error.message ?: "No se pudo guardar la corrección."
                )
            }
        }
    }

    fun nombreAccion(intencion: IntentClassifier.Intencion): String = when (intencion) {
        IntentClassifier.Intencion.LLAMAR_CONTACTO -> "llamar al número reconocido"
        IntentClassifier.Intencion.ENVIAR_MENSAJE -> "abrir el compositor de mensajes"
        IntentClassifier.Intencion.LEER_TEXTO -> "leer el texto en voz alta"
        IntentClassifier.Intencion.CREAR_ALARMA -> "crear una alarma"
        IntentClassifier.Intencion.ABRIR_APP -> "abrir la aplicación reconocida"
        IntentClassifier.Intencion.CONSULTAR_HORA -> "consultar la hora"
        IntentClassifier.Intencion.LINTERNA -> "cambiar el estado de la linterna"
        IntentClassifier.Intencion.AJUSTES_RED -> "abrir los ajustes de Wi-Fi"
        IntentClassifier.Intencion.GUIA_TELEFONO -> "mostrar la guía"
        IntentClassifier.Intencion.CONSULTA_COGNITIVA -> "consultar la guía de negocio"
        IntentClassifier.Intencion.AYUDA -> "mostrar los comandos disponibles"
    }

    private suspend fun procesarResultado(resultado: AsistenteVozFacade.Resultado) {
        _estado.value = _estado.value.copy(
            escuchando = false,
            textoParcial = "",
            mensajeError = null
        )
        agregarMensaje(resultado.textoOriginal, esUsuario = true, corregible = true)

        if (resultado.confianzaVosk < 0.70f && !resultado.transcripcionNube) {
            agregarMensaje(
                "La confianza de la transcripción local es baja. No ejecutaré ninguna acción; " +
                    "puedes corregir el texto y volver a intentarlo.",
                esUsuario = false
            )
            return
        }
        if (resultado.asrSimulado) {
            agregarMensaje(
                "El ASR cloud está simulado; no transcribió el audio. Se conserva el texto de Vosk.",
                esUsuario = false
            )
        }

        if (
            resultado.intencion == IntentClassifier.Intencion.CONSULTA_COGNITIVA
            || resultado.transcripcionNube
        ) {
            consultarGuia(resultado.textoOriginal)
            return
        }

        val missing = parametroFaltante(resultado.intencion, resultado.entidades)
        if (missing != null) {
            responder(missing)
            return
        }

        if (actionExecutor.requiresConfirmation(resultado.intencion)) {
            _estado.value = _estado.value.copy(
                estadoAccion = EstadoAccion.EsperandoConfirmacion(
                    resultado.intencion,
                    resultado.entidades,
                    resultado.textoOriginal
                )
            )
            hablar("¿Confirmas que debo ${nombreAccion(resultado.intencion)}?")
            return
        }

        responder(respuestaInformativa(resultado.intencion))
    }

    private suspend fun consultarGuia(pregunta: String) {
        val token = AuthSession.token(getApplication())
        if (token.isNullOrBlank()) {
            responder("Para consultar la guía del servidor, primero vincula tu cuenta. No envié la pregunta.")
            return
        }

        try {
            val response = withContext(Dispatchers.IO) {
                ApiClient.assistantApi.consultar(
                    authorization = "Bearer $token",
                    request = AssistantQueryRequest(pregunta = pregunta, idioma = "es")
                )
            }
            responder(response.respuesta)
        } catch (error: CancellationException) {
            throw error
        } catch (error: HttpException) {
            responder("La guía respondió con HTTP ${error.code()}; no se pudo completar la consulta.")
        } catch (_: IOException) {
            responder("No hay conexión con la guía del servidor. Inténtalo cuando tengas Internet.")
        } catch (error: Exception) {
            responder(error.message ?: "No se pudo consultar la guía.")
        }
    }

    private fun parametroFaltante(
        intencion: IntentClassifier.Intencion,
        entidades: EntityExtractor.Entidades
    ): String? = when (intencion) {
        IntentClassifier.Intencion.LLAMAR_CONTACTO, IntentClassifier.Intencion.ENVIAR_MENSAJE ->
            if (entidades.numeroTelefono.isNullOrBlank()) "Indica un número de teléfono explícito." else null
        IntentClassifier.Intencion.CREAR_ALARMA ->
            if (entidades.hora == null) "Indica la hora de la alarma, por ejemplo: «pon alarma a las 7»." else null
        IntentClassifier.Intencion.ABRIR_APP ->
            if (entidades.nombreApp.isNullOrBlank()) "Indica qué aplicación quieres abrir." else null
        IntentClassifier.Intencion.LEER_TEXTO ->
            if (entidades.textoALeer.isNullOrBlank()) "Indica el texto que quieres que lea." else null
        else -> null
    }

    private fun respuestaInformativa(intencion: IntentClassifier.Intencion): String = when (intencion) {
        IntentClassifier.Intencion.CONSULTAR_HORA ->
            "Son las ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}."
        IntentClassifier.Intencion.GUIA_TELEFONO ->
            "Toca el micrófono y di, por ejemplo: llama al número, pon una alarma o abre WhatsApp."
        IntentClassifier.Intencion.AYUDA ->
            "Puedo llamar, preparar un mensaje, leer texto, crear una alarma, abrir aplicaciones, " +
                "consultar la hora, usar la linterna o responder preguntas de negocio."
        else -> "No pude completar esa solicitud."
    }

    private fun respuestaDeAccion(intencion: IntentClassifier.Intencion): String = when (intencion) {
        IntentClassifier.Intencion.LLAMAR_CONTACTO -> "Acción de llamada iniciada en el teléfono."
        IntentClassifier.Intencion.ENVIAR_MENSAJE -> "Se abrió la aplicación de mensajes; confirma allí el envío."
        IntentClassifier.Intencion.LEER_TEXTO -> "Lectura completada."
        IntentClassifier.Intencion.CREAR_ALARMA -> "Se abrió el reloj para crear la alarma."
        IntentClassifier.Intencion.ABRIR_APP -> "Se abrió la aplicación solicitada."
        IntentClassifier.Intencion.LINTERNA -> "Se actualizó el estado de la linterna."
        IntentClassifier.Intencion.AJUSTES_RED -> "Se abrieron los ajustes de Wi-Fi."
        else -> "Acción completada."
    }

    private fun responder(texto: String) {
        agregarMensaje(texto, esUsuario = false)
        hablar(texto)
    }

    private fun agregarMensaje(texto: String, esUsuario: Boolean, corregible: Boolean = false) {
        _estado.value = _estado.value.copy(
            mensajes = _estado.value.mensajes + MensajeAsistente(
                texto = texto,
                esUsuario = esUsuario,
                corregible = corregible
            )
        )
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
