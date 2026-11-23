package com.mypes.vinto.asistente

/**
 * Las ocho guías paso a paso del alcance del perfil (mismo contenido que el frontend web).
 * Pasos generales de Android: los nombres de los menús cambian un poco según la marca.
 * Textos en quechua: pendientes de revisión por el hablante del equipo.
 */
data class Guia(val id: String, val titulo: String, val claves: List<String>, val pasos: List<String>)

object Guias {
    val TODAS: List<Guia> = listOf(
        Guia(
            "letra", "Aumentar el tamaño de la letra", listOf("letra", "grande", "tamaño", "fuente"),
            listOf(
                "Abre Ajustes (el ícono de engranaje).", "Entra en Pantalla.",
                "Toca Tamaño de fuente o Tamaño y estilo de fuente.",
                "Mueve la barra hacia la derecha hasta que leas cómodo.",
                "Vuelve atrás: el cambio se guarda solo."
            )
        ),
        Guia(
            "ubicacion", "Compartir mi ubicación",
            listOf("ubicación", "ubicacion", "dónde estoy", "donde estoy", "mapa"),
            listOf(
                "Abre WhatsApp y entra al chat de la persona.",
                "Toca el clip o el signo + junto al cuadro de texto.", "Elige Ubicación.",
                "Toca Enviar tu ubicación actual.", "Si pide permiso de ubicación, toca Permitir."
            )
        ),
        Guia(
            "foto", "Enviar una foto", listOf("foto", "imagen", "galería", "galeria"),
            listOf(
                "Abre WhatsApp y entra al chat.", "Toca el clip o el signo +.",
                "Elige Galería (o Cámara para tomar una nueva).", "Toca la foto que quieres enviar.",
                "Toca el botón verde de enviar."
            )
        ),
        Guia(
            "wifi", "Conectarme a una red wifi", listOf("wifi", "wi-fi", "red", "internet"),
            listOf(
                "Desliza el dedo desde arriba de la pantalla hacia abajo.",
                "Mantén presionado el ícono de Wi-Fi.", "Toca el nombre de la red.",
                "Escribe la contraseña con cuidado (mayúsculas y minúsculas cuentan).", "Toca Conectar."
            )
        ),
        Guia(
            "datos", "Activar los datos móviles", listOf("datos", "megas", "saldo"),
            listOf(
                "Desliza el dedo desde arriba de la pantalla hacia abajo.",
                "Busca el ícono de Datos móviles (dos flechas).",
                "Tócalo para que quede encendido (de color).",
                "Si no aparece, entra en Ajustes > Conexiones o Red > Datos móviles."
            )
        ),
        Guia(
            "contacto", "Guardar un contacto",
            listOf("contacto", "guardar número", "guardar numero", "agenda"),
            listOf(
                "Abre la aplicación Teléfono o Contactos.", "Toca el signo + o Crear contacto.",
                "Escribe el nombre y el número.", "Toca Guardar."
            )
        ),
        Guia(
            "espacio", "Liberar espacio de almacenamiento",
            listOf("espacio", "memoria", "almacenamiento", "lleno"),
            listOf(
                "Abre Ajustes.", "Entra en Almacenamiento o Mantenimiento del dispositivo.",
                "Toca Liberar espacio o revisa Archivos grandes.",
                "Borra videos y archivos que ya no necesites (revisa antes de borrar).",
                "En WhatsApp: Ajustes > Almacenamiento y datos > Administrar almacenamiento."
            )
        ),
        Guia(
            "volumen", "Subir o bajar el volumen", listOf("volumen", "sonido", "no se escucha", "fuerte"),
            listOf(
                "Usa los botones del costado del teléfono: el de arriba sube y el de abajo baja.",
                "Para más opciones, toca los tres puntos que aparecen junto a la barra de volumen.",
                "Ahí puedes subir por separado el volumen de llamadas, música y notificaciones."
            )
        )
    )

    fun porId(id: String?): Guia? = TODAS.firstOrNull { it.id == id }

    fun buscar(texto: String): Guia? {
        val t = texto.lowercase()
        return TODAS.firstOrNull { g -> g.claves.any { t.contains(it) } }
    }
}
