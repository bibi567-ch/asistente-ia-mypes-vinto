# DOCUMENTO C: DOCUMENTACIÓN TÉCNICA - TECNOLOGÍAS DE INTERNET

**PORTADA**
**UNIVERSIDAD ADVENTISTA DE BOLIVIA**
**INGENIERÍA EN SISTEMAS**
**TECNOLOGÍAS DE INTERNET**

**SISTEMA:** Asistente Conversacional Offline con Procesamiento de Lenguaje Natural para la Gestión de MYPES en Vinto
**FECHA:** [Fecha]

---

## 1. REQUISITOS DEL SOFTWARE

### 1.1 Matriz de Requisitos Funcionales (RF)
* *(Incluye los RF estándar: Registro, Autenticación, Ventas por voz, Stock, Reportes).*
* **RF-011 (Modificado):** El sistema debe resolver conflictos de datos al sincronizar mediante políticas LWW (Last Write Wins) y alertas visuales para reconciliación manual en el backend.
* **RF-013 (Nuevo):** El sistema debe incluir un paso de Confirmación Visual (VUI + GUI) antes de guardar una transacción dictada por voz para mitigar errores por ruido ambiental.

### 1.2 Requisitos No Funcionales (RNF)
| Código | Categoría | Descripción | Métrica |
| :--- | :--- | :--- | :--- |
| **RNF-002** | Rendimiento | Desarrollo en Kotlin Nativo para garantizar un consumo RAM estricto en el dispositivo. | **< 500 MB RAM** |
| **RNF-005** | Usabilidad | Operable por personas con baja alfabetización digital. | **≥ 70 puntos SUS** |

### 1.3 Nuevas Métricas de Evaluación del Proyecto
Para evitar el riesgo de evaluar únicamente percepciones subjetivas, el proyecto se medirá mediante:
1. **Métrica de Eficiencia:** Tiempo promedio en registrar una venta por voz vs. tiempo en registrarlo en el cuaderno físico.
2. **Métrica de Eficacia:** Tasa de errores en el cálculo de inventario al fin de mes (Sistema automatizado vs Método tradicional).
3. **Métrica de Satisfacción:** Encuesta SUS (System Usability Scale).

---

## 2. MODELADO DE CASOS DE USO

### Especificación Narrativa: Registrar Venta por Voz/Texto
* **Actor:** Comerciante.
* **Flujo Principal:**
  1. El comerciante dicta: *"Vendí 3 gaseosas a mi casera"*.
  2. El sistema transcribe usando **Vosk** (afinado con vocabulario dinámico local).
  3. El sistema clasifica intención con **TFLite**.
  4. **(Paso de Seguridad):** El sistema muestra una tarjeta visual grande de confirmación: "¿Guardar venta de 3 Gaseosas?".
  5. El usuario confirma. El sistema guarda en SQLite (Outbox local).

---

## 3. DISEÑO UX/UI EN FIGMA

### 3.1 Arquitectura de Información y UX
* **Mitigación de Ruido Ambiental:** El diseño en Figma debe contemplar tipografía extra-grande para la **Confirmación Visual**, asegurando que el comerciante valide lo que la IA escuchó antes de que impacte su contabilidad.
* **Personas:** María (52 años, tienda) y Carlos (38 años, ferretería). Operan en entornos de mucho ruido (mercado local).

### 3.2 Sistema de Diseño
* **Desarrollo Visual:** Descartando PWA, el diseño en Figma se orientará directamente a componentes nativos de **Material Design 3 (Android)**.
* **Paleta:** Verde (#2E7D32) para transacciones de ingreso, Rojo (#F44336) para advertencias de stock o fallos de reconocimiento de voz.

---
*Fin del Documento C - Preparado para presentación.*
