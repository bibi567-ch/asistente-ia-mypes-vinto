package com.mypes.vinto.nlu

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.mypes.vinto.BuildConfig
import com.mypes.vinto.sync.AuthSession
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class AsistenteVozFacade(context: Context) : AutoCloseable {
    private val asr = AsrEngine(context)
    private val clasificador = IntentClassifier()
    private val extractor = EntityExtractor()
    private val applicationContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val cloudPreferences =
        applicationContext.getSharedPreferences("voice_cloud", Context.MODE_PRIVATE)
    private val quechuaModeKey = "quechua_mode"

    data class Resultado(
        val intencion: IntentClassifier.Intencion,
        val entidades: EntityExtractor.Entidades,
        val textoOriginal: String,
        val confianzaVosk: Float,
        val transcripcionNube: Boolean = false,
        val asrSimulado: Boolean = false,
        val intencionRemota: IntentClassifier.Intencion? = null,
        val clasificacionRemota: Boolean = false,
        val requiereTranscripcionNube: Boolean = false
    )

    fun inicializar(): Boolean = asr.inicializar()

    fun modoQuechua(): Boolean = cloudPreferences.getBoolean(quechuaModeKey, false)

    fun setModoQuechua(enabled: Boolean) {
        cloudPreferences.edit().putBoolean(quechuaModeKey, enabled).apply()
    }

    fun procesar(texto: String, confianza: Float = 0f): Resultado = Resultado(
        intencion = clasificador.clasificar(texto),
        entidades = extractor.extraer(texto),
        textoOriginal = texto,
        confianzaVosk = confianza
    )

    fun escuchar(
        onParcial: (String) -> Unit,
        onResultado: (Resultado) -> Unit,
        onError: (String) -> Unit
    ) {
        asr.escuchar(object : AsrEngine.Resultado {
            override fun onTextoParcial(texto: String) = onParcial(texto)
            override fun onTextoFinal(texto: String, confianza: Float, audioPcm: ByteArray) {
                val modoQuechuaActivo = modoQuechua()
                if (confianza >= 0.70f && !modoQuechuaActivo) {
                    onResultado(procesar(texto, confianza))
                    return
                }

                val baseUrl = cloudPreferences.getString("api_base_url", null)?.trim()
                    ?.takeIf(String::isNotEmpty) ?: BuildConfig.API_BASE_URL
                val accessToken = AuthSession.token(applicationContext)
                    ?: cloudPreferences.getString("access_token", null)?.trim()
                if (accessToken.isNullOrEmpty()) {
                    onResultado(
                        procesar(texto, confianza).copy(
                            requiereTranscripcionNube = modoQuechuaActivo
                        )
                    )
                    return
                }

                scope.launch {
                    var cloudFailure: String? = null
                    var cloudSimulated = false
                    val cloudResponse = try {
                        CloudAsrService(baseUrl, accessToken).transcribeIfNeeded(
                            applicationContext,
                            audioPcm.toWaveRequestBody(),
                            confianza,
                            forceCloud = modoQuechuaActivo
                        )
                    } catch (error: IOException) {
                        cloudFailure = error.message ?: "No se pudo contactar el servicio cloud."
                        null
                    } catch (error: HttpException) {
                        cloudFailure = "El servicio cloud respondió con HTTP ${error.code()}."
                        null
                    } catch (error: IllegalArgumentException) {
                        cloudFailure = error.message ?: "La configuración cloud no es válida."
                        null
                    }
                    val cloudText = cloudResponse?.takeUnless { it.simulada }?.texto
                        ?.takeIf(String::isNotBlank)
                    val remoteIntent = cloudResponse
                        ?.takeIf { it.clasificada }
                        ?.let {
                            IntentClassifier().clasificarResultadoRemoto(
                                it.intencion,
                                it.confianzaIntencion
                            )
                        }
                    if (cloudResponse?.simulada == true) {
                        cloudSimulated = true
                        cloudFailure = cloudResponse.mensaje
                            ?: "El reconocimiento cloud está en modo demostración y no transcribió el audio."
                    }
                    withContext(Dispatchers.Main) {
                        onResultado(
                            procesar(cloudText ?: texto, confianza).copy(
                                transcripcionNube = cloudText != null,
                                asrSimulado = cloudSimulated,
                                intencionRemota = remoteIntent,
                                clasificacionRemota = cloudResponse?.clasificada == true,
                                requiereTranscripcionNube = modoQuechuaActivo
                            )
                        )
                        cloudFailure?.let(onError)
                    }
                }
            }
            override fun onError(mensaje: String) = onError(mensaje)
        })
    }

    fun procesarPinDictado(
        onPinDetectado: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        asr.escuchar(object : AsrEngine.Resultado {
            override fun onTextoParcial(texto: String) = Unit
            override fun onTextoFinal(texto: String, confianza: Float, audioPcm: ByteArray) {
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
    override fun close() {
        scope.cancel()
        asr.close()
    }

    private fun ByteArray.toWaveRequestBody() =
        toWave().toRequestBody("audio/wav".toMediaType())

    private fun ByteArray.toWave(): ByteArray {
        val header = ByteArrayOutputStream(44)
        fun writeInt(value: Int) {
            repeat(4) { offset -> header.write((value shr (offset * 8)) and 0xff) }
        }
        fun writeShort(value: Int) {
            header.write(value and 0xff)
            header.write((value shr 8) and 0xff)
        }
        header.write("RIFF".toByteArray(Charsets.US_ASCII))
        writeInt(size + 36)
        header.write("WAVEfmt ".toByteArray(Charsets.US_ASCII))
        writeInt(16)
        writeShort(1)
        writeShort(1)
        writeInt(16_000)
        writeInt(32_000)
        writeShort(2)
        writeShort(16)
        header.write("data".toByteArray(Charsets.US_ASCII))
        writeInt(size)
        header.write(this)
        return header.toByteArray()
    }
}
