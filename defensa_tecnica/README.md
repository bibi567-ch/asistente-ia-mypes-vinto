# Carpeta de defensa técnica

Esta carpeta está separada del código y de `docs/` porque es una guía personal para preparar y explicar la defensa: qué está implementado, cómo se demuestra, qué decisiones se tomaron y qué límites siguen abiertos.

## Documentos

1. [Análisis de fallas y soluciones](01_analisis_fallas_y_soluciones.md)
2. [Guion de demostración y modelado C4](02_guion_demostracion_c4.md)
3. [Matriz de evidencia y criterios de cierre](03_matriz_evidencia.md)
4. [Preguntas y respuestas técnicas](04_preguntas_respuestas.md)
5. [Manual completo de arquitectura, Docker y comandos](05_manual_completo_comandos.md)

## Regla de presentación

No decir “funciona” solo porque existe una clase, una captura o una compilación. Para cada afirmación usar esta cadena:

```text
requisito -> componente -> comando/escenario -> resultado observado -> evidencia -> límite
```

Si falta el resultado observado o la evidencia, usar **pendiente**, **bloqueado** o **demostración de código**, según corresponda.

## Preparación mínima antes de la defensa

- Llevar un APK debug reproducible y un dispositivo/emulador identificado.
- Llevar PostgreSQL o Docker operativo y una base de demostración con datos ficticios.
- Ejecutar las pruebas justo antes de la presentación y guardar los resultados.
- No usar contraseñas, tokens, teléfonos ni datos reales.
- Mostrar el ASR simulado como simulado; no presentarlo como transcripción cloud real.
