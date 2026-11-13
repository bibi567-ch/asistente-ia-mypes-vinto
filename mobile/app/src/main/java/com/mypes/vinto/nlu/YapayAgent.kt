package com.mypes.vinto.nlu

import android.content.Context
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Producto
import com.mypes.vinto.data.ProductoDao
import com.mypes.vinto.data.VentaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Normalizer
import java.util.Calendar
import java.util.UUID

data class MensajeChat(
    val texto: String,
    val esUsuario: Boolean,
    val creadoEn: Long = System.currentTimeMillis()
)

class YapayAgent private constructor(private val tools: Tools, @Suppress("UNUSED_PARAMETER") marker: Unit) {
    constructor(context: Context) : this(
        Tools(
            MypesDatabase.obtener(context).productoDao(),
            MypesDatabase.obtener(context).ventaDao()
        ),
        Unit
    )

    internal constructor(tools: Tools) : this(tools, Unit)

    private val _estadoChat = MutableStateFlow(
        listOf(
            MensajeChat(
                "Soy Yapay. Puedo consultar tus ventas y ayudarte con tu negocio. " +
                    "Antes de guardar un producto, siempre te pediré confirmación.",
                esUsuario = false
            )
        )
    )
    val estadoChat: StateFlow<List<MensajeChat>> = _estadoChat.asStateFlow()
    private var productoPendiente: ProductoPendiente? = null

    data class ProductoPendiente(val nombre: String, val precio: Double, val stock: Double)

    /**
     * Prompt de sistema para el adaptador LLM futuro. En esta versión no se envían
     * conversaciones a un servicio remoto; las intenciones se resuelven localmente.
     */
    val systemPrompt: String = """
        Eres Yapay, un consultor financiero experto en contabilidad, auditoría y administración
        de pequeñas empresas. Ayudas a Doña Carmen a hacer crecer su negocio. Responde en el
        idioma del usuario (quechua, castellano o aymara) y usa las herramientas disponibles
        solo para acciones solicitadas. Antes de guardar, actualizar o borrar cualquier dato,
        explica el cambio y solicita confirmación explícita. No inventes ventas, costos ni márgenes.
    """.trimIndent()

    suspend fun procesarComando(texto: String): String {
        val comando = texto.trim()
        if (comando.isEmpty()) return responder("No recibí un mensaje. ¿Puedes repetir?")
        agregarMensaje(MensajeChat(comando, esUsuario = true))

        val pendiente = productoPendiente
        if (pendiente != null) {
            return when (normalizar(comando)) {
                "si", "sí", "confirmo", "confirmar", "ari" -> {
                    productoPendiente = null
                    val insertado = runCatching {
                        tools.agregarProducto(pendiente.nombre, pendiente.precio, pendiente.stock)
                    }.getOrElse {
                        return responder("No se pudo guardar el producto: ${it.message ?: "error de almacenamiento"}.")
                    }
                    if (insertado) {
                        responder("Producto guardado en este dispositivo.")
                    } else {
                        responder("Ya existe un producto con ese nombre; no se guardó otro.")
                    }
                }
                "no", "cancelar", "cancela" -> {
                    productoPendiente = null
                    responder("De acuerdo. No se guardó ningún producto.")
                }
                else -> responder("Tengo un producto pendiente. Responde «sí» para guardarlo o «no» para cancelarlo.")
            }
        }

        val nuevoProducto = extraerProducto(comando)
        if (nuevoProducto != null) {
            productoPendiente = nuevoProducto
            return responder(
                "Voy a agregar ${nuevoProducto.nombre} con precio ${nuevoProducto.precio} Bs " +
                    "y stock ${nuevoProducto.stock}. ¿Confirmas que lo guarde en este dispositivo?"
            )
        }

        return when {
            contiene(comando, "venta", "vendí", "vendi", "factur") -> {
                val total = runCatching { tools.consultarVentasDelDia() }
                    .getOrElse { return responder("No pude consultar las ventas: ${it.message ?: "error de almacenamiento"}.") }
                responder("Las ventas registradas hoy suman ${"%.2f".format(total)} Bs. ${tools.obtenerConsejoFinanciero()}")
            }
            contiene(comando, "consejo", "recomend", "margen", "finanza") ->
                responder(tools.obtenerConsejoFinanciero())
            else -> responder("Puedo consultar las ventas de hoy o ayudarte a preparar un producto para agregar.")
        }
    }

    private fun responder(texto: String): String {
        agregarMensaje(MensajeChat(texto, esUsuario = false))
        return texto
    }

    fun registrarInteraccion(texto: String, respuesta: String) {
        if (texto.isNotBlank()) agregarMensaje(MensajeChat(texto, esUsuario = true))
        agregarMensaje(MensajeChat(respuesta, esUsuario = false))
    }

    private fun agregarMensaje(mensaje: MensajeChat) {
        _estadoChat.value = _estadoChat.value + mensaje
    }

    private fun extraerProducto(texto: String): ProductoPendiente? {
        val match = ADD_PRODUCT_REGEX.find(texto) ?: return null
        val nombre = match.groupValues[1].trim().trim(',', '.', ' ')
        val precio = match.groupValues[2].replace(',', '.').toDoubleOrNull() ?: return null
        val stock = match.groupValues[3].takeIf(String::isNotBlank)
            ?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        if (nombre.isEmpty() || nombre.length > 160 || !precio.isFinite() || precio < 0.0 ||
            !stock.isFinite() || stock < 0.0
        ) return null
        return ProductoPendiente(nombre, precio, stock)
    }

    private fun contiene(texto: String, vararg terminos: String): Boolean {
        val normalizado = normalizar(texto)
        return terminos.any { normalizado.contains(normalizar(it)) }
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase().trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")

    class Tools(
        private val insertarProducto: suspend (Producto) -> Boolean,
        private val totalVentasDesde: suspend (Long) -> Double
    ) {
        constructor(productoDao: ProductoDao, ventaDao: VentaDao) : this(
            { producto -> productoDao.insertarPlantillaSiNoExiste(producto) },
            { desde -> ventaDao.totalDesde(desde) }
        )

        suspend fun agregarProducto(nombre: String, precio: Double, stock: Double): Boolean {
            require(nombre.isNotBlank() && nombre.length <= 160) { "El nombre debe tener entre 1 y 160 caracteres." }
            require(precio.isFinite() && precio >= 0.0) { "El precio debe ser un número no negativo." }
            require(stock.isFinite() && stock >= 0.0) { "El stock debe ser un número no negativo." }

            val idProducto = Normalizer.normalize(nombre.trim().lowercase(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .replace(Regex("[^a-z0-9]+"), "_")
                .trim('_')
                .take(120)
            return insertarProducto(
                Producto(
                    id = "yapay_${idProducto}_${UUID.randomUUID()}",
                    nombre = nombre.trim(),
                    precio = precio,
                    stock = stock
                )
            )
        }

        suspend fun consultarVentasDelDia(): Double {
            val inicioDelDia = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            return totalVentasDesde(inicioDelDia)
        }

        fun obtenerConsejoFinanciero(): String =
            "Registra también el costo de compra de cada producto para calcular márgenes reales; " +
                "el inventario actual solo guarda precio de venta."
    }

    private companion object {
        val ADD_PRODUCT_REGEX = Regex(
            """(?i)(?:agrega|agregar|añade|anade)\s+(.+?)\s+por\s+(\d+(?:[.,]\d{1,2})?)(?:\s*(?:bs|bolivianos?))?(?:\s+con\s+stock\s+(\d+(?:[.,]\d+)?))?\s*$"""
        )
    }
}
