package com.mypes.vinto.presentation.vinto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypes.vinto.asistente.AlmacenVinto
import com.mypes.vinto.asistente.Comando
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(almacen: AlmacenVinto) {
    var lista by remember { mutableStateOf(almacen.historial()) }
    val formato = remember { SimpleDateFormat("dd/MM HH:mm", Locale("es", "BO")) }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        EncabezadoVinto("Historial", "Las órdenes que me diste") {
            if (lista.isNotEmpty()) {
                TextButton(onClick = { almacen.borrarHistorial(); lista = emptyList() }) {
                    Text("Borrar", color = TextoGris)
                }
            }
        }
        val ejecutadas = lista.count { it.ejecutado }
        if (lista.isNotEmpty()) {
            Text(
                "${lista.size} órdenes · $ejecutadas ejecutadas",
                color = TextoGris, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
        } else {
            Panel(Modifier.padding(horizontal = 16.dp)) {
                Text("Todavía no hay órdenes.", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Text("Ve a la pestaña Agente y prueba: «¿qué hora es?».", color = TextoGris, fontSize = 14.sp)
            }
        }
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(lista, key = { it.id }) { r ->
                val comando = runCatching { Comando.valueOf(r.comando) }.getOrDefault(Comando.DESCONOCIDO)
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(comando.titulo, color = VerdeNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(formato.format(Date(r.fecha)), color = TextoGris, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("«${r.texto}»", color = TextoBlanco, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        when {
                            r.ejecutado -> Etiqueta("Ejecutado", VerdeNeon)
                            comando == Comando.DESCONOCIDO -> Etiqueta("No entendida", RojoCancelar)
                            else -> Etiqueta("No ejecutado", NaranjaAlerta)
                        }
                        if (r.confirmado) Etiqueta("Confirmado", TextoGris)
                    }
                }
            }
        }
    }
}

@Composable
fun AjustesScreen(almacen: AlmacenVinto, irA: (String) -> Unit, cerrarSesion: () -> Unit) {
    var voz by remember { mutableStateOf(almacen.respuestasPorVoz) }
    var velocidad by remember { mutableFloatStateOf(almacen.velocidadVoz) }

    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        EncabezadoVinto("Ajustes", "Voz, cuenta y datos")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Responder con voz", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                        Text("IA Vinto lee en voz alta sus respuestas", color = TextoGris, fontSize = 13.sp)
                    }
                    Switch(
                        checked = voz,
                        onCheckedChange = { voz = it; almacen.respuestasPorVoz = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = VerdeNeon, checkedThumbColor = Color(0xFF06130D))
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("Velocidad de la voz: ${"%.1f".format(velocidad)}×", color = TextoBlanco, fontSize = 14.sp)
                Slider(
                    value = velocidad,
                    onValueChange = { velocidad = it },
                    onValueChangeFinished = { almacen.velocidadVoz = velocidad },
                    valueRange = 0.6f..1.4f,
                    colors = SliderDefaults.colors(thumbColor = VerdeNeon, activeTrackColor = VerdeNeon)
                )
            }
            Panel {
                Text("Reconocimiento de voz", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Text(
                    "Funciona sin internet con el modelo Vosk en castellano guardado en el teléfono. " +
                        "El quechua se reconoce con MMS en el servidor cuando hay conexión.",
                    color = TextoGris, fontSize = 13.sp
                )
            }
            Panel(onClick = { irA("api_login") }) {
                Text("Vincular cuenta del servidor", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Text("Inicia sesión en el backend para respaldar datos", color = TextoGris, fontSize = 13.sp)
            }
            Panel(onClick = { irA("sync") }) {
                Text("Sincronización", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Text("Ver los envíos pendientes al servidor", color = TextoGris, fontSize = 13.sp)
            }
            OutlinedButton(onClick = cerrarSesion, modifier = Modifier.padding(top = 4.dp)) {
                Text("Cerrar sesión", color = RojoCancelar)
            }
            Text("IA Vinto · Proyecto Integrador UAB 2026", color = TextoGris, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
        }
    }
}

