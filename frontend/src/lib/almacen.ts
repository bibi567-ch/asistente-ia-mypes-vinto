/** Almacenamiento local (localStorage) con manejo de errores: la app funciona sin conexión. */
export function leer<T>(clave: string, porDefecto: T): T {
  try { const v = localStorage.getItem(clave); return v ? (JSON.parse(v) as T) : porDefecto } catch { return porDefecto }
}
export function guardar<T>(clave: string, valor: T) {
  try { localStorage.setItem(clave, JSON.stringify(valor)) } catch { /* almacenamiento lleno o bloqueado */ }
}
export const uuid = () =>
  (crypto as Crypto & { randomUUID?: () => string }).randomUUID?.() ??
  'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0; return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16)
  })
