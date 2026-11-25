package com.mypes.vinto.viewmodel

import android.app.Application
import android.net.Uri
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class MensajeAsistente(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val esUsuario: Boolean,
    val corregible: Boolean = false
)

typealias ChatMessage = MensajeAsistente

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
    val modoQuechua: Boolean = false,
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
    val mensajeError: String? = null,
    val rutaPendiente: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)
    private val asistente = AsistenteVozFacade(application)
    private val actionExecutor = PhoneActionExecutor(application)
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private val _estado = MutableStateFlow(DashboardEstado())
    val estado: StateFlow<DashboardEstado> = _estado
    val historial: StateFlow<List<ChatMessage>> = _estado
        .map { it.mensajes }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _estado.value.mensajes)
    val escuchando: StateFlow<Boolean> = _estado
        .map { it.escuchando }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        _estado.value = _estado.value.copy(
            modoQuechua = asistente.modoQuechua()
        )
        viewModelScope.launch(Dispatchers.IO) {
            val modeloDisponible = asistente.inicializar()
            withContext(Dispatchers.Main) {
                _estado.value = _estado.value.copy(
                    modeloDisponible = modeloDisponible,
                    mensajeError = if (modeloDisponible) null else
                        "No se pudo cargar el modelo de voz. Revisa los assets de Vosk y vuelve a abrir Yapay."
                )
            }
        }
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
        if (!prepararEscucha()) return
        asistente.escuchar(
            onParcial = ::actualizarTextoParcial,
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

    fun iniciarEscuchaParaChat(onResultado: (AsistenteVozFacade.Resultado) -> Unit): Boolean {
        if (!prepararEscucha()) return false
        asistente.escuchar(
            onParcial = ::actualizarTextoParcial,
            onResultado = { resultado ->
                viewModelScope.launch {
                    _estado.value = _estado.value.copy(escuchando = false, textoParcial = "")
                    onResultado(resultado)
                }
            },
            onError = { mensaje ->
                viewModelScope.launch {
                    _estado.value = _estado.value.copy(
                        escuchando = false,
                        textoParcial = "",
                        mensajeError = mensaje
                    )
                }
            }
        )
        return true
    }

    private fun prepararEscucha(): Boolean {
        if (_estado.value.escuchando) return false
        if (_estado.value.modeloDisponible != true) {
            _estado.value = _estado.value.copy(
                mensajeError = "El modelo de voz todavía no está disponible."
            )
            return false
        }
        _estado.value = _estado.value.copy(
            escuchando = true,
            textoParcial = "",
            mensajeError = null
        )
        return true
    }

    private fun actualizarTextoParcial(texto: String) {
        _estado.value = _estado.value.copy(textoParcial = texto)
    }

    fun detenerEscucha() {
        asistente.detener()
        _estado.value = _estado.value.copy(escuchando = false, textoParcial = "")
    }

    fun setModoQuechua(enabled: Boolean) {
        asistente.setModoQuechua(enabled)
        _estado.value = _estado.value.copy(modoQuechua = enabled)
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

    fun consumirRutaPendiente() {
        _estado.value = _estado.value.copy(rutaPendiente = null)
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
        IntentClassifier.Intencion.SALUDO -> "responder al saludo"
        IntentClassifier.Intencion.DESCONOCIDA -> "ninguna acción"
        IntentClassifier.Intencion.REGISTRAR_VENTA -> "abrir el formulario de venta"
        IntentClassifier.Intencion.CONSULTAR_STOCK -> "consultar existencias"
        IntentClassifier.Intencion.CONSULTAR_VENTAS -> "consultar las ventas de hoy"
        IntentClassifier.Intencion.AGREGAR_PRODUCTO -> "abrir el inventario"
    }

    private suspend fun procesarResultado(resultado: AsistenteVozFacade.Resultado) {
        _estado.value = _estado.value.copy(
            escuchando = false,
            textoParcial = "",
            mensajeError = null
        )
        if (resultado.textoOriginal.isBlank() || normalizarTexto(resultado.textoOriginal) == "de") {
            responder("No te escuché bien. Mantén presionado el micrófono, habla y suelta para enviar.")
            return
        }
        agregarMensaje(resultado.textoOriginal, esUsuario = true, corregible = true)

        if (resultado.requiereTranscripcionNube && !resultado.transcripcionNube) {
            responder("El modo quechua no produjo una transcripción cloud. Revisa la conexión y la configuración del servicio; no ejecuté ninguna acción.")
            return
        }
        if (resultado.confianzaVosk < 0.70f && !resultado.transcripcionNube) {
            agregarMensaje(
                "La confianza de la transcripción local es baja. No ejecutaré ninguna acción; " +
                    "puedes corregir el texto y volver a intentarlo.",
                esUsuario = false
            )
            return
        }
        if (resultado.transcripcionNube && resultado.clasificacionRemota &&
            resultado.intencionRemota == null
        ) {
            responder("Transcribí el audio, pero no reconocí una intención con suficiente confianza. No ejecuté ninguna acción.")
            return
        }

        val intencion = resultado.intencionRemota ?: resultado.intencion
        if (intencion == IntentClassifier.Intencion.DESCONOCIDA) {
            responder("No entendí el comando. No ejecuté ninguna acción; prueba con otra frase.")
            return
        }
        if (intencion == IntentClassifier.Intencion.CONSULTA_COGNITIVA ||
            (resultado.transcripcionNube && !resultado.clasificacionRemota)
        ) {
            consultarGuia(resultado.textoOriginal)
            return
        }

        when (intencion) {
            IntentClassifier.Intencion.REGISTRAR_VENTA -> {
                val parametros = mutableListOf<String>()
                resultado.entidades.producto?.let { producto ->
                    parametros += "producto=${Uri.encode(producto)}"
                }
                resultado.entidades.cantidad
                    ?.takeIf { it.isFinite() && it > 0.0 }
                    ?.let { cantidad -> parametros += "cantidad=$cantidad" }
                val ruta = if (parametros.isEmpty()) {
                    "venta_manual"
                } else {
                    "venta_manual?${parametros.joinToString("&")}"
                }
                _estado.value = _estado.value.copy(
                    rutaPendiente = ruta
                )
                responder("Abrí el formulario de venta. Revisa el producto y la cantidad antes de guardar.")
                return
            }
            IntentClassifier.Intencion.AGREGAR_PRODUCTO -> {
                _estado.value = _estado.value.copy(rutaPendiente = "inventario")
                responder("Abrí el inventario. Completa y confirma los datos del producto para guardarlo.")
                return
            }
            IntentClassifier.Intencion.CONSULTAR_STOCK -> {
                consultarStock(resultado.entidades.producto)
                return
            }
            IntentClassifier.Intencion.CONSULTAR_VENTAS -> {
                consultarVentasDeHoy()
                return
            }
            else -> Unit
        }

        val missing = parametroFaltante(intencion, resultado.entidades)
        if (missing != null) {
            responder(missing)
            return
        }

        if (actionExecutor.requiresConfirmation(intencion)) {
            _estado.value = _estado.value.copy(
                estadoAccion = EstadoAccion.EsperandoConfirmacion(
                    intencion,
                    resultado.entidades,
                    resultado.textoOriginal
                )
            )
            hablar("¿Confirmas que debo ${nombreAccion(intencion)}?")
            return
        }

        responder(respuestaInformativa(intencion))
    }

    private suspend fun consultarStock(productoDictado: String?) {
        val productos = db.productoDao().observarTodos().first()
        val consulta = productoDictado
            ?.let(::normalizarTexto)
            ?.takeUnless { it in setOf("producto", "productos", "stock", "inventario", "existencias") }
            .orEmpty()
        val encontrados = if (consulta.isBlank()) {
            productos.take(5)
        } else {
            productos.filter { normalizarTexto(it.nombre).contains(consulta) }
        }
        val respuesta = when {
            encontrados.isEmpty() && consulta.isNotBlank() ->
                "No encontré «$productoDictado» en el inventario local."
            encontrados.isEmpty() -> "Todavía no hay productos en el inventario local."
            else -> encontrados.joinToString(
                prefix = "Existencias locales: ",
                separator = "; "
            ) { "${it.nombre}: ${it.stock}" }
        }
        responder(respuesta)
    }

    private suspend fun consultarVentasDeHoy() {
        val inicioDelDia = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        val total = db.ventaDao().totalDesde(inicioDelDia)
        val cantidad = db.ventaDao().contarDesde(inicioDelDia)
        responder("Hoy registraste $cantidad ventas por ${"%.2f".format(total)} Bs en este dispositivo.")
    }

    private fun normalizarTexto(texto: String): String =
        Normalizer.normalize(texto.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

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
        IntentClassifier.Intencion.SALUDO ->
            "¡Imaynalla! Soy Yapay. Puedo ayudarte con ventas, inventario y consultas del negocio."
        IntentClassifier.Intencion.DESCONOCIDA ->
            "No entendí el comando. No ejecuté ninguna acción."
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
        IntentClassifier.Intencion.REGISTRAR_VENTA -> "Se abrió el formulario de venta."
        IntentClassifier.Intencion.CONSULTAR_STOCK -> "Se consultó el inventario local."
        IntentClassifier.Intencion.CONSULTAR_VENTAS -> "Se consultaron las ventas locales."
        IntentClassifier.Intencion.AGREGAR_PRODUCTO -> "Se abrió el inventario."
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
