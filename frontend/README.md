# IA Vinto — Frontend web (React + Vite + Tailwind)

Frontend del agente conversacional IA Vinto, con el estilo del prototipo Figma (fondo oscuro, acento verde #00E676, Inter y Montserrat) y el enfoque del perfil: **usar las herramientas del celular por voz**, sin funciones de ventas.

## Qué hace

| Pantalla | Función |
|---|---|
| Bienvenida | Entrada sin cuenta, ingreso o registro |
| Agente | Micrófono (Web Speech API) o texto → intención → confirmación → acción |
| Contactos | CRUD de contactos frecuentes («llama a mi hermana»); importa de la agenda en Chrome Android |
| Guías | Las 8 guías paso a paso del perfil, con lectura en voz alta |
| Historial | Órdenes con intención, confianza y estado; respaldo en `POST /api/v1/logs/comandos` |
| Ajustes | Idioma de voz, respuesta hablada, recordatorios, estado del servidor y sesión |

Intenciones (mismas reglas que `IntentClassifier.kt`): llamar, enviar mensaje, leer texto, alarma, abrir app, hora, linterna, ajustes de red, guía y ayuda. Si la confianza del reconocimiento es menor a 0,70 (umbral de `CloudAsrFallback.kt`), siempre pide confirmación.

## Ejecutar en la computadora

```bash
cd frontend
npm install
npm run dev          # https://localhost:5173
npm test             # 23 pruebas del clasificador y del extractor
```

## Usarlo desde el celular (misma red wifi)

1. En la computadora: `npm run celular`. Vite muestra una línea `Network: https://192.168.x.x:5173`.
2. En el celular, abre **Chrome** y entra a esa dirección (con **https**).
3. Aparece «La conexión no es privada» porque el certificado es de desarrollo: toca **Configuración avanzada → Continuar**.
4. Cuando lo pida, permite el **micrófono**. Para la linterna, permite la **cámara**.
5. Opcional: menú de Chrome → **Agregar a pantalla principal** para usarla como app.

Si Windows pregunta por el firewall al iniciar, permite el acceso en **redes privadas**.

## Conectar con el backend

El frontend llama a `/api`, y Vite lo reenvía a `http://localhost:3000` (backend Express). Para pasar por el gateway del prototipo de Arquitectura:

```bash
VITE_API_TARGET=http://localhost:8080 npm run celular      # Linux/macOS
set VITE_API_TARGET=http://localhost:8080&& npm run celular # Windows (cmd)
```

Sin backend, el agente, los contactos, las guías y el historial funcionan igual (todo se guarda en el teléfono).

## Límites conocidos (navegador)

- El reconocimiento de voz de Chrome necesita internet y no reconoce quechua. El reconocimiento sin conexión y en quechua es de la app Android (Vosk + MMS).
- El navegador no puede crear alarmas del sistema: crea recordatorios que suenan mientras la app está abierta.
- La linterna solo funciona en navegadores que permiten controlar el flash (Chrome Android en la mayoría de equipos).
- `POST /api/v1/logs/comandos` existe en `backend/src/routes/logs.routes.ts`, pero `app.ts` no monta ese router: hasta que se monte, el respaldo responde 404 y las órdenes quedan guardadas en el teléfono.
