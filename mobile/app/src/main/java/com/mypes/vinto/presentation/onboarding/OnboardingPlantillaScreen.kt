package com.mypes.vinto.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.PlantillaRepository
import com.mypes.vinto.plantillas.PlantillasNegocio
import com.mypes.vinto.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingPlantillaScreen(nav: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { PlantillaRepository(MypesDatabase.obtener(context).productoDao()) }
    var aplicando by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp)) {
        Text("Elige tu plantilla", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Cargaremos productos iniciales para tu negocio.", color = TextoGris)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(PlantillasNegocio.TODAS, key = { it.id }) { plantilla ->
                Button(
                    enabled = !aplicando,
                    onClick = {
                        scope.launch {
                            aplicando = true
                            repo.aplicarPlantilla(plantilla.id)
                            nav.navigate("dashboard") { popUpTo("onboarding_plantilla") { inclusive = true } }
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
        if (aplicando) CircularProgressIndicator(color = VerdeNeon)
    }
}
