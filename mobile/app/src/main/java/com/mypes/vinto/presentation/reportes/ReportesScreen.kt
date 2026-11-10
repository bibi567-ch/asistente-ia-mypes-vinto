package com.mypes.vinto.presentation.reportes

import android.content.Intent
import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.mypes.vinto.BuildConfig
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Venta
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.text.SimpleDateFormat
import java.io.File
import java.util.Locale
import java.util.UUID

@Composable
fun ReportesScreen(nav: NavController) {
    val context = LocalContext.current
    val database = remember { MypesDatabase.obtener(context) }
    var inicioDelDia by remember { mutableLongStateOf(inicioDeHoy()) }
    var mostrarVistaDemo by rememberSaveable { mutableStateOf(false) }
    val totalVentas by database.ventaDao()
        .observarTotalDesde(inicioDelDia)
        .collectAsState(initial = 0.0)
    val cantidadVentas by database.ventaDao()
        .observarCantidadDesde(inicioDelDia)
        .collectAsState(initial = 0)
    val ventasHoy by database.ventaDao()
        .observarVentasEntre(inicioDelDia, finDelDia(inicioDelDia))
        .collectAsState(initial = emptyList())
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var exportandoCsv by remember { mutableStateOf(false) }
    var errorExportacion by remember { mutableStateOf<String?>(null) }
    val dias = remember(inicioDelDia) { ultimosSieteDias(inicioDelDia) }
    val totalesObservados by remember(dias) {
        combine(dias.map { dia ->
            database.ventaDao().observarTotalEntre(dia.inicio, dia.fin)
        }) { totales -> totales.toList() }
    }.collectAsState(initial = List(dias.size) { 0.0 })
    val totalesGrafico = if (mostrarVistaDemo && BuildConfig.DEBUG) {
        listOf(85.0, 120.0, 70.0, 165.0, 110.0, 210.0, 145.0)
    } else {
        totalesObservados
    }
    val maximoGrafico = totalesGrafico.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

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
        Spacer(Modifier.height(20.dp))

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

        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ventas de los últimos 7 días", color = TextoBlanco, fontWeight = FontWeight.Bold)
            if (BuildConfig.DEBUG) {
                Text(
                    if (mostrarVistaDemo) "Demo activa" else "Datos reales",
                    color = if (mostrarVistaDemo) AzulElectrico else TextoGris,
                    fontSize = 12.sp
                )
            }
        }
        if (BuildConfig.DEBUG) {
            Text(
                if (mostrarVistaDemo) "Volver a datos reales" else "Vista previa con datos ficticios (no se guardan)",
                color = AzulElectrico,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { mostrarVistaDemo = !mostrarVistaDemo }
            )
        }
        if (mostrarVistaDemo && BuildConfig.DEBUG) {
            Text(
                "Vista demostrativa: valores ficticios solo para el gráfico; no se guardan ni se comparten.",
                color = TextoGris,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(12.dp))
        Card(
            Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dias.forEachIndexed { index, dia ->
                    val total = totalesGrafico.getOrElse(index) { 0.0 }
                    val altura = (total / maximoGrafico).toFloat().coerceIn(0f, 1f)
                    Column(
                        Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            String.format(Locale.getDefault(), "%.0f", total),
                            color = TextoGris,
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                        Box(
                            Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(altura)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(VerdeNeon)
                            )
                        }
                        Text(dia.etiqueta, color = TextoGris, fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    exportandoCsv = true
                    errorExportacion = null
                    try {
                        val archivoCsv = withContext(Dispatchers.IO) {
                            val directorio = File(context.cacheDir, "reports")
                            check(directorio.isDirectory || directorio.mkdirs()) {
                                "No se pudo preparar el archivo del reporte."
                            }
                            File(directorio, "reporte_ventas_${UUID.randomUUID()}.csv").apply {
                                writeText(crearContenidoCsv(ventasHoy), Charsets.UTF_8)
                            }
                        }
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            archivoCsv
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "Reporte de ventas")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Reporte de ventas de hoy: ${String.format(Locale.getDefault(), "%.2f", totalVentas)} Bs en $cantidadVentas transacciones."
                            )
                            putExtra(Intent.EXTRA_STREAM, uri)
                            clipData = ClipData.newUri(context.contentResolver, "Reporte CSV", uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Exportar reporte a..."))
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        errorExportacion = error.message ?: "No se pudo exportar el reporte."
                    } finally {
                        exportandoCsv = false
                    }
                }
            },
            enabled = !exportandoCsv,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulElectrico)
        ) {
            Text(
                if (exportandoCsv) "Preparando CSV..." else "Exportar y compartir CSV",
                color = TextoBlanco,
                fontWeight = FontWeight.Bold
            )
        }
        errorExportacion?.let {
            Text(it, color = TextoGris, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(12.dp))
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

internal fun crearContenidoCsv(ventas: List<Venta>): String {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return buildString {
        append('\uFEFF')
        appendLine(listOf("Fecha", "Total Bs", "Forma de pago", "Cliente").joinToString(";") { escaparCsv(it) })
        ventas.forEach { venta ->
            appendLine(
                listOf(
                    fecha.format(java.util.Date(venta.creadoEn)),
                    String.format(Locale.ROOT, "%.2f", venta.total),
                    venta.formaPago,
                    venta.clienteFiado.orEmpty()
                ).joinToString(";") { escaparCsv(it) }
            )
        }
    }
}

private fun escaparCsv(valor: String): String =
    "\"${valor.replace("\"", "\"\"")}\""

private fun inicioDeHoy(): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun finDelDia(inicio: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = inicio
        add(Calendar.DAY_OF_YEAR, 1)
    }.timeInMillis

private data class RangoDia(val inicio: Long, val fin: Long, val etiqueta: String)

private fun ultimosSieteDias(inicioDeHoy: Long): List<RangoDia> {
    val calendario = Calendar.getInstance().apply {
        timeInMillis = inicioDeHoy
        add(Calendar.DAY_OF_YEAR, -6)
    }
    val formato = SimpleDateFormat("EE", Locale.getDefault())
    return List(7) {
        val inicio = calendario.timeInMillis
        calendario.add(Calendar.DAY_OF_YEAR, 1)
        val fin = calendario.timeInMillis
        val etiqueta = formato.format(inicio).replaceFirstChar { it.titlecase(Locale.getDefault()) }
        RangoDia(inicio, fin, etiqueta)
    }
}
