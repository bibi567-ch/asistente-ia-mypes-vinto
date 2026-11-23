package com.mypes.vinto.asistente

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Lo que devolvió el API Gateway para una petición, con lo necesario para mostrar el patrón. */
data class RespuestaGateway(
    /** Código HTTP; -1 si no hubo conexión con el gateway. */
    val codigo: Int,
    /** Cabecera x-circuit-state: CERRADO, ABIERTO o SEMIABIERTO. */
    val estadoCircuito: String?,
    val milis: Long,
    /** Texto transcrito por el servicio en línea (MMS simulado). */
    val texto: String?,
    /** Indicación de degradación, por ejemplo vosk_local. */
    val fallback: String?,
    /** circuito_abierto, tiempo_agotado o fallo_del_servicio. */
    val motivo: String?,
    val error: String?
) {
    val usoNube: Boolean get() = codigo == 200 && texto != null

    fun resumen(): String = when {
        usoNube -> "☁ Nube: respondió en $milis ms · circuito ${estadoCircuito ?: "?"}"
        fallback != null -> "☁ Nube no disponible (${motivoLegible()}, $milis ms, circuito ${estadoCircuito ?: "?"}). Seguí con Vosk local."
        codigo == 401 -> "☁ La sesión del gateway venció. Conéctate otra vez en Más › Nube protegida."
        codigo == -1 -> "☁ Sin conexión con el gateway. Seguí con Vosk local."
        else -> "☁ El gateway respondió $codigo. Seguí con Vosk local."
    }

    fun motivoLegible(): String = when (motivo) {
        "circuito_abierto" -> "circuito abierto"
        "tiempo_agotado" -> "tiempo agotado"
        "fallo_del_servicio" -> "fallo del servicio"
        else -> motivo ?: "sin motivo"
    }
}

/**
 * Cliente del API Gateway (carpeta arquitectura-gateway-prototipo).
 * El teléfono habla SOLO con el gateway: login, transcripción en línea y, para la demo,
 * el cambio de modo del servicio de IA simulado.
 * Las llamadas son bloqueantes: usarlas dentro de withContext(Dispatchers.IO).
 */
class ClienteGateway(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("gateway", Context.MODE_PRIVATE)
    private val http = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()
    private val tipoJson = "application/json; charset=utf-8".toMediaType()

    /** Con el cable y «adb reverse tcp:8080 tcp:8080», 127.0.0.1 del teléfono es la PC. */
    var baseUrl: String
        get() = prefs.getString("url", "http://127.0.0.1:8080/") ?: "http://127.0.0.1:8080/"
        set(v) {
            val limpio = v.trim()
            prefs.edit().putString("url", if (limpio.endsWith("/")) limpio else "$limpio/").apply()
        }

    var token: String?
        get() = prefs.getString("token", null)
        private set(v) = prefs.edit().putString("token", v).apply()

    var usuario: String?
        get() = prefs.getString("usuario", null)
        private set(v) = prefs.edit().putString("usuario", v).apply()

    val conectado: Boolean get() = !token.isNullOrBlank()

    fun desconectar() {
        token = null
        usuario = null
    }

    private fun post(url: String, cuerpo: JSONObject, conToken: Boolean): RespuestaGateway {
        val inicio = System.nanoTime()
        return try {
            val peticion = Request.Builder()
                .url(url)
                .post(cuerpo.toString().toRequestBody(tipoJson))
                .apply { if (conToken) token?.let { header("Authorization", "Bearer $it") } }
                .build()
            http.newCall(peticion).execute().use { r ->
                val milis = (System.nanoTime() - inicio) / 1_000_000
                val texto = r.body?.string().orEmpty()
                val json = runCatching { JSONObject(texto) }.getOrNull()
                RespuestaGateway(
                    codigo = r.code,
                    estadoCircuito = r.header("x-circuit-state"),
                    milis = milis,
                    texto = json?.optString("texto")?.takeIf { it.isNotBlank() },
                    fallback = json?.optString("fallback")?.takeIf { it.isNotBlank() },
                    motivo = json?.optString("motivo")?.takeIf { it.isNotBlank() },
                    error = json?.optString("error")?.takeIf { it.isNotBlank() }
                ).also { if (r.code == 200) json?.optString("token")?.takeIf { it.isNotBlank() }?.let { token = it } }
            }
        } catch (e: Exception) {
            RespuestaGateway(-1, null, (System.nanoTime() - inicio) / 1_000_000, null, null, null, e.message ?: "sin conexión")
        }
    }

    /** POST /api/v1/auth/login a través del gateway; guarda el JWT. */
    fun ingresar(usuarioDemo: String, password: String): RespuestaGateway {
        val r = post(
            baseUrl + "api/v1/auth/login",
            JSONObject().put("usuario", usuarioDemo).put("password", password),
            conToken = false
        )
        if (r.codigo == 200 && conectado) usuario = usuarioDemo
        return r
    }

    /** POST /api/v1/asr/transcribe: el gateway lo protege con JWT y con el Circuit Breaker de la IA. */
    fun transcribir(texto: String, idioma: String = "quh"): RespuestaGateway =
        post(
            baseUrl + "api/v1/asr/transcribe",
            JSONObject().put("textoSimulado", texto).put("idioma", idioma),
            conToken = true
        )

    /**
     * Solo para la demostración: cambia el modo del servicio de IA simulado (puerto 5000)
     * a normal, lento, caido o aleatorio. No pasa por el gateway: es el «interruptor» de la falla.
     */
    fun cambiarModoIa(modo: String): Boolean {
        val url = baseUrl.toHttpUrlOrNull()?.newBuilder()?.port(5000)?.encodedPath("/admin/modo")?.build()
            ?: return false
        return runCatching {
        val peticion = Request.Builder()
            .url(url)
            .post(JSONObject().put("modo", modo).toString().toRequestBody(tipoJson))
            .build()
        http.newCall(peticion).execute().use { it.isSuccessful }
        }.getOrDefault(false)
    }
}
