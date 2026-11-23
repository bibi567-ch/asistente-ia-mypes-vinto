package com.mypes.vinto.presentation.dashboard

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import com.mypes.vinto.nlu.IntentClassifier
import androidx.navigation.NavController
import com.mypes.vinto.viewmodel.EstadoAccion
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(nav: NavController, vm: DashboardViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    val context = LocalContext.current
    val solicitarCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) vm.confirmarAccion() else vm.cancelarAccion()
    }
    val accionPendiente = estado.estadoAccion as? EstadoAccion.EsperandoConfirmacion
    if (accionPendiente != null) {
        val detalle = accionPendiente.parametros.contacto
            ?: accionPendiente.parametros.numeroTelefono
            ?: accionPendiente.parametros.nombreApp
            ?: accionPendiente.parametros.textoALeer
        val accion = vm.nombreAccion(accionPendiente.intencion)
        AlertDialog(
            onDismissRequest = vm::cancelarAccion,
            title = { Text("Confirmar acción") },
            text = { Text("¿Quieres que ejecute: $accion${detalle?.let { " ($it)" } ?: ""}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (
                            accionPendiente.intencion == IntentClassifier.Intencion.LINTERNA &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ) {
                            solicitarCamara.launch(Manifest.permission.CAMERA)
                        } else {
                            vm.confirmarAccion()
                        }
                    }
                ) { Text("Sí") }
            },
            dismissButton = { TextButton(onClick = vm::cancelarAccion) { Text("Cancelar") } }
        )
    }
    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Text("Vinto Voz", color = TextoBlanco, fontSize = 24.sp)
        Text("Asistente telefónico offline", color = TextoGris)
        Spacer(Modifier.height(24.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { if (estado.escuchando) vm.detenerEscucha() else vm.iniciarEscucha() },
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (estado.escuchando) RojoCancelar else VerdeNeon
                )
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Hablar", tint = FondoOscuro, modifier = Modifier.size(56.dp))
            }
        }
        if (estado.textoParcial.isNotBlank()) Text(estado.textoParcial, color = VerdeNeon, modifier = Modifier.padding(vertical = 12.dp))
        estado.respuesta?.let { Text(it, color = TextoBlanco, modifier = Modifier.padding(vertical = 8.dp)) }
        estado.mensajeError?.let { Text(it, color = RojoCancelar) }
        Spacer(Modifier.height(24.dp))
        Text("Historial de voz", color = TextoBlanco, fontSize = 18.sp)
        estado.historial.forEach { entrada ->
            val resultado = when {
                entrada.ejecutado -> "completado"
                entrada.confirmado -> "no completado"
                else -> "pendiente"
            }
            Text("${entrada.intencion}: ${entrada.textoComando} · $resultado", color = TextoGris)
        }
    }
}
