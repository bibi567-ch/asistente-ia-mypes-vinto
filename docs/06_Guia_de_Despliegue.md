### Guía de Configuración, Ejecución y Despliegue Local (v2.0)
**Universidad Adventista de Bolivia · Ingeniería en Sistemas · Proyecto Integrador Yapay**

| Control del documento | Detalle |
| ------ | ------ |
| **Versión de la guía** | 2.0 (Consolidada - Noviembre 2026) |
| **Sistema** | Asistente Conversacional Offline Edge AI (Yapay) |
| **Entorno de Despliegue** | Desarrollo Local / Entorno Experimental (No Producción) |
| **Stack Backend** | Node.js 24.x, TypeScript, Express 5, PostgreSQL 16 (Docker Compose) |
| **Stack Móvil** | Kotlin Nativo, Jetpack Compose, Room v6 + SQLCipher, Vosk ASR Edge |
| **Integrantes** | Eva Chino, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo |

---

#### 1. PRERREQUISITOS DEL ENTORNO DE DESARROLLO

Para ejecutar y compilar el proyecto Yapay en un entorno local de desarrollo, se requiere contar con las siguientes herramientas instaladas y configuradas:

| Herramienta / Runtime | Versión Mínima / Recomendada | Propósito en el Proyecto |
| ------ | ------ | ------ |
| **Node.js** | v24.x LTS (o v20.x+) | Runtime de ejecución del backend REST API y scripts de administración. |
| **npm** | v10.x+ | Gestor de paquetes y ejecutor de scripts del servidor backend. |
| **Docker Desktop** | v24.x+ con Docker Compose v2 | Orquestación del contenedor aislado de base de datos PostgreSQL 16. |
| **PostgreSQL** | v16.x (vía Docker) | Motor de base de datos relacional para la persistencia del backend. |
| **Android Studio** | Ladybug / Jellyfish (o superior) | IDE para la compilación, depuración y ejecución del cliente Android. |
| **JDK (Java Dev Kit)** | JDK 17 (o JDK 21) | Kit de desarrollo Java requerido por Gradle para compilar la app Kotlin. |
| **Android SDK** | API 26+ (Android 8.0 Oreo) | SDK objetivo para el soporte móvil en dispositivos de gama media/baja. |

---

#### 2. CONFIGURACIÓN E INICIALIZACIÓN DEL BACKEND CLOUD

##### Paso 2.1: Clonación del Repositorio
- Clone el repositorio del proyecto e ingrese al directorio raíz.

##### Paso 2.2: Despliegue de PostgreSQL 16
- Ejecute el comando de Docker Compose para iniciar la instancia aislada de PostgreSQL.
- Verifique que el contenedor `mypes_postgres` se encuentre activo escuchando en el puerto local `5432`.

##### Paso 2.3: Configuración de Variables de Entorno
- Copie el archivo de ejemplo de variables de entorno al archivo `.env` del backend.
- Configure la clave secreta requerida para la firma de tokens JWT.
- La base de datos local se configura para escuchar únicamente en `127.0.0.1` (loopback).

##### Paso 2.4: Instalación de Dependencias y Migraciones
- Desde la carpeta del backend, instale los paquetes de Node.js.
- Ejecute el comando de migración de base de datos para aplicar la estructura de tablas relacionales en PostgreSQL 16.

##### Paso 2.5: Creación del Propietario y Negocio Inicial
- Ejecute el script de administración especificando el nombre de la MYPE y el correo del propietario.
- Guarde las credenciales temporales generadas para las pruebas de autenticación REST.

##### Paso 2.6: Iniciar el Servidor API Gateway
- Inicie el servidor de desarrollo Node.js/Express.
- Verifique la salud de la API consultando la ruta de salud del servidor (debe responder HTTP 200 OK).

---

#### 3. CONFIGURACIÓN Y COMPILACIÓN DEL CLIENTE MÓVIL ANDROID

##### Paso 3.1: Descarga del Modelo de Voz Vosk (Edge ASR)
- El reconocimiento de voz offline utiliza el modelo liviano en español Vosk (~117 MB descomprimido).
- En Windows PowerShell, ejecute el script de descarga dentro de la carpeta del proyecto móvil.
- Verifique la ubicación del archivo binario del modelo en la carpeta de assets de la aplicación.

##### Paso 3.2: Configuración de la URL Base de la API
- **Emulador Android Studio:** Configure la dirección de loopback especial del emulador (puerto 3000).
- **Dispositivo Físico:** Conecte el teléfono a la misma red Wi-Fi y configure la IP local de la computadora de desarrollo.

##### Paso 3.3: Compilación y Pruebas Unitarias
1. Abra el proyecto móvil en Android Studio y configure JDK 17.
2. Ejecute la tarea de compilación para generar el archivo APK debug (`app-debug.apk`).
3. Ejecute la suite de pruebas unitarias JVM para verificar los componentes NLU, derivación de PIN y plantillas.

---

#### 4. VERIFICACIÓN Y PRUEBAS DE INTEGRACIÓN LOCAL

1. **Autenticación Remota:** Pruebe el inicio de sesión o registro desde la app Android y verifique la entrega del token JWT.
2. **Sincronización Outbox:** Desconecte la red en el teléfono, registre 2 ventas por voz, reconecte la red y ejecute la sincronización en lote.

---

#### 5. CI/CD INTEGRACIÓN CONTINUA EN GITHUB ACTIONS

- El flujo automatizado de verificación en GitHub Actions se ejecuta automáticamente en la rama principal.
- Ejecuta compilación TypeScript, chequeo de tipos, pruebas unitarias del servidor e integración en PostgreSQL 16 aislado.

> **Límite de Verificación:** El CI aprueba la salud del servidor backend, pero no constituye prueba funcional de la interfaz Android en hardware móvil real.
