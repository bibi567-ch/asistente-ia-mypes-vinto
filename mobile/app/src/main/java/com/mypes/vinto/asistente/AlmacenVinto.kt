package com.mypes.vinto.asistente

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class ContactoVinto(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String = "",
    val alias: String = "",
    val numero: String = ""
)

data class RegistroOrden(
    val id: String = UUID.randomUUID().toString(),
    val texto: String = "",
    val comando: String = Comando.DESCONOCIDO.name,
    val confirmado: Boolean = false,
    val ejecutado: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)

/**
 * Datos locales del agente (contactos frecuentes, historial de órdenes y preferencias).
 * Se guardan solo en el teléfono, en las preferencias privadas de la app.
 */
class AlmacenVinto(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("iavinto", Context.MODE_PRIVATE)
    private val gson = Gson()

    private inline fun <reified T> leerLista(clave: String): List<T> = runCatching {
        val json = prefs.getString(clave, null) ?: return emptyList()
        gson.fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type) ?: emptyList()
    }.getOrDefault(emptyList())

    private fun <T> guardarLista(clave: String, lista: List<T>) {
        prefs.edit().putString(clave, gson.toJson(lista)).apply()
    }

    // Contactos
    fun contactos(): List<ContactoVinto> = leerLista<ContactoVinto>("contactos").sortedBy { it.nombre.lowercase() }

    fun guardarContacto(c: ContactoVinto) {
        guardarLista("contactos", contactos().filterNot { it.id == c.id } + c)
    }

    fun borrarContacto(id: String) {
        guardarLista("contactos", contactos().filterNot { it.id == id })
    }

    /** Busca por alias («mi hermana») o por nombre («Rosa»), sin tildes ni mayúsculas. */
    fun buscarContacto(texto: String?): ContactoVinto? {
        if (texto.isNullOrBlank()) return null
        val t = ExtractorComandos.sinTildes(texto.lowercase().trim())
        val sinMi = t.removePrefix("mi ").removePrefix("don ").removePrefix("doña ").removePrefix("dona ")
        return contactos().firstOrNull { c ->
            val alias = ExtractorComandos.sinTildes(c.alias.lowercase().trim())
            val nombre = ExtractorComandos.sinTildes(c.nombre.lowercase().trim())
            (alias.isNotEmpty() && (alias == t || alias == sinMi || alias.removePrefix("mi ") == sinMi)) ||
                nombre == t || nombre == sinMi || nombre.split(" ").firstOrNull() == sinMi
        }
    }

    // Historial
    fun historial(): List<RegistroOrden> = leerLista<RegistroOrden>("historial").sortedByDescending { it.fecha }

    fun registrar(r: RegistroOrden) {
        guardarLista("historial", (listOf(r) + historial()).take(200))
    }

    fun borrarHistorial() = guardarLista("historial", emptyList<RegistroOrden>())

    // Preferencias
    var respuestasPorVoz: Boolean
        get() = prefs.getBoolean("voz", true)
        set(v) = prefs.edit().putBoolean("voz", v).apply()

    var velocidadVoz: Float
        get() = prefs.getFloat("velocidad", 0.9f)
        set(v) = prefs.edit().putFloat("velocidad", v).apply()
}
