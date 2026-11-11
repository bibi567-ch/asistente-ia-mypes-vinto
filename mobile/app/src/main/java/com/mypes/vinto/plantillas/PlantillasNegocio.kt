package com.mypes.vinto.plantillas

object PlantillasNegocio {
    data class Plantilla(
        val id: String,
        val nombre: String,
        val rubro: String,
        val categorias: List<String>,
        val unidades: List<String>,
        val productosEjemplo: List<ProductoEjemplo>
    )

    data class ProductoEjemplo(
        val nombre: String,
        val unidad: String,
        val precioSugerido: Double,
        val categoria: String
    )

    val ABARROTES = Plantilla(
        id = "abarrotes",
        nombre = "Tienda de abarrotes",
        rubro = "Abarrotes",
        categorias = listOf("Almacén", "Bebidas", "Limpieza", "Snacks"),
        unidades = listOf("unidad", "docena", "cuartilla", "caja"),
        productosEjemplo = listOf(
            ProductoEjemplo("Arroz", "cuartilla", 25.0, "Almacén"),
            ProductoEjemplo("Fideo", "cuartilla", 12.0, "Almacén"),
            ProductoEjemplo("Aceite", "litro", 12.5, "Almacén"),
            ProductoEjemplo("Azúcar", "kilo", 8.0, "Almacén"),
            ProductoEjemplo("Gaseosa", "unidad", 5.0, "Bebidas")
        )
    )

    val PENSION = Plantilla(
        id = "pension",
        nombre = "Pensión / Almuerzo",
        rubro = "Pensión",
        categorias = listOf("Menú del día", "Bebidas", "Postres"),
        unidades = listOf("plato", "vaso", "unidad"),
        productosEjemplo = listOf(
            ProductoEjemplo("Almuerzo completo", "plato", 15.0, "Menú del día"),
            ProductoEjemplo("Sopa", "plato", 8.0, "Menú del día"),
            ProductoEjemplo("Refresco", "vaso", 3.0, "Bebidas"),
            ProductoEjemplo("Postre", "unidad", 5.0, "Postres")
        )
    )

    val BROASTERIA = Plantilla(
        id = "broasteria",
        nombre = "Broastería / Pollo frito",
        rubro = "Broastería",
        categorias = listOf("Pollo", "Acompañamientos", "Bebidas"),
        unidades = listOf("cuarto", "medio", "entero", "unidad"),
        productosEjemplo = listOf(
            ProductoEjemplo("Cuarto de pollo", "cuarto", 18.0, "Pollo"),
            ProductoEjemplo("Medio pollo", "medio", 35.0, "Pollo"),
            ProductoEjemplo("Pollo entero", "entero", 65.0, "Pollo"),
            ProductoEjemplo("Papas fritas", "unidad", 10.0, "Acompañamientos"),
            ProductoEjemplo("Gaseosa", "unidad", 5.0, "Bebidas")
        )
    )

    val FERRETERIA = Plantilla(
        id = "ferreteria",
        nombre = "Ferretería",
        rubro = "Ferretería",
        categorias = listOf("Herramientas", "Materiales", "Pinturas", "Eléctrico"),
        unidades = listOf("unidad", "metro", "kilo", "bolsa"),
        productosEjemplo = listOf(
            ProductoEjemplo("Clavos", "kilo", 15.0, "Materiales"),
            ProductoEjemplo("Cemento", "bolsa", 55.0, "Materiales"),
            ProductoEjemplo("Cable eléctrico", "metro", 3.5, "Eléctrico"),
            ProductoEjemplo("Pintura blanca", "litro", 35.0, "Pinturas"),
            ProductoEjemplo("Martillo", "unidad", 45.0, "Herramientas")
        )
    )

    val FARMACIA = Plantilla(
        id = "farmacia",
        nombre = "Farmacia",
        rubro = "Farmacia",
        categorias = listOf("Medicamentos", "Vitaminas", "Higiene", "Primeros Auxilios"),
        unidades = listOf("caja", "frasco", "blister", "unidad"),
        productosEjemplo = listOf(
            ProductoEjemplo("Paracetamol", "blister", 2.0, "Medicamentos"),
            ProductoEjemplo("Ibuprofeno", "blister", 3.0, "Medicamentos"),
            ProductoEjemplo("Vitamina C", "frasco", 45.0, "Vitaminas"),
            ProductoEjemplo("Alcohol en gel", "frasco", 15.0, "Higiene"),
            ProductoEjemplo("Curitas", "caja", 5.0, "Primeros Auxilios")
        )
    )

    val PASTELERIA = Plantilla(
        id = "pasteleria",
        nombre = "Pastelería / Panadería",
        rubro = "Pastelería",
        categorias = listOf("Panadería", "Repostería", "Tortas", "Bebidas"),
        unidades = listOf("unidad", "docena", "porción", "caja"),
        productosEjemplo = listOf(
            ProductoEjemplo("Pan francés", "unidad", 0.5, "Panadería"),
            ProductoEjemplo("Tortilla", "unidad", 1.0, "Panadería"),
            ProductoEjemplo("Flan", "porción", 4.0, "Repostería"),
            ProductoEjemplo("Gelatina", "porción", 2.0, "Repostería"),
            ProductoEjemplo("Torta 20 personas", "unidad", 80.0, "Tortas"),
            ProductoEjemplo("Coca-Cola", "unidad", 16.0, "Bebidas")
        )
    )

    val TODAS = listOf(ABARROTES, PENSION, BROASTERIA, FERRETERIA, FARMACIA, PASTELERIA)
    fun porId(id: String): Plantilla? = TODAS.firstOrNull { it.id == id }
}
