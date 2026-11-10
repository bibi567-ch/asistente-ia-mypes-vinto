package com.mypes.vinto.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.RadioButton
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

private val rubrosDisponibles = listOf("Abarrotes", "Pensión", "Broastería", "Ferretería")

@Composable
fun RegistroScreen(nav: NavController, vm: RegistroViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var carnet by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombreNegocio by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf(rubrosDisponibles.first()) }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        if (estado is EstadoRegistro.Exito || estado is EstadoRegistro.ExitoOffline) {
            nav.navigate("onboarding_plantilla") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val formularioValido = nombre.isNotBlank()
        && nombre.trim().length <= 160
        && carnet.isNotBlank()
        && carnet.trim().length <= 40
        && celular.isNotBlank()
        && celular.trim().length <= 40
        && direccion.isNotBlank()
        && direccion.trim().length <= 240
        && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        && email.trim().length <= 254
        && password.length in 12..256
        && password.toByteArray(Charsets.UTF_8).size <= 1024
        && nombreNegocio.isNotBlank()
        && nombreNegocio.trim().length <= 160
        && rubro.trim().length <= 80
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
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; vm.reiniciarError() },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = carnet,
            onValueChange = { carnet = it; vm.reiniciarError() },
            label = { Text("Carnet de identidad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = celular,
            onValueChange = { celular = it; vm.reiniciarError() },
            label = { Text("Celular") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it; vm.reiniciarError() },
            label = { Text("Dirección") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; vm.reiniciarError() },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; vm.reiniciarError() },
            label = { Text("Contraseña para la nube (mínimo 12 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Text("Datos de tu negocio", color = VerdeNeon, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = nombreNegocio,
            onValueChange = { nombreNegocio = it; vm.reiniciarError() },
            label = { Text("Nombre del negocio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text("Rubro", color = TextoBlanco)
        rubrosDisponibles.forEach { opcion ->
            Row(
                Modifier.fillMaxWidth().clickable { rubro = opcion; vm.reiniciarError() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = rubro == opcion,
                    onClick = { rubro = opcion; vm.reiniciarError() }
                )
                Text(opcion, color = TextoGris)
            }
        }

        Spacer(Modifier.height(16.dp))
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
        when (val actual = estado) {
            is EstadoRegistro.Error -> Text(actual.mensaje, color = RojoCancelar)
            EstadoRegistro.Cargando -> Text("Guardando perfil y registrando cuenta...", color = VerdeNeon)
            else -> Unit
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                vm.registrar(
                    nombre,
                    carnet,
                    celular,
                    direccion,
                    email,
                    password,
                    nombreNegocio,
                    rubro,
                    pin
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
                    if (estado is EstadoRegistro.Error) "Reintentar registro" else "Crear cuenta y empezar",
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
                "No se guardó la contraseña de nube. Podrás vincular una cuenta desde Sincronización.",
                color = TextoGris,
                fontSize = 12.sp
            )
        }
    }
}
