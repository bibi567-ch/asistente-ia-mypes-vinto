# Mapa de presentación

Este archivo es la puerta de entrada. El resto del repositorio se lee desde aquí.

**Nombre vigente:** prototipo de asistente conversacional offline para registrar una operación y consultar el saldo local. No es un sistema de almacén. No lleva compras, proveedores, lotes, costos ni facturación.

**Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas, Luis Gerardo Lazo Bustamante.

## Qué abrir en cada materia

| Materia | Documento que se defiende | Qué no decir |
|---|---|---|
| Investigación I | [Perfil final](perfil/Perfil_final.md) y [Cronograma](Cronograma_final.md) | No prometer aimara, predicción, Trust Score, lector de barras ni impresora. Eso no está en el alcance. |
| Arquitectura de Software | [SAD](03_SAD_Arquitectura.md) y [diagramas C1/C2](../diagrams/README.md) | El código actual es Node.js, Express y PostgreSQL. El SAD v1.1 habla de Spring. Se presenta como versión entregada, no como el código de hoy. |
| Tecnologías en Internet | [Decisiones de stack](04_Decisiones_Stack.md), [plan de API](11_Plan_Implementacion_y_API.md) y `backend/` | PostgreSQL no es MongoDB. El registro de la operación aún no es idempotente: esa ruta no existe. |

## Estado real (no inflar)

Implementado en `backend/`: login de propietario, catálogo de un negocio, migraciones, pruebas.

No implementado: aplicación Android, registro de la operación, descuento del saldo, cola offline, voz, quechua.

Figma es diseño. No sustituye el APK.

## Dónde está lo viejo

[docs/archive/](archive/README.md) guarda borradores anteriores. No se presentan. No se borran de la historia de Git: sirven si un docente pide el rastro de una decisión.

## Orden de lectura

1. Este mapa.
2. [Nota de nombre](Nota_de_nombre.md).
3. El documento de la materia que toca defender.
4. El código, solo para mostrar lo que ya corre.
