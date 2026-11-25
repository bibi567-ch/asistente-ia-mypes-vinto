package com.mypes.vinto

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import com.mypes.vinto.presentation.auth.ApiLoginScreen
import com.mypes.vinto.presentation.auth.LoginScreen
import com.mypes.vinto.presentation.auth.RegistroScreen
import com.mypes.vinto.presentation.chat.ChatScreen
import com.mypes.vinto.presentation.dashboard.DashboardScreen
import com.mypes.vinto.presentation.fiados.FiadosScreen
import com.mypes.vinto.presentation.inventario.InventarioScreen
import com.mypes.vinto.presentation.onboarding.OnboardingPlantillaScreen
import com.mypes.vinto.presentation.perfil.PerfilScreen
import com.mypes.vinto.presentation.reportes.ReportesScreen
import com.mypes.vinto.presentation.sync.SincronizacionScreen
import com.mypes.vinto.presentation.ventas.VentaManualScreen
import com.mypes.vinto.update.AppUpdateManager
import com.mypes.vinto.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val solicitarAudio = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                NavHost(navController = nav, startDestination = "login") {
                    composable("login") { LoginScreen(nav) }
                    composable("registro") { RegistroScreen(nav) }
                    composable("onboarding_plantilla") { OnboardingPlantillaScreen(nav) }
                    composable("dashboard") { DashboardScreen(nav) }
                    composable("perfil") { PerfilScreen(nav) }
                    composable("fiados") { FiadosScreen(nav) }
                    composable(
                        route = "inventario?filtro={filtro}",
                        arguments = listOf(
                            navArgument("filtro") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { entry ->
                        InventarioScreen(nav, filtroInicial = entry.arguments?.getString("filtro"))
                    }
                    composable(
                        route = "venta_manual?producto={producto}&cantidad={cantidad}",
                        arguments = listOf(
                            navArgument("producto") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            },
                            navArgument("cantidad") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { entry ->
                        VentaManualScreen(
                            nav = nav,
                            productoSugerido = entry.arguments?.getString("producto").orEmpty(),
                            cantidadSugerida = entry.arguments?.getString("cantidad")
                                ?.toDoubleOrNull()
                                ?.takeIf { it.isFinite() } ?: 1.0
                        )
                    }
                    composable("chat") {
                        val dashboardEntry = remember(nav) {
                            nav.getBackStackEntry("dashboard")
                        }
                        val voiceViewModel: DashboardViewModel = viewModel(
                            viewModelStoreOwner = dashboardEntry
                        )
                        ChatScreen(
                            nav = nav,
                            onNavegarVenta = { producto, cantidad ->
                                val productoCodificado = Uri.encode(producto.orEmpty())
                                val cantidadCodificada = cantidad?.toString().orEmpty()
                                nav.navigate(
                                    "venta_manual?producto=$productoCodificado&cantidad=$cantidadCodificada"
                                )
                            },
                            onNavegarInventario = { filtro ->
                                val ruta = filtro?.takeIf(String::isNotBlank)
                                    ?.let { "inventario?filtro=${Uri.encode(it)}" }
                                    ?: "inventario"
                                nav.navigate(ruta)
                            },
                            onNavegarFiados = { nav.navigate("fiados") },
                            voiceViewModel = voiceViewModel
                        )
                    }
                    composable("sync") { SincronizacionScreen(nav) }
                    composable("api_login") { ApiLoginScreen(nav) }
                    composable("reportes") { ReportesScreen(nav) }
                }
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarAudio.launch(Manifest.permission.RECORD_AUDIO)
        }
        lifecycleScope.launch {
            AppUpdateManager(applicationContext).checkAndInstall()
        }
    }
}
