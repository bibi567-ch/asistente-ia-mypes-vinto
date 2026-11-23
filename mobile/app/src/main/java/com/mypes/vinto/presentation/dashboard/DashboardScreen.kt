package com.mypes.vinto.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.DashboardViewModel
import com.mypes.vinto.viewmodel.EstadoAccion

@Composable
fun DashboardScreen(nav: NavController, vm: DashboardViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(estado.mensajes.size) {
        if (estado.mensajes.isNotEmpty()) listState.animateScrollToItem(estado.mensajes.lastIndex)
    }

    Box(Modifier.fillMaxSize().background(FondoOscuro)) {
        Column(Modifier.fillMaxSize()) {
            DashboardHeader(
                listening = estado.escuchando,
                onProfile = { nav.navigate("perfil") }
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 112.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "MIÉRCOLES, 11 DE NOVIEMBRE DE 2026",
                        color = TextoGris,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("¡Buenas tardes!", color = TextoBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("Ferretería El Constructor • Nodo local activo", color = TextoGris, fontSize = 12.sp)
                        }
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = SuperficieOscura
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ShoppingCart, null, tint = VerdeNeon)
                            }
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = SuperficieOscura,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VerdeNeon.copy(alpha = 0.12f))
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ASISTENTE VOCAL MYPE", color = VerdeNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("VOSK / EDGE", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Registra ventas, compras y fiados por voz natural en castellano o quechua.",
                                color = TextoBlanco,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = estado.modeloDisponible != false
                            ) {
                                Icon(Icons.Default.Mic, null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (estado.escuchando) "Detener escucha" else "Hablar con Yapay")
                            }
                        }
                    }
                }

                item {
                    Text("RESUMEN OPERATIVO DE HOY", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricCard(Modifier.weight(1f), "VENTAS HOY", "Bs 0", "0 ventas", Icons.Default.PointOfSale)
                            MetricCard(Modifier.weight(1f), "CAJA DISPONIBLE", "Bs 0", "Saldo local", Icons.Default.Assessment)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricCard(Modifier.weight(1f), "INVENTARIO", "Activo", "Ver productos", Icons.Default.Inventory2)
                            MetricCard(Modifier.weight(1f), "STOCK CRÍTICO", "0", "Sin alertas", Icons.Default.Warning)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricCard(Modifier.weight(1f), "IVA", "13%", "Alícuota vigente", Icons.Default.Assessment)
                            MetricCard(Modifier.weight(1f), "EDGE OFFLINE", "0 KB", "Modo seguro", Icons.Default.Shield)
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SuperficieOscura
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("ANÁLISIS YAPAY IA", color = VerdeNeon, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Se acerca la temporada alta: anticipa compras de impermeabilizantes, tuberías PVC, canaletas y selladores.",
                                color = TextoBlanco,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                items(estado.mensajes.takeLast(8), key = { it.id }) { mensaje ->
                    ChatBubble(mensaje)
                }
            }
        }

        BottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onVoice = { if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha() },
            onInventory = { nav.navigate("inventario") },
            onSales = { nav.navigate("venta_manual") },
            onFiados = { nav.navigate("fiados") },
            onSettings = { nav.navigate("perfil") }
        )
    }

    val pendiente = estado.estadoAccion as? EstadoAccion.EsperandoConfirmacion
    if (pendiente != null) {
        AlertDialog(
            onDismissRequest = vm::cancelarAccion,
            title = { Text("Confirmar acción", color = TextoBlanco) },
            text = { Text("¿Confirmas que debo ${vm.nombreAccion(pendiente.intencion)}?", color = TextoGris) },
            confirmButton = { Button(onClick = vm::confirmarAccion) { Text("Sí, continuar") } },
            dismissButton = { OutlinedButton(onClick = vm::cancelarAccion) { Text("Cancelar") } },
            containerColor = SuperficieOscura
        )
    }
}

@Composable
private fun DashboardHeader(listening: Boolean, onProfile: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FondoOscuro,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SuperficieOscura)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = SuperficieOscura) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, null, tint = VerdeNeon)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Yapay", color = TextoBlanco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(6.dp))
                        Text("AI", color = VerdeNeon, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Text(if (listening) "LOCAL ASR / ESCUCHANDO" else "INICIO / VOZ", color = TextoGris, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = SuperficieOscura,
                onClick = onProfile
            ) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = VerdeNeon, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(modifier: Modifier, label: String, value: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(14.dp),
        color = SuperficieOscura
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, color = TextoGris, fontSize = 11.sp)
                Icon(icon, null, tint = VerdeNeon, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(value, color = TextoBlanco, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(detail, color = VerdeNeon, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun BottomNav(
    modifier: Modifier,
    onVoice: () -> Unit,
    onInventory: () -> Unit,
    onSales: () -> Unit,
    onFiados: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = FondoOscuro.copy(alpha = 0.96f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuperficieOscura)
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            NavItem("Inventario", Icons.Default.Inventory2, onInventory)
            NavItem("Venta", Icons.Default.PointOfSale, onSales)
            Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = VerdeNeon, onClick = onVoice) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Mic, null, tint = FondoOscuro)
                }
            }
            NavItem("Fiados", Icons.Default.Person, onFiados)
            NavItem("Ajustes", Icons.Default.Settings, onSettings)
        }
    }
}

@Composable
private fun NavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = TextoGris, modifier = Modifier.size(20.dp))
            Text(label, color = TextoGris, fontSize = 9.sp)
        }
    }
}

@Composable
private fun ChatBubble(mensaje: com.mypes.vinto.viewmodel.MensajeAsistente) {
    val align = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
    Row(Modifier.fillMaxWidth(), horizontalArrangement = align) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (mensaje.esUsuario) 16.dp else 4.dp,
                bottomEnd = if (mensaje.esUsuario) 4.dp else 16.dp
            ),
            color = if (mensaje.esUsuario) VerdeNeon.copy(alpha = 0.18f) else SuperficieOscura,
            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeNeon.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Text(mensaje.texto, modifier = Modifier.padding(12.dp), color = TextoBlanco, fontSize = 13.sp)
        }
    }
}
