package com.mypes.vinto.presentation.ventas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.InventarioViewModel
import com.mypes.vinto.viewmodel.VentaFormulario
import com.mypes.vinto.viewmodel.VentaViewModel
import java.text.Normalizer

@Composable
fun VentaManualScreen(
    nav: NavController,
    productoSugerido: String = "",
    cantidadSugerida: Double = 1.0,
    vm: VentaViewModel = viewModel(),
    invVm: InventarioViewModel = viewModel()
) {
    val form by vm.form.collectAsState()
    val productos by invVm.productos.collectAsState()
    var mostrarSelector by remember { mutableStateOf(false) }
    var clienteFiado by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var sugerenciaNoEncontrada by remember { mutableStateOf<String?>(null) }
    var prefijoAplicado by remember { mutableStateOf(false) }

    LaunchedEffect(productoSugerido, cantidadSugerida, productos) {
        if (prefijoAplicado) return@LaunchedEffect
        if (productoSugerido.isBlank() || productos.isNotEmpty()) {
            val cantidad = cantidadSugerida.takeIf { it.isFinite() }
            val producto = productoSugerido
                .takeIf(String::isNotBlank)
                ?.let { sugerido ->
                    productos.firstOrNull { normalizarNombre(it.nombre) == normalizarNombre(sugerido) }
                }
            val cantidadFormulario = cantidad ?: if (productoSugerido.isNotBlank()) 1.0 else null

            if (cantidadFormulario != null) {
                cantidadTexto = cantidadFormulario.toString()
            }
            if (producto != null) {
                precioTexto = producto.precio.toString()
                vm.actualizarFormulario(
                    form.copy(
                        productoId = producto.id,
                        nombreProducto = producto.nombre,
                        precioUnitario = producto.precio,
                        cantidad = cantidadFormulario ?: 1.0
                    )
                )
            } else {
                if (productoSugerido.isNotBlank()) {
                    sugerenciaNoEncontrada = productoSugerido
                    mostrarSelector = true
                }
                if (cantidadFormulario != null) {
                    vm.actualizarFormulario(form.copy(cantidad = cantidadFormulario))
                }
            }
            prefijoAplicado = true
        }
    }

    LaunchedEffect(form.exito) {
        if (form.exito) {
            clienteFiado = ""
            cantidadTexto = ""
            precioTexto = ""
            vm.actualizarFormulario(VentaFormulario())
            nav.popBackStack()
        }
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text("Registrar venta", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Confirma el producto y la cantidad", color = TextoGris, fontSize = 14.sp)
        sugerenciaNoEncontrada?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                "No encontré \"$it\" en el inventario. Selecciona el producto correcto.",
                color = NaranjaAlerta,
                fontSize = 13.sp
            )
        }
        Spacer(Modifier.height(24.dp))

        Card(
            Modifier.fillMaxWidth().clickable { mostrarSelector = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Producto", color = TextoGris, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (form.nombreProducto.isBlank()) "Selecciona un producto" else form.nombreProducto,
                    color = if (form.nombreProducto.isBlank()) TextoGris else TextoBlanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = { input ->
                    cantidadTexto = input
                    val cantidad = input.replace(',', '.').toDoubleOrNull()
                        ?.takeIf { it.isFinite() } ?: 0.0
                    vm.actualizarFormulario(form.copy(cantidad = cantidad))
                },
                label = { Text("Cantidad") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = precioTexto,
                onValueChange = { input ->
                    precioTexto = input
                    val precio = input.replace(',', '.').toDoubleOrNull()
                        ?.takeIf { it.isFinite() } ?: 0.0
                    vm.actualizarFormulario(form.copy(precioUnitario = precio))
                },
                label = { Text("Precio (Bs)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = form.formaPago == "CONTADO",
                onClick = { vm.actualizarFormulario(form.copy(formaPago = "CONTADO")) },
                label = { Text("Al contado") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VerdeNeon.copy(alpha = 0.2f),
                    selectedLabelColor = VerdeNeon
                )
            )
            FilterChip(
                selected = form.formaPago == "FIADO",
                onClick = { vm.actualizarFormulario(form.copy(formaPago = "FIADO")) },
                label = { Text("Al fiado") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NaranjaAlerta.copy(alpha = 0.2f),
                    selectedLabelColor = NaranjaAlerta
                )
            )
        }

        if (form.formaPago == "FIADO") {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = clienteFiado,
                onValueChange = {
                    clienteFiado = it
                    vm.actualizarFormulario(form.copy(clienteFiado = it.ifBlank { null }))
                },
                label = { Text("Cliente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", color = TextoGris, fontSize = 16.sp)
                Text(
                    "${form.total} Bs",
                    color = VerdeNeon,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        form.error?.let {
            Text(it, color = RojoCancelar, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = { vm.guardarVenta() },
            enabled = form.esValido && !form.guardando,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon)
        ) {
            if (form.guardando) {
                CircularProgressIndicator(color = FondoOscuro, modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar venta", color = FondoOscuro, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Cancelar", color = RojoCancelar)
        }
    }

    if (mostrarSelector) {
        AlertDialog(
            onDismissRequest = { mostrarSelector = false },
            title = { Text("Selecciona un producto") },
            text = {
                if (productos.isEmpty()) {
                    Text("No hay productos. Agrega uno primero.")
                } else {
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        items(productos, key = { it.id }) { producto ->
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    vm.actualizarFormulario(
                                        form.copy(
                                            productoId = producto.id,
                                            nombreProducto = producto.nombre,
                                            precioUnitario = producto.precio
                                        )
                                    )
                                    precioTexto = producto.precio.toString()
                                    mostrarSelector = false
                                }.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(producto.nombre, color = TextoBlanco)
                                    Text("Stock: ${producto.stock}", color = TextoGris, fontSize = 12.sp)
                                }
                                Text("${producto.precio} Bs", color = TextoGris)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarSelector = false }) { Text("Cerrar") }
            }
        )
    }
}

private fun normalizarNombre(nombre: String): String =
    Normalizer.normalize(nombre.trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
