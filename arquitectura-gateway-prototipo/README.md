# Prototipo API Gateway + Circuit Breaker — IA Vinto (ARQ-643)

Prototipo funcional del patrón **API Gateway con Circuit Breaker** para el agente conversacional IA Vinto (cliente Android Kotlin, agente Yapay). El gateway es el punto único de entrada del teléfono hacia la nube y protege la llamada al **reconocimiento de voz en línea** (servidor MMS): si ese servicio falla o se pone lento, el circuito se abre y el gateway responde al instante con `fallback: "vosk_local"`, para que el teléfono use la transcripción local de Vosk.

## Servicios

| Servicio | Puerto | Archivo | Función |
|---|---|---|---|
| Gateway | 8080 | `src/gateway.ts` | Enruta, valida JWT, limita tasa, Circuit Breaker por servicio (opossum) |
| Auth (simulado) | 3001 | `src/authMock.ts` | Emite JWT HS256 de 15 min |
| Sync (simulado) | 4000 | `src/syncMock.ts` | Recibe lotes Outbox de forma idempotente (`operationId`) |
| IA (simulado) | 5000 | `src/iaMock.ts` | Representa el servidor MMS; modos `normal`, `lento`, `caido`, `aleatorio` |

## Ejecutar

Requisitos: Node.js 20 o superior.

```bash
npm install
npm test        # 7 pruebas automáticas del patrón
npm run demo    # demostración completa en un solo comando (fases A, B, C y D)
```

Por separado, en cuatro terminales: `npm run auth`, `npm run sync`, `npm run ia`, `npm start`.
Con Docker: `docker compose up --build`.

## Probar a mano (curl)

```bash
# 1. Iniciar sesión
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/login -H 'content-type: application/json' \
  -d '{"usuario":"comerciante","password":"vinto2026"}' | node -pe 'JSON.parse(require("fs").readFileSync(0)).token')

# 2. Transcripción en línea (circuito cerrado)
curl -i -X POST localhost:8080/api/v1/asr/transcribe -H "authorization: Bearer $TOKEN" \
  -H 'content-type: application/json' -d '{"textoSimulado":"llama a mi hermana","idioma":"quh"}'

# 3. Simular caída del servidor de IA y repetir el paso 2 varias veces
curl -X POST localhost:5000/admin/modo -H 'content-type: application/json' -d '{"modo":"caido"}'

# 4. Ver el estado de los circuitos (rol admin)
ADMIN=$(curl -s -X POST localhost:8080/api/v1/auth/login -H 'content-type: application/json' \
  -d '{"usuario":"admin","password":"admin2026"}' | node -pe 'JSON.parse(require("fs").readFileSync(0)).token')
curl localhost:8080/gateway/estado -H "authorization: Bearer $ADMIN"
```

## Parámetros del Circuit Breaker (variables de entorno)

| Variable | Valor por defecto | Significado |
|---|---|---|
| `CB_TIMEOUT_MS` | 2000 | Una llamada más lenta cuenta como fallo |
| `CB_ERROR_PCT` | 50 | Porcentaje de fallos en la ventana para abrir |
| `CB_VOLUME` | 4 | Mínimo de llamadas antes de evaluar |
| `CB_RESET_MS` | 10000 | Tiempo abierto antes de la llamada de prueba (semiabierto) |
| `CB_WINDOW_MS` | 10000 | Ventana estadística |

Los usuarios `comerciante/vinto2026` y `admin/admin2026` son de demostración y solo existen en el servicio simulado.

## Panel de arquitectura en vivo (recomendado para la defensa)

1. `npm run servicios` (levanta gateway, auth, sync, IA y el panel).
2. Abrir **http://localhost:8090** en el navegador.
3. El panel dibuja los contenedores (C4 nivel 2) y colorea cada Circuit Breaker según su estado real
   (CERRADO verde, ABIERTO rojo, SEMIABIERTO ámbar). Cada botón ejecuta un escenario de calidad (ID-01 a ID-06)
   contra el gateway real y la tabla dice qué pieza de la arquitectura actuó y en qué línea del código está.

El panel es un cliente más, como la app Android: está en `src/panel.ts` y `public/index.html`, fuera del gateway.

## Demostración desde el celular (app Android)

1. En la PC: `npm run servicios` (deja corriendo gateway :8080, auth :3001, sync :4000 e IA :5000).
2. Celular conectado por cable: en la carpeta `mobile`, `.\instalar_en_celular.ps1 -ConGateway -SoloAbrir`
   (hace `adb reverse tcp:8080 tcp:8080` y `adb reverse tcp:5000 tcp:5000`).
3. En la app: **Más › Nube protegida** → Conectar (usuario `comerciante`, contraseña `vinto2026`).
4. «Enviar 1»: HTTP 200, circuito CERRADO, ~150 ms. Tocar «Caído» y luego «Enviar 10 seguidas»:
   las primeras fallan (~150 ms) y, al abrirse el circuito, el resto responde 503 en ~1 ms con `fallback: vosk_local`.
5. Tocar «Normal», esperar 10 s y «Enviar 1»: SEMIABIERTO → CERRADO.
6. Con la sesión conectada, cada orden en la pestaña **IA Vinto** también consulta la nube a través del gateway y muestra
   «☁ Nube: …» o «☁ Nube no disponible … Seguí con Vosk local».

Sin cable: escribir en la app la IP de la PC (`http://192.168.x.x:8080/`) y permitir Node.js en el firewall de Windows (redes privadas).
