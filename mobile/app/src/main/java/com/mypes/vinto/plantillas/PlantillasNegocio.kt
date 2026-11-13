package com.mypes.vinto.plantillas

object PlantillasNegocio {
    data class CategoriaMargen(
        val nombre: String,
        val margenMin: Double,
        val margenObjetivo: Double
    )

    data class ProductoEjemplo(
        val nombre: String,
        val unidad: String,
        val precioSugerido: Double,
        val categoria: String
    )

    data class Plantilla(
        val id: String,
        val nombre: String,
        val rubro: String,
        val categorias: List<CategoriaMargen>,
        val productos: List<ProductoEjemplo>
    ) {
        val unidades: List<String> = productos.map { it.unidad }.distinct()
        val productosEjemplo: List<ProductoEjemplo> get() = productos
    }

    val FARMACIA = Plantilla(
        id = "farmacia",
        nombre = "Farmacia Eliana",
        rubro = "Farmacia",
        categorias = listOf(
            CategoriaMargen("Medicamentos", 20.0, 27.5),
            CategoriaMargen("Vitaminas", 30.0, 35.0),
            CategoriaMargen("Higiene", 25.0, 32.0),
            CategoriaMargen("Primeros auxilios", 25.0, 32.0)
        ),
        productos = listOf(
            ProductoEjemplo("Paracetamol 500 mg", "blíster", 2.0, "Medicamentos"),
            ProductoEjemplo("Ibuprofeno 400 mg", "blíster", 3.0, "Medicamentos"),
            ProductoEjemplo("Amoxicilina 500 mg", "caja", 25.0, "Medicamentos"),
            ProductoEjemplo("Loratadina 10 mg", "blíster", 4.0, "Medicamentos"),
            ProductoEjemplo("Vitamina C 1 g", "frasco", 45.0, "Vitaminas"),
            ProductoEjemplo("Complejo B", "frasco", 38.0, "Vitaminas"),
            ProductoEjemplo("Alcohol en gel", "frasco", 15.0, "Higiene"),
            ProductoEjemplo("Jabón antibacterial", "unidad", 8.0, "Higiene"),
            ProductoEjemplo("Curitas", "caja", 5.0, "Primeros auxilios"),
            ProductoEjemplo("Gasa estéril", "paquete", 7.0, "Primeros auxilios")
        )
    )

    val PASTELERIA = Plantilla(
        id = "pasteleria",
        nombre = "Pastelería Pampas",
        rubro = "Pastelería",
        categorias = listOf(
            CategoriaMargen("Panadería", 35.0, 45.0),
            CategoriaMargen("Repostería", 40.0, 50.0),
            CategoriaMargen("Tortas", 35.0, 45.0),
            CategoriaMargen("Bebidas", 25.0, 35.0)
        ),
        productos = listOf(
            ProductoEjemplo("Pan francés", "unidad", 0.5, "Panadería"),
            ProductoEjemplo("Tortilla", "unidad", 1.0, "Panadería"),
            ProductoEjemplo("Pan integral", "unidad", 2.0, "Panadería"),
            ProductoEjemplo("Queque", "porción", 4.0, "Repostería"),
            ProductoEjemplo("Flan", "porción", 4.0, "Repostería"),
            ProductoEjemplo("Gelatina", "porción", 2.0, "Repostería"),
            ProductoEjemplo("Torta de chocolate", "unidad", 120.0, "Tortas"),
            ProductoEjemplo("Torta de 20 personas", "unidad", 80.0, "Tortas"),
            ProductoEjemplo("Café", "vaso", 5.0, "Bebidas"),
            ProductoEjemplo("Refresco", "vaso", 3.0, "Bebidas")
        )
    )

    val FERRETERIA = Plantilla(
        id = "ferreteria",
        nombre = "Ferretería",
        rubro = "Ferretería",
        categorias = listOf(
            CategoriaMargen("Herramientas", 25.0, 35.0),
            CategoriaMargen("Materiales", 15.0, 22.0),
            CategoriaMargen("Pinturas", 20.0, 30.0),
            CategoriaMargen("Eléctrico", 20.0, 30.0)
        ),
        productos = listOf(
            ProductoEjemplo("Clavos", "kilo", 15.0, "Materiales"),
            ProductoEjemplo("Cemento", "bolsa", 55.0, "Materiales"),
            ProductoEjemplo("Arena", "metro cúbico", 180.0, "Materiales"),
            ProductoEjemplo("Martillo", "unidad", 45.0, "Herramientas"),
            ProductoEjemplo("Destornillador", "unidad", 18.0, "Herramientas"),
            ProductoEjemplo("Alicate", "unidad", 35.0, "Herramientas"),
            ProductoEjemplo("Pintura látex blanca", "galón", 110.0, "Pinturas"),
            ProductoEjemplo("Brocha", "unidad", 15.0, "Pinturas"),
            ProductoEjemplo("Cable eléctrico", "metro", 3.5, "Eléctrico"),
            ProductoEjemplo("Foco LED", "unidad", 12.0, "Eléctrico")
        )
    )

    val LICORERIA = Plantilla(
        id = "licoreria",
        nombre = "Licorería",
        rubro = "Licorería",
        categorias = listOf(
            CategoriaMargen("Cervezas", 18.0, 25.0),
            CategoriaMargen("Vinos", 25.0, 35.0),
            CategoriaMargen("Destilados", 22.0, 32.0),
            CategoriaMargen("Bebidas sin alcohol", 25.0, 35.0)
        ),
        productos = listOf(
            ProductoEjemplo("Cerveza nacional", "botella", 10.0, "Cervezas"),
            ProductoEjemplo("Cerveza importada", "botella", 18.0, "Cervezas"),
            ProductoEjemplo("Cerveza en lata", "lata", 9.0, "Cervezas"),
            ProductoEjemplo("Vino tinto", "botella", 45.0, "Vinos"),
            ProductoEjemplo("Vino blanco", "botella", 42.0, "Vinos"),
            ProductoEjemplo("Singani", "botella", 55.0, "Destilados"),
            ProductoEjemplo("Ron", "botella", 75.0, "Destilados"),
            ProductoEjemplo("Whisky", "botella", 140.0, "Destilados"),
            ProductoEjemplo("Agua mineral", "botella", 5.0, "Bebidas sin alcohol"),
            ProductoEjemplo("Gaseosa", "botella", 12.0, "Bebidas sin alcohol")
        )
    )

    val BROASTERIA = Plantilla(
        id = "broasteria",
        nombre = "Broastería",
        rubro = "Broastería",
        categorias = listOf(
            CategoriaMargen("Pollo", 30.0, 40.0),
            CategoriaMargen("Acompañamientos", 40.0, 50.0),
            CategoriaMargen("Bebidas", 25.0, 35.0),
            CategoriaMargen("Salsas y extras", 35.0, 45.0)
        ),
        productos = listOf(
            ProductoEjemplo("Cuarto de pollo", "porción", 18.0, "Pollo"),
            ProductoEjemplo("Medio pollo", "porción", 35.0, "Pollo"),
            ProductoEjemplo("Pollo entero", "unidad", 65.0, "Pollo"),
            ProductoEjemplo("Alitas broaster", "porción", 25.0, "Pollo"),
            ProductoEjemplo("Papas fritas", "porción", 10.0, "Acompañamientos"),
            ProductoEjemplo("Arroz", "porción", 5.0, "Acompañamientos"),
            ProductoEjemplo("Ensalada", "porción", 7.0, "Acompañamientos"),
            ProductoEjemplo("Gaseosa", "unidad", 5.0, "Bebidas"),
            ProductoEjemplo("Agua mineral", "botella", 5.0, "Bebidas"),
            ProductoEjemplo("Salsa de la casa", "porción", 2.0, "Salsas y extras")
        )
    )

    val MINIMARKET = Plantilla(
        id = "minimarket",
        nombre = "Minimarket",
        rubro = "Minimarket",
        categorias = listOf(
            CategoriaMargen("Almacén", 15.0, 22.0),
            CategoriaMargen("Bebidas", 20.0, 28.0),
            CategoriaMargen("Limpieza", 20.0, 30.0),
            CategoriaMargen("Snacks", 25.0, 35.0)
        ),
        productos = listOf(
            ProductoEjemplo("Arroz", "kilo", 8.0, "Almacén"),
            ProductoEjemplo("Fideo", "paquete", 6.0, "Almacén"),
            ProductoEjemplo("Aceite", "litro", 12.5, "Almacén"),
            ProductoEjemplo("Azúcar", "kilo", 8.0, "Almacén"),
            ProductoEjemplo("Gaseosa", "unidad", 5.0, "Bebidas"),
            ProductoEjemplo("Agua embotellada", "botella", 4.0, "Bebidas"),
            ProductoEjemplo("Detergente", "bolsa", 9.0, "Limpieza"),
            ProductoEjemplo("Lavandina", "litro", 7.0, "Limpieza"),
            ProductoEjemplo("Papas fritas", "paquete", 6.0, "Snacks"),
            ProductoEjemplo("Galletas", "paquete", 5.0, "Snacks")
        )
    )

    val TODAS = listOf(FARMACIA, PASTELERIA, FERRETERIA, LICORERIA, BROASTERIA, MINIMARKET)

    fun porId(id: String): Plantilla? = when (id) {
        "abarrotes" -> MINIMARKET
        else -> TODAS.firstOrNull { it.id == id }
    }
}
