package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.LoginEstado
import com.mypes.vinto.viewmodel.LoginViewModel

@Composable
fun LoginScreen(nav: NavController, vm: LoginViewModel = viewModel()) {
    var pin by remember { mutableStateOf("") }
    val estado by vm.estado.collectAsState()

    LaunchedEffect(estado) {
        if (estado is LoginEstado.Exito) {
            nav.navigate("dashboard") { popUpTo("login") { inclusive = true } }
        }
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎤", fontSize = 42.sp)
        Text("Asistente Financiero IA", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextoBlanco)
        Text("Ingresa tu PIN de 4 dígitos", color = TextoGris)
        Spacer(Modifier.size(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) { index ->
                Text(
                    if (index < pin.length) "•" else "○",
                    color = if (index < pin.length) VerdeNeon else TextoGris,
                    fontSize = 32.sp
                )
            }
        }
        Spacer(Modifier.size(16.dp))
        when (val actual = estado) {
            is LoginEstado.Error -> Text(actual.mensaje, color = RojoCancelar)
            is LoginEstado.Bloqueado -> Text("Bloqueado por ${actual.segundosRestantes}s", color = NaranjaAlerta)
            is LoginEstado.Verificando -> Text("Verificando...", color = VerdeNeon)
            else -> Unit
        }
        Spacer(Modifier.size(12.dp))
        val teclas = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫", "0", "C")
        teclas.chunked(3).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { tecla ->
                    Button(
                        onClick = {
                            when (tecla) {
                                "⌫" -> { pin = pin.dropLast(1); vm.reiniciar() }
                                "C" -> { pin = ""; vm.reiniciar() }
                                else -> if (pin.length < 4) {
                                    pin += tecla
                                    if (pin.length == 4) vm.verificarPin(pin)
                                }
                            }
                        },
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SuperficieOscura)
                    ) { Text(tecla, color = Color.White, fontSize = 20.sp) }
                }
            }
            Spacer(Modifier.size(8.dp))
        }
        TextButton(onClick = { nav.navigate("onboarding_plantilla") }) {
            Text("¿Primera vez? Configurar negocio", color = VerdeNeon)
        }
    }
}
