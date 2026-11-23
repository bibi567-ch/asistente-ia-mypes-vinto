/**
 * Extractor de datos para las órdenes del teléfono: número, contacto, mensaje, hora,
 * aplicación y estado de la linterna. Usa los mismos nombres de campo que espera
 * PhoneActionExecutor.kt (numeroTelefono, mensaje, hora, minutos, nombreApp, linternaEncendida).
 */
export interface Entidades {
  numeroTelefono?: string
  contacto?: string
  mensaje?: string
  hora?: number
  minutos?: number
  nombreApp?: string
  linternaEncendida: boolean
  textoALeer?: string
}

const NUMEROS: Record<string, number> = {
  una: 1, uno: 1, un: 1, dos: 2, tres: 3, cuatro: 4, cinco: 5, seis: 6, siete: 7, ocho: 8,
  nueve: 9, diez: 10, once: 11, doce: 12, trece: 13, catorce: 14, quince: 15, veinte: 20,
  treinta: 30, cuarenta: 40, cincuenta: 50, cero: 0,
}
const DIGITOS: Record<string, string> = {
  cero: '0', uno: '1', una: '1', dos: '2', tres: '3', cuatro: '4', cinco: '5', seis: '6', siete: '7', ocho: '8', nueve: '9',
}
export const APPS = ['whatsapp', 'youtube', 'chrome', 'facebook', 'tiktok', 'telegram', 'maps', 'mapas', 'cámara', 'camara', 'galería', 'galeria', 'teléfono', 'telefono', 'mensajes', 'ajustes', 'configuración', 'configuracion']

const sinTildes = (s: string) => s.normalize('NFD').replace(/\p{M}+/gu, '')
const aNumero = (t?: string) => (t === undefined ? undefined : /^\d+$/.test(t) ? Number(t) : NUMEROS[sinTildes(t)])

export function extraer(texto: string): Entidades {
  const t = texto.toLowerCase().trim()
  const e: Entidades = { linternaEncendida: !/\b(apaga|apagar|desactiva)/.test(t) }

  // Número: dígitos (7 o más) o dígitos hablados ("siete siete dos ...").
  const conDigitos = t.replace(/[\s-]/g, '').match(/\+?\d{7,}/)
  if (conDigitos) e.numeroTelefono = conDigitos[0]
  else {
    const hablados = t.split(/\s+/).map((w) => DIGITOS[sinTildes(w)]).filter(Boolean).join('')
    if (hablados.length >= 7) e.numeroTelefono = hablados
  }

  // Mensaje: lo que va después de "que diga", "diciendo", "dile que" o dos puntos.
  const msg = texto.match(/(?:que diga|diciendo|dile que|:)\s*(.+)$/i)
  if (msg) e.mensaje = msg[1].trim()

  // Contacto: "llama a mi hermana", "mensaje a don Juan", "llama a Rosa".
  const c = t.match(/(?:llama(?:r)?|telefonea(?:r)?|mensaje)\s+(?:a\s+)?((?:mi|don|doña)\s+)?([\p{L}]+)/u)
  if (c && !['un', 'una', 'el', 'la', 'que'].includes(c[2])) e.contacto = `${c[1] ?? ''}${c[2]}`.trim()

  // Hora: "a las 6", "a las seis y media", "6:30", "a las 7 de la noche".
  const h = t.match(/(?:a las|a la)\s+(\d{1,2}|[\p{L}]+)(?::(\d{2}))?(?:\s+y\s+(media|cuarto|\d{1,2}|[\p{L}]+))?/u)
    ?? t.match(/\b(\d{1,2}):(\d{2})\b/)
  if (h) {
    let hora = aNumero(h[1])
    let min = h[2] ? Number(h[2]) : h[3] === 'media' ? 30 : h[3] === 'cuarto' ? 15 : aNumero(h[3]) ?? 0
    if (hora !== undefined && /de la (tarde|noche)/.test(t) && hora < 12) hora += 12
    if (hora !== undefined && hora >= 0 && hora <= 23 && min >= 0 && min <= 59) { e.hora = hora; e.minutos = min }
  }

  e.nombreApp = APPS.find((a) => t.includes(a))
  const leer = texto.match(/(?:lee|leer)\s+(?:esto|este texto|el texto)?\s*:?\s*(.+)$/i)
  if (leer && leer[1].length > 3) e.textoALeer = leer[1].trim()
  return e
}
