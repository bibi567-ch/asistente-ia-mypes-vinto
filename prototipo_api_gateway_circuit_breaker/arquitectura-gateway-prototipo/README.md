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
