package com.mypes.vinto.presentation.vinto

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mypes.vinto.presentation.theme.AzulElectrico
import com.mypes.vinto.presentation.theme.BordeOscuro
import com.mypes.vinto.presentation.theme.SuperficieOscura
import com.mypes.vinto.presentation.theme.TextoBlanco
import com.mypes.vinto.presentation.theme.TextoGris
import com.mypes.vinto.presentation.theme.VerdeNeon

/** Encabezado de cada pantalla: título, subtítulo y un indicador a la derecha. */
@Composable
fun EncabezadoVinto(titulo: String, sub: String, derecha: @Composable () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, color = TextoBlanco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = TextoGris, fontSize = 13.sp)
        }
        derecha()
    }
}

/** Tarjeta oscura con borde, como los paneles «vidrio» del Figma. */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borde: Color = BordeOscuro,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(SuperficieOscura)
        .border(BorderStroke(1.dp, borde), RoundedCornerShape(18.dp))
    Column(
        (if (onClick != null) base.clickable(onClick = onClick) else base).padding(16.dp),
        content = contenido
    )
}

/** Pastilla pequeña de estado (por ejemplo «Ejecutado», «Sin conexión»). */
@Composable
fun Etiqueta(texto: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(texto, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Sugerencia tocable debajo del chat. */
@Composable
fun Sugerencia(texto: String, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(SuperficieOscura)
            .border(1.dp, BordeOscuro, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(texto, color = TextoBlanco, fontSize = 13.sp)
    }
}

/** Orbe animado del micrófono (verde en reposo, pulsa al escuchar). */
@Composable
fun Orbe(tam: Dp, escuchando: Boolean, contenido: @Composable () -> Unit) {
    val transicion = rememberInfiniteTransition(label = "orbe")
    val pulso by transicion.animateFloat(
        initialValue = 1f,
        targetValue = if (escuchando) 1.18f else 1.04f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulso"
    )
    Box(Modifier.size(tam * 1.3f), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(tam)
                .scale(pulso * 1.15f)
                .clip(CircleShape)
                .background(VerdeNeon.copy(alpha = if (escuchando) 0.25f else 0.12f))
        )
        Box(
            Modifier
                .size(tam)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(VerdeNeon, if (escuchando) AzulElectrico else Color(0xFF00B85C)))),
            contentAlignment = Alignment.Center
        ) { contenido() }
    }
}

@Composable
fun CampoVinto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeNeon,
            unfocusedBorderColor = BordeOscuro,
            focusedTextColor = TextoBlanco,
            unfocusedTextColor = TextoBlanco,
            cursorColor = VerdeNeon,
            focusedLabelColor = VerdeNeon,
            unfocusedLabelColor = TextoGris
        )
    )
}

@Composable
fun FilaDato(icono: String, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Text(icono, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(texto, color = TextoBlanco, fontSize = 15.sp)
    }
}
