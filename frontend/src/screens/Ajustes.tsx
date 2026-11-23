import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Encabezado from '../components/Encabezado'
import { preferencias, recordatorios, type Preferencias } from '../lib/datos'
import { cerrarSesion, estadoServidor, sesionActual } from '../lib/api'
import { hablar, vozDisponible } from '../lib/voz'

export default function Ajustes() {
  const ir = useNavigate()
  const [p, setP] = useState<Preferencias>(preferencias.leer)
  const [servidor, setServidor] = useState('Comprobando…')
  const [recs, setRecs] = useState(recordatorios.todos)
  const s = sesionActual()
  useEffect(() => {
    estadoServidor().then((r) => setServidor(r.database === 'connected' ? 'Servidor y base de datos en línea' : 'Servidor en línea, base de datos no disponible'))
      .catch(() => setServidor('Servidor no disponible (el agente sigue funcionando en el teléfono)'))
  }, [])
  const cambiar = (n: Partial<Preferencias>) => { const x = { ...p, ...n }; setP(x); preferencias.guardar(x) }
  const fila = 'vidrio rounded-2xl p-4 space-y-3'
  return (
    <div className="escena min-h-full pb-24">
      <Encabezado titulo="Ajustes" />
      <main className="max-w-lg mx-auto px-4 py-4 space-y-4">
        <section className={fila}>
          <h2 className="titulo">Voz</h2>
          <label><span className="etiqueta">Idioma del reconocimiento</span>
            <select className="campo" value={p.idiomaVoz} onChange={(e) => cambiar({ idiomaVoz: e.target.value })}>
              <option value="es-BO">Español de Bolivia</option><option value="es-419">Español de Latinoamérica</option><option value="es-ES">Español de España</option>
            </select></label>
          <p className="text-xs text-suave">El navegador no reconoce quechua por voz; las palabras en quechua del catálogo (wawqiy, kichay, q'anchay) funcionan escritas o dentro de frases en castellano. El reconocimiento de quechua es de la app Android (Vosk + MMS).</p>
          <label className="flex items-center justify-between"><span>Responder en voz alta</span>
            <input type="checkbox" className="w-5 h-5 accent-[#00E676]" checked={p.respuestasPorVoz} onChange={(e) => cambiar({ respuestasPorVoz: e.target.checked })} /></label>
          <label><span className="etiqueta">Velocidad de la voz: {p.velocidad.toFixed(1)}</span>
            <input type="range" min={0.6} max={1.4} step={0.1} value={p.velocidad} onChange={(e) => cambiar({ velocidad: Number(e.target.value) })} className="w-full accent-[#00E676]" /></label>
          <button className="btn-borde h-11 w-full" onClick={() => hablar('Hola, así suena mi voz.', true, p.velocidad)}>Probar voz</button>
          {!vozDisponible() && <p className="text-xs text-ambar">Este navegador no tiene reconocimiento de voz. Usa Chrome en Android.</p>}
        </section>
        <section className={fila}>
          <h2 className="titulo">Recordatorios</h2>
          {recs.length === 0 ? <p className="text-sm text-suave">No hay recordatorios.</p> : recs.map((r) => (
            <div key={r.id} className="flex justify-between items-center text-sm">
              <span>{String(r.hora).padStart(2, '0')}:{String(r.minutos).padStart(2, '0')} · {r.texto}</span>
              <button className="text-rojo text-xs" onClick={() => { recordatorios.borrar(r.id); setRecs(recordatorios.todos()) }}>Quitar</button>
            </div>
          ))}
        </section>
        <section className={fila}>
          <h2 className="titulo">Cuenta y servidor</h2>
          <p className="text-sm">{s ? `Sesión: ${s.email}` : 'Sin cuenta (modo local)'}</p>
          <p className="text-sm text-suave">{servidor}</p>
          {s ? <button className="btn-sutil h-11 w-full" onClick={() => { cerrarSesion(); ir('/') }}>Cerrar sesión</button>
            : <button className="btn-neon h-11 w-full" onClick={() => ir('/ingresar')}>Ingresar</button>}
        </section>
        <p className="text-center text-xs text-suave">IA Vinto · versión web 1.0 · Proyecto Integrador UAB 2026</p>
      </main>
    </div>
  )
}
