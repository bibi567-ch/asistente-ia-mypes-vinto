package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.viewmodel.EstadoRegistro
import com.mypes.vinto.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(nav: NavController, vm: RegistroViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var carnet by remember { mutableStateOf("") }
    var expedido by remember { mutableStateOf("La Paz (LP)") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var lugarNacimiento by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        if (estado is EstadoRegistro.Exito || estado is EstadoRegistro.ExitoOffline) {
            nav.navigate("onboarding_plantilla") { popUpTo("login") { inclusive = true } }
        }
    }

    val valido = nombres.isNotBlank() && apellidos.isNotBlank() &&
        carnet.isNotBlank() && celular.isNotBlank() &&
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
        password.length in 12..256 && pin.length == 4 && pin.all(Char::isDigit)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextoBlanco)
            }
            Column(Modifier.weight(1f)) {
                Text("CREAR CUENTA", color = VerdeNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text("Sesión activa bilingüe", color = TextoBlanco, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Mic, null, tint = VerdeNeon)
        }

        Spacer(Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SuperficieOscura,
            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeNeon.copy(alpha = 0.12f))
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, null, tint = VerdeNeon)
                    Spacer(Modifier.width(8.dp))
                    Text("¡Imaynalla, casera!", color = TextoBlanco, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Te ayudaré a crear tu cuenta. Puedes escribir los datos o dictarlos cuando completes el flujo de voz.",
                    color = TextoGris,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        RegistroField("Nombres", nombres) { nombres = it; vm.reiniciarError() }
        RegistroField("Apellidos", apellidos) { apellidos = it; vm.reiniciarError() }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                RegistroField("Carnet", carnet) { carnet = it; vm.reiniciarError() }
            }
            Column(Modifier.weight(1f)) {
                RegistroField("Expedido", expedido) { expedido = it; vm.reiniciarError() }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                RegistroField("Fecha nacimiento", fechaNacimiento) { fechaNacimiento = it; vm.reiniciarError() }
            }
            Column(Modifier.weight(1f)) {
                RegistroField("Lugar nacimiento", lugarNacimiento) { lugarNacimiento = it; vm.reiniciarError() }
            }
        }
        RegistroField("Celular / WhatsApp", celular, KeyboardType.Phone) { celular = it; vm.reiniciarError() }
        RegistroField("Correo electrónico", email, KeyboardType.Email) { email = it; vm.reiniciarError() }

        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = SuperficieOscura
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, null, tint = VerdeNeon)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Puesto #42 • Vinto Centro", color = TextoBlanco)
                    Text("Nombre y rubro del negocio se completan al continuar", color = TextoGris, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("SEGURIDAD DE CUENTA", color = VerdeNeon, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(6.dp))
        RegistroPasswordField("Contraseña (mínimo 12 caracteres)", password) { password = it; vm.reiniciarError() }
        RegistroPasswordField("PIN local de 4 dígitos", pin, isPin = true) { value ->
            if (value.length <= 4 && value.all(Char::isDigit)) {
                pin = value
                vm.reiniciarError()
            }
        }

        Spacer(Modifier.height(10.dp))
        when (val actual = estado) {
            is EstadoRegistro.Error -> Text(actual.mensaje, color = RojoCancelar)
            EstadoRegistro.Cargando -> Text("Guardando perfil y vinculando cuenta...", color = VerdeNeon)
            else -> Unit
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                vm.registrar(
                    nombres = nombres,
                    apellidos = apellidos,
                    carnet = carnet,
                    expedido = expedido,
                    fechaNacimiento = fechaNacimiento,
                    lugarNacimiento = lugarNacimiento,
                    celular = celular,
                    email = email,
                    password = password,
                    pin = pin
                )
            },
            enabled = valido && estado !is EstadoRegistro.Cargando,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = FondoOscuro)
        ) {
            Icon(Icons.Default.PersonAdd, null)
            Spacer(Modifier.width(8.dp))
            Text("Crear cuenta e iniciar", fontWeight = FontWeight.Bold)
        }

        val error = estado as? EstadoRegistro.Error
        if (error?.puedeContinuarOffline == true) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = vm::continuarSinNube,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaAlerta)
            ) { Text("Continuar solo con perfil local", color = FondoOscuro) }
        }

        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, null, tint = VerdeNeon, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Datos cifrados • modo offline disponible", color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun RegistroField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    Text(label.uppercase(), color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = registroFieldColors()
    )
}

@Composable
private fun RegistroPasswordField(label: String, value: String, isPin: Boolean = false, onChange: (String) -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(label.uppercase(), color = TextoGris, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPin) KeyboardType.NumberPassword else KeyboardType.Password
        ),
        colors = registroFieldColors()
    )
}

@Composable
private fun registroFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VerdeNeon,
    unfocusedBorderColor = SuperficieOscura,
    focusedContainerColor = SuperficieOscura,
    unfocusedContainerColor = SuperficieOscura,
    focusedTextColor = TextoBlanco,
    unfocusedTextColor = TextoBlanco,
    cursorColor = VerdeNeon
)
