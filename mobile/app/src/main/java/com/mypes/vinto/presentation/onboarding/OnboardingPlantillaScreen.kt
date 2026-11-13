package com.mypes.vinto.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.PlantillaRepository
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.BusinessProfileRequest
import com.mypes.vinto.plantillas.PlantillasNegocio
import com.mypes.vinto.presentation.theme.*
import com.mypes.vinto.sync.AuthSession
import kotlinx.coroutines.launch

private val rubrosDisponibles = listOf(
    "Abarrotes",
    "Minimarket",
    "Pensión",
    "Broastería",
    "Ferretería",
    "Farmacia",
    "Pastelería",
    "Licorería"
)

@Composable
fun OnboardingPlantillaScreen(nav: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { MypesDatabase.obtener(context) }
    val repo = remember { PlantillaRepository(database.productoDao()) }
    var nombreNegocio by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf(rubrosDisponibles.first()) }
    var aplicando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var puedeContinuarLocalmente by remember { mutableStateOf(false) }

    fun abrirDashboard() {
        nav.navigate("dashboard") {
            popUpTo("onboarding_plantilla") { inclusive = true }
        }
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp)) {
        Text("Configura tu negocio", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Estos datos se guardarán en tu perfil y en la nube cuando haya conexión.", color = TextoGris)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = nombreNegocio,
            onValueChange = { nombreNegocio = it; error = null },
            label = { Text("Nombre del negocio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text("Rubro", color = TextoBlanco, fontWeight = FontWeight.Bold)
        rubrosDisponibles.forEach { opcion ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(selected = rubro == opcion, onClick = { rubro = opcion; error = null })
                Text(opcion, color = TextoGris)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Elige una plantilla de productos", color = TextoBlanco, fontWeight = FontWeight.Bold)
        Text("Puedes modificar estos ejemplos después.", color = TextoGris, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(PlantillasNegocio.TODAS, key = { it.id }) { plantilla ->
                Button(
                    enabled = !aplicando,
                    onClick = {
                        if (nombreNegocio.isBlank() || nombreNegocio.trim().length > 160) {
                            error = "Ingresa un nombre de negocio de hasta 160 caracteres."
                            return@Button
                        }
                        scope.launch {
                            aplicando = true
                            error = null
                            puedeContinuarLocalmente = false
                            try {
                                if (database.usuarioDao().actualizarNegocio(nombreNegocio.trim(), rubro) != 1) {
                                    error = "No se encontró el perfil local para guardar el negocio."
                                    return@launch
                                }
                                repo.aplicarPlantilla(plantilla.id)
                                val token = AuthSession.token(context)
                                if (token.isNullOrBlank()) {
                                    abrirDashboard()
                                    return@launch
                                }
                                try {
                                    ApiClient.registroApi.actualizarNegocio(
                                        authorization = "Bearer $token",
                                        request = BusinessProfileRequest(nombreNegocio.trim(), rubro)
                                    )
                                    abrirDashboard()
                                } catch (failure: Exception) {
                                    error = failure.message
                                        ?: "El negocio quedó guardado localmente, pero no se actualizó en la nube."
                                    puedeContinuarLocalmente = true
                                }
                            } catch (failure: Exception) {
                                error = failure.message ?: "No se pudo guardar la configuración del negocio."
                            } finally {
                                aplicando = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuperficieOscura)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(plantilla.nombre, color = TextoBlanco)
                        Text("${plantilla.productosEjemplo.size} productos sugeridos", color = TextoGris, fontSize = 12.sp)
                    }
                }
            }
        }
        error?.let { Text(it, color = RojoCancelar, modifier = Modifier.padding(top = 12.dp)) }
        if (puedeContinuarLocalmente) {
            Text(
                "El perfil está guardado en el dispositivo, pero el negocio aún no se actualizó en la nube.",
                color = TextoGris,
                fontSize = 12.sp
            )
            Button(onClick = ::abrirDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Continuar con perfil local")
            }
        }
        if (aplicando) CircularProgressIndicator(color = VerdeNeon)
    }
}
