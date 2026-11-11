package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.LoginEstado
import com.mypes.vinto.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(nav: NavController, vm: LoginViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val asistente = remember { AsistenteVozFacade(context) }
    val extractor = remember { EntityExtractor() }
    var identificador by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var escuchandoPin by remember { mutableStateOf(false) }
    var vozLista by remember { mutableStateOf(false) }
    var estadoVoz by remember { mutableStateOf<String?>(null) }
    val estado by vm.estado.collectAsState()

    LaunchedEffect(asistente) {
        vozLista = withContext(Dispatchers.IO) { asistente.inicializar() }
        if (!vozLista) estadoVoz = "No se pudo cargar el reconocimiento de voz."
    }

    DisposableEffect(asistente) {
        onDispose { asistente.close() }
    }

    LaunchedEffect(estado) {
        when (estado) {
            is LoginEstado.Exito ->
                nav.navigate("dashboard") { popUpTo("login") { inclusive = true } }
            LoginEstado.RequiereRegistro -> nav.navigate("registro")
            else -> Unit
        }
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎤", fontSize = 42.sp)
        Text("Ingresa a tu cuenta", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextoBlanco)
        Text("Usa tu carnet, celular o correo y tu PIN", color = TextoGris)
        Spacer(Modifier.size(20.dp))

        OutlinedTextField(
            value = identificador,
            onValueChange = { identificador = it; vm.reiniciar() },
            label = { Text("Carnet, celular o correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(20.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { index ->
                    Text(
                        if (index < pin.length) "•" else "○",
                        color = if (index < pin.length) VerdeNeon else TextoGris,
                        fontSize = 32.sp
                    )
                }
            }
            Button(
                enabled = vozLista && estado !is LoginEstado.Verificando,
                onClick = {
                    if (escuchandoPin) {
                        asistente.detener()
                        escuchandoPin = false
                    } else {
                        estadoVoz = null
                        escuchandoPin = true
                        asistente.procesarPinDictado(
                            onPinDetectado = { pinDetectado, _ ->
                                escuchandoPin = false
                                pin = pinDetectado
                                vm.verificarPin(pinDetectado, identificador)
                            },
                            onError = { mensaje ->
                                escuchandoPin = false
                                estadoVoz = mensaje
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (escuchandoPin) RojoCancelar else AzulElectrico
                )
            ) {
                Text(if (escuchandoPin) "Detener" else "🎙 PIN", color = Color.White)
            }
        }
        if (escuchandoPin) {
            Text("Di los cuatro números, por ejemplo: uno, dos, tres, cuatro", color = AzulElectrico, fontSize = 12.sp)
        }
        estadoVoz?.let { Text(it, color = NaranjaAlerta, fontSize = 12.sp) }

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
                                    if (pin.length == 4) vm.verificarPin(pin, identificador)
                                }
                            }
                        },
                        modifier = Modifier.size(72.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuperficieOscura)
                    ) { Text(tecla, color = Color.White, fontSize = 20.sp) }
                }
            }
            Spacer(Modifier.size(8.dp))
        }
        TextButton(onClick = { nav.navigate("registro") }) {
            Text("Crear cuenta nueva", color = VerdeNeon)
        }
    }
}
