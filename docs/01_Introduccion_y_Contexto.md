# 1. Introducción y contexto

## 1.1 Introducción

El proyecto propone un **Asistente Conversacional Offline con Procesamiento de Lenguaje Natural en el dispositivo** para apoyar la gestión de ventas e inventario en MYPES del centro de Vinto, Cochabamba.

La solución se plantea como una aplicación móvil Android con enfoque offline-first, entrada por teclado y voz, persistencia local y sincronización diferida. En esta etapa se documentan el problema, los requisitos, la arquitectura y la experiencia de usuario. La implementación, las pruebas y las métricas definitivas quedan sujetas a validación posterior.

## 1.2 Contexto del problema

Las micro y pequeñas empresas pueden desarrollar sus actividades con conectividad intermitente, dispositivos de recursos limitados y poco tiempo disponible para tareas administrativas. En ese contexto, el registro manual de ventas e inventario puede dificultar la actualización de existencias y la consulta de información del negocio.

### Problema principal

Los comerciantes necesitan registrar y consultar operaciones comerciales de manera rápida y sencilla, incluso cuando no existe conexión a Internet, sin depender de interfaces complejas o de procesos manuales propensos a errores.

### Causas identificadas

- Procesos de registro manual.
- Interfaces con demasiados pasos para tareas frecuentes.
- Conectividad intermitente.
- Dificultad para introducir datos mientras se atiende al cliente.
- Diferentes niveles de experiencia digital.

### Consecuencias posibles

- Registros incompletos o duplicados.
- Dificultad para conocer el stock disponible.
- Pérdida de tiempo administrativo.
- Menor trazabilidad de las operaciones.

## 1.3 Oportunidad de solución

Un diseño offline-first combinado con una interfaz conversacional puede reducir la dependencia de la conectividad y simplificar tareas frecuentes. El procesamiento local de voz y de intención se considera una alternativa tecnológica que deberá evaluarse mediante pruebas de precisión, latencia, consumo y experiencia de usuario.

## 1.4 Stakeholders

| Stakeholder | Rol | Interés |
|---|---|---|
| Comerciante o propietario de MYPE | Usuario principal | Registrar ventas, consultar inventario y revisar resúmenes. |
| Personal de atención | Usuario operativo | Ejecutar operaciones rápidas durante la atención. |
| Administrador, si se incluye en el alcance final | Usuario autorizado | Revisar incidencias y gestionar aspectos administrativos. |
| Equipo de proyecto | Diseñador e implementador | Producir requisitos, arquitectura, prototipo y validaciones. |
| Docentes/evaluadores | Revisores | Evaluar la calidad técnica y el cumplimiento de la rúbrica. |

## 1.5 Alcance

### Incluido en la propuesta

- Aplicación móvil Android.
- Registro y consulta de ventas e inventario.
- Entrada por teclado y voz.
- Confirmación antes de operaciones que modifican datos.
- Persistencia local.
- Cola de operaciones pendientes.
- Sincronización diferida cuando exista conectividad.
- Prototipo UX/UI y documentación arquitectónica.

### Excluido de la primera entrega

- Pasarelas de pago.
- Aplicación para iOS o Windows.
- Analítica empresarial avanzada.
- Entrenamiento dinámico en producción.
- Despliegue productivo.
- Afirmaciones de precisión, rendimiento o seguridad sin evidencia de prueba.

## 1.6 Relación con otros documentos

- Requisitos: `docs/02_Requisitos_del_Sistema.md`.
- Historias de usuario y backlog: `docs/03_Historias_Usuario_y_Backlog.md`.
- Arquitectura y UX: `docs/04_Arquitectura_SAD_y_UX.md`.
- SAD: `docs/SAD_Arquitectura_Software.md`.
- Trazabilidad: `docs/05_Matriz_Trazabilidad.md`.
- Auditoría de rúbrica: `docs/06_Auditoria_Rubricas.md`.
