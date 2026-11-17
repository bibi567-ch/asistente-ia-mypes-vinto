# 🤖 Yapay — Asistente Conversacional Offline Edge AI para MYPES

## 📋 Descripción del Proyecto
Yapay es un prototipo de asistente conversacional offline-first impulsado por inteligencia artificial en el dispositivo (Edge AI), diseñado específicamente para microempresas (MYPES) y pequeños comercios del centro de Vinto, Cochabamba.

El sistema permite a comerciantes registrar ventas al contado y fiadas, gestionar cobros de deudas, consultar el saldo local de existencias y recibir reportes diarios mediante comandos de voz naturales en castellano, quechua, aimara y variantes lingüísticas mixtas, operando al 100 % sin conexión a Internet.


---

## ✨ Características Principales

- 🎤 **Procesamiento de Voz On-Device (Edge AI):** Reconocimiento de voz local con el motor Vosk () sin consumo de APIs externas ni costo recurrente.
- 📱 **Operación Offline-First:** Persistencia local cifrada con Room v6 + SQLCipher y Android Keystore.
- 🛍️ **Plantillas por Rubro Comercial:** Carga inicial rápida con 6 plantillas ajustables (farmacia, pastelería, ferretería, comercio de bebidas, broastería y minimarket).
- 🛡️ **Confirmación Visual Obligatoria:** Mitigación de errores de transcripción mediante un modal flotante de revisión previa antes de confirmar cualquier venta dictada.
- 🔄 **Sincronización Asíncrona e Idempotente:** Cola Outbox local gestionada por WorkManager con reintentos mediante backoff exponencial y claves de idempotencia UUID v4 ().
- 🌐 **Backend Escalable y Resiliente:** API REST desarrollada en Node.js 24 LTS, Express 5 y TypeScript sobre PostgreSQL 16, protegida con API Gateway (Circuit Breaker) y autenticación JWT con refresh tokens rotativos.

---

## 🏗️ Arquitectura del Sistema (C4 Level 2)



---

## 🛠️ Stack Tecnológico

### Cliente Móvil (Android)
- **Lenguaje & Framework:** Kotlin Nativo, Jetpack Compose, Material Design 3.
- **Persistencia Local:** Room v6, SQLite, SQLCipher (Cifrado AES-256 en reposo).
- **Procesamiento de Voz Edge:** Vosk ASR SDK ().
- **Segundo Plano & Red:** Android WorkManager, OkHttp 4, Retrofit 2.

### Servidor Cloud (Backend)
- **Runtime & Framework:** Node.js 24 LTS, Express 5, TypeScript 5.
- **Base de Datos:** PostgreSQL 16 (desplegado vía Docker Compose).
- **Seguridad & Autenticación:** JWT con refresh tokens rotativos, Hashing scrypt / PBKDF2.
- **Resiliencia & Real-time:** API Gateway con Circuit Breaker, Server-Sent Events (SSE).

---

## 🚀 Guía de Instalación y Ejecución Local

### Prerrequisitos
- **Node.js** v24.x LTS & npm <command>

Usage:

npm install        install all the dependencies in your project
npm install <foo>  add the <foo> dependency to your project
npm test           run this project's tests
npm run <foo>      run the script named <foo>
npm <command> -h   quick help on <command>
npm -l             display usage info for all commands
npm help <term>    search for help on <term>
npm help npm       more involved overview

All commands:

    access, adduser, audit, bugs, cache, ci, completion,
    config, dedupe, deprecate, diff, dist-tag, docs, doctor,
    edit, exec, explain, explore, find-dupes, fund, get, help,
    help-search, hook, init, install, install-ci-test,
    install-test, link, ll, login, logout, ls, org, outdated,
    owner, pack, ping, pkg, prefix, profile, prune, publish,
    query, rebuild, repo, restart, root, run-script, sbom,
    search, set, shrinkwrap, star, stars, start, stop, team,
    test, token, uninstall, unpublish, unstar, update, version,
    view, whoami

Specify configs in the ini-formatted file:
    /home/sandbox/.npmrc
or on the command line via: npm <command> --key=value

More configuration info: npm help config
Configuration fields: npm help 7 config

npm@10.9.8 /usr/lib/node_modules/npm v10+
- **Docker Desktop** v24+ (con Docker Compose v2)
- **Android Studio** (Ladybug / Jellyfish) & JDK 17
- Dispositivo Android v8.0+ (API 26+) o Emulador AVD

### 1. Iniciar el Backend Cloud (API REST + Database)

- Clonar el repositorio e ingresar a la carpeta .
- Iniciar la base de datos PostgreSQL 16 con .
- Copiar las variables de entorno de plantilla y ajustar la clave secreta .
- Ejecutar  para instalar dependencias,  para aplicar esquemas relacionales y  para iniciar el servidor REST.

### 2. Compilar e Instalar la Aplicación Móvil (Android)

- Abrir el proyecto en la carpeta  en Android Studio.
- Ejecutar el script  para colocar el modelo acústico  en la carpeta de assets.
- Compilar la variante de depuración mediante .

---

## 📂 Estructura del Repositorio



---

## 🧪 Pruebas y Validación de Calidad

El proyecto cuenta con suites de pruebas en múltiples capas:

- **Backend (Node.js 24 + TypeScript):** Verificación de sintaxis, compilación TypeScript, pruebas unitarias de controladores y prueba de integración sobre base de datos PostgreSQL 16 aislada ().
- **Móvil (Android Kotlin):** Pruebas unitarias JVM () que verifican la extracción de entidades de voz (unidades dialectales como cuartilla, montos en Bolivianos), clasificación de intenciones y derivación de PIN con PBKDF2.
- **Integración Continua (CI/CD):** Pipeline automatizado en GitHub Actions () que valida el backend en cada integración.

---

## 🎨 Diseños UX/UI y Prototipado en Figma

El prototipo navegable y el sistema de diseño completo (arquetipos de usuario, componentes con variantes y estados de UI: Vacío, Carga, Error y Éxito) están especificados en el documento de Requisitos y UX.

---

## 👥 Equipo del Proyecto (UAB 2026-2)

| Integrante | Rol en el Proyecto | Especialidad / Contribución |
| :--- | :--- | :--- |
| **Eva Lizeth Chino Quispe** | Líder de Proyecto | Gestión de Requisitos, Alcance y QA Documental |
| **Christian Gonzales Mamani** | Ingeniero de IA | Pipeline Conversacional Edge AI & Vosk Integration |
| **Marializ Mamani Quispe** | Desarrolladora Backend | API REST Node.js, Seguridad JWT & PostgreSQL 16 |
| **Kevin Jhonathan Rocha Rojas** | Diseñador UX/UI | Prototipado Interactivo Figma & Sistema de Diseño |
| **Luis Gerardo Lazo Bustamante** | Arquitecto de Software | Arquitectura C4, Tácticas ATAM & QA Técnico |

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**. Consulta el archivo  para obtener más información.
