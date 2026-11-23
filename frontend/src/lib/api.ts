/**
 * Cliente del backend (Express + PostgreSQL del repositorio). Todas las rutas pasan por el
 * proxy /api de Vite. Contratos tomados de backend/src/routes/*.ts.
 */
import { leer, guardar } from './almacen'

export interface Sesion { token: string; email: string; expiraEn: number }
const CLAVE = 'iavinto.sesion'
export const sesionActual = (): Sesion | null => {
  const s = leer<Sesion | null>(CLAVE, null)
  return s && s.expiraEn > Date.now() ? s : null
}
export const cerrarSesion = () => guardar(CLAVE, null)

export class ApiError extends Error { constructor(public status: number, public code: string, msg: string) { super(msg) } }

async function pedir<T>(ruta: string, init: RequestInit = {}, token?: string): Promise<T> {
  let r: Response
  try {
    r = await fetch(ruta, { ...init, headers: { 'content-type': 'application/json', ...(token ? { authorization: `Bearer ${token}` } : {}), ...init.headers } })
  } catch {
    throw new ApiError(0, 'sin_conexion', 'No se pudo conectar con el servidor.')
  }
  const data = await r.json().catch(() => ({}))
  if (!r.ok) {
    const e = (data as { error?: { code?: string; message?: string } | string }).error
    const code = typeof e === 'string' ? e : e?.code ?? 'error'
    throw new ApiError(r.status, code, typeof e === 'object' && e?.message ? e.message : `Error ${r.status}`)
  }
  return data as T
}

type Token = { accessToken: string; expiresIn: number }
const abrir = (email: string, t: Token): Sesion => {
  const s = { token: t.accessToken, email, expiraEn: Date.now() + t.expiresIn * 1000 }
  guardar(CLAVE, s); return s
}

export async function ingresar(email: string, password: string) {
  return abrir(email, await pedir<Token>('/api/v1/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }))
}

export interface Registro { nombre: string; carnet: string; celular: string; email: string; password: string }
export async function registrar(d: Registro) {
  return abrir(d.email, await pedir<Token>('/api/v1/auth/register', { method: 'POST', body: JSON.stringify(d) }))
}

export async function estadoServidor() {
  return pedir<{ status: string; database: string }>('/api/v1/health')
}

/** POST /api/v1/logs/comandos (logs.routes.ts). Devuelve false si la ruta no está disponible. */
export async function enviarRegistroVoz(token: string, r: { eventId: string; texto: string; intencion: string; confirmado: boolean; ejecutado: boolean; timestamp: number }) {
  await pedir('/api/v1/logs/comandos', { method: 'POST', body: JSON.stringify(r) }, token)
}
