# Qué actualizar en GitHub, y qué hacer ya en las otras dos materias

Repositorio: https://github.com/bibi567-ch/asistente-ia-mypes-vinto  
Revisión: 13/10/2026. Rama `main`. Último push: 11/10/2026.

El perfil que se imprime es `Perfil_final.md`. El calendario es `Cronograma_final.md`. Este archivo dice qué tocar en GitHub y cómo pararse en Arquitectura y en Tecnologías en Internet.

---

## 1. Settings, hoy

Entré a la configuración del repositorio por la API. Esto es lo que hay:

| Dato | Estado | Qué hacer |
|---|---|---|
| Visibilidad | Público | Puede quedarse público. No hay `.env` commiteado y el `.gitignore` lo excluye. No suban audios, padrón de la ACCV ni datos de comerciantes |
| Descripción | Vacía | Péguenla. Texto listo abajo |
| Sitio web | Vacío | Déjenlo vacío. No hay despliegue |
| Temas | Ninguno | Opcional. Si ponen temas, usen `android`, `offline-first`, `sqlite`. No pongan `inventario` ni `erp` |
| Colaboradores con escritura | bibi567-ch (admin), christian68522, 16jhona, lichi-12, adamarMM | El equipo ya está. No hace falta abrir el repo a más gente para la defensa: con el enlace público alcanza |
| Issues y wiki | Activos | No los usen como segunda documentación. La fuente es `docs/` |

Descripción para pegar en **Settings → General → Description**:

```text
Prototipo de asistente conversacional offline para registrar ventas y consultar el saldo local. UAB, Ingeniería en Sistemas, 2026-2. No es un sistema de almacén.
```

Esa descripción no dice “ventas e inventario”. El nombre largo del perfil no cabe bien en el campo About; el nombre largo vive en el perfil.

---

## 2. El problema de nombre, con archivos

El título vivo del alcance sigue siendo el de su compañero. Está en `docs/01_Proyecto_y_Alcance.md`, línea del título: “gestión automatizada de ventas e inventario”. El SAD v1.1 lo repite en la portada. Los casos de uso dicen “Consultar inventario”. El requisito RF-014 pide registrar compras a proveedores. Eso contradice el límite del perfil: no hay compras ni proveedores.

No reescriban en silencio el SAD v1.1. Ese archivo dice, él mismo, que es la versión ya presentada con Spring Boot. Borrarlo parece que la entrega anterior nunca existió. Se conserva y se agrega una nota.

### Qué sí se cambia en `main`

| Archivo | Acción |
|---|---|
| `README.md` | Cambiar el título y el párrafo de estado. Texto listo en la sección 3 |
| `docs/01_Proyecto_y_Alcance.md` | Reemplazar el título y el MVP. Texto listo en la sección 4 |
| `docs/perfil/Perfil_final.md` | Archivo nuevo. Es el perfil que se defiende el 22/10 |
| `docs/Cronograma_final.md` | Archivo nuevo. Reemplaza, como calendario vigente, las 8 semanas y las 18 semanas viejas |
| `docs/Nota_de_nombre.md` | Archivo nuevo. Una página para Arquitectura y para TI. Texto listo en la sección 5 |

### Qué no se toca esta semana

| Archivo | Por qué |
|---|---|
| `docs/03_SAD_Arquitectura.md` | Es la v1.1 entregada. Se cita como antecedente. El SAD v2 se escribe después del examen del 19/10, con el nombre nuevo y con Node |
| `docs/archive/` | Historial. No se reescribe |
| `backend/` | No se le cambia el dominio para el examen del 14/10 |
| Figma | No es la aplicación. No se presenta como tal |

### Palabras que salen de los documentos vigentes

No se escribe “ventas e inventario”, ni “gestión automatizada de ventas e inventario”, ni “módulo de inventario”, ni “compra a proveedores”.

Se escribe:

- registrar una venta
- consultar el saldo local
- descontar lo vendido del saldo guardado en el teléfono
- plantilla del rubro

“Existencias” puede quedar, porque está en el título del perfil. “Inventario”, como nombre del sistema, no.

---

## 3. README: párrafos para reemplazar

Sustituyan el título y la sección “Estado del repositorio” por esto.

```markdown
# Asistente conversacional offline para MYPES de Vinto

Prototipo para registrar ventas y consultar el saldo local de existencias, sin conexión en el momento de la atención. Universidad Adventista de Bolivia, Ingeniería en Sistemas, gestión 2026-2.

El trabajo no es un sistema de almacén: no lleva compras, proveedores, lotes, costos ni facturación.

## Estado del repositorio

Hay una API local en Node.js, TypeScript, Express y PostgreSQL. Tiene migraciones, alta de un propietario, inicio de sesión y alta y consulta de productos de un solo negocio.

Todavía no hay aplicación Android. Tampoco está el registro de la venta, el descuento del saldo ni el envío de la cola local. No se presenta este repositorio como un prototipo usable por un comerciante.

La voz queda para cuando el registro por pantalla se guarde sin red y siga ahí al reabrir la aplicación.
```

El resto del README (cómo levantar Postgres, la tabla de documentos, el equipo) puede quedar. En el párrafo de arquitectura, cambien la frase “fuente autoritativa comercial” si quieren, pero no es urgente. Sí saquen cualquier título que diga inventario.

---

## 4. `docs/01`: identificación y MVP

Reemplacen la identificación y el MVP por esto. El stack Node se queda.

```markdown
# 01 Proyecto y alcance

## Identificación

**Título:** Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe; Christian Gonzales Mamani; Marializ Mamani Quispe; Kevin Jhonathan Rocha Rojas; Luis Gerardo Lazo Bustamante.

## Qué se construye

Un prototipo que permite anotar una venta y consultar cuánto queda, primero en el teléfono. La consolidación en la API ocurre después, cuando hay red, y no es necesaria para atender.

## Recorrido que tiene que funcionar antes que nada

1. Guardar y consultar productos en el teléfono.
2. Anotar una venta de productos ya cargados y confirmar antes de guardar.
3. Cerrar la aplicación, sin red, abrirla y encontrar la misma venta y el mismo saldo.
4. Enviar esa operación a la API y a PostgreSQL cuando vuelva la red.
5. Reintentar con el mismo identificador y no duplicar la venta.
6. Recién después, texto y voz.

Quedan fuera: compras a proveedores, lotes, costos, facturación, voz en quechua, aimara, lector de códigos, impresora y cobro por QR.

El perfil que se defiende está en `docs/perfil/Perfil_final.md`. Si este archivo y el perfil se contradicen, manda el perfil.
```

---

## 5. `docs/Nota_de_nombre.md`: páginenlo en las dos materias

```markdown
# Nota de nombre y de alcance

Fecha: 13/10/2026.

El SAD v1.1 y varios requisitos usaron el nombre “gestión de ventas e inventario”. Ese nombre queda retirado de los documentos vigentes.

El sistema se llama como el perfil de Investigación I: prototipo de asistente conversacional offline para el registro de ventas y la consulta del saldo local de existencias, configurable por tipo de negocio.

Consecuencias para Arquitectura de Software y para Tecnologías en Internet:

- El caso de uso “Consultar inventario” pasa a llamarse “Consultar el saldo”.
- El caso de uso de compras a proveedores (RF-014 en `docs/02_Requisitos_y_UX.md`) queda fuera. No se diseña y no se codifica.
- Descontar la cantidad vendida es parte de registrar la venta. No abre un módulo de almacén.
- El backend vigente es Node.js, TypeScript, Express y PostgreSQL. Spring Boot pertenece solo al SAD v1.1, que se conserva como versión ya entregada.
- PostgreSQL no se presenta como base NoSQL. Si la materia pide MongoDB, el ejercicio se entrega aparte y no guarda las ventas del prototipo.
```

En `docs/02_Requisitos_y_UX.md`, marquen RF-014 como excluido. No lo borren sin dejar la marca: si no, parece que nunca estuvo y el docente que ya lo vio va a preguntar.

En `diagrams/Casos_Uso.puml` y `diagrams/Flujos_Usuario.md`, el renombre se hace en la misma rama del SAD v2, después del 19/10. No hace falta esta noche.

---

## 6. Arquitectura de Software: qué hacer ya

El examen del 19/10 no se gana con código nuevo. Se gana si pueden explicar, sin leer, este escenario:

> El comerciante anota una venta sin señal. Cierra la aplicación. La abre. La venta y el saldo siguen ahí. Cuando vuelve la red, la misma operación llega una sola vez.

Atributo: la disponibilidad del registro local. Estímulo: corte de red y cierre de la app. Respuesta: el saldo no depende del servidor. Medida: cero pérdidas de la operación ya confirmada en el teléfono.

Decisión que tienen que poder defender: primero SQLite, después PostgreSQL. La venta confirmada no se resuelve con “gana el último que escribió”. Se resuelve con un identificador único. Si el mismo identificador vuelve, se acepta una vez.

No digan que el sistema garantiza menos de 500 MB ni el 100 % offline. Eso está escrito en el SAD v1.1 como meta. No está medido.

Para el 16/11 y el 23/11 el paquete es:

1. SAD v2 de pocas páginas, con el nombre de la nota y con Node, no con Spring.
2. C1 y C2 que coincidan con los procesos que existen: API de salud, login y productos. La app Android se dibuja como contenedor en construcción, no como contenedor ya desplegado, mientras `mobile/` no tenga proyecto.
3. Un ADR de una página: se deja Spring porque el repositorio común ya está en Node y porque Tecnologías en Internet trabaja esa plataforma. PostgreSQL se mantiene.
4. La demo del teléfono, si Kevin llegó al formulario. Si no llegó, se muestra la API y se dice en qué paso está la aplicación. No se muestra Figma como si fuera el sistema corriendo.

Docente en la portada del SAD: Ing. Víctor Hugo Sarzuri Flores. Hoy el archivo dice “completar”.

---

## 7. Tecnologías en Internet: qué hacer ya

**14/10, examen.** Repasen con lo que ya programaron, no con un tutorial nuevo:

- `POST /api/v1/auth/login` devuelve un token. Credencial mala: 401. Cuerpo imposible: 400.
- `GET` y `POST /api/v1/products` exigen el token. Sin token no se ve el catálogo de otro negocio.
- El alta repetida del mismo nombre en el mismo negocio responde 409.
- PostgreSQL es relacional. MongoDB guarda documentos. Decir que PostgreSQL “también es NoSQL” está mal.
- Idempotencia: repetir el mismo `POST` de una venta, con el mismo identificador, no debe crear dos ventas. Hoy esa ruta no existe. En el examen se explica el concepto. No se afirma que el código ya lo hace.

**11/11, entrega de backend.** Una sola historia, terminada:

- autenticación, que ya está;
- producto con cantidad disponible, no solo nombre y precio;
- `POST` del registro de la venta, en una transacción, que descuenta esa cantidad;
- si el identificador de la operación ya fue aceptado, la respuesta dice que ya estaba y no inserta otra vez.

No entren compras, fiados completos, reportes ni voz. Si piden Mongoose, un script o una mini API en otra carpeta, con datos de ejemplo, rotulada “ejercicio de la materia”. No dupliquen el catálogo en dos bases.

**25/11, plataforma.** Primero confirmen con la docente, por escrito, si el cliente web es obligatorio. Si lo es, una pantalla: entrar, ver productos, ver las ventas ya enviadas. Escritorio y teléfono del navegador, sin un sistema de administración paralelo. Si no lo es, no lo construyan “por si acaso”: les quita los días del Android.

La aplicación del comerciante sigue siendo Android. La web, si existe, es para la materia, no para el puesto.

---

## 8. Orden de la rama

Una rama, no cinco commits sueltos sobre `main`:

`docs/nombre-y-perfil`

1. README.
2. `docs/01_Proyecto_y_Alcance.md`.
3. `docs/perfil/Perfil_final.md`.
4. `docs/Cronograma_final.md`.
5. `docs/Nota_de_nombre.md`.
6. La marca de RF-014 como excluido.

Eva revisa que ninguna de esas páginas diga “ventas e inventario”. Recién ahí se integra. El backend no entra en esa rama.
