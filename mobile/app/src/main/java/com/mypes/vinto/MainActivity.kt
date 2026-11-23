package com.mypes.vinto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mypes.vinto.presentation.auth.LoginScreen
import com.mypes.vinto.presentation.dashboard.DashboardScreen

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
                    composable("dashboard") { DashboardScreen(nav) }
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
    }
}
