import { useEffect, useRef, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Check, Mic, Send, Square, X } from 'lucide-react'
import Encabezado from '../components/Encabezado'
import Orbe from '../components/Orbe'
import { ejecutar, planificar, type Plan } from '../lib/agente'
import { historial, preferencias } from '../lib/datos'
import { escuchar, hablar, MENSAJE_ERROR_VOZ, UMBRAL_CONFIANZA, vozDisponible, type Escucha } from '../lib/voz'
import { leer, guardar } from '../lib/almacen'

type Msg = { de: 'agente' | 'usuario'; texto: string; confianza?: number }
const SUGERENCIAS = ['¿Qué hora es?', 'Abre WhatsApp', 'Alarma a las seis y media', 'Guía para compartir ubicación', 'Ayuda']
const SALUDO: Msg = { de: 'agente', texto: 'Hola, soy IA Vinto. Toca el micrófono y dime qué quieres hacer con tu celular.' }

export default function Agente() {
  const ir = useNavigate()
  const prefs = preferencias.leer()
  const [msgs, setMsgs] = useState<Msg[]>(() => leer<Msg[]>('iavinto.chat', [SALUDO]))
  const [texto, setTexto] = useState('')
  const [parcial, setParcial] = useState('')
  const [escuchando, setEscuchando] = useState(false)
  const [plan, setPlan] = useState<Plan | null>(null)
  const [confianzaPlan, setConfianzaPlan] = useState<number | undefined>()
  const escucha = useRef<Escucha | null>(null)
  const fin = useRef<HTMLDivElement>(null)

  useEffect(() => { guardar('iavinto.chat', msgs.slice(-60)); fin.current?.scrollIntoView({ behavior: 'smooth' }) }, [msgs, parcial, plan])
  const decir = (t: string) => { setMsgs((m) => [...m, { de: 'agente', texto: t }]); hablar(t, prefs.respuestasPorVoz, prefs.velocidad) }

  async function correr(p: Plan, confirmado: boolean, confianza?: number) {
    const r = await ejecutar(p)
    historial.agregar({ texto: p.texto, intencion: p.intencion, confirmado, ejecutado: r.ok, confianza })
    decir(r.respuesta)
    if (r.ir) window.setTimeout(() => ir(r.ir!), 700)
  }

  async function procesar(t: string, confianza?: number) {
    const limpio = t.trim(); if (!limpio) return
    setMsgs((m) => [...m, { de: 'usuario', texto: limpio, confianza }])
    const p = planificar(limpio)
    if (p.falta) {
      historial.agregar({ texto: limpio, intencion: p.intencion, confirmado: false, ejecutado: false, confianza })
      decir(p.falta); if (p.ir) window.setTimeout(() => ir(p.ir!), 700); return
    }
    // Misma regla que ConfirmationPolicy.kt; además, si la confianza es baja (< 0,70) se confirma siempre.
    const baja = confianza !== undefined && confianza < UMBRAL_CONFIANZA
    if (p.confirmar || baja) { setPlan(p); setConfianzaPlan(confianza); return }
    await correr(p, false, confianza)
  }

  function microfono() {
    if (escuchando) { escucha.current?.detener(); return }
    setParcial('')
    escucha.current = escuchar(prefs.idiomaVoz, {
      parcial: setParcial,
      final: (t, c) => { setParcial(''); void procesar(t, c) },
      error: (m) => { const txt = MENSAJE_ERROR_VOZ[m] ?? `Error de voz: ${m}`; if (txt) decir(txt) },
      fin: () => { setEscuchando(false); setParcial('') },
    })
    setEscuchando(!!escucha.current)
  }

  function enviar(e: FormEvent) { e.preventDefault(); const t = texto; setTexto(''); void procesar(t) }

  return (
    <div className="escena min-h-full flex flex-col pb-[76px]">
      <Encabezado titulo="IA Vinto" sub="Tu asistente de voz" />

      <section className="flex-1 overflow-y-auto sin-barra px-4 py-4 space-y-3" aria-live="polite">
        {msgs.map((m, i) => (
          <div key={i} className={`flex ${m.de === 'usuario' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[85%] rounded-2xl px-4 py-2.5 text-[15px] leading-relaxed ${m.de === 'usuario' ? 'bg-neon/15 border border-neon/30' : 'vidrio'}`}>
              {m.texto}
              {m.confianza !== undefined && m.confianza > 0 && (
                <span className={`block text-[11px] mt-1 ${m.confianza < UMBRAL_CONFIANZA ? 'text-ambar' : 'text-suave'}`}>
                  Confianza {Math.round(m.confianza * 100)} %{m.confianza < UMBRAL_CONFIANZA ? ' · baja, te pido confirmar' : ''}
                </span>
              )}
            </div>
          </div>
        ))}
        {parcial && <div className="flex justify-end"><div className="max-w-[85%] rounded-2xl px-4 py-2.5 border border-dashed border-neon/40 text-suave italic">{parcial}…</div></div>}
        <div ref={fin} />
      </section>

      {plan && (
        <div className="fixed inset-0 z-50 bg-black/60 flex items-end" role="dialog" aria-modal aria-labelledby="conf-titulo">
          <div className="vidrio w-full max-w-lg mx-auto rounded-t-3xl p-5 pb-8">
            <p className="text-xs text-suave">Entendí:</p>
            <p className="text-sm italic text-gris mb-2">«{plan.texto}»</p>
            <h2 id="conf-titulo" className="titulo text-xl text-neon">{plan.titulo}</h2>
            <ul className="mt-2 space-y-1 text-sm">{plan.detalle.map((d) => <li key={d}>{d}</li>)}</ul>
            {confianzaPlan !== undefined && confianzaPlan < UMBRAL_CONFIANZA && (
              <p className="text-xs text-ambar mt-2">La confianza del reconocimiento fue baja ({Math.round(confianzaPlan * 100)} %). Revisa antes de confirmar.</p>
            )}
            <div className="grid grid-cols-2 gap-3 mt-5">
              <button className="btn-sutil h-12 flex items-center justify-center gap-2" onClick={() => { historial.agregar({ texto: plan.texto, intencion: plan.intencion, confirmado: false, ejecutado: false, confianza: confianzaPlan }); setPlan(null); decir('Cancelado. Puedes volver a decirlo.') }}><X size={18} /> Corregir</button>
              <button className="btn-neon h-12 flex items-center justify-center gap-2" onClick={() => { const p = plan; setPlan(null); void correr(p, true, confianzaPlan) }}><Check size={18} /> Confirmar</button>
            </div>
          </div>
        </div>
      )}

      <div className="fixed bottom-[68px] inset-x-0 z-30 px-4 pb-3 pt-2 bg-gradient-to-t from-fondo via-fondo/95 to-transparent">
        <div className="max-w-lg mx-auto">
          <div className="flex gap-2 overflow-x-auto sin-barra pb-3">
            {SUGERENCIAS.map((s) => <button key={s} onClick={() => void procesar(s)} className="shrink-0 text-xs px-3 py-1.5 rounded-full btn-sutil">{s}</button>)}
          </div>
          <div className="flex items-center gap-3">
            <button onClick={microfono} aria-label={escuchando ? 'Detener' : 'Hablar'} className="relative shrink-0 w-16 h-16 rounded-full flex items-center justify-center"
              title={vozDisponible() ? '' : 'Este navegador no reconoce voz'}>
              <span className="absolute inset-0"><Orbe tam={64} escuchando={escuchando} /></span>
              <span className="relative text-[#06130D]">{escuchando ? <Square size={22} fill="currentColor" /> : <Mic size={26} />}</span>
            </button>
            <form onSubmit={enviar} className="flex-1 flex gap-2">
              <input className="campo" placeholder="O escribe tu orden…" value={texto} onChange={(e) => setTexto(e.target.value)} aria-label="Orden escrita" />
              <button className="btn-neon w-12 shrink-0 flex items-center justify-center" disabled={!texto.trim()} aria-label="Enviar"><Send size={18} /></button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
