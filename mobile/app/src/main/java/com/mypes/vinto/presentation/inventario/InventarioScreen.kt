package com.mypes.vinto.presentation.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.data.Producto
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(nav: NavController, vm: InventarioViewModel = viewModel()) {
    val productos by vm.productos.collectAsState()
    var dialogoCrear by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var busqueda by remember { mutableStateOf("") }

    val productosFiltrados = productos.filter {
        it.nombre.contains(busqueda.trim(), ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Inventario", color = TextoBlanco, fontSize = 24.sp)
            IconButton(onClick = { dialogoCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto", tint = VerdeNeon)
            }
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombre, color = TextoBlanco)
                            Text("${producto.precio} Bs", color = TextoGris)
                            Text("${producto.stock} unidades", color = if (producto.stock < 5) NaranjaAlerta else VerdeNeon)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { productoAEditar = producto }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AzulElectrico)
                            }
                            IconButton(onClick = { vm.eliminarProducto(producto) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RojoCancelar)
                            }
                        }
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

    if (dialogoCrear) {
        DialogoProducto(
            titulo = "Nuevo producto",
            productoInicial = null,
            onDismiss = { dialogoCrear = false },
            onGuardar = { nombre, precio, stock ->
                vm.agregarProducto(nombre, precio, stock)
                dialogoCrear = false
            }
        )
    }

    if (productoAEditar != null) {
        DialogoProducto(
            titulo = "Editar producto",
            productoInicial = productoAEditar,
            onDismiss = { productoAEditar = null },
            onGuardar = { nombre, precio, stock ->
                vm.actualizarProducto(productoAEditar!!.copy(nombre = nombre, precio = precio, stock = stock))
                productoAEditar = null
            }
        )
    }
}

@Composable
fun DialogoProducto(
    titulo: String,
    productoInicial: Producto?,
    onDismiss: () -> Unit,
    onGuardar: (String, Double, Double) -> Unit
) {
    var nombre by remember(productoInicial) { mutableStateOf(productoInicial?.nombre ?: "") }
    var precio by remember(productoInicial) { mutableStateOf(productoInicial?.precio?.toString() ?: "") }
    var stock by remember(productoInicial) { mutableStateOf(productoInicial?.stock?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (Bs)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val precioNum = precio.replace(',', '.').toDoubleOrNull()
                val stockNum = stock.replace(',', '.').toDoubleOrNull()
                if (nombre.isNotBlank() && precioNum != null && stockNum != null) {
                    onGuardar(nombre, precioNum, stockNum)
                }
            }) {
                Text("Guardar", color = VerdeNeon)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = RojoCancelar)
            }
        }
    )
}
