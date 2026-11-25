package com.mypes.vinto.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.DashboardViewModel

@Composable
fun ChatScreen(
    nav: NavController,
    onNavegarVenta: (String?, Double?) -> Unit,
    onNavegarInventario: (String?) -> Unit,
    onNavegarFiados: () -> Unit,
    voiceViewModel: DashboardViewModel,
    vm: ChatViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val mensajes by vm.mensajes.collectAsState()
    val estadoVoz by voiceViewModel.estado.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) listState.animateScrollToItem(mensajes.lastIndex)
    }
    LaunchedEffect(estado.destinoPendiente) {
        when (val destino = estado.destinoPendiente) {
            is ChatDestination.Venta -> onNavegarVenta(destino.producto, destino.cantidad)
            is ChatDestination.Inventario -> onNavegarInventario(destino.filtro)
            ChatDestination.Fiados -> onNavegarFiados()
            null -> return@LaunchedEffect
        }
        vm.consumirDestinoPendiente()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(FondoOscuro).imePadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextoBlanco)
            }
            Column {
                Text("Yapay", color = TextoBlanco, fontSize = 22.sp)
                Text("Asistente local para tu negocio", color = TextoGris, fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(mensajes, key = { index, mensaje -> "${mensaje.creadoEn}-$index" }) { _, mensaje ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        shape = RoundedCornerShape(16.dp),
                        color = if (mensaje.esUsuario) VerdeNeon else SuperficieOscura
                    ) {
                        Text(
                            text = mensaje.texto,
                            color = if (mensaje.esUsuario) FondoOscuro else TextoBlanco,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            if (estado.procesando) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(4.dp),
                            color = VerdeNeon,
                            strokeWidth = 2.dp
                        )
                        Text("Yapay está procesando…", color = TextoGris, fontSize = 12.sp)
                    }
                }
            }
        }

        if (estadoVoz.escuchando) {
            Text(
                estadoVoz.textoParcial.ifBlank { "Escuchando… suelta para enviar." },
                color = TextoBlanco,
                fontSize = 12.sp
            )
        }
        estadoVoz.mensajeError?.let { mensaje ->
            Text(mensaje, color = RojoCancelar, fontSize = 12.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = estado.textoEntrada,
                onValueChange = vm::actualizarTexto,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe a Yapay", color = TextoGris) },
                enabled = !estado.procesando && !estadoVoz.escuchando,
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeNeon,
                    unfocusedBorderColor = TextoGris,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco,
                    cursorColor = VerdeNeon
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { vm.enviarMensaje() })
            )
            Button(
                onClick = vm::enviarMensaje,
                enabled = estado.textoEntrada.isNotBlank() && !estado.procesando &&
                    !estadoVoz.escuchando,
                modifier = Modifier.padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeNeon,
                    disabledContainerColor = SuperficieOscura
                )
            ) {
                Text("Enviar", color = if (estado.textoEntrada.isNotBlank()) FondoOscuro else TextoGris)
            }
            val vozDisponible = estadoVoz.modeloDisponible == true && !estado.procesando
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (estadoVoz.escuchando) RojoCancelar else SuperficieOscura)
                    .pointerInput(vozDisponible) {
                        if (vozDisponible) {
                            detectTapGestures(
                                onPress = {
                                    val inicioExitoso = voiceViewModel.iniciarEscuchaParaChat(
                                        vm::procesarTranscripcionVoz
                                    )
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        if (inicioExitoso) voiceViewModel.detenerEscucha()
                                    }
                                }
                            )
                        }
                    }
                    .semantics(mergeDescendants = true) {
                        role = Role.Button
                        contentDescription = if (estadoVoz.escuchando) {
                            "Grabando. Suelta para enviar."
                        } else {
                            "Mantén presionado para hablar."
                        }
                        if (!vozDisponible) disabled()
                        onClick(label = if (estadoVoz.escuchando) "Detener grabación" else "Iniciar grabación") {
                            when {
                                !vozDisponible && !estadoVoz.escuchando -> false
                                estadoVoz.escuchando -> {
                                    voiceViewModel.detenerEscucha()
                                    true
                                }
                                else -> voiceViewModel.iniciarEscuchaParaChat(vm::procesarTranscripcionVoz)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = null,
                    tint = if (estadoVoz.escuchando) TextoBlanco else VerdeNeon
                )
            }
        }
        Text(
            "Las ventas se abren para revisión; Yapay no las guarda sin confirmación.",
            color = TextoGris,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
