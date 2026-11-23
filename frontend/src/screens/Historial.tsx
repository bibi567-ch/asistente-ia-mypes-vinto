import { useState } from 'react'
import { CloudUpload, Trash2 } from 'lucide-react'
import Encabezado from '../components/Encabezado'
import { historial } from '../lib/datos'
import { ApiError, enviarRegistroVoz, sesionActual } from '../lib/api'
import { NOMBRE_INTENCION } from '../lib/nlu/intentClassifier'
import { useAviso } from '../components/Aviso'

/** Historial de órdenes. Se respalda en POST /api/v1/logs/comandos cuando hay sesión (patrón Outbox). */
export default function Historial() {
  const aviso = useAviso()
  const [lista, setLista] = useState(historial.todos)
  const [enviando, setEnviando] = useState(false)
  const pendientes = lista.filter((r) => !r.sincronizado)
  async function sincronizar() {
    const s = sesionActual()
    if (!s) { aviso('Ingresa con tu cuenta para respaldar el historial.', 'info'); return }
    setEnviando(true); const hechos: string[] = []
    try {
      for (const r of pendientes) {
        await enviarRegistroVoz(s.token, { eventId: r.eventId, texto: r.texto, intencion: r.intencion, confirmado: r.confirmado, ejecutado: r.ejecutado, timestamp: r.timestamp })
        hechos.push(r.eventId)
      }
      aviso(`${hechos.length} órdenes respaldadas`, 'ok')
    } catch (e) {
      const a = e as ApiError
      aviso(a.status === 404 ? 'El servidor no tiene habilitada la ruta /api/v1/logs. Quedan guardadas en el teléfono.' : `No se pudo respaldar: ${a.message}`, 'error')
    } finally { historial.marcar(hechos); setLista(historial.todos()); setEnviando(false) }
  }
  const total = lista.length, ok = lista.filter((r) => r.ejecutado).length
  return (
    <div className="escena min-h-full pb-24">
      <Encabezado titulo="Historial" sub={`${total} órdenes · ${ok} ejecutadas · ${pendientes.length} sin respaldar`} />
      <main className="max-w-lg mx-auto px-4 py-4 space-y-3">
        <div className="flex gap-2">
          <button className="btn-neon h-11 flex-1 flex items-center justify-center gap-2" onClick={sincronizar} disabled={enviando || pendientes.length === 0}>
            <CloudUpload size={18} /> {enviando ? 'Respaldando…' : 'Respaldar en el servidor'}</button>
          <button className="btn-sutil h-11 px-3" aria-label="Borrar historial" onClick={() => { historial.limpiar(); setLista([]) }}><Trash2 size={18} /></button>
        </div>
        {lista.length === 0 && <p className="text-center text-sm text-suave pt-6">Aquí aparecerán las órdenes que le des al agente.</p>}
        <ul className="space-y-2">
          {lista.map((r) => (
            <li key={r.eventId} className="vidrio rounded-xl p-3">
              <div className="flex justify-between gap-2 text-xs">
                <span className="text-neon">{NOMBRE_INTENCION[r.intencion]}</span>
                <span className="text-suave">{new Date(r.timestamp).toLocaleString('es-BO', { dateStyle: 'short', timeStyle: 'short' })}</span>
              </div>
              <p className="mt-1">«{r.texto}»</p>
              <p className="text-[11px] text-suave mt-1">
                {r.ejecutado ? 'Ejecutada' : 'No ejecutada'} · {r.confirmado ? 'confirmada' : 'sin confirmación'}
                {r.confianza ? ` · confianza ${Math.round(r.confianza * 100)} %` : ''} · {r.sincronizado ? 'respaldada' : 'solo en el teléfono'}
              </p>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}
