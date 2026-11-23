package com.mypes.vinto.nlu

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings

class PhoneActionExecutor(private val context: Context) {
    fun requiresConfirmation(intencion: IntentClassifier.Intencion): Boolean =
        ConfirmationPolicy.requiresConfirmation(intencion)

    fun ejecutar(
        intencion: IntentClassifier.Intencion,
        entidades: EntityExtractor.Entidades,
        confirmar: Boolean = false
    ): Boolean {
        if (requiresConfirmation(intencion) && !confirmar) return false

        return try {
            when (intencion) {
                IntentClassifier.Intencion.LLAMAR_CONTACTO -> {
                    val numero = entidades.numeroTelefono
                        ?.filter(Char::isDigit)
                        ?.takeIf(String::isNotEmpty)
                        ?: return false
                    val action = if (
                        context.checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        Intent.ACTION_CALL
                    } else {
                        Intent.ACTION_DIAL
                    }
                    context.startActivity(
                        Intent(action, Uri.parse("tel:$numero"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    true
                }

                IntentClassifier.Intencion.ENVIAR_MENSAJE -> {
                    val numero = entidades.numeroTelefono
                        ?.filter(Char::isDigit)
                        ?.takeIf(String::isNotEmpty)
                        ?: return false
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$numero"))
                            .putExtra("sms_body", entidades.mensaje.orEmpty())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    true
                }

                IntentClassifier.Intencion.CREAR_ALARMA -> {
                    val hora = entidades.hora ?: return false
                    val minutos = entidades.minutos ?: 0
                    if (hora !in 0..23 || minutos !in 0..59) return false
                    context.startActivity(
                        Intent(AlarmClock.ACTION_SET_ALARM)
                            .putExtra(AlarmClock.EXTRA_HOUR, hora)
                            .putExtra(AlarmClock.EXTRA_MINUTES, minutos)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    true
                }

                IntentClassifier.Intencion.ABRIR_APP -> {
                    val packageName = entidades.nombreApp?.let(::paqueteConocido) ?: return false
                    val launchIntent =
                        context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
                    context.startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    true
                }

                IntentClassifier.Intencion.LINTERNA -> {
                    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                        cameraManager.getCameraCharacteristics(id)
                            .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                    } ?: return false
                    cameraManager.setTorchMode(cameraId, entidades.linternaEncendida)
                    true
                }

                IntentClassifier.Intencion.AJUSTES_RED -> {
                    context.startActivity(
                        Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    true
                }

                else -> false
            }
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: CameraAccessException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private fun paqueteConocido(nombre: String): String? = when (nombre.lowercase()) {
        "whatsapp" -> "com.whatsapp"
        "youtube" -> "com.google.android.youtube"
        "chrome" -> "com.android.chrome"
        "teléfono", "telefono", "llamadas" -> "com.google.android.dialer"
        "mensajes" -> "com.google.android.apps.messaging"
        "ajustes", "configuración", "configuracion" -> "com.android.settings"
        else -> null
    }
}
