package com.mypes.vinto.presentation.perfil

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Usuario
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(nav: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { MypesDatabase.obtener(context) }
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(database) {
        try {
            usuario = withContext(Dispatchers.IO) { database.usuarioDao().obtener() }
        } catch (_: Exception) {
            error = true
        } finally {
            cargando = false
        }
    }

    Column(
        Modifier.fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Mi perfil", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Box(
            Modifier.size(100.dp)
                .background(VerdeNeon, CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 48.sp)
        }
        Spacer(Modifier.height(24.dp))

        when {
            cargando -> Text("Cargando datos...", color = TextoGris)
            error -> Text("No se pudieron cargar los datos del perfil.", color = TextoGris)
            usuario == null -> Text("No hay un perfil local registrado.", color = TextoGris)
            else -> {
                val perfil = requireNotNull(usuario)
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PerfilRow("Nombre", perfil.nombre)
                        PerfilRow("Carnet", perfil.carnet)
                        PerfilRow("Celular", perfil.celular)
                        PerfilRow("Dirección", perfil.direccion)
                        PerfilRow("Correo", perfil.email)
                        HorizontalDivider(color = TextoGris.copy(alpha = 0.3f))
                        Text("Negocio", color = VerdeNeon, fontWeight = FontWeight.Bold)
                        PerfilRow("Nombre del negocio", perfil.nombreNegocio)
                        PerfilRow("Rubro", perfil.rubro)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Volver al inicio", color = TextoBlanco)
        }
    }
}

@Composable
private fun PerfilRow(label: String, value: String?) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(label, color = TextoGris, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            value?.takeIf(String::isNotBlank) ?: "No registrado",
            color = TextoBlanco,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
