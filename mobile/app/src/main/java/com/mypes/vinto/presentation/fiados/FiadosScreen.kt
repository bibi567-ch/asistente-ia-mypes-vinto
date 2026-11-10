package com.mypes.vinto.presentation.fiados

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.mypes.vinto.data.Venta
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.FiadosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FiadosScreen(nav: NavController, vm: FiadosViewModel = viewModel()) {
    val fiados by vm.fiados.collectAsState()
    val cobroEnCurso by vm.cobroEnCurso.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    var ventaSeleccionada by remember { mutableStateOf<Venta?>(null) }
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val totalPendiente = fiados.sumOf { it.total }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)
    ) {
        Text("Fiados pendientes", color = TextoBlanco, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "${fiados.size} deudas · ${String.format(Locale.getDefault(), "%.2f", totalPendiente)} Bs",
            color = NaranjaAlerta,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Al cobrar se registra el ingreso localmente y queda pendiente de sincronización.",
            color = TextoGris,
            fontSize = 12.sp
        )
        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = NaranjaAlerta, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))

        if (fiados.isEmpty()) {
            Box(
                Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Excelente! No hay deudas pendientes.", color = VerdeNeon, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fiados, key = { it.id }) { venta ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    venta.clienteFiado?.takeIf(String::isNotBlank) ?: "Cliente",
                                    color = TextoBlanco,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    formatoFecha.format(Date(venta.creadoEn)),
                                    color = TextoGris,
                                    fontSize = 12.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${String.format(Locale.getDefault(), "%.2f", venta.total)} Bs",
                                    color = NaranjaAlerta,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    enabled = cobroEnCurso == null,
                                    onClick = { ventaSeleccionada = venta }
                                ) {
                                    Text(
                                        if (cobroEnCurso == venta.id) "Guardando..." else "Cobrar",
                                        color = VerdeNeon,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Volver", color = TextoBlanco)
        }
    }

    ventaSeleccionada?.let { venta ->
        AlertDialog(
            onDismissRequest = { ventaSeleccionada = null },
            title = { Text("Confirmar cobro") },
            text = {
                Text(
                    "¿Confirmas el pago completo de ${String.format(Locale.getDefault(), "%.2f", venta.total)} Bs de ${venta.clienteFiado ?: "Cliente"}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ventaSeleccionada = null
                        vm.cobrarDeuda(venta.id)
                    }
                ) {
                    Text("Confirmar cobro", color = VerdeNeon)
                }
            },
            dismissButton = {
                TextButton(onClick = { ventaSeleccionada = null }) {
                    Text("Cancelar", color = TextoGris)
                }
            },
            containerColor = SuperficieOscura
        )
    }
}
