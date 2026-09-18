# 2. Requisitos del Sistema

En esta sección se definen los Requisitos Funcionales (RF), Requisitos No Funcionales (RNF) y los Requisitos Arquitectónicamente Significativos que guiaron las decisiones de diseño del sistema, de acuerdo a los estándares de la materia.

## 2.1 Matriz de Requisitos Funcionales (RF)
Se utiliza la sintaxis formal: *«El sistema debe [acción] + [entidad/objeto] + [condición/contexto]»*.

| Código | Módulo | Descripción del Requisito | Prioridad |
| :--- | :--- | :--- | :--- |
| **RF-001** | Procesamiento de Voz | El sistema debe transcribir comandos de voz a texto utilizando el modelo NLP offline. | Alta |
| **RF-002** | Gestión de Ventas | El sistema debe registrar una venta en la base de datos local al confirmar el comando de voz del usuario. | Alta |
| **RF-003** | Gestión de Inventario | El sistema debe descontar del inventario la cantidad de productos vendidos de manera automática. | Alta |
| **RF-004** | Consulta de Stock | El sistema debe permitir consultar la cantidad de un producto específico mediante lenguaje natural. | Alta |
| **RF-005** | Notificaciones | El sistema debe alertar visual y auditivamente al usuario cuando un producto alcance el stock mínimo. | Media |
| **RF-006** | Panel Principal | El sistema debe desplegar un dashboard con el resumen de métricas clave (ventas del día) al iniciar. | Alta |
| **RF-007** | Gestión de Productos | El sistema debe permitir el registro manual o por voz de nuevos productos al catálogo local. | Alta |
| **RF-008** | Sincronización | El sistema debe sincronizar los registros locales con la base de datos en la nube cuando detecte conexión a internet. | Media |
| **RF-009** | Autenticación | El sistema debe autenticar al usuario localmente mediante un PIN simple de 4 dígitos. | Alta |
| **RF-010** | Reportes | El sistema debe generar un reporte resumen de las ventas diarias en formato PDF local. | Baja |

## 2.2 Requisitos No Funcionales (RNF)

| Código | Categoría | Descripción del Requisito | Prioridad |
| :--- | :--- | :--- | :--- |
| **RNF-001** | Rendimiento | El sistema debe procesar el comando de voz y emitir una respuesta en un tiempo no mayor a 2 segundos en un dispositivo con 2GB de RAM. | Alta |
| **RNF-002** | Confiabilidad / Offline | El sistema debe garantizar un 100% de operatividad para el registro de ventas sin requerir conexión a internet. | Alta |
| **RNF-003** | Seguridad | El sistema debe cifrar los datos del inventario y ventas locales utilizando el algoritmo AES-256 (Datos en reposo). | Alta |
| **RNF-004** | Usabilidad / Accesibilidad | El sistema debe cumplir con pautas WCAG 2.1 AA y requerir un máximo de un toque para activar la escucha activa, reduciendo la carga cognitiva. | Alta |
| **RNF-005** | Compatibilidad | El sistema debe ser totalmente compatible y fluido en dispositivos móviles con sistema operativo Android 8.0 o superior. | Alta |

## 2.3 Requisitos Arquitectónicamente Significativos (Drivers Arquitectónicos)
Estos requisitos condicionan fuertemente las decisiones de la arquitectura.

| ID | Requisito | Impacto arquitectónico | Prioridad |
| :--- | :--- | :--- | :--- |
| **RA-01** | Operar 100% offline (RNF-002) | Obliga a usar bases de datos locales (SQLite/Room) y modelos NLP on-device (Gemma/Vosk). | Alta |
| **RA-02** | Recursos de hardware limitados (<2GB RAM) | Limita el tamaño de los modelos y requiere optimización extrema de la memoria (Grouped-query attention). | Alta |
| **RA-03** | Sincronización diferida (RF-008) | Requiere un manejador de trabajos en segundo plano (WorkManager) y manejo de conflictos de datos. | Media |
| **RA-04** | Cifrado de datos en reposo (RNF-003) | Afecta el rendimiento de lectura/escritura en la base de datos local (requiere SQLCipher). | Media |
| **RA-05** | Interfaz basada en voz (VUI) | Cambia el paradigma visual clásico, requiriendo un orquestador de intenciones a nivel cliente. | Alta |
