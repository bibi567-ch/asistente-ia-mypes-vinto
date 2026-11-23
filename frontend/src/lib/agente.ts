/**
 * Agente IA Vinto en la web: une el clasificador, el extractor y las acciones del teléfono.
 * Equivale a YapayAgent + PhoneActionExecutor de la app Kotlin, con las capacidades
 * que un navegador permite en Android (tel:, sms:, wa.me, linterna, voz).
 */
import { clasificar, requiereConfirmacion, NOMBRE_INTENCION, type Intencion } from './nlu/intentClassifier'
import { extraer, type Entidades } from './nlu/entityExtractor'
import { contactos, recordatorios } from './datos'
import { buscarGuia, GUIAS } from './guias'

export interface Plan {
  texto: string
  intencion: Intencion
  entidades: Entidades
  titulo: string
  detalle: string[]
  confirmar: boolean
  /** Si falta un dato, el agente lo pide antes de ejecutar. */
  falta?: string
  /** Navegación interna (por ejemplo, a una guía). */
  ir?: string
}
export interface Resultado { ok: boolean; respuesta: string; ir?: string }

const APP_WEB: Record<string, string> = {
  whatsapp: 'https://wa.me/', youtube: 'https://m.youtube.com', facebook: 'https://m.facebook.com',
  tiktok: 'https://www.tiktok.com', telegram: 'https://t.me', maps: 'https://maps.google.com', mapas: 'https://maps.google.com',
  chrome: 'https://www.google.com', 'teléfono': 'tel:', telefono: 'tel:', mensajes: 'sms:',
}
const dosDig = (n: number) => String(n).padStart(2, '0')
const numeroDe = (e: Entidades) => e.numeroTelefono ?? contactos.buscar(e.contacto)?.numero
const nombreDe = (e: Entidades) => contactos.buscar(e.contacto)?.nombre ?? e.contacto ?? e.numeroTelefono

export function planificar(texto: string): Plan {
  const intencion = clasificar(texto)
  const e = extraer(texto)
  const base = { texto, intencion, entidades: e, titulo: NOMBRE_INTENCION[intencion], confirmar: requiereConfirmacion(intencion) }
  switch (intencion) {
    case 'LLAMAR_CONTACTO': {
      const n = numeroDe(e)
      return { ...base, detalle: [`A: ${nombreDe(e) ?? '—'}`, `Número: ${n ?? 'sin número'}`],
        falta: n ? undefined : e.contacto ? `No tengo el número de «${e.contacto}». Guárdalo en Contactos o dime el número.` : 'Dime a quién llamar o el número.' }
    }
    case 'ENVIAR_MENSAJE': {
      const n = numeroDe(e)
      return { ...base, detalle: [`Para: ${nombreDe(e) ?? '—'}`, `Mensaje: ${e.mensaje ?? '(vacío)'}`, 'Tú confirmas el envío en la aplicación de mensajes.'],
        falta: n ? undefined : 'Dime a quién enviar el mensaje o guarda el contacto.' }
    }
    case 'CREAR_ALARMA':
      return { ...base, detalle: [`Hora: ${e.hora !== undefined ? `${dosDig(e.hora)}:${dosDig(e.minutos ?? 0)}` : 'sin hora'}`, 'Se guarda como recordatorio de IA Vinto y suena mientras la app esté abierta.'],
        falta: e.hora === undefined ? 'Dime la hora, por ejemplo: «alarma a las seis y media».' : undefined }
    case 'ABRIR_APP':
      return { ...base, detalle: [`Aplicación: ${e.nombreApp ?? '—'}`],
        falta: !e.nombreApp ? 'Dime qué aplicación abrir: WhatsApp, YouTube, Facebook, mapas…' : !APP_WEB[e.nombreApp] ? `Desde el navegador no puedo abrir «${e.nombreApp}». Te muestro la guía.` : undefined }
    case 'LEER_TEXTO':
      return { ...base, detalle: [e.textoALeer ? `Texto: «${e.textoALeer}»` : 'Leeré el texto que copiaste (portapapeles).'] }
    case 'LINTERNA':
      return { ...base, detalle: [e.linternaEncendida ? 'Encender la linterna' : 'Apagar la linterna'] }
    case 'AJUSTES_RED':
      return { ...base, confirmar: false, detalle: ['El navegador no puede cambiar los ajustes; te muestro la guía paso a paso.'], ir: `/guias/${/dato/.test(texto.toLowerCase()) ? 'datos' : 'wifi'}` }
    case 'GUIA_TELEFONO': {
      const g = buscarGuia(texto)
      return { ...base, detalle: [g ? g.titulo : 'Te muestro la lista de guías'], ir: g ? `/guias/${g.id}` : '/guias' }
    }
    case 'CONSULTAR_HORA':
    case 'AYUDA':
    case 'DESCONOCIDO':
      return { ...base, detalle: [] }
  }
}

let pista: MediaStreamTrack | null = null
async function linterna(encender: boolean): Promise<boolean> {
  try {
    if (!encender) { pista?.stop(); pista = null; return true }
    if (!pista) {
      const s = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
      pista = s.getVideoTracks()[0]
    }
    const caps = (pista.getCapabilities?.() ?? {}) as { torch?: boolean }
    if (!caps.torch) { pista.stop(); pista = null; return false }
    await pista.applyConstraints({ advanced: [{ torch: true } as MediaTrackConstraintSet] })
    return true
  } catch { pista?.stop(); pista = null; return false }
}

const AYUDA = 'Puedes decirme: «llama a mi hermana», «manda mensaje a Rosa que diga ya llegué», «alarma a las seis y media», «abre WhatsApp», «qué hora es», «prende la linterna» o «guía para compartir ubicación».'

export async function ejecutar(p: Plan): Promise<Resultado> {
  const e = p.entidades
  switch (p.intencion) {
    case 'LLAMAR_CONTACTO': {
      const n = numeroDe(e); if (!n) return { ok: false, respuesta: p.falta ?? 'Falta el número.' }
      window.location.href = `tel:${n}`
      return { ok: true, respuesta: `Llamando a ${nombreDe(e)}.` }
    }
    case 'ENVIAR_MENSAJE': {
      const n = numeroDe(e); if (!n) return { ok: false, respuesta: p.falta ?? 'Falta el número.' }
      window.location.href = `sms:${n}?body=${encodeURIComponent(e.mensaje ?? '')}`
      return { ok: true, respuesta: 'Abrí tu aplicación de mensajes con el texto listo. Revisa y toca enviar.' }
    }
    case 'CREAR_ALARMA': {
      if (e.hora === undefined) return { ok: false, respuesta: p.falta! }
      recordatorios.agregar({ hora: e.hora, minutos: e.minutos ?? 0, texto: p.texto })
      if ('Notification' in window && Notification.permission === 'default') void Notification.requestPermission()
      return { ok: true, respuesta: `Listo, te aviso a las ${dosDig(e.hora)}:${dosDig(e.minutos ?? 0)}.` }
    }
    case 'ABRIR_APP': {
      const url = e.nombreApp ? APP_WEB[e.nombreApp] : undefined
      if (!url) return { ok: false, respuesta: p.falta ?? 'No puedo abrir esa aplicación.', ir: '/guias' }
      window.open(url, '_blank')
      return { ok: true, respuesta: `Abriendo ${e.nombreApp}.` }
    }
    case 'LEER_TEXTO': {
      let t = e.textoALeer
      if (!t) { try { t = await navigator.clipboard.readText() } catch { /* sin permiso */ } }
      return t ? { ok: true, respuesta: t } : { ok: false, respuesta: 'No encontré texto para leer. Copia un texto y vuelve a pedírmelo.' }
    }
    case 'LINTERNA': {
      const ok = await linterna(e.linternaEncendida)
      return ok ? { ok, respuesta: e.linternaEncendida ? 'Linterna encendida.' : 'Linterna apagada.' }
        : { ok, respuesta: 'Este navegador no permite controlar la linterna. En la app Android sí funciona.' }
    }
    case 'CONSULTAR_HORA': {
      const d = new Date()
      return { ok: true, respuesta: `Son las ${d.getHours()} y ${d.getMinutes()} minutos.` }
    }
    case 'AJUSTES_RED':
    case 'GUIA_TELEFONO':
      return { ok: true, respuesta: p.ir === '/guias' ? `Tengo ${GUIAS.length} guías. Elige una.` : `Te muestro la guía: ${p.detalle[0]}.`, ir: p.ir }
    case 'AYUDA':
      return { ok: true, respuesta: AYUDA }
    case 'DESCONOCIDO':
      return { ok: false, respuesta: `No entendí la orden. ${AYUDA}` }
  }
}
