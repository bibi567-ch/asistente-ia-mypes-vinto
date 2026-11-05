# 📱 ARQ — Arquitectura de Software
**Para revisión del equipo (WhatsApp)**  
**Rama:** `arq-tec` · **Repo:** https://github.com/bibi567-ch/asistente-ia-mypes-vinto  
**No tocar `main` directo** · **Perfil de investigación = fuera del repo**

---

## 1. Qué pide la evaluación (guía de patrones)

| Criterio | Peso | ¿Lo tenemos? | Qué falta para cerrar |
|---|---:|---|---|
| Justificación del patrón + atributos de calidad | 25% | Borrador listo | Revisar y memorizar para la defensa |
| Diagrama **C4 Nivel 2** | 25% | Mermaid borrador | Versión final limpia + en `diagrams/` |
| **Prototipo ejecutable** del patrón | 35% | API batch idempotente sí | Demo en vivo + evidencia; móvil e2e si alcanza |
| Documento + defensa | 15% | Informe 5 secciones en repo | Ensayar respuestas y trade-offs |

**Patrón del equipo (uno solo, no cambiar a mitad):**  
→ **Outbox** (venta offline en el teléfono + envío después + `operationId` para no duplicar).

---

## 2. Por qué Outbox (para que todos lo digan igual)

- En Vinto **no hay Internet estable** en el momento de atender.  
- Si solo se guardara en el servidor, se pierde la venta.  
- Outbox = guardar **primero en el teléfono**, encolar, y cuando hay red mandar el lote.  
- Si se reenvía el mismo ID → el servidor responde **DUPLICADA** (no hay venta doble).

**2 escenarios de calidad (tabla formal)**

| ID | Atributo | Qué pasa | Medida |
|---|---|---|---|
| AQ-OUT-01 | Disponibilidad | Venta **sin red** | Queda guardada local y encolada; sobrevive cerrar la app |
| AQ-OUT-02 | Confiabilidad | Mismo `operationId` dos veces | Segunda = DUPLICADA; no duplica en BD |

---

## 3. Qué tiene que hacer cada uno (ARQ)

### 👨 Luis (C4 / QA técnico) — dueño del patrón en papel
- [ ] C4 Nivel 2 final (móvil + Outbox + API + PostgreSQL)
- [ ] Revisar informe ARQ de 5 secciones
- [ ] **SAD v2** como archivo **nuevo** (Node/Express; **no borrar** SAD v1.1 con Spring)
- [ ] Checklist de defensa: qué se demostra vs qué es solo diseño

### 👩 Marializ (backend) — dueña de la demo del patrón
- [ ] Demo de **3 minutos**: login → `POST /transacciones/batch` → repetir mismo ID → ver DUPLICADA
- [ ] Guardar request/response ficticios en `evidence/EV-SYNC-01`
- [ ] Asegurar que el endpoint y el servicio de sync respondan estable en local

### 👨 Kevin (UI)
- [ ] En la app: estado visible **“pendiente de sync”** / offline (según Figma)
- [ ] No prometer sync completa en teléfono si no está probada
- [ ] Captura de pantalla del flujo offline si ya existe en Compose

### 👩 Eva (documentación / calidad)
- [ ] Unir informe ARQ + evidencias + fechas
- [ ] Verificar que nadie presente Spring Boot como stack **actual**
- [ ] Coordinar ensayo de defensa con Luis y Marializ

### 👨 Christian (PLN)
- [ ] El patrón Outbox **no depende de la voz**
- [ ] En defensa: si preguntan por Vosk, decir límites reales; no mezclar con Outbox

---

## 4. Producto / Figma — qué falta del lado arquitectura

El Figma muestra el **flujo de usuario**. Para ARQ importa que la arquitectura lo soporte:

| Pantalla / flujo Figma | Qué implica en arquitectura | Estado |
|---|---|---|
| Login / PIN | Auth local ≠ JWT del servidor | En código; documentar la diferencia |
| Dashboard | Datos locales primero | Parcial |
| Inventario / alta producto | Room + opcional API | Alta local reportada |
| Venta | Escritura local + fila Outbox | Código parcial; demo UI a completar |
| Sync | Worker + batch API | API sí; e2e móvil pendiente |

**Prioridad para “terminar” el producto alineado a Figma (orden):**
1. Inventario y venta **por pantalla** offline (sin voz).  
2. Badge/estado “pendiente de sincronizar”.  
3. Demo API Outbox (obligatoria para nota del patrón).  
4. Voz solo si 1–3 ya funcionan.

---

## 5. Orden para “terminar” esta semana (ARQ)

1. **Luis** cierra C4 + arranca SAD v2.  
2. **Marializ** ensaya demo batch idempotente y la graba/archiva.  
3. **Kevin** alinea UI crítica (inventario/venta/estado offline) con Figma.  
4. **Eva** arma el paquete de defensa (informe + evidence).  
5. Ensayo grupal: 1) problema offline 2) por qué Outbox 3) demo API 4) trade-offs.

---

## 6. Mensaje corto para el grupo

> ARQ: patrón **Outbox**, no cambiamos a Gateway ni Kafka.  
> La nota fuerte es la **demo ejecutable** (batch duplicado = DUPLICADA).  
> SAD v1.1 se **conserva**; Luis escribe **SAD v2** aparte con Node.  
> Figma no es arquitectura: hay que mostrar código + prueba.  
> Rama `arq-tec`.

---

## 7. Trade-offs (para la oral)

| Gana | Paga |
|---|---|
| Vender sin Internet | Más lógica de cola y reintentos |
| No duplicar ventas (UUID) | El móvil debe guardar bien el `operationId` |
| Sin Kafka/Rabbit | La “cola” vive en el teléfono |

---

*Archivo: `docs/entregas/WHATSAPP_ARQ_Revision_Equipo.md` · rama `arq-tec`*
