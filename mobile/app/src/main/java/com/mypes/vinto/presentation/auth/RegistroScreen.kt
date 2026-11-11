package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
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
    var email by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        if (estado is EstadoRegistro.Exito || estado is EstadoRegistro.ExitoOffline) {
            nav.navigate("onboarding_plantilla") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val formularioValido = nombres.isNotBlank()
        && nombres.trim().length <= 80
        && apellidos.isNotBlank()
        && apellidos.trim().length <= 100
        && "$nombres $apellidos".trim().length <= 160
        && carnet.isNotBlank()
        && carnet.trim().length <= 40
        && expedido.isNotBlank()
        && expedido.trim().length <= 40
        && fechaNacimiento.isNotBlank()
        && fechaNacimiento.trim().length <= 32
        && lugarNacimiento.isNotBlank()
        && lugarNacimiento.trim().length <= 120
        && celular.isNotBlank()
        && celular.trim().length <= 40
        && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        && email.trim().length <= 254
        && password.length in 12..256
        && password.toByteArray(Charsets.UTF_8).size <= 1024
        && pin.length == 4
        && pin.all(Char::isDigit)

    Column(
        Modifier.fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Crea tu cuenta", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Registra tus datos para empezar", color = TextoGris, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it; vm.reiniciarError() },
            label = { Text("Nombres") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it; vm.reiniciarError() },
            label = { Text("Apellidos") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = carnet,
            onValueChange = { carnet = it; vm.reiniciarError() },
            label = { Text("Carnet de identidad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = expedido,
            onValueChange = { expedido = it; vm.reiniciarError() },
            label = { Text("Expedido (por ejemplo, La Paz - LP)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it; vm.reiniciarError() },
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("DD/MM/AAAA") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = lugarNacimiento,
            onValueChange = { lugarNacimiento = it; vm.reiniciarError() },
            label = { Text("Lugar de nacimiento") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; vm.reiniciarError() },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = celular,
            onValueChange = { celular = it; vm.reiniciarError() },
            label = { Text("Celular") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
        Text("Credencial de nube", color = VerdeNeon, fontWeight = FontWeight.Bold)
        Text("La contraseña se envía al servidor para crear la cuenta y se guarda como hash; no se conserva en este dispositivo.", color = TextoGris, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; vm.reiniciarError() },
            label = { Text("Contraseña para la nube (mínimo 12 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
        Text("Seguridad local", color = VerdeNeon, fontWeight = FontWeight.Bold)
        Text("El PIN se almacena como hash en este dispositivo.", color = TextoGris, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 4 && it.all(Char::isDigit)) {
                    pin = it
                    vm.reiniciarError()
                }
            },
            label = { Text("PIN de 4 dígitos") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
        Text("El nombre y rubro del negocio se completan en el siguiente paso.", color = TextoGris, fontSize = 13.sp)
        when (val actual = estado) {
            is EstadoRegistro.Error -> Text(actual.mensaje, color = RojoCancelar)
            EstadoRegistro.Cargando -> Text("Guardando perfil y registrando cuenta...", color = VerdeNeon)
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
            enabled = formularioValido && estado !is EstadoRegistro.Cargando,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon)
        ) {
            if (estado is EstadoRegistro.Cargando) {
                CircularProgressIndicator(modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    if (estado is EstadoRegistro.Error) "Reintentar registro" else "Crear cuenta y continuar",
                    color = FondoOscuro,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val error = estado as? EstadoRegistro.Error
        if (error?.puedeContinuarOffline == true) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = vm::continuarSinNube,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaAlerta)
            ) {
                Text("Continuar solo con perfil local")
            }
            Text(
                "La contraseña no se guardó localmente. Podrás vincular la cuenta desde Sincronización.",
                color = TextoGris,
                fontSize = 12.sp
            )
        }
    }
}

