# 1. Introducción y Contexto del Proyecto

## 1.1 Antecedentes
El municipio de Vinto, ubicado en la provincia de Quillacollo del departamento de Cochabamba, ha experimentado un acelerado crecimiento comercial en los últimos años. La Avenida 6 de Agosto, la Plaza Principal y los mercados locales concentran una alta densidad de microempresas familiares: tiendas de abarrotes, ferreterías, farmacias, papelerías y pequeños restaurantes. Estos negocios operan con márgenes de ganancia reducidos y una alta dependencia del flujo de caja diario.

La mayoría de estos comercios carece de infraestructura robusta. Se adopta masivamente el uso de teléfonos inteligentes de gama de entrada (menos de 2 GB de RAM, Android 8 o anteriores) [1]. La conectividad a Internet es intermitente y costosa [2]. Los sistemas de gestión tradicionales son rechazados por su costo, incompatibilidad de hardware y la baja alfabetización digital de los usuarios [3][4][5][6][7][8].

Avances recientes en IA en el dispositivo (on-device AI) permiten ejecutar modelos de PLN como Gemma 3n y Vosk (offline español) en dispositivos de baja gama, haciendo viable una solución conversacional offline [9][10][11][12].

## 1.2 Situación Problemática
Con base en el análisis, se identifican las siguientes relaciones causa-efecto:
- **Causa:** Baja alfabetización digital de comerciantes para operar interfaces gráficas tradicionales (GUI).
  **Efecto:** Abandono temprano de sistemas de gestión, manteniendo procesos manuales e ineficientes.
- **Causa:** Limitada RAM (< 2 GB) de dispositivos Android de gama baja.
  **Efecto:** Incompatibilidad con apps modernas, saturación y rechazo a la tecnología.
- **Causa:** Conectividad intermitente a internet.
  **Efecto:** Imposibilidad de usar sistemas basados en la nube, provocando pérdida de registros.
- **Causa:** Software comercial actual diseñado solo para GUI y hardware potente.
  **Efecto:** Exclusión tecnológica de las MYPES de Vinto.

## 1.3 Formulación del Problema
«La baja alfabetización digital de los comerciantes del centro de Vinto, sumada a la limitada capacidad de memoria RAM de sus dispositivos Android de gama baja y a la conectividad intermitente, provoca el rechazo y abandono de los sistemas de gestión comercial basados en interfaces gráficas tradicionales, debido a la inexistencia de una arquitectura de software offline-first que se adapte a estas restricciones técnicas y permita la interacción mediante lenguaje natural, perpetuando procesos manuales ineficientes, errores en el cálculo de ganancias y falta de trazabilidad financiera, lo que limita el crecimiento y la sostenibilidad de las microempresas de la zona.»

## 1.4 Identificación de Stakeholders (Interesados)
| Rol / Actor | Descripción | Responsabilidad / Interés en el sistema |
| :--- | :--- | :--- |
| **Dueños de MYPES (Usuarios Finales)** | Comerciantes del centro de Vinto. | Interés principal: Controlar su negocio de forma fácil y rápida sin necesidad de internet constante. |
| **Proveedores / Distribuidores** | Empresas que surten inventario a las MYPES. | Interés indirecto: Podrían beneficiarse a futuro de reportes de escasez de stock. |
| **Equipo de Desarrollo** | Estudiantes de Ing. de Sistemas. | Diseñar, desarrollar, probar y desplegar la solución arquitectónica y el diseño UX/UI. |
| **Docentes / Evaluadores** | Tribunales y catedráticos de la universidad. | Evaluar el cumplimiento técnico, metodológico y arquitectónico del proyecto de grado. |

## 1.5 Alcance del Sistema
**Incluido en el alcance:**
*   Interfaz conversacional (voz a texto) utilizando NLP en el dispositivo para registrar ventas y consultas de stock.
*   Gestión de inventario y ventas con base de datos local (Offline-first).
*   Sincronización en segundo plano de datos locales con la nube cuando haya conexión a internet.
*   Panel básico (Dashboard) responsivo (Desktop/Mobile) para visualización de métricas.

**Excluido del alcance:**
*   Integración directa con el sistema de impuestos nacionales (SIAT).
*   Pasarelas de pago digitales o pago con tarjetas de crédito.
*   Gestión avanzada de recursos humanos o nóminas.
*   Hardware específico (se utilizará el dispositivo móvil actual del usuario).

## 1.6 Referencias Bibliográficas
[1] Instituto Nacional de Estadística (INE), "Encuesta de Hogares 2023: Acceso a Tecnologías de la Información y Comunicación," La Paz, Bolivia, 2024.
[2] Autoridad de Regulación y Fiscalización de Telecomunicaciones y Transportes (ATT), "Informe de Cobertura y Calidad de Servicios de Internet en Bolivia," Cochabamba, Bolivia, 2024.
[3] M. González, "Estudio de usabilidad de sistemas ERP en MYPES de la región andina," Rev. Latinoam. Ing. Softw., vol. 12, no. 1, pp. 45-62, 2024.
[4] N. P. Al-Bana et al., "Multidimensional Usability Analysis of a Local Store Mobile Retail Application Prototype," Int. J. Inf. Comput., vol. 8, no. 2, 2026.
[5] I. Sommerville, Ingeniería de Software, 10ª ed. Pearson Education, 2022.
[6] A. R. Kurniawan et al., "Development and Usability Evaluation of a Web-Based POS para Hardware Stores," J. Res. Innov., vol. 8, no. 2, 2026.
[7] MultiCont, "Sistema de Gestión Comercial MultiCont," 2024.
[8] J. Nielsen, Usability Engineering. Academic Press, 1993.
[9] R. Sharma and P. Kumar, "Conversational Interfaces for Low-Literacy Users," IEEE Access, 2024.
[10] L. Chen et al., "Challenges of Cloud-Based NLP in Intermittent Connectivity Environments," IEEE Internet Things J., 2024.
[11] Google AI Edge, "Gemma 3n: On-Device Language Model for Android," 2026.
[12] Vosk, "Vosk Speech Recognition Toolkit: Offline Models for Spanish," 2026.
