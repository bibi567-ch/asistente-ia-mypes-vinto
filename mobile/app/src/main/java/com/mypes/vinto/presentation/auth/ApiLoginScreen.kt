package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.ApiLoginEstado
import com.mypes.vinto.viewmodel.ApiLoginViewModel

@Composable
fun ApiLoginScreen(
    nav: NavController,
    vm: ApiLoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val estado by vm.estado.collectAsState()

    LaunchedEffect(estado) {
        if (estado is ApiLoginEstado.Exito) nav.popBackStack()
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Vincular cuenta", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Este acceso es independiente del PIN local y se usa para sincronizar con el servidor.",
            color = TextoGris
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; vm.reiniciarError() },
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; vm.reiniciarError() },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        when (val actual = estado) {
            is ApiLoginEstado.Error -> Text(actual.mensaje, color = RojoCancelar)
            is ApiLoginEstado.Iniciando -> Text("Conectando...", color = VerdeNeon)
            else -> Unit
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.iniciarSesion(email, password) },
            enabled = estado !is ApiLoginEstado.Iniciando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado is ApiLoginEstado.Iniciando) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Iniciar sesión para sincronizar")
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.registrarCuentaLocal(email, password) },
            enabled = estado !is ApiLoginEstado.Iniciando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon)
        ) {
            Text("Crear cuenta con perfil local", color = FondoOscuro)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Si registraste el perfil sin conexión, usa el mismo correo y crea aquí la cuenta en la nube. La contraseña no se guarda en el dispositivo.",
            color = TextoGris
        )
    }
}
