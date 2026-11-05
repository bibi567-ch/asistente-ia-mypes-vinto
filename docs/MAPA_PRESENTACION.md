# Mapa del trabajo y las tres materias

Para la explicación del stack real, la justificación de PostgreSQL/Node.js y el reparto de tareas por integrante, consultar el [plan de segunda entrega](14_Segunda_Entrega_Tareas_por_Materia.md).

**Corte:** rama `docs/arq-tec-cumplimiento-sin-perfil`. La [Matriz de cumplimiento y evidencias](13_Matriz_Cumplimiento_Tres_Materias_2026-2.md) desglosa los criterios de los programas analíticos ARQ-643, TEC-644 e INV-645. No predice calificaciones ni sustituye las instrucciones docentes.

El proyecto es un prototipo de asistente conversacional offline, configurable por rubro, para registrar ventas y consultar el saldo local de existencias en MYPES del centro de Vinto. No es un sistema completo de almacén.

**Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.

> **Perfil de Investigación:** documento **externo** al repositorio. Este mapa solo describe qué aporta el **código y la documentación técnica** del repo a cada materia.

## Cómo se reparte entre las materias

| Materia | Aporte del repositorio | Qué se debe evidenciar |
|---|---|---|
| Investigación I (INV-645) | Alcance técnico, cronograma de entregas del semestre y trazabilidad de decisiones. El **perfil escrito** se entrega por fuera del repo. | Perfil revisado, ensayo/estado del arte, referencias verificables, prácticas y defensa (fuera de este repositorio). |
| Arquitectura de Software (ARQ-643) | [SAD v1.1](03_SAD_Arquitectura.md), decisiones y [diagramas](../diagrams/README.md). | SAD actualizado como versión nueva (v2) alineado a Node.js/Express/PostgreSQL + Android/Room; C4 coherente con código; patrones justificados; escenarios y evaluación de calidad; artículo científico y defensa. La v1.1 conserva decisiones anteriores de Spring Boot. |
| Tecnologías en Internet (TEC-644) | [Backend](../backend/README.md), requisitos/UX y [app Android](../mobile/README.md). | Pruebas de API REST, autenticación, persistencia y evidencias de seguridad/despliegue local. El sílabo incluye web front-end HTML/CSS/JavaScript y MongoDB/Mongoose; Android Compose y PostgreSQL no demuestran esos dos contenidos. Consultar al docente si se requieren prácticas separadas. |

## Estado del producto observado

- La rama contiene código de app Android con Kotlin/Compose, Room, PIN local, inventario y componentes adicionales de NLU, ventas y sincronización.
- La estudiante reportó que instaló la app en un Motorola G60 y guardó un producto de prueba. Falta añadir una evidencia repetible y fechada en `evidence/`.
- La pantalla de inventario revisada permite agregar productos; no ofrece acción de eliminación.
- La existencia de un cliente Retrofit, una cola Outbox y un Worker no demuestra una sincronización completa. La dirección móvil `10.0.2.2` está configurada para emulador.
- El README del backend describe una API local con PostgreSQL, autenticación JWT y rutas para catálogo y lotes. Docker Compose local no equivale a despliegue en nube ni a TLS/HTTPS.
- El modelo Vosk no está incluido en Git; la prueba de voz depende de instalarlo localmente y registrar su resultado.

## Control de estado

Usar las etiquetas **documentado**, **en código**, **demostrado por el equipo**, **pendiente de evidencia** o **brecha de alcance/tecnología**. No reemplazar “pendiente” con “cumplido” por la mera existencia de un archivo. Registrar fecha, commit, entorno, pasos, resultado y límites en cada evidencia.

## Versiones anteriores

`docs/archive/` conserva borradores y versiones históricas. Antes de reutilizar uno, comprobar que coincide con el nombre, alcance, tecnologías y criterios actuales.
