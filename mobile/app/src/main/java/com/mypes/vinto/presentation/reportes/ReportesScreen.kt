package com.mypes.vinto.presentation.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

@Composable
fun ReportesScreen(nav: NavController) {
    val context = LocalContext.current
    val database = remember { MypesDatabase.obtener(context) }
    var inicioDelDia by remember { mutableLongStateOf(inicioDeHoy()) }
    val totalVentas by database.ventaDao()
        .observarTotalDesde(inicioDelDia)
        .collectAsState(initial = 0.0)
    val cantidadVentas by database.ventaDao()
        .observarCantidadDesde(inicioDelDia)
        .collectAsState(initial = 0)

    LaunchedEffect(Unit) {
        while (true) {
            val proximoDia = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis
            delay((proximoDia - System.currentTimeMillis()).coerceAtLeast(1L))
            inicioDelDia = inicioDeHoy()
        }
    }

    Column(
        Modifier.fillMaxSize().background(FondoOscuro).padding(24.dp)
    ) {
        Text("Reporte del día", color = TextoBlanco, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Resumen de tus ventas de hoy", color = TextoGris, fontSize = 14.sp)
        Spacer(Modifier.height(32.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total vendido hoy", color = TextoGris, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "${String.format(Locale.getDefault(), "%.2f", totalVentas)} Bs",
                    color = VerdeNeon,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Transacciones realizadas", color = TextoGris, fontSize = 16.sp)
                Text(
                    cantidadVentas.toString(),
                    color = AzulElectrico,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { nav.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SuperficieOscura)
        ) {
            Text("Volver al inicio", color = TextoBlanco)
        }
    }
}

private fun inicioDeHoy(): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
