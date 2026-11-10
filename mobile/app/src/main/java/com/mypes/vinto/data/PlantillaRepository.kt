package com.mypes.vinto.data

import com.mypes.vinto.plantillas.PlantillasNegocio
import java.text.Normalizer

class PlantillaRepository(private val productoDao: ProductoDao) {
    suspend fun aplicarPlantilla(plantillaId: String): Int {
        val plantilla = PlantillasNegocio.porId(plantillaId) ?: return 0
        var insertados = 0
        plantilla.productosEjemplo.forEach { ejemplo ->
            val idProducto = Normalizer.normalize(ejemplo.nombre.trim().lowercase(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .replace(Regex("[^a-z0-9]+"), "_")
                .trim('_')
            val insertado = productoDao.insertarPlantillaSiNoExiste(
                Producto(
                    id = "${plantilla.id}_$idProducto",
                    nombre = ejemplo.nombre,
                    precio = ejemplo.precioSugerido,
                    stock = 0.0,
                    actualizadoEn = System.currentTimeMillis()
                )
            )
            if (insertado) insertados++
        }
        return insertados
    }
}
