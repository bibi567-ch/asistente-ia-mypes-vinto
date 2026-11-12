package com.mypes.vinto.plantillas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantillasNegocioTest {
    @Test
    fun `all requested businesses contain margin categories and at least ten products`() {
        assertEquals(
            setOf("farmacia", "pasteleria", "ferreteria", "licoreria", "broasteria", "minimarket"),
            PlantillasNegocio.TODAS.map { it.id }.toSet()
        )
        PlantillasNegocio.TODAS.forEach { plantilla ->
            assertTrue(plantilla.categorias.isNotEmpty())
            assertTrue(plantilla.productos.size >= 10)
            plantilla.categorias.forEach { categoria ->
                assertTrue(categoria.margenMin >= 0.0)
                assertTrue(categoria.margenObjetivo >= categoria.margenMin)
            }
            plantilla.productos.forEach { producto ->
                assertTrue(producto.unidad.isNotBlank())
                assertTrue(producto.precioSugerido >= 0.0)
                assertTrue(plantilla.categorias.any { it.nombre == producto.categoria })
            }
        }
    }

    @Test
    fun `legacy abarrotes template id resolves to minimarket`() {
        assertEquals("minimarket", PlantillasNegocio.porId("abarrotes")?.id)
    }
}
