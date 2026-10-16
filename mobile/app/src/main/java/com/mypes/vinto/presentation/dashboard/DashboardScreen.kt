package com.mypes.vinto.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("MypesLocal", color = TextoBlanco, fontSize = 24.sp)
                Text("Tu negocio, incluso sin conexión", color = TextoGris)
            }
            Button(
                onClick = { if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (estado.escuchando) RojoCancelar else VerdeNeon
                )
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Hablar", tint = FondoOscuro)
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
    }
}
