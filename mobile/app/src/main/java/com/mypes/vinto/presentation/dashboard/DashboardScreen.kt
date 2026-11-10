package com.mypes.vinto.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(nav: NavController, vm: DashboardViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("MypesLocal", color = TextoBlanco, fontSize = 24.sp)
                Text("Tu negocio, incluso sin conexión", color = TextoGris)
            }
            Row {
                IconButton(onClick = { nav.navigate("perfil") }) {
                    Text("👤", fontSize = 24.sp)
                }
                IconButton(
                    onClick = { if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (estado.escuchando) RojoCancelar else VerdeNeon
                    )
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Hablar", tint = FondoOscuro)
                }
            }
        }
        if (estado.textoParcial.isNotBlank()) Text(estado.textoParcial, color = VerdeNeon, modifier = Modifier.padding(vertical = 12.dp))
        estado.mensajeError?.let { Text(it, color = RojoCancelar) }
        Spacer(Modifier.height(24.dp))
        Text("Productos: ${estado.totalProductos}", color = TextoBlanco, fontSize = 18.sp)
        Text("Alertas de stock: ${estado.alertas}", color = if (estado.alertas > 0) NaranjaAlerta else TextoGris)
        Spacer(Modifier.height(20.dp))
        Button(onClick = { nav.navigate("inventario") }, modifier = Modifier.fillMaxWidth()) {
            Text("Abrir inventario")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate("venta_manual") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar venta")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate("chat") }, modifier = Modifier.fillMaxWidth()) {
            Text("Asistente de voz")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate("sync") }, modifier = Modifier.fillMaxWidth()) {
            Text("Sincronización")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate("api_login") }, modifier = Modifier.fillMaxWidth()) {
            Text("Vincular cuenta del servidor")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate("reportes") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver reporte del día")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { nav.navigate("fiados") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaAlerta)
        ) {
            Text("Módulo de Fiados (Deudas)", color = FondoOscuro)
        }
    }
}
