/**
 * Clasificador de intenciones. Port directo de
 * mobile/app/src/main/java/com/mypes/vinto/nlu/IntentClassifier.kt
 * (mismas intenciones, mismas palabras clave y mismo orden de evaluación),
 * para que la web y la app Android entiendan las órdenes igual.
 */
export type Intencion =
  | 'LLAMAR_CONTACTO' | 'ENVIAR_MENSAJE' | 'LEER_TEXTO' | 'CREAR_ALARMA'
  | 'ABRIR_APP' | 'CONSULTAR_HORA' | 'LINTERNA' | 'AJUSTES_RED'
  | 'GUIA_TELEFONO' | 'AYUDA' | 'DESCONOCIDO'

const REGLAS: Array<[Intencion, string[]]> = [
  ['LLAMAR_CONTACTO', ['llama', 'llamar', 'telefonear', 'wawq']],
  ['ENVIAR_MENSAJE', ['envía mensaje', 'enviar mensaje', 'manda mensaje', 'mensaje a']],
  ['LEER_TEXTO', ['lee', 'leer', 'qillqata']],
  ['CREAR_ALARMA', ['alarma', 'despertador', 'recordatorio']],
  ['ABRIR_APP', ['abre', 'abrir', 'apertura', 'kichay']],
  ['CONSULTAR_HORA', ['qué hora', 'que hora', 'hora es']],
  ['LINTERNA', ['linterna', 'flash', "q'anchay"]],
  ['AJUSTES_RED', ['wifi', 'bluetooth', 'datos', 'ajustes de red', 'internet']],
  ['GUIA_TELEFONO', ['guía', 'guia', 'cómo uso', 'como uso', 'ayudame a usar']],
  ['AYUDA', ['ayuda', 'qué puedo', 'que puedo', 'comandos']],
]

export function clasificar(texto: string): Intencion {
  const normalizado = texto.toLowerCase().trim()
  for (const [intencion, expresiones] of REGLAS) {
    if (expresiones.some((e) => normalizado.includes(e))) return intencion
  }
  return 'DESCONOCIDO'
}

/** Port de ConfirmationPolicy.kt: acciones que cambian algo piden confirmación. */
const CON_CONFIRMACION = new Set<Intencion>([
  'LLAMAR_CONTACTO', 'ENVIAR_MENSAJE', 'LEER_TEXTO', 'CREAR_ALARMA', 'ABRIR_APP', 'LINTERNA', 'AJUSTES_RED',
])
export const requiereConfirmacion = (i: Intencion) => CON_CONFIRMACION.has(i)

export const NOMBRE_INTENCION: Record<Intencion, string> = {
  LLAMAR_CONTACTO: 'Llamar a un contacto', ENVIAR_MENSAJE: 'Preparar un mensaje', LEER_TEXTO: 'Leer un texto en voz alta',
  CREAR_ALARMA: 'Crear una alarma', ABRIR_APP: 'Abrir una aplicación', CONSULTAR_HORA: 'Consultar la hora',
  LINTERNA: 'Linterna', AJUSTES_RED: 'Ajustes de red', GUIA_TELEFONO: 'Guía paso a paso', AYUDA: 'Ayuda', DESCONOCIDO: 'No entendí',
}
