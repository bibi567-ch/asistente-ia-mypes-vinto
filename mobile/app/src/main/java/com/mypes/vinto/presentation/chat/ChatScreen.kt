package com.mypes.vinto.presentation.chat

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.IntentClassifier
import com.mypes.vinto.nlu.YapayAgent
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.launch

data class Mensaje(val texto: String, val esUsuario: Boolean)

@Composable
fun ChatScreen(nav: NavController) {
    val context = LocalContext.current
    val asistente = remember { AsistenteVozFacade(context) }
    val yapay = remember { YapayAgent(context) }
    val mensajesYapay by yapay.estadoChat.collectAsState()
    val scope = rememberCoroutineScope()
    val resultadoProcesado = remember { AtomicBoolean(false) }
    var textoParcial by remember { mutableStateOf("") }
    var escuchando by remember { mutableStateOf(false) }
    var modeloDisponible by remember { mutableStateOf<Boolean?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { modeloDisponible = asistente.inicializar() }
    LaunchedEffect(mensajesYapay.size, textoParcial) { scrollState.animateScrollTo(scrollState.maxValue) }
    DisposableEffect(asistente) { onDispose { asistente.close() } }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        Row(
            Modifier.fillMaxWidth().background(SuperficieOscura).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(VerdeNeon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("IA", fontSize = 14.sp, color = FondoOscuro, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Asistente", color = TextoBlanco, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    when {
                        escuchando -> "Escuchando..."
                        modeloDisponible == false -> "Modelo de voz no disponible"
                        else -> "Listo"
                    },
                    color = if (escuchando) NaranjaAlerta else TextoGris,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            mensajesYapay.forEach { BurbujaMensaje(Mensaje(it.texto, it.esUsuario)) }
            if (textoParcial.isNotBlank()) BurbujaMensaje(Mensaje(textoParcial, true))
        }

        Row(
            Modifier.fillMaxWidth().background(SuperficieOscura).padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(80.dp).background(if (escuchando) RojoCancelar else VerdeNeon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    enabled = modeloDisponible == true || escuchando,
                    onClick = {
                        if (escuchando) {
                            resultadoProcesado.set(true)
                            asistente.detener()
                            escuchando = false
                        } else {
                            resultadoProcesado.set(false)
                            escuchando = true
                            textoParcial = ""
                            asistente.escuchar(
                                onParcial = { textoParcial = it },
                                onResultado = { resultado ->
                                    if (resultadoProcesado.compareAndSet(false, true)) {
                                        escuchando = false
                                        textoParcial = ""
                                        if (resultado.intencion == IntentClassifier.Intencion.VENTA) {
                                            yapay.registrarInteraccion(
                                                resultado.textoOriginal,
                                                generarRespuesta(resultado)
                                            )
                                            val producto = Uri.encode(resultado.entidades.producto.orEmpty())
                                            val cantidad = resultado.entidades.cantidad
                                                ?.takeIf { it.isFinite() } ?: 1.0
                                            nav.navigate(
                                                "venta_manual?producto=$producto&cantidad=$cantidad"
                                            )
                                        } else {
                                            scope.launch {
                                                yapay.procesarComando(resultado.textoOriginal)
                                            }
                                        }
                                    }
                                },
                                onError = { error ->
                                    if (resultadoProcesado.compareAndSet(false, true)) {
                                        escuchando = false
                                        textoParcial = ""
                                        yapay.registrarInteraccion(
                                            "",
                                            "No se pudo reconocer la voz: $error"
                                        )
                                    }
                                }
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (escuchando) "Detener micrófono" else "Iniciar micrófono",
                        tint = FondoOscuro,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

private fun generarRespuesta(resultado: AsistenteVozFacade.Resultado): String =
    when (resultado.intencion) {
        IntentClassifier.Intencion.VENTA ->
            "Entendí una posible venta. No se ha registrado todavía; revisa los datos y confírmala en el formulario."
        IntentClassifier.Intencion.COMPRA ->
            "Entendí una posible compra, pero esta conversación todavía no la registra."
        IntentClassifier.Intencion.FIADO ->
            "Entendí que podría ser una venta fiada. No se ha guardado; registra los datos en el formulario."
        IntentClassifier.Intencion.CONSULTA_STOCK -> {
            val producto = resultado.entidades.producto ?: "producto"
            "Escuché una consulta sobre $producto. La consulta de stock por voz aún no está conectada."
        }
        IntentClassifier.Intencion.CONSULTA_VENTAS ->
            "La consulta de ventas por voz aún no está conectada."
        IntentClassifier.Intencion.CANCELAR ->
            "De acuerdo. No se realizó ningún cambio."
        IntentClassifier.Intencion.DESCONOCIDO ->
            "No entendí bien. ¿Puedes repetir?"
        else -> "Entendí la solicitud, pero todavía no está conectada a una operación."
    }

@Composable
private fun BurbujaMensaje(mensaje: Mensaje) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
    ) {
        Card(
            Modifier,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (mensaje.esUsuario) 16.dp else 4.dp,
                bottomEnd = if (mensaje.esUsuario) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (mensaje.esUsuario) AzulElectrico else SuperficieOscura
            )
        ) {
            Text(mensaje.texto, Modifier.padding(12.dp), color = TextoBlanco, fontSize = 14.sp)
        }
    }
}
