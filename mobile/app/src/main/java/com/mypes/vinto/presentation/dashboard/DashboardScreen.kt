package com.mypes.vinto.presentation.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.DashboardViewModel
import com.mypes.vinto.viewmodel.EstadoAccion
import com.mypes.vinto.viewmodel.MensajeAsistente

@Composable
fun DashboardScreen(nav: NavController, vm: DashboardViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    val listState = rememberLazyListState()
    var menuAbierto by remember { mutableStateOf(false) }
    var mensajeCorregible by remember { mutableStateOf<MensajeAsistente?>(null) }
    var textoCorregido by remember { mutableStateOf("") }

    LaunchedEffect(estado.mensajes.size, estado.textoParcial) {
        val lastIndex = estado.mensajes.size - 1
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        Row(
            Modifier.fillMaxWidth().background(SuperficieOscura).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Yapay", color = TextoBlanco, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text(
                    when {
                        estado.escuchando -> "Escuchando..."
                        estado.modeloDisponible == false -> "Modelo de voz no disponible"
                        else -> "Asistente offline-first"
                    },
                    color = if (estado.escuchando) NaranjaAlerta else TextoGris,
                    fontSize = 12.sp
                )
            }
            Box {
                IconButton(onClick = { menuAbierto = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = TextoBlanco)
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    listOf(
                        "perfil" to "Mi perfil",
                        "sync" to "Sincronización",
                        "inventario" to "Inventario",
                        "venta_manual" to "Registrar venta",
                        "fiados" to "Fiados",
                        "reportes" to "Reportes",
                        "api_login" to "Vincular cuenta"
                    ).forEach { (route, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                menuAbierto = false
                                nav.navigate(route)
                            }
                        )
                    }
                }
            }
        }

        Text(
            "Las correcciones se guardan en el dispositivo. Solo se envían si eliges autorizarlo.",
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            color = TextoGris,
            fontSize = 12.sp
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(estado.mensajes, key = { it.id }) { mensaje ->
                ChatBubble(mensaje) {
                    mensajeCorregible = mensaje
                    textoCorregido = mensaje.texto
                }
            }
            if (estado.textoParcial.isNotBlank()) {
                item(key = "partial") {
                    ChatBubble(MensajeAsistente(texto = estado.textoParcial, esUsuario = true)) {}
                }
            }
            estado.mensajeError?.let { error ->
                item(key = "error") {
                    Text(error, color = RojoCancelar, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }

        Box(
            Modifier.fillMaxWidth().background(SuperficieOscura).padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.size(82.dp).background(
                    if (estado.escuchando) RojoCancelar else VerdeNeon,
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    enabled = estado.modeloDisponible == true || estado.escuchando,
                    onClick = {
                        if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha()
                    },
                    modifier = Modifier.size(74.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (estado.escuchando) "Detener micrófono" else "Hablar",
                        tint = FondoOscuro,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }

    val pendiente = estado.estadoAccion as? EstadoAccion.EsperandoConfirmacion
    if (pendiente != null) {
        AlertDialog(
            onDismissRequest = vm::cancelarAccion,
            title = { Text("Confirmar acción") },
            text = {
                Text(
                    "Entendí: «${pendiente.textoOriginal}».\n\n" +
                        "¿Confirmas que debo ${vm.nombreAccion(pendiente.intencion)}?"
                )
            },
            confirmButton = {
                Button(onClick = vm::confirmarAccion) { Text("Sí, continuar") }
            },
            dismissButton = {
                OutlinedButton(onClick = vm::cancelarAccion) { Text("Cancelar") }
            }
        )
    }

    mensajeCorregible?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { mensajeCorregible = null },
            title = { Text("Corregir transcripción") },
            text = {
                Column {
                    Text(
                        "El texto se enviará al servidor para aprendizaje solo si autorizas esta opción.",
                        color = TextoGris,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Original: ${mensaje.texto}", color = TextoGris)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textoCorregido,
                        onValueChange = { textoCorregido = it },
                        label = { Text("Texto corregido") },
                        singleLine = false,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = textoCorregido.isNotBlank(),
                    onClick = {
                        vm.registrarCorreccion(mensaje.texto, textoCorregido, compartir = true)
                        mensajeCorregible = null
                    }
                ) { Text("Autorizar envío al servidor") }
            },
            dismissButton = {
                Row {
                    TextButton(
                        enabled = textoCorregido.isNotBlank(),
                        onClick = {
                            vm.registrarCorreccion(mensaje.texto, textoCorregido, compartir = false)
                            mensajeCorregible = null
                        }
                    ) { Text("Solo en dispositivo") }
                    TextButton(onClick = { mensajeCorregible = null }) { Text("Cancelar") }
                }
            }
        )
    }
}

@Composable
private fun ChatBubble(mensaje: MensajeAsistente, onCorrect: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
    ) {
        Card(
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
            Column(Modifier.padding(12.dp)) {
                Text(mensaje.texto, color = TextoBlanco, fontSize = 14.sp)
                if (mensaje.corregible) {
                    TextButton(onClick = onCorrect) {
                        Text("Corregir texto", color = VerdeNeon, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
