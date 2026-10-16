package com.mypes.vinto.data

import com.mypes.vinto.plantillas.PlantillasNegocio
import java.util.UUID

class PlantillaRepository(private val productoDao: ProductoDao) {
    suspend fun aplicarPlantilla(plantillaId: String): Int {
        val plantilla = PlantillasNegocio.porId(plantillaId) ?: return 0
        plantilla.productosEjemplo.forEach { ejemplo ->
            productoDao.insertar(
                Producto(
                    id = UUID.randomUUID().toString(),
                    nombre = ejemplo.nombre,
                    precio = ejemplo.precioSugerido,
                    stock = 0.0,
                    actualizadoEn = System.currentTimeMillis()
                )
            )
        }
        return plantilla.productosEjemplo.size
    }
}
