package com.mypes.vinto.presentation.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
    var busqueda by remember { mutableStateOf("") }
    val productosFiltrados = productos.filter {
        it.nombre.contains(busqueda.trim(), ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Inventario", color = TextoBlanco, fontSize = 24.sp)
            IconButton(onClick = { dialogo = true }) { Icon(Icons.Default.Add, "Agregar", tint = VerdeNeon) }
        }
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar producto...", color = TextoGris) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextoGris) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeNeon,
                unfocusedBorderColor = TextoGris,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            shape = RoundedCornerShape(12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(productosFiltrados, key = { it.id }) { producto ->
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
            if (productosFiltrados.isEmpty()) {
                item {
                    Text(
                        if (busqueda.isBlank()) "Todavía no hay productos." else "No se encontraron productos.",
                        color = TextoGris,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
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
