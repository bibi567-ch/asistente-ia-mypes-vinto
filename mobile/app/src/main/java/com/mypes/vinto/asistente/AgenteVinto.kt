package com.mypes.vinto.asistente

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.util.Calendar

/** Lo que el agente entendió y lo que va a hacer, antes de ejecutar. */
data class Plan(
    val texto: String,
    val comando: Comando,
    val datos: DatosComando,
    val detalle: List<String>,
    val confirmar: Boolean,
    /** Si falta un dato, el agente lo pide en vez de ejecutar. */
    val falta: String? = null,
    /** Navegación interna (por ejemplo "guia/wifi"). */
    val ir: String? = null
) {
    val titulo: String get() = comando.titulo
}

data class Resultado(val ok: Boolean, val respuesta: String, val ir: String? = null)

/**
 * Agente IA Vinto: une el clasificador, el extractor y las acciones del teléfono.
 * Las acciones usan intents estándar de Android, sin servicios externos.
 */
class AgenteVinto(private val context: Context, private val almacen: AlmacenVinto) {

    companion object {
        const val AYUDA =
            "Puedes decirme: «vendí 3 panes», «¿cuánto vendí hoy?», «cuánto queda de arroz», «llama a mi hermana», «manda mensaje a Rosa que diga ya llegué», " +
                "«alarma a las seis y media», «abre WhatsApp», «qué hora es», «prende la linterna» " +
                "o «guía para compartir ubicación»."

        /** Paquetes conocidos de cada aplicación (el primero que esté instalado se abre). */
        private val PAQUETES = mapOf(
            "whatsapp" to listOf("com.whatsapp", "com.whatsapp.w4b"),
            "youtube" to listOf("com.google.android.youtube"),
            "chrome" to listOf("com.android.chrome"),
            "facebook" to listOf("com.facebook.katana", "com.facebook.lite"),
            "tiktok" to listOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill"),
            "telegram" to listOf("org.telegram.messenger"),
            "maps" to listOf("com.google.android.apps.maps"),
            "mapas" to listOf("com.google.android.apps.maps")
        )
    }

    private fun dos(n: Int) = n.toString().padStart(2, '0')
    private fun numeroDe(d: DatosComando) = d.numeroTelefono ?: almacen.buscarContacto(d.contacto)?.numero
    private fun nombreDe(d: DatosComando) =
        almacen.buscarContacto(d.contacto)?.nombre ?: d.contacto ?: d.numeroTelefono

    fun planificar(texto: String): Plan {
        val comando = ClasificadorComandos.clasificar(texto)
        val d = ExtractorComandos.extraer(texto)
        val confirmar = ClasificadorComandos.requiereConfirmacion(comando)
        fun plan(detalle: List<String>, falta: String? = null, ir: String? = null, conf: Boolean = confirmar) =
            Plan(texto, comando, d, detalle, conf, falta, ir)

        return when (comando) {
            Comando.LLAMAR_CONTACTO -> {
                val n = numeroDe(d)
                plan(
                    listOf("A: ${nombreDe(d) ?: "—"}", "Número: ${n ?: "sin número"}"),
                    falta = when {
                        n != null -> null
                        d.contacto != null ->
                            "No tengo el número de «${d.contacto}». Guárdalo en Contactos o dime el número."
                        else -> "Dime a quién llamar o el número."
                    }
                )
            }
            Comando.ENVIAR_MENSAJE -> {
                val n = numeroDe(d)
                plan(
                    listOf(
                        "Para: ${nombreDe(d) ?: "—"}", "Mensaje: ${d.mensaje ?: "(vacío)"}",
                        "Tú confirmas el envío en la aplicación de mensajes."
                    ),
                    falta = if (n == null) "Dime a quién enviar el mensaje o guarda el contacto." else null
                )
            }
            Comando.CREAR_ALARMA -> plan(
                listOf(
                    "Hora: ${if (d.hora != null) "${dos(d.hora)}:${dos(d.minutos ?: 0)}" else "sin hora"}",
                    "Se crea en el reloj de tu teléfono."
                ),
                falta = if (d.hora == null) "Dime la hora, por ejemplo: «alarma a las seis y media»." else null
            )
            Comando.ABRIR_APP -> plan(
                listOf("Aplicación: ${d.nombreApp ?: "—"}"),
                falta = if (d.nombreApp == null)
                    "Dime qué aplicación abrir: WhatsApp, YouTube, Facebook, cámara, mapas…" else null
            )
            Comando.LEER_TEXTO -> plan(
                listOf(d.textoALeer?.let { "Texto: «$it»" } ?: "Leeré el texto que copiaste (portapapeles).")
            )
            Comando.LINTERNA -> plan(listOf(if (d.linternaEncendida) "Encender la linterna" else "Apagar la linterna"))
            Comando.AJUSTES_RED -> {
                val t = texto.lowercase()
                val destino = when {
                    t.contains("bluetooth") -> "Ajustes de Bluetooth"
                    t.contains("dato") -> "Ajustes de datos móviles"
                    else -> "Ajustes de wifi"
                }
                plan(listOf("Abrir: $destino", "Tú eliges la red o activas la opción."))
            }
            Comando.GUIA_TELEFONO -> {
                val g = Guias.buscar(texto)
                plan(listOf(g?.titulo ?: "Te muestro la lista de guías"), ir = g?.let { "guia/${it.id}" } ?: "guias")
            }
            Comando.CONSULTAR_HORA, Comando.AYUDA, Comando.DESCONOCIDO -> plan(emptyList())
        }
    }

    private fun abrir(intent: Intent): Boolean = try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    } catch (e: SecurityException) {
        false
    }

    fun ejecutar(p: Plan): Resultado {
        val d = p.datos
        return when (p.comando) {
            Comando.LLAMAR_CONTACTO -> {
                val n = numeroDe(d) ?: return Resultado(false, p.falta ?: "Falta el número.")
                val puedeLlamar = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED
                val accion = if (puedeLlamar) Intent.ACTION_CALL else Intent.ACTION_DIAL
                if (abrir(Intent(accion, Uri.parse("tel:$n")))) {
                    Resultado(
                        true,
                        if (puedeLlamar) "Llamando a ${nombreDe(d)}."
                        else "Abrí el marcador con el número de ${nombreDe(d)}. Toca el botón verde para llamar."
                    )
                } else Resultado(false, "No pude abrir el teléfono.")
            }
            Comando.ENVIAR_MENSAJE -> {
                val n = numeroDe(d) ?: return Resultado(false, p.falta ?: "Falta el número.")
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$n"))
                    .putExtra("sms_body", d.mensaje.orEmpty())
                if (abrir(intent)) Resultado(true, "Abrí tus mensajes con el texto listo. Revisa y toca enviar.")
                else Resultado(false, "No encontré una aplicación de mensajes.")
            }
            Comando.CREAR_ALARMA -> {
                val h = d.hora ?: return Resultado(false, p.falta ?: "Falta la hora.")
                val m = d.minutos ?: 0
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_HOUR, h)
                    .putExtra(AlarmClock.EXTRA_MINUTES, m)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, "IA Vinto")
                    .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                if (abrir(intent)) Resultado(true, "Listo, alarma creada a las ${dos(h)}:${dos(m)}.")
                else Resultado(false, "No encontré la aplicación de reloj.")
            }
            Comando.ABRIR_APP -> abrirApp(d.nombreApp)
            Comando.LEER_TEXTO -> {
                val texto = d.textoALeer ?: runCatching {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()
                }.getOrNull()
                if (texto.isNullOrBlank()) Resultado(false, "No encontré texto para leer. Copia un texto y vuelve a pedírmelo.")
                else Resultado(true, texto)
            }
            Comando.LINTERNA -> if (linterna(d.linternaEncendida)) {
                Resultado(true, if (d.linternaEncendida) "Linterna encendida." else "Linterna apagada.")
            } else Resultado(false, "Este teléfono no tiene linterna disponible.")
            Comando.CONSULTAR_HORA -> {
                val c = Calendar.getInstance()
                Resultado(true, "Son las ${c.get(Calendar.HOUR_OF_DAY)} y ${c.get(Calendar.MINUTE)} minutos.")
            }
            Comando.AJUSTES_RED -> {
                val t = p.texto.lowercase()
                val accion = when {
                    t.contains("bluetooth") -> Settings.ACTION_BLUETOOTH_SETTINGS
                    t.contains("dato") -> Settings.ACTION_DATA_ROAMING_SETTINGS
                    else -> Settings.ACTION_WIFI_SETTINGS
                }
                if (abrir(Intent(accion)) || abrir(Intent(Settings.ACTION_WIRELESS_SETTINGS))) {
                    Resultado(true, "Abrí los ajustes. Elige la opción que necesitas.")
                } else Resultado(false, "No pude abrir los ajustes. Te muestro la guía.", "guia/wifi")
            }
            Comando.GUIA_TELEFONO -> Resultado(
                true,
                if (p.ir == "guias") "Tengo ${Guias.TODAS.size} guías. Elige una." else "Te muestro la guía: ${p.detalle.first()}.",
                p.ir
            )
            Comando.AYUDA -> Resultado(true, AYUDA)
            Comando.DESCONOCIDO -> Resultado(false, "No entendí la orden. $AYUDA")
        }
    }

    private fun abrirApp(nombre: String?): Resultado {
        val n = nombre ?: return Resultado(false, "Dime qué aplicación abrir.")
        val base = ExtractorComandos.sinTildes(n)
        val ok = when (base) {
            "camara" -> abrir(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
            "galeria" -> abrir(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY))
            "telefono" -> abrir(Intent(Intent.ACTION_DIAL))
            "mensajes" -> abrir(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MESSAGING))
            "ajustes", "configuracion" -> abrir(Intent(Settings.ACTION_SETTINGS))
            else -> {
                val paquetes = PAQUETES[base].orEmpty()
                val lanzador = paquetes.firstNotNullOfOrNull { context.packageManager.getLaunchIntentForPackage(it) }
                when {
                    lanzador != null -> abrir(lanzador)
                    paquetes.isNotEmpty() -> {
                        abrir(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${paquetes.first()}")))
                        return Resultado(false, "No encontré $n instalada. Te abrí la tienda para instalarla.")
                    }
                    else -> false
                }
            }
        }
        return if (ok) Resultado(true, "Abriendo $n.") else Resultado(false, "No pude abrir $n.")
    }

    private fun linterna(encender: Boolean): Boolean = runCatching {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val id = cm.cameraIdList.firstOrNull { cid ->
            cm.getCameraCharacteristics(cid).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return false
        cm.setTorchMode(id, encender)
        true
    }.getOrDefault(false)
}
