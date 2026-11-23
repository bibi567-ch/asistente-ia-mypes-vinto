package com.mypes.vinto.presentation.vinto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypes.vinto.asistente.ClienteGateway
import com.mypes.vinto.asistente.RespuestaGateway
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val NegroTexto = Color(0xFF06130D)

private fun colorEstado(estado: String?): Color = when (estado) {
    "CERRADO" -> VerdeNeon
    "ABIERTO" -> RojoCancelar
    "SEMIABIERTO" -> NaranjaAlerta
    else -> TextoGris
}

/**
 * Demostración del patrón API Gateway + Circuit Breaker desde el teléfono.
 * El teléfono habla solo con el gateway; cada respuesta muestra el estado del circuito,
 * el tiempo y si se usó la nube o el fallback (Vosk local).
 */
@Composable
fun GatewayScreen(volver: () -> Unit) {
    val context = LocalContext.current
    val cliente = remember { ClienteGateway(context) }
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf(cliente.baseUrl) }
    var usuario by remember { mutableStateOf("comerciante") }
    var password by remember { mutableStateOf("vinto2026") }
    var orden by remember { mutableStateOf("vendí dos panes") }
    var conectado by remember { mutableStateOf(cliente.conectado) }
    var ocupado by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    val historial = remember { mutableStateListOf<RespuestaGateway>() }
    val ultimoEstado = historial.firstOrNull { it.estadoCircuito != null }?.estadoCircuito

    fun ejecutar(bloque: suspend () -> Unit) {
        if (ocupado) return
        ocupado = true
        scope.launch {
            try { bloque() } finally { ocupado = false }
        }
    }

    fun enviar(veces: Int) = ejecutar {
        repeat(veces) {
            val r = withContext(Dispatchers.IO) { cliente.transcribir(orden) }
            historial.add(0, r)
            if (r.codigo == 401) {
                conectado = false
                aviso = "La sesión venció (dura 15 minutos). Toca Conectar otra vez."
                return@ejecutar
            }
        }
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = volver) { Text("‹ Volver", color = TextoBlanco) }
        }
        EncabezadoVinto("Nube protegida", "API Gateway + Circuit Breaker") {
            Etiqueta(ultimoEstado ?: if (conectado) "Conectado" else "Sin conectar", colorEstado(ultimoEstado))
        }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Panel {
                Text(
                    "El teléfono no habla con los servicios de la nube: habla solo con el gateway. " +
                        "Si el servicio de IA falla, el gateway abre el circuito y responde al instante " +
                        "que se use el reconocimiento local (Vosk).",
                    color = TextoGris, fontSize = 13.sp
                )
            }

            // 1. Conexión
            Text("1. Conectar al gateway", color = VerdeNeon, fontWeight = FontWeight.SemiBold)
            CampoVinto(url, { url = it }, "Dirección del gateway")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoVinto(usuario, { usuario = it }, "Usuario", Modifier.weight(1f))
                CampoVinto(password, { password = it }, "Contraseña", Modifier.weight(1f), KeyboardType.Password)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = !ocupado,
                    onClick = {
                        cliente.baseUrl = url
                        ejecutar {
                            val r = withContext(Dispatchers.IO) { cliente.ingresar(usuario, password) }
                            conectado = cliente.conectado && r.codigo == 200
                            aviso = when {
                                conectado -> "Conectado como $usuario. El token JWT lo validó el gateway."
                                r.codigo == -1 -> "No llego al gateway (${r.error}). ¿Está corriendo en la PC y hiciste «adb reverse tcp:8080 tcp:8080»?"
                                r.codigo == 401 -> "Usuario o contraseña incorrectos (401)."
                                else -> "El gateway respondió ${r.codigo}${r.motivo?.let { " ($it)" } ?: ""}."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = NegroTexto),
                    modifier = Modifier.weight(1f)
                ) { Text(if (conectado) "Reconectar" else "Conectar", fontWeight = FontWeight.Bold) }
                if (conectado) {
                    OutlinedButton(
                        onClick = { cliente.desconectar(); conectado = false; aviso = "Desconectado." },
                        modifier = Modifier.weight(1f)
                    ) { Text("Desconectar", color = TextoBlanco) }
                }
            }
            aviso?.let { Text(it, color = if (conectado) VerdeNeon else NaranjaAlerta, fontSize = 13.sp) }

            // 2. Enviar órdenes
            Spacer(Modifier.height(4.dp))
            Text("2. Enviar órdenes a la IA en línea", color = VerdeNeon, fontWeight = FontWeight.SemiBold)
            CampoVinto(orden, { orden = it }, "Orden (texto que reconoció Vosk)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = conectado && !ocupado,
                    onClick = { enviar(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = NegroTexto),
                    modifier = Modifier.weight(1f)
                ) { Text("Enviar 1", fontWeight = FontWeight.Bold) }
                Button(
                    enabled = conectado && !ocupado,
                    onClick = { enviar(10) },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulElectrico),
                    modifier = Modifier.weight(1f)
                ) { Text("Enviar 10 seguidas", fontWeight = FontWeight.Bold) }
            }

            // 3. Interruptor de la demo
            Spacer(Modifier.height(4.dp))
            Text("3. Simular la falla (servicio de IA de prueba)", color = VerdeNeon, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("normal" to "Normal", "caido" to "Caído", "lento" to "Lento").forEach { (modo, nombre) ->
                    OutlinedButton(
                        enabled = !ocupado,
                        onClick = {
                            cliente.baseUrl = url
                            ejecutar {
                                val ok = withContext(Dispatchers.IO) { cliente.cambiarModoIa(modo) }
                                aviso = if (ok) "Servicio de IA en modo «$modo»." else "No pude cambiar el modo (¿hiciste «adb reverse tcp:5000 tcp:5000»?)."
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(nombre, color = if (modo == "caido") RojoCancelar else TextoBlanco) }
                }
            }

            // 4. Resultado de cada petición
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Respuestas", color = TextoBlanco, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (historial.isNotEmpty()) TextButton(onClick = { historial.clear() }) { Text("Limpiar", color = TextoGris) }
            }
            if (historial.isEmpty()) Text("Todavía no enviaste órdenes.", color = TextoGris, fontSize = 13.sp)
            historial.take(30).forEachIndexed { i, r ->
                Panel(borde = colorEstado(r.estadoCircuito).copy(alpha = 0.5f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("#${historial.size - i}", color = TextoGris, fontSize = 12.sp, modifier = Modifier.width(40.dp))
                        Text(
                            if (r.codigo == -1) "sin red" else "HTTP ${r.codigo}",
                            color = if (r.codigo == 200) VerdeNeon else RojoCancelar,
                            fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.weight(1f)
                        )
                        Text("${r.milis} ms", color = TextoBlanco, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                        Etiqueta(r.estadoCircuito ?: "—", colorEstado(r.estadoCircuito))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when {
                            r.usoNube -> "Nube (MMS): «${r.texto}»"
                            r.fallback != null -> "Fallback ${r.fallback} · ${r.motivoLegible()} → el teléfono usa Vosk local"
                            else -> r.error ?: "Sin respuesta"
                        },
                        color = TextoGris, fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

