package com.mypes.vinto.presentation.vinto

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypes.vinto.asistente.AlmacenVinto
import com.mypes.vinto.asistente.ContactoVinto
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon

/** Contactos frecuentes con alias («mi hermana», «don Juan») para llamar o escribir por voz. */
@Composable
fun ContactosScreen(almacen: AlmacenVinto) {
    val context = LocalContext.current
    var lista by remember { mutableStateOf(almacen.contactos()) }
    var editando by remember { mutableStateOf<ContactoVinto?>(null) }
    var borrar by remember { mutableStateOf<ContactoVinto?>(null) }

    // Elegir un número de la agenda: el sistema da permiso solo para el contacto elegido.
    val agenda = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val uri = res.data?.data
        if (res.resultCode == Activity.RESULT_OK && uri != null) {
            runCatching {
                context.contentResolver.query(
                    uri,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null, null, null
                )?.use { c ->
                    if (c.moveToFirst()) {
                        editando = ContactoVinto(nombre = c.getString(0).orEmpty(), numero = c.getString(1).orEmpty())
                    }
                }
            }
        }
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        EncabezadoVinto("Contactos", "Para llamar o escribir por voz") {
            IconButton(onClick = { editando = ContactoVinto() }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar contacto", tint = VerdeNeon)
            }
        }
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { editando = ContactoVinto() },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = Color(0xFF06130D)),
                modifier = Modifier.weight(1f)
            ) { Text("Nuevo", fontWeight = FontWeight.Bold) }
            OutlinedButton(
                onClick = {
                    agenda.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
                },
                modifier = Modifier.weight(1f)
            ) { Text("Desde la agenda", color = TextoBlanco) }
        }
        Spacer(Modifier.height(12.dp))

        if (lista.isEmpty()) {
            Panel(Modifier.padding(horizontal = 16.dp)) {
                Text("Aún no tienes contactos.", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Guarda a tus contactos con un alias, por ejemplo «mi hermana». Después solo dices «llama a mi hermana».",
                    color = TextoGris, fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(lista, key = { it.id }) { c ->
                Panel(onClick = { editando = c }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Call, contentDescription = null, tint = VerdeNeon)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.nombre, color = TextoBlanco, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                listOf(c.alias.takeIf { it.isNotBlank() }?.let { "«$it»" }, c.numero)
                                    .filterNotNull().joinToString(" · "),
                                color = TextoGris, fontSize = 13.sp
                            )
                        }
                        IconButton(onClick = { borrar = c }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = TextoGris)
                        }
                    }
                }
            }
        }
    }

    editando?.let { inicial ->
        var nombre by remember(inicial.id) { mutableStateOf(inicial.nombre) }
        var alias by remember(inicial.id) { mutableStateOf(inicial.alias) }
        var numero by remember(inicial.id) { mutableStateOf(inicial.numero) }
        val valido = nombre.isNotBlank() && numero.filter { it.isDigit() }.length >= 7
        AlertDialog(
            onDismissRequest = { editando = null },
            containerColor = SuperficieOscura,
            titleContentColor = TextoBlanco,
            title = { Text(if (inicial.nombre.isBlank()) "Nuevo contacto" else "Editar contacto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CampoVinto(nombre, { nombre = it }, "Nombre")
                    CampoVinto(alias, { alias = it }, "Alias (ej. mi hermana)")
                    CampoVinto(numero, { numero = it }, "Número", teclado = KeyboardType.Phone)
                }
            },
            confirmButton = {
                Button(
                    enabled = valido,
                    onClick = {
                        almacen.guardarContacto(inicial.copy(nombre = nombre.trim(), alias = alias.trim(), numero = numero.trim()))
                        lista = almacen.contactos()
                        editando = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = Color(0xFF06130D))
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editando = null }) { Text("Cancelar", color = TextoGris) } }
        )
    }

    borrar?.let { c ->
        AlertDialog(
            onDismissRequest = { borrar = null },
            containerColor = SuperficieOscura,
            titleContentColor = TextoBlanco,
            textContentColor = TextoGris,
            title = { Text("¿Borrar a ${c.nombre}?") },
            text = { Text("Solo se borra de IA Vinto, no de la agenda del teléfono.") },
            confirmButton = {
                Button(
                    onClick = { almacen.borrarContacto(c.id); lista = almacen.contactos(); borrar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoCancelar)
                ) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { borrar = null }) { Text("Cancelar", color = TextoGris) } }
        )
    }
}

