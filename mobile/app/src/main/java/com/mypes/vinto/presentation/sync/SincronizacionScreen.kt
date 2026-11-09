package com.mypes.vinto.presentation.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.sync.AuthSession
import com.mypes.vinto.viewmodel.SincronizacionViewModel

@Composable
fun SincronizacionScreen(
    nav: NavController,
    vm: SincronizacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var tieneSesion by remember { mutableStateOf(AuthSession.token(context) != null) }
    val pendientes by vm.pendientes.collectAsState()
    val errores by vm.errores.collectAsState()
    val trabajo by vm.trabajo.collectAsState()

    androidx.compose.runtime.LaunchedEffect(trabajo) {
        if (trabajo == androidx.work.WorkInfo.State.FAILED) {
            tieneSesion = AuthSession.token(context) != null
        }
    }

    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tieneSesion = AuthSession.token(context) != null
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sincronización", color = TextoBlanco, fontSize = 28.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Las ventas se guardan primero en el dispositivo y se envían cuando hay conexión.",
            color = TextoGris
        )
        Spacer(Modifier.height(24.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (tieneSesion) "Cuenta API vinculada" else "Cuenta API no vinculada",
                    color = if (tieneSesion) VerdeNeon else NaranjaAlerta
                )
                Text("Ventas pendientes: $pendientes", color = TextoBlanco)
                if (errores > 0) Text("Operaciones con error: $errores", color = NaranjaAlerta)
                Text(vm.textoEstado(trabajo), color = TextoGris)
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { if (tieneSesion) vm.sincronizar() else nav.navigate("api_login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (tieneSesion) "Sincronizar ahora" else "Vincular cuenta para sincronizar")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
