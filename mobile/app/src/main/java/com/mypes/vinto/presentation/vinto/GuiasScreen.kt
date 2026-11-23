package com.mypes.vinto.presentation.vinto

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypes.vinto.asistente.AlmacenVinto
import com.mypes.vinto.asistente.Guias
import com.mypes.vinto.asistente.Locutor
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon

@Composable
fun GuiasScreen(abrir: (String) -> Unit) {
    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        EncabezadoVinto("Guías", "Aprende a usar tu celular paso a paso")
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(Guias.TODAS, key = { it.id }) { g ->
                Panel(onClick = { abrir(g.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(g.titulo, color = TextoBlanco, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("${g.pasos.size} pasos", color = TextoGris, fontSize = 13.sp)
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = VerdeNeon)
                    }
                }
            }
        }
    }
}

@Composable
fun GuiaDetalleScreen(id: String?, almacen: AlmacenVinto, volver: () -> Unit) {
    val context = LocalContext.current
    val locutor = remember { Locutor(context) }
    DisposableEffect(Unit) { onDispose { locutor.close() } }
    val guia = Guias.porId(id)

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = volver) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = TextoBlanco) }
            Text(guia?.titulo ?: "Guía no encontrada", color = TextoBlanco, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        if (guia == null) return@Column

        Button(
            onClick = {
                val lectura = guia.pasos.mapIndexed { i, p -> "Paso ${i + 1}. $p" }.joinToString(" ")
                locutor.hablar("${guia.titulo}. $lectura", almacen.velocidadVoz)
            },
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = Color(0xFF06130D)),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Icon(Icons.Filled.VolumeUp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Escuchar la guía", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(guia.pasos) { i, paso ->
                Panel(onClick = { locutor.hablar(paso, almacen.velocidadVoz) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(32.dp).clip(CircleShape).background(VerdeNeon.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) { Text("${i + 1}", color = VerdeNeon, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.width(12.dp))
                        Text(paso, color = TextoBlanco, fontSize = 16.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                Text(
                    "Los nombres de los menús pueden cambiar un poco según la marca del teléfono. Toca un paso para escucharlo.",
                    color = TextoGris, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
