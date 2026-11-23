package com.mypes.vinto.presentation.vinto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.BordeOscuro
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.NaranjaAlerta
import com.mypes.vinto.presentation.theme.RojoCancelar
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon
import com.mypes.vinto.viewmodel.NegocioViewModel
import com.mypes.vinto.viewmodel.ResumenCaja
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val LOCALE_BO = Locale("es", "BO")
fun bs(n: Double): String = "Bs " + String.format(LOCALE_BO, "%,.2f", n)
private val NegroBoton = Color(0xFF06130D)

@Composable
fun Kpi(titulo: String, valor: String, ayuda: String, color: Color = VerdeNeon, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SuperficieOscura)
            .padding(14.dp)
    ) {
        Text(titulo, color = TextoGris, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(valor, color = color, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(ayuda, color = TextoGris, fontSize = 11.sp)
    }
}

@Composable
private fun FilaKpi(a: @Composable (Modifier) -> Unit, b: @Composable (Modifier) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        a(Modifier.weight(1f))
        b(Modifier.weight(1f))
    }
}

@Composable
private fun Aviso(titulo: String, texto: String, color: Color = NaranjaAlerta) {
    Panel(borde = color.copy(alpha = 0.5f)) {
        Text(titulo, color = color, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(2.dp))
        Text(texto, color = TextoGris, fontSize = 13.sp)
    }
}

@Composable
private fun Acceso(icono: String, titulo: String, sub: String, onClick: () -> Unit) {
    Panel(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icono, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, color = TextoBlanco, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(sub, color = TextoGris, fontSize = 12.sp)
            }
            Text("›", color = VerdeNeon, fontSize = 22.sp)
        }
    }
}

private fun saludo(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Buenos días"
    in 12..18 -> "Buenas tardes"
    else -> "Buenas noches"
}

/** Inicio: resumen del día, alertas de stock y accesos rápidos (Dashboard del Figma). */
@Composable
fun InicioScreen(irA: (String) -> Unit, vm: NegocioViewModel = viewModel()) {
    val ventas by vm.ventas.collectAsState()
    val movimientos by vm.movimientos.collectAsState()
    val productos by vm.productos.collectAsState()
    val caja = ResumenCaja.de(ventas, movimientos, NegocioViewModel.inicioDelDia())
    val bajos = productos.filter { it.stock < 5.0 }
    val fiadosPendientes = ventas.filter { it.formaPago == "FIADO" && !it.pagado }

    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        EncabezadoVinto(saludo(), SimpleDateFormat("EEEE d 'de' MMMM", LOCALE_BO).format(Date()))
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FilaKpi(
                { Kpi("Ventas de hoy", bs(caja.ventasTotales), "${caja.cantidadVentas} ventas", modifier = it) },
                { Kpi("Caja estimada", bs(caja.cajaEstimada), "Efectivo + cobros − gastos", AzulElectrico, it) }
            )
            FilaKpi(
                { Kpi("Fiados pendientes", "${fiadosPendientes.size}", bs(fiadosPendientes.sumOf { v -> v.total }), NaranjaAlerta, it) },
                { Kpi("Stock bajo", "${bajos.size}", "de ${productos.size} productos", if (bajos.isEmpty()) VerdeNeon else RojoCancelar, it) }
            )

            Panel(onClick = { irA("agente") }, borde = VerdeNeon.copy(alpha = 0.4f)) {
                Text("🎙  Habla con IA Vinto", color = VerdeNeon, fontWeight = FontWeight.Bold)
                Text(
                    "«Vendí 3 panes», «¿cuánto vendí hoy?», «llama a mi hermana», «alarma a las seis»",
                    color = TextoGris, fontSize = 13.sp
                )
            }
            if (bajos.isNotEmpty()) {
                Aviso(
                    "Reponer pronto",
                    bajos.take(4).joinToString(" · ") { "${it.nombre} (${it.stock.toInt()})" },
                    RojoCancelar
                )
            }

            Text("Accesos rápidos", color = TextoBlanco, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            FilaKpi(
                { m -> BotonGrande("Registrar venta", m) { irA("venta_manual") } },
                { m -> BotonGrande("Inventario", m, false) { irA("inventario") } }
            )
            FilaKpi(
                { m -> BotonGrande("Fiados", m, false) { irA("fiados") } },
                { m -> BotonGrande("Reporte del día", m, false) { irA("reportes") } }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BotonGrande(texto: String, modifier: Modifier, principal: Boolean = true, onClick: () -> Unit) {
    if (principal) {
        Button(
            onClick = onClick, modifier = modifier.height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = NegroBoton)
        ) { Text(texto, fontWeight = FontWeight.Bold) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier.height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text(texto, color = TextoBlanco)
        }
    }
}

/** Ventas: historial de todas las ventas con forma de pago y estado. */
@Composable
fun VentasScreen(irA: (String) -> Unit, vm: NegocioViewModel = viewModel()) {
    val ventas by vm.ventas.collectAsState()
    var filtro by remember { mutableStateOf("TODAS") }
    val formato = remember { SimpleDateFormat("dd/MM HH:mm", LOCALE_BO) }
    val desde = NegocioViewModel.inicioDelDia()
    val hoy = ventas.filter { it.creadoEn >= desde }
    val lista = when (filtro) {
        "HOY" -> hoy
        "FIADO" -> ventas.filter { it.formaPago == "FIADO" }
        else -> ventas
    }

    Column(Modifier.fillMaxSize().background(FondoOscuro)) {
        EncabezadoVinto("Ventas", "Historial y registro") {
            Button(
                onClick = { irA("venta_manual") },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = NegroBoton)
            ) { Text("+ Venta", fontWeight = FontWeight.Bold) }
        }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FilaKpi(
                { Kpi("Hoy", bs(hoy.sumOf { v -> v.total }), "${hoy.size} ventas", modifier = it) },
                { Kpi("Ticket promedio", bs(if (hoy.isEmpty()) 0.0 else hoy.sumOf { v -> v.total } / hoy.size), "por venta de hoy", AzulElectrico, it) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("TODAS" to "Todas", "HOY" to "Hoy", "FIADO" to "Al fiado").forEach { (clave, nombre) ->
                    Box(Modifier.clip(RoundedCornerShape(50)).background(if (filtro == clave) VerdeNeon.copy(alpha = 0.18f) else SuperficieOscura)) {
                        TextButton(onClick = { filtro = clave }) {
                            Text(nombre, color = if (filtro == clave) VerdeNeon else TextoGris, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (lista.isEmpty()) {
                Aviso("Sin ventas", "Registra una venta con el botón «+ Venta» o diciendo «vendí 2 panes».", TextoGris)
            }
            lista.take(150).forEach { v ->
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(bs(v.total), color = TextoBlanco, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(formato.format(Date(v.creadoEn)), color = TextoGris, fontSize = 12.sp)
                        }
                        when {
                            v.formaPago != "FIADO" -> Etiqueta("Contado", VerdeNeon)
                            v.pagado -> Etiqueta("Fiado cobrado", AzulElectrico)
                            else -> Etiqueta("Fiado · ${v.clienteFiado ?: "cliente"}", NaranjaAlerta)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/** Caja: separa ventas, efectivo, fiados, cobros y gastos del día (Caja del Figma). */
@Composable
fun CajaScreen(irA: (String) -> Unit, vm: NegocioViewModel = viewModel()) {
    val ventas by vm.ventas.collectAsState()
    val movimientos by vm.movimientos.collectAsState()
    val desde = NegocioViewModel.inicioDelDia()
    val c = ResumenCaja.de(ventas, movimientos, desde)
    val formato = remember { SimpleDateFormat("HH:mm", LOCALE_BO) }
    var nuevoGasto by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        EncabezadoVinto("Caja", "Flujo de dinero de hoy")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Aviso("Ventas no es lo mismo que ganancia", "La ganancia necesita el costo de cada producto. Aquí ves el dinero que entró y salió.")
            FilaKpi(
                { Kpi("Ventas totales", bs(c.ventasTotales), "${c.cantidadVentas} ventas", modifier = it) },
                { Kpi("Al contado", bs(c.contado), "Efectivo del día", modifier = it) }
            )
            FilaKpi(
                { Kpi("Ventas al fiado", bs(c.fiado), "Pendiente de cobro", NaranjaAlerta, it) },
                { Kpi("Cobros de fiados", bs(c.cobros), "Recibidos hoy", AzulElectrico, it) }
            )
            FilaKpi(
                { Kpi("Compras y gastos", bs(c.egresos), "Salidas del día", RojoCancelar, it) },
                { Kpi("Caja estimada", bs(c.cajaEstimada), "Contado + cobros − gastos", modifier = it) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonGrande("Registrar gasto", Modifier.weight(1f)) { nuevoGasto = true }
                BotonGrande("Reporte", Modifier.weight(1f), false) { irA("reportes") }
            }
            Text("Movimientos de hoy", color = TextoBlanco, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            val hoy = movimientos.filter { it.creadoEn >= desde }
            if (hoy.isEmpty()) Text("Todavía no hay movimientos hoy.", color = TextoGris, fontSize = 13.sp)
            hoy.forEach { m ->
                val salida = m.tipo == "EGRESO"
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(m.concepto, color = TextoBlanco, fontSize = 14.sp)
                            Text("${formato.format(Date(m.creadoEn))} · ${m.tipo.lowercase()}", color = TextoGris, fontSize = 12.sp)
                        }
                        Text(
                            (if (salida) "− " else "+ ") + bs(m.monto),
                            color = if (salida) RojoCancelar else if (m.tipo == "INGRESO") VerdeNeon else NaranjaAlerta,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (nuevoGasto) {
        var concepto by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }
        val valor = monto.replace(',', '.').toDoubleOrNull()
        AlertDialog(
            onDismissRequest = { nuevoGasto = false },
            containerColor = SuperficieOscura,
            titleContentColor = TextoBlanco,
            title = { Text("Registrar compra o gasto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CampoVinto(concepto, { concepto = it }, "Concepto (ej. compra de harina)")
                    CampoVinto(monto, { monto = it }, "Monto en Bs", teclado = KeyboardType.Decimal)
                }
            },
            confirmButton = {
                Button(
                    enabled = concepto.isNotBlank() && valor != null && valor > 0.0,
                    onClick = { vm.registrarEgreso(concepto, valor ?: 0.0); nuevoGasto = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeNeon, contentColor = NegroBoton)
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { nuevoGasto = false }) { Text("Cancelar", color = TextoGris) } }
        )
    }
}

/** Análisis: productos más vendidos, ventas de 7 días y alertas (Análisis del Figma). */
@Composable
fun AnalisisScreen(vm: NegocioViewModel = viewModel()) {
    val ventas by vm.ventas.collectAsState()
    val ranking by vm.ranking.collectAsState()
    val productos by vm.productos.collectAsState()
    val dias = (6 downTo 0).map { d ->
        val ini = NegocioViewModel.inicioDelDia(d)
        val fin = NegocioViewModel.inicioDelDia(d - 1)
        val etiqueta = SimpleDateFormat("EEE", LOCALE_BO).format(Date(ini)).take(3)
        etiqueta to ventas.filter { it.creadoEn in ini until fin }.sumOf { it.total }
    }
    val maximo = dias.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0
    val semana = dias.sumOf { it.second }

    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        EncabezadoVinto("Análisis", "Cómo va tu negocio")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FilaKpi(
                { Kpi("Últimos 7 días", bs(semana), "ventas registradas", modifier = it) },
                { Kpi("Promedio diario", bs(semana / 7), "en la semana", AzulElectrico, it) }
            )
            Panel {
                Text("Ventas por día", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                    dias.forEach { (dia, total) ->
                        Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight((total / maximo).toFloat().coerceIn(0.03f, 0.85f))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (total > 0) VerdeNeon else BordeOscuro)
                            )
                            Text(dia, color = TextoGris, fontSize = 11.sp)
                        }
                    }
                }
            }
            Panel {
                Text("Productos más vendidos", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                if (ranking.isEmpty()) Text("Aún no hay ventas con detalle de producto.", color = TextoGris, fontSize = 13.sp)
                ranking.take(5).forEachIndexed { i, r ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${i + 1}.", color = VerdeNeon, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
                        Text(r.nombre, color = TextoBlanco, modifier = Modifier.weight(1f))
                        Text("${"%.0f".format(r.cantidad)} u · ${bs(r.total)}", color = TextoGris, fontSize = 13.sp)
                    }
                }
            }
            val sinStock = productos.filter { it.stock <= 0.0 }
            if (sinStock.isNotEmpty()) {
                Aviso("Productos agotados", sinStock.joinToString(" · ") { it.nombre }, RojoCancelar)
            }
            Aviso(
                "Para calcular la ganancia",
                "Hace falta registrar el costo de compra de cada producto. Por ahora el análisis usa solo las ventas.",
                TextoGris
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Pagos QR y Facturas: preparados en el diseño, pero sin proveedor conectado (igual que el Figma). */
@Composable
fun IntegracionPendienteScreen(titulo: String, sub: String, aviso: String, pasos: List<String>, volver: () -> Unit) {
    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = volver) { Text("‹ Volver", color = TextoBlanco) }
        }
        EncabezadoVinto(titulo, sub) { Etiqueta("Sin conectar", RojoCancelar) }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Aviso("Proveedor no conectado", aviso)
            Panel {
                Text("Qué hace falta para activarlo", color = TextoBlanco, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                pasos.forEachIndexed { i, p -> Text("${i + 1}. $p", color = TextoGris, fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp)) }
            }
        }
    }
}

/** «Más»: todas las demás herramientas del negocio y del asistente. */
@Composable
fun MasScreen(irA: (String) -> Unit) {
    Column(Modifier.fillMaxSize().background(FondoOscuro).verticalScroll(rememberScrollState())) {
        EncabezadoVinto("Más", "Todas las herramientas")
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Negocio", color = VerdeNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Acceso("📦", "Inventario", "Productos, precios y stock") { irA("inventario") }
            Acceso("🧾", "Fiados", "Deudas de clientes y cobros") { irA("fiados") }
            Acceso("📈", "Análisis", "Ventas de la semana y más vendidos") { irA("analisis") }
            Acceso("📊", "Reporte del día", "Resumen y exportar CSV") { irA("reportes") }
            Acceso("📱", "Pagos QR", "Cobros con QR (requiere proveedor)") { irA("pagos_qr") }
            Acceso("🧮", "Facturas", "Facturación (requiere SIAT)") { irA("facturas") }
            Text("Asistente", color = VerdeNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            Acceso("👥", "Contactos", "Para llamar o escribir por voz") { irA("contactos") }
            Acceso("📖", "Guías", "Aprende a usar tu celular") { irA("guias") }
            Acceso("🕘", "Historial de órdenes", "Lo que le pediste a IA Vinto") { irA("historial") }
            Acceso("💬", "Chat Yapay", "Asistente de negocio anterior") { irA("chat") }
            Text("Arquitectura", color = VerdeNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            Acceso("☁", "Nube protegida", "API Gateway + Circuit Breaker en vivo") { irA("gateway") }
            Text("Cuenta", color = VerdeNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            Acceso("🔄", "Sincronización", "Envíos pendientes al servidor") { irA("sync") }
            Acceso("👤", "Perfil", "Tus datos y tu negocio") { irA("perfil") }
            Acceso("⚙️", "Ajustes", "Voz, cuenta y sesión") { irA("ajustes") }
            Spacer(Modifier.height(16.dp))
        }
    }
}
