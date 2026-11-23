### Reporte de Integracion y Despliegue Continuo (CI/CD v2.0)
**Universidad Adventista de Bolivia · Ingenieria en Sistemas · Proyecto Integrador Yapay**

| Control del documento | Detalle |
| ------ | ------ |
| **Version del reporte** | 2.0 (Consolidada - Noviembre 2026) |
| **Sistema** | Asistente Conversacional Offline Edge AI (Yapay) |
| **Workflow Automatizado** |  () |
| **Entorno de Verificacion** | GitHub Actions Runner ( con PostgreSQL 16) |
| **Integrantes** | Eva Chino, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo |

---

#### 1. RESUMEN DEL PIPELINE DE INTEGRACION CONTINUA (CI)

El proyecto cuenta con una canalizacion automatizada de Integracion Continua configurada mediante **GitHub Actions** (). Este pipeline se activa automaticamente ante cada evento de  o  sobre las ramas  y , asegurando la calidad tecnica y la no regresion del codigo fuente del backend.

##### Etapas del Job de Verificacion ():
1. **Ambiente de Ejecucion:** Provisionamiento de contenedor  con servicio acoplado de **PostgreSQL 16**.
2. **Instalacion de Dependencias:** Ejecucion de  para garantizar la instalacion limpia e identica del arbol de paquetes Node.js 24.
3. **Migraciones de Base de Datos:** Ejecucion de  para verificar la aplicacion secuencial y libre de errores de los scripts SQL relacionales (versiones  a ).
4. **Compilacion y Chequeo de Tipos:** Ejecucion de  y  para validar la compilacion rigurosa de TypeScript 5 y la coherencia de tipos en el codigo de aplicacion y pruebas.
5. **Pruebas Unitarias:** Ejecucion de  para verificar la logica aislada de la API, controladores y servicios de dominio.
6. **Pruebas de Integracion:** Ejecucion de  contra el servicio PostgreSQL 16 aislado, validando autenticacion JWT, idempotencia de lotes Outbox y politicas de error HTTP.

---

#### 2. BITACORA DE EJECUCIONES VERIFICADAS EN GITHUB ACTIONS

| Fecha UTC | Rama | Commit / Revision | Estado | Alcance Tecnico Verificado | Enlace Oficial GitHub Actions |
| ------ | ------ | ------ | ------ | ------ | ------ |
| **2026-11-12** |  |  | **EXITO (100%)** | Migraciones SQL, build TypeScript, typecheck, 11 unit tests y 9 integration tests en PostgreSQL 16. | Run #43157514411 |
| **2026-11-12** |  |  | **EXITO (100%)** | Verificacio completa del pipeline de integracion continua para la rama de desarrollo. | Run #43150156269 |

> **Nota de Trazabilidad:** El Run #43157514411 en la rama  representa la linea base verificada de la version 2.0 del proyecto. Las ejecuciones locales previas que sufrieron bloqueos por configuracion de credenciales PostgreSQL quedan respaldadas como antecedentes historicos en el documento 05_Resultados_de_Pruebas.md.

---

#### 3. ALCANCE Y LIMITACIONES EXPLICITAS DEL PIPELINE CI

Para mantener la maxima transparencia academica e industrial, se delimita explicitamente la cobertura alcanzada por el reporte de CI/CD:

##### Componentes Verificados por CI:
* **Sintaxis y Tipado:** Compilacion estricta en TypeScript para la API Node.js 24 / Express 5.
* **Base de Datos:** Ejecucion correcta de migraciones DDL relacionales sobre PostgreSQL 16.
* **Pruebas de API REST:** Verificacion unitaria y de integracion de la API REST, autenticacion JWT y sincronizacion de lotes idempotentes.
* **Aislamiento Multitenant:** Verificacion de aislamiento de datos por negocio y generacion de respuestas de prueba.

##### Componentes Excluidos de CI (Requieren Pruebas Manuales / Hardware):
* **Cliente Android:** Compilacion nativa y pruebas UI del cliente movil Android (Kotlin / Jetpack Compose).
* **Motor ASR Edge:** Evaluacion acustica del motor ASR Vosk Edge en dispositivo movil con ruido ambiental.
* **Cifrado Local:** Verificacion de la persistencia local Room v7 cifrada mediante SQLCipher AES-256 en smartphones.
* **Conectividad Fisica:** Conexion fisica de red local (Wi-Fi/datos) entre el dispositivo Android y el servidor API Gateway.
* **Servidor Productivo:** Despliegue en servidores productivos con certificados TLS/HTTPS y dominio publico.

---

#### 4. HOJA DE RUTA PARA LA AMPLIACION DEL PIPELINE (CI/CD MOVIL)

Como trabajo futuro para consolidar la automatizacion integral del sistema Yapay, se recomienda incorporar un workflow secundario  con los siguientes pasos:
1. Configuracion de **JDK 17** y **Android SDK (API 34)** en el runner.
2. Ejecucion automatizada de pruebas unitarias JVM del cliente: .
3. Compilacion automatizada del paquete APK debug: .
4. Publicacion automatica de artefactos APK en los releases de GitHub.
