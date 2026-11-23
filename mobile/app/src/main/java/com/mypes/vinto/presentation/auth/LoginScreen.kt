package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.ApiLoginEstado
import com.mypes.vinto.viewmodel.ApiLoginViewModel

@Composable
fun LoginScreen(
    nav: NavController,
    vm: ApiLoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val estado by vm.estado.collectAsState()

    LaunchedEffect(estado) {
        if (estado is ApiLoginEstado.Exito) {
            nav.navigate("dashboard") { popUpTo("login") { inclusive = true } }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 28.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = SuperficieOscura,
            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeNeon.copy(alpha = 0.25f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = VerdeNeon,
                    shadowElevation = 12.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, null, tint = FondoOscuro, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Yapay", color = TextoBlanco, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = SuperficieOscura) {
                Text(
                    "V2.4-EDGE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = VerdeNeon,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text("Copiloto inteligente de cobro y finanzas", color = TextoGris, fontSize = 14.sp)

        Spacer(Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SuperficieOscura,
            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeNeon.copy(alpha = 0.12f))
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                Surface(modifier = Modifier.size(38.dp), shape = CircleShape, color = VerdeNeon) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, null, tint = FondoOscuro, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("YAPAY AGENT // ASISTENTE ACTIVO", color = VerdeNeon, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "¡Imaynalla kashanki! Si olvidaste tu contraseña, presiona el micrófono o crea una cuenta nueva.",
                        color = TextoBlanco,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("CORREO O TELÉFONO DEL PUESTO", modifier = Modifier.fillMaxWidth(), color = TextoGris, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; vm.reiniciarError() },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("puesto72@mercado.bo", color = TextoGris) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = loginFieldColors()
        )

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("CONTRASEÑA OPERATIVA", color = TextoGris, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("¿Olvidaste tu contraseña?", color = VerdeNeon, fontSize = 11.sp)
        }
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; vm.reiniciarError() },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("••••••••••••", color = TextoGris) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextoGris)
                }
            },
            colors = loginFieldColors()
        )

        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, null, tint = VerdeNeon, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Recordar sesión en este teléfono", color = TextoBlanco, fontSize = 13.sp)
                Text("CIFRADO LOCAL AES-256", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(Modifier.height(12.dp))
        when (val actual = estado) {
            is ApiLoginEstado.Error -> Text(actual.mensaje, color = RojoCancelar, fontSize = 13.sp)
            ApiLoginEstado.Iniciando -> Text("Conectando con el servidor...", color = VerdeNeon, fontSize = 13.sp)
            else -> Unit
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { vm.iniciarSesion(email, password) },
            enabled = estado !is ApiLoginEstado.Iniciando && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = FondoOscuro)
        ) {
            Text("Iniciar sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(SuperficieOscura))
            Text("  AUTENTICACIÓN VOZ / IA  ", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Box(Modifier.weight(1f).height(1.dp).background(SuperficieOscura))
        }

        Spacer(Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = SuperficieOscura,
            onClick = { nav.navigate("registro") }
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, null, tint = VerdeNeon)
                Spacer(Modifier.width(10.dp))
                Text("Crear cuenta nueva para comerciante", color = TextoBlanco, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = FondoOscuro,
            border = androidx.compose.foundation.BorderStroke(1.dp, SuperficieOscura)
        ) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WifiOff, null, tint = VerdeNeon)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("MODO OFFLINE UAB", color = VerdeNeon, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("Sin conexión a internet", color = TextoGris, fontSize = 12.sp)
                }
                TextButton(onClick = { nav.navigate("dashboard") }) { Text("Entrar demo", color = TextoBlanco) }
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("● GATEWAY: NODE.JS 24", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text("EDGE: VOSK", color = VerdeNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VerdeNeon,
    unfocusedBorderColor = SuperficieOscura,
    focusedContainerColor = SuperficieOscura,
    unfocusedContainerColor = SuperficieOscura,
    focusedTextColor = TextoBlanco,
    unfocusedTextColor = TextoBlanco,
    cursorColor = VerdeNeon
)
