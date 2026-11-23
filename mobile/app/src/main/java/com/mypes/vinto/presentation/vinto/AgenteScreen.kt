package com.mypes.vinto.presentation.vinto

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.mypes.vinto.asistente.AgenteVinto
import com.mypes.vinto.asistente.AlmacenVinto
import com.mypes.vinto.asistente.ClienteGateway
import com.mypes.vinto.asistente.Comando
import com.mypes.vinto.asistente.Locutor
import com.mypes.vinto.asistente.Plan
import com.mypes.vinto.asistente.RegistroOrden
import android.net.Uri
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.nlu.AsrEngine
import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.viewmodel.NegocioViewModel
import kotlinx.coroutines.flow.first
import com.mypes.vinto.presentation.theme.BordeOscuro
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

private data class MensajeVinto(val texto: String, val esUsuario: Boolean)

private val SUGERENCIAS = listOf(
    "¿Cuánto vendí hoy?", "Vendí 2 panes", "¿Qué hora es?", "Abre WhatsApp",
    "Alarma a las seis y media", "Prende la linterna", "Guía para compartir ubicación", "Ayuda"
)
private const val SALUDO = "Hola, soy IA Vinto. Toca el micrófono y dime qué quieres hacer con tu celular."

/** Pantalla principal: chat con el agente, micrófono (Vosk sin internet) y confirmación. */
@Composable
fun AgenteScreen(almacen: AlmacenVinto, irA: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val agente = remember { AgenteVinto(context, almacen) }
    val clasificadorNegocio = remember { IntentClassifier() }
    val gateway = remember { ClienteGateway(context) }
    val extractorNegocio = remember { EntityExtractor() }
    val asr = remember { AsrEngine(context) }
    val locutor = remember { Locutor(context) }
    val mensajes = remember { mutableStateListOf(MensajeVinto(SALUDO, false)) }
    var parcial by remember { mutableStateOf("") }
    var escuchando by remember { mutableStateOf(false) }
    var modelo by remember { mutableStateOf<Boolean?>(null) }
    var texto by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf<Plan?>(null) }
    val terminado = remember { AtomicBoolean(true) }
    val lista = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose {
            asr.close()
            locutor.close()
        }
    }
    LaunchedEffect(Unit) {
        // Copiar y cargar el modelo Vosk tarda unos segundos la primera vez: fuera del hilo principal.
        modelo = withContext(Dispatchers.IO) { asr.inicializar() }
    }
    LaunchedEffect(mensajes.size, parcial) {
        if (mensajes.isNotEmpty()) lista.animateScrollToItem(mensajes.size - 1 + if (parcial.isNotBlank()) 1 else 0)
    }

    val permisoAudio = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    fun decir(respuesta: String) {
        mensajes.add(MensajeVinto(respuesta, false))
        if (almacen.respuestasPorVoz) locutor.hablar(respuesta, almacen.velocidadVoz)
    }

    fun correr(p: Plan, confirmado: Boolean) {
        val r = agente.ejecutar(p)
        almacen.registrar(RegistroOrden(texto = p.texto, comando = p.comando.name, confirmado = confirmado, ejecutado = r.ok))
        decir(r.respuesta)
        r.ir?.let { destino -> scope.launch { delay(700); irA(destino) } }
    }

    fun procesarNegocio(orden: String): Boolean {
        val intencion = clasificadorNegocio.clasificar(orden)
        val e = extractorNegocio.extraer(orden)
        fun registrar(ok: Boolean) =
            almacen.registrar(RegistroOrden(texto = orden, comando = Comando.DESCONOCIDO.name, confirmado = false, ejecutado = ok))
        when (intencion) {
            IntentClassifier.Intencion.VENTA, IntentClassifier.Intencion.FIADO -> {
                val producto = Uri.encode(e.producto.orEmpty())
                val cantidad = e.cantidad?.takeIf { it.isFinite() && it > 0 } ?: 1.0
                registrar(true)
                decir("Te abro el formulario de venta con ${e.producto ?: "el producto"}. Revisa y confirma.")
                scope.launch { delay(600); irA("venta_manual?producto=$producto&cantidad=$cantidad") }
            }
            IntentClassifier.Intencion.CONSULTA_VENTAS -> scope.launch {
                val desde = NegocioViewModel.inicioDelDia()
                val dao = MypesDatabase.obtener(context).ventaDao()
                val (total, cantidad) = withContext(Dispatchers.IO) { dao.totalDesde(desde) to dao.contarDesde(desde) }
                registrar(true)
                decir("Hoy vendiste ${bs(total)} en $cantidad ventas.")
            }
            IntentClassifier.Intencion.CONSULTA_STOCK -> scope.launch {
                val lista = withContext(Dispatchers.IO) { MypesDatabase.obtener(context).productoDao().observarTodos().first() }
                val buscado = e.producto?.lowercase()
                val p = lista.firstOrNull { buscado != null && it.nombre.lowercase().contains(buscado) }
                registrar(p != null)
                decir(
                    when {
                        p != null -> "Te quedan ${"%.0f".format(p.stock)} de ${p.nombre}."
                        buscado != null -> "No encontré «$buscado» en tu inventario."
                        else -> "Tienes ${lista.size} productos; ${lista.count { it.stock < 5.0 }} con stock bajo."
                    }
                )
            }
            IntentClassifier.Intencion.COMPRA -> {
                registrar(true)
                decir("Registra la compra en Caja, con el botón «Registrar gasto».")
                scope.launch { delay(600); irA("caja") }
            }
            IntentClassifier.Intencion.COBRO -> {
                registrar(true)
                decir("Te muestro los fiados pendientes para cobrar.")
                scope.launch { delay(600); irA("fiados") }
            }
            else -> return false
        }
        return true
    }

    fun procesar(entrada: String) {
        val limpio = entrada.trim()
        if (limpio.isEmpty()) return
        mensajes.add(MensajeVinto(limpio, true))
        // Arquitectura: si el teléfono está conectado al API Gateway, pide la segunda transcripción
        // en línea. Si el circuito está abierto, el gateway responde fallback y se sigue con Vosk local.
        if (gateway.conectado) scope.launch {
            val r = withContext(Dispatchers.IO) { gateway.transcribir(limpio) }
            mensajes.add(MensajeVinto(r.resumen(), false))
        }
        val p = agente.planificar(limpio)
        // Órdenes del negocio (versión anterior de la app): ventas, fiados, stock y resumen del día.
        if (p.comando == Comando.DESCONOCIDO && procesarNegocio(limpio)) return
        if (p.falta != null) {
            almacen.registrar(RegistroOrden(texto = limpio, comando = p.comando.name))
            decir(p.falta)
            p.ir?.let { destino -> scope.launch { delay(700); irA(destino) } }
            return
        }
        if (p.confirmar) plan = p else correr(p, confirmado = false)
    }

    fun microfono() {
        if (escuchando) {
            terminado.set(true)
            asr.detener()
            escuchando = false
            parcial = ""
            return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permisoAudio.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (modelo != true) {
            decir(if (modelo == null) "Estoy preparando el reconocimiento de voz, espera unos segundos." else "No se pudo cargar el modelo de voz. Puedes escribir la orden.")
            return
        }
        locutor.callar()
        terminado.set(false)
        escuchando = true
        parcial = ""
        asr.escuchar(object : AsrEngine.Resultado {
            override fun onTextoParcial(texto: String) {
                if (!terminado.get()) parcial = texto
            }

            override fun onTextoFinal(texto: String) {
                if (terminado.compareAndSet(false, true)) {
                    asr.detener()
                    escuchando = false
                    parcial = ""
                    procesar(texto)
                }
            }

            override fun onError(mensaje: String) {
                if (terminado.compareAndSet(false, true)) {
                    asr.detener()
                    escuchando = false
                    parcial = ""
                    decir("No pude escucharte bien ($mensaje). Intenta de nuevo o escribe la orden.")
                }
            }
        })
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        EncabezadoVinto("IA Vinto", "Tu asistente de voz") {
            Etiqueta(
                when (modelo) {
                    true -> "Voz sin internet"
                    false -> "Voz no disponible"
                    null -> "Preparando voz…"
                },
                when (modelo) {
                    true -> VerdeNeon
                    false -> RojoCancelar
                    null -> NaranjaAlerta
                }
            )
        }

        LazyColumn(
            state = lista,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(mensajes) { _, m -> Burbuja(m) }
            if (parcial.isNotBlank()) item { Burbuja(MensajeVinto("$parcial…", true), atenuada = true) }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(SuperficieOscura.copy(alpha = 0.6f))
                .padding(top = 10.dp, bottom = 12.dp)
        ) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SUGERENCIAS.forEach { s -> Sugerencia(s) { procesar(s) } }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.clip(CircleShape).clickable { microfono() }) {
                    Orbe(tam = 64.dp, escuchando = escuchando) {
                        Icon(
                            if (escuchando) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (escuchando) "Detener" else "Hablar",
                            tint = Color06,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    placeholder = { Text("O escribe una orden…", color = TextoGris) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 6.dp),
                    shape = RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { procesar(texto); texto = "" }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdeNeon,
                        unfocusedBorderColor = BordeOscuro,
                        focusedTextColor = TextoBlanco,
                        unfocusedTextColor = TextoBlanco,
                        cursorColor = VerdeNeon
                    ),
                    trailingIcon = {
                        IconButton(onClick = { procesar(texto); texto = "" }) {
                            Icon(Icons.Filled.Send, contentDescription = "Enviar", tint = VerdeNeon)
                        }
                    }
                )
            }
        }
    }

    plan?.let { p ->
        AlertDialog(
            onDismissRequest = { plan = null },
            containerColor = SuperficieOscura,
            titleContentColor = TextoBlanco,
            textContentColor = TextoBlanco,
            title = { Text(p.titulo, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Escuché: «${p.texto}»", color = TextoGris, fontSize = 13.sp)
                    p.detalle.forEach { FilaDato("•", it) }
                    if (p.comando == Comando.LLAMAR_CONTACTO) {
                        Text("¿Confirmas?", color = NaranjaAlerta, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { plan = null; correr(p, confirmado = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = Color06)
                ) { Text("Sí, hazlo", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    almacen.registrar(RegistroOrden(texto = p.texto, comando = p.comando.name))
                    plan = null
                    decir("Cancelado. No hice ningún cambio.")
                }) { Text("No, cancelar", color = TextoBlanco) }
            }
        )
    }
}

private val Color06 = androidx.compose.ui.graphics.Color(0xFF06130D)

@Composable
private fun Burbuja(m: MensajeVinto, atenuada: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.esUsuario) Arrangement.End else Arrangement.Start) {
        val forma = RoundedCornerShape(
            topStart = 18.dp, topEnd = 18.dp,
            bottomStart = if (m.esUsuario) 18.dp else 4.dp,
            bottomEnd = if (m.esUsuario) 4.dp else 18.dp
        )
        Box(
            Modifier
                .widthIn(max = 300.dp)
                .clip(forma)
                .background(if (m.esUsuario) VerdeNeon.copy(alpha = 0.15f) else SuperficieOscura)
                .border(1.dp, if (m.esUsuario) VerdeNeon.copy(alpha = 0.35f) else BordeOscuro, forma)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                m.texto,
                color = if (atenuada) TextoGris else TextoBlanco,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}
