package com.mypes.vinto.presentation.vinto

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mypes.vinto.asistente.AlmacenVinto
import com.mypes.vinto.presentation.theme.FondoOscuro
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon

private data class Pestana(val ruta: String, val nombre: String, val icono: ImageVector)

private val PESTANAS = listOf(
    Pestana("inicio", "Inicio", Icons.Filled.Home),
    Pestana("ventas", "Ventas", Icons.Filled.PointOfSale),
    Pestana("agente", "IA Vinto", Icons.Filled.Mic),
    Pestana("caja", "Caja", Icons.Filled.AccountBalanceWallet),
    Pestana("mas", "Más", Icons.Filled.Apps)
)

/** Pantallas de la versión anterior que se abren en la navegación principal (pantalla completa). */
private val RUTAS_RAIZ = setOf("api_login", "sync", "inventario", "fiados", "reportes", "perfil", "chat")

/** Pestaña que se marca para cada pantalla interna. */
private fun pestanaDe(ruta: String?): String? = when {
    ruta == null -> null
    ruta.startsWith("guia") || ruta in setOf("contactos", "historial", "ajustes", "analisis", "pagos_qr", "facturas", "gateway") -> "mas"
    else -> ruta
}

/**
 * Inicio de IA Vinto después del ingreso con PIN: agente de voz, contactos, guías,
 * historial y ajustes, con la barra inferior del prototipo Figma.
 * [navRaiz] es la navegación principal (login, cuenta del servidor, sincronización).
 */
@Composable
fun VintoApp(navRaiz: NavController) {
    val context = LocalContext.current
    val almacen = remember { AlmacenVinto(context) }
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    fun irA(destino: String) {
        when {
            destino in RUTAS_RAIZ || destino.startsWith("venta_manual") -> navRaiz.navigate(destino)
            else -> nav.navigate(destino) { launchSingleTop = true }
        }
    }

    Scaffold(
        containerColor = FondoOscuro,
        bottomBar = {
            NavigationBar(containerColor = SuperficieOscura) {
                PESTANAS.forEach { p ->
                    val activa = pestanaDe(rutaActual) == p.ruta
                    NavigationBarItem(
                        selected = activa,
                        onClick = {
                            nav.navigate(p.ruta) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(p.icono, contentDescription = p.nombre) },
                        label = { Text(p.nombre, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeNeon,
                            selectedTextColor = VerdeNeon,
                            indicatorColor = VerdeNeon.copy(alpha = 0.15f),
                            unselectedIconColor = TextoGris,
                            unselectedTextColor = TextoGris
                        )
                    )
                }
            }
        }
    ) { relleno ->
        NavHost(navController = nav, startDestination = "inicio", modifier = Modifier.padding(relleno)) {
            composable("inicio") { InicioScreen({ irA(it) }) }
            composable("ventas") { VentasScreen({ irA(it) }) }
            composable("caja") { CajaScreen({ irA(it) }) }
            composable("mas") { MasScreen { irA(it) } }
            composable("analisis") { AnalisisScreen() }
            composable("gateway") { GatewayScreen { nav.popBackStack() } }
            composable("pagos_qr") {
                IntegracionPendienteScreen(
                    "Pagos QR", "Cobros con QR del banco",
                    "No se genera un QR de pago real ni se confirma un cobro sin una respuesta verificable del banco. " +
                        "Una captura de pantalla no confirma el pago.",
                    listOf(
                        "Contrato con un banco o proveedor de cobros QR.",
                        "Credenciales de su API guardadas en el servidor, nunca en el teléfono.",
                        "Confirmación del pago por el servidor antes de sumarlo a la caja."
                    )
                ) { nav.popBackStack() }
            }
            composable("facturas") {
                IntegracionPendienteScreen(
                    "Facturas", "Facturación electrónica",
                    "La app no emite facturas válidas: eso requiere estar habilitado en el SIAT de Impuestos Nacionales.",
                    listOf(
                        "NIT del negocio habilitado en la modalidad de facturación que corresponda.",
                        "Certificado y credenciales del SIAT guardados en el servidor.",
                        "Envío y validación de cada factura por el servidor."
                    )
                ) { nav.popBackStack() }
            }
            composable("agente") { AgenteScreen(almacen) { irA(it) } }
            composable("contactos") { ContactosScreen(almacen) }
            composable("guias") { GuiasScreen { id -> irA("guia/$id") } }
            composable("guia/{id}") { e ->
                GuiaDetalleScreen(e.arguments?.getString("id"), almacen) { nav.popBackStack() }
            }
            composable("historial") { HistorialScreen(almacen) }
            composable("ajustes") {
                AjustesScreen(
                    almacen,
                    irA = { irA(it) },
                    cerrarSesion = {
                        navRaiz.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}
