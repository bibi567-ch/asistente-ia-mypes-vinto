/** Contactos frecuentes, recordatorios e historial de órdenes (todo local, funciona sin conexión). */
import { leer, guardar, uuid } from './almacen'
import type { Intencion } from './nlu/intentClassifier'

export interface Contacto { id: string; nombre: string; alias: string; numero: string }
export interface Recordatorio { id: string; hora: number; minutos: number; texto: string; creado: number }
/** Mismo esquema que espera POST /api/v1/logs/comandos, más el estado de sincronización. */
export interface RegistroVoz {
  eventId: string; texto: string; intencion: Intencion; confirmado: boolean; ejecutado: boolean
  timestamp: number; confianza?: number; sincronizado: boolean
}
export interface Preferencias { idiomaVoz: string; respuestasPorVoz: boolean; velocidad: number }

const K = { contactos: 'iavinto.contactos', recordatorios: 'iavinto.recordatorios', historial: 'iavinto.historial', prefs: 'iavinto.preferencias' }

export const contactos = {
  todos: () => leer<Contacto[]>(K.contactos, []),
  guardar: (c: Omit<Contacto, 'id'> & { id?: string }) => {
    const lista = contactos.todos()
    const nuevo = { ...c, id: c.id ?? uuid() } as Contacto
    guardar(K.contactos, c.id ? lista.map((x) => (x.id === c.id ? nuevo : x)) : [...lista, nuevo])
    return nuevo
  },
  borrar: (id: string) => guardar(K.contactos, contactos.todos().filter((c) => c.id !== id)),
  /** Busca por alias o nombre: "mi hermana" encuentra el alias "hermana". */
  buscar: (texto?: string) => {
    if (!texto) return undefined
    const t = texto.toLowerCase().replace(/^(mi|don|doña)\s+/, '')
    return contactos.todos().find((c) => [c.alias, c.nombre].some((v) => v && (v.toLowerCase() === t || v.toLowerCase().includes(t))))
  },
}

export const recordatorios = {
  todos: () => leer<Recordatorio[]>(K.recordatorios, []),
  agregar: (r: Omit<Recordatorio, 'id' | 'creado'>) => { const n = { ...r, id: uuid(), creado: Date.now() }; guardar(K.recordatorios, [...recordatorios.todos(), n]); return n },
  borrar: (id: string) => guardar(K.recordatorios, recordatorios.todos().filter((r) => r.id !== id)),
}

export const historial = {
  todos: () => leer<RegistroVoz[]>(K.historial, []),
  agregar: (r: Omit<RegistroVoz, 'eventId' | 'timestamp' | 'sincronizado'>) => {
    const n: RegistroVoz = { ...r, eventId: uuid(), timestamp: Date.now(), sincronizado: false }
    guardar(K.historial, [n, ...historial.todos()].slice(0, 300)); return n
  },
  marcar: (ids: string[]) => guardar(K.historial, historial.todos().map((r) => (ids.includes(r.eventId) ? { ...r, sincronizado: true } : r))),
  limpiar: () => guardar(K.historial, []),
}

export const preferencias = {
  leer: () => leer<Preferencias>(K.prefs, { idiomaVoz: 'es-BO', respuestasPorVoz: true, velocidad: 1 }),
  guardar: (p: Preferencias) => guardar(K.prefs, p),
}
