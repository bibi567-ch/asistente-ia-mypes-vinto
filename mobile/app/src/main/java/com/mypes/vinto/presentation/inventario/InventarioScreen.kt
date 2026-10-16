package com.mypes.vinto.presentation.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(nav: NavController, vm: InventarioViewModel = viewModel()) {
    val productos by vm.productos.collectAsState()
    var dialogo by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Inventario", color = TextoBlanco, fontSize = 24.sp)
            IconButton(onClick = { dialogo = true }) { Icon(Icons.Default.Add, "Agregar", tint = VerdeNeon) }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(productos, key = { it.id }) { producto ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SuperficieOscura)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(producto.nombre, color = TextoBlanco)
                            Text("${producto.precio} Bs", color = TextoGris)
                        }
                        Text("${producto.stock} unidades", color = if (producto.stock < 5) NaranjaAlerta else VerdeNeon)
                    }
                }
            }
        }
    }
    if (dialogo) {
        AlertDialog(
            onDismissRequest = { dialogo = false },
            title = { Text("Nuevo producto") },
            text = {
                Column {
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(precio, { precio = it }, label = { Text("Precio (Bs)") })
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock inicial") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val precioNumero = precio.replace(',', '.').toDoubleOrNull()
                    val stockNumero = stock.replace(',', '.').toDoubleOrNull()
                    if (nombre.isNotBlank() && precioNumero != null && stockNumero != null) {
                        vm.agregarProducto(nombre, precioNumero, stockNumero)
                        nombre = ""; precio = ""; stock = ""; dialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { dialogo = false }) { Text("Cancelar") } }
        )
    }
}
