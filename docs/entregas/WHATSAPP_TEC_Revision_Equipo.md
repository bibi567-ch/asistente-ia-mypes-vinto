# 📱 TEC — Tecnologías en Internet
**Para revisión del equipo (WhatsApp)**  
**Rama:** `arq-tec` · **Repo:** https://github.com/bibi567-ch/asistente-ia-mypes-vinto  
**No tocar `main` directo** · **Perfil de investigación = fuera del repo**

---

## 1. Qué pide la evaluación (rúbrica)

| Criterio | Peso | ¿Lo tenemos? | Qué falta para cerrar |
|---|---:|---|---|
| Repo público + estructura + README | 15% | Casi | Confirmar que el repo esté **público** |
| JWT + RBAC | 20% | JWT sí · RBAC débil | Roles claros en middleware o declarar límite |
| MongoDB + Mongoose | 20% | **No** | Justificar PostgreSQL + lab Mongo **o** acuerdo del docente |
| Diagramas C4 + secuencia JWT | 15% | Borrador listo | Revisar y pegar en el informe final |
| API REST + **Swagger** | 15% | API sí · Swagger **no** | Implementar `/api-docs` |
| Informe 9 secciones | 15% | Borrador en repo | Completar evidencias y fechas |

**Stack que pide la guía de 2ª entrega:** React + Vite + Tailwind · Node/Express · MongoDB · JWT · Swagger.  
**Stack real del producto:** Android + Node/Express + PostgreSQL.

---

## 2. Frontend (Figma → producto)

**Diseño de referencia**
- Prototipo: https://snake-dijon-92194767.figma.site/
- Sistema de diseño: verde `#2E7D32`, rojo cancelar `#F44336`, botones grandes, flujo Login → Dashboard → Inventario

**Dos frentes (no confundir)**

| Frente | Para qué | Estado |
|---|---|---|
| **A. App Android (producto real offline)** | Lo que usa el comerciante sin red | Login PIN, onboarding, dashboard, inventario **en código**; falta pulir UI según Figma, eliminar producto, venta completa, evidencias |
| **B. Front web React (criterio TEC)** | Cumplir guía de 2ª entrega | **Hay que armarlo** (login + catálogo contra la API) si el docente no acepta solo Android |

**Decisión del equipo (elegir una y escribirla):**
1. Hacer **React mínimo** (login + productos) **y** seguir puliendo Android, o  
2. Pedir al docente **por escrito** evaluar Android Compose en lugar de React.

---

## 3. Qué tiene que hacer cada uno (TEC)

### 👩 Eva (líder / documentación)
- [ ] Verificar repo **público** y enlace en la carátula del informe
- [ ] Preguntar al docente: ¿aceptan PostgreSQL + Android o exigen Mongo + React?
- [ ] Guardar la respuesta por escrito
- [ ] Cerrar informe TEC (9 secciones) + capturas en `evidence/`
- [ ] Coordinar que nadie diga “tenemos Mongo/Swagger/React” si no está en el código

### 👩 Marializ (backend / seguridad)
- [ ] **Swagger OpenAPI 3** en la API (`/api-docs`)
- [ ] Probar y documentar: login JWT, productos, batch
- [ ] Justificación PostgreSQL vs Mongo (ya hay texto base)
- [ ] Si el docente exige Mongo → lab pequeño Mongoose **aparte**
- [ ] Revisar `.env` / `.gitignore` (cero secretos en Git)

### 👨 Luis (arquitectura / QA técnico)
- [ ] C4 contenedores final (API + móvil + BD)
- [ ] Diagrama de secuencia JWT limpio para el informe TEC
- [ ] Revisar que el README de instalación funcione paso a paso

### 👨 Kevin (UX / frontend)
- [ ] **Prioridad UI:** alinear pantallas Android con Figma (colores, botones, estados vacío/carga/error)
- [ ] Si van por React: SPA Vite + Tailwind (Login, lista productos, alta producto) contra `http://localhost:3000`
- [ ] Capturas anonimizadas de UI real (no solo Figma)
- [ ] No presentar Figma como si fuera la app instalada

### 👨 Christian (PLN / voz)
- [ ] Lista corta: qué hace y qué **no** hace Vosk hoy
- [ ] No bloquear la entrega TEC con voz; voz después de que venta/inventario por pantalla funcionen

---

## 4. Orden para “terminar” esta semana (TEC)

1. **Hoy:** Eva habla con el docente (Mongo/React).  
2. **Marializ:** Swagger + pruebas API.  
3. **Kevin:** pantallas Figma → Android **o** React mínimo (según decisión).  
4. **Luis:** diagramas al informe.  
5. **Eva:** cierra PDF/MD + evidence.  
6. Ensayo de 5 minutos: health → login → crear producto → listar.

---

## 5. Mensaje corto para el grupo

> TEC: no digan que tenemos Mongo ni Swagger hasta que esté en el repo.  
> Frontend = Figma es diseño; hay que **implementar** en Android y, si el docente lo pide, React.  
> Trabajar en rama `arq-tec`. Eva coordina la respuesta del docente.

---

*Archivo: `docs/entregas/WHATSAPP_TEC_Revision_Equipo.md` · rama `arq-tec`*
