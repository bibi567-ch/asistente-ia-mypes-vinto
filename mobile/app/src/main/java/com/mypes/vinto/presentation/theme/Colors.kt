package com.mypes.vinto.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Paleta del prototipo Figma de IA Vinto (fondo oscuro con acento verde neón).
val FondoOscuro = Color(0xFF0A0A0A)
val SuperficieOscura = Color(0xFF18181B)
val BordeOscuro = Color(0xFF27272A)
val VerdeNeon = Color(0xFF00E676)
val TextoBlanco = Color(0xFFFAFAFA)
val TextoGris = Color(0xFFA1A1AA)
val TextoSuave = Color(0xFF90A4AE)
val RojoCancelar = Color(0xFFFF5252)
val NaranjaAlerta = Color(0xFFFFAB00)
val AzulElectrico = Color(0xFF2979FF)

val EsquemaVinto = darkColorScheme(
    primary = VerdeNeon,
    onPrimary = Color(0xFF06130D),
    secondary = AzulElectrico,
    background = FondoOscuro,
    onBackground = TextoBlanco,
    surface = SuperficieOscura,
    onSurface = TextoBlanco,
    surfaceVariant = BordeOscuro,
    onSurfaceVariant = TextoGris,
    error = RojoCancelar,
    outline = BordeOscuro
)
