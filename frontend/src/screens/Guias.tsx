import { Link, useParams } from 'react-router-dom'
import { ChevronRight, Volume2 } from 'lucide-react'
import Encabezado from '../components/Encabezado'
import { GUIAS } from '../lib/guias'
import { hablar } from '../lib/voz'
import { preferencias } from '../lib/datos'

export default function Guias() {
  const { id } = useParams()
  const g = GUIAS.find((x) => x.id === id)
  const p = preferencias.leer()
  if (g) return (
    <div className="escena min-h-full pb-24">
      <Encabezado titulo="Guía paso a paso" sub={g.titulo} />
      <main className="max-w-lg mx-auto px-4 py-4">
        <Link to="/guias" className="text-sm text-gris">← Todas las guías</Link>
        <h2 className="titulo text-xl mt-3">{g.titulo}</h2>
        <button className="btn-borde h-11 px-4 mt-3 flex items-center gap-2" onClick={() => hablar(`${g.titulo}. ` + g.pasos.map((s, i) => `Paso ${i + 1}: ${s}`).join(' '), true, p.velocidad)}>
          <Volume2 size={18} /> Leer en voz alta</button>
        <ol className="mt-5 space-y-3">
          {g.pasos.map((s, i) => (
            <li key={i} className="vidrio rounded-xl p-4 flex gap-3">
              <span className="shrink-0 w-8 h-8 rounded-full bg-neon/15 text-neon flex items-center justify-center font-semibold">{i + 1}</span>
              <span className="pt-1">{s}</span>
            </li>
          ))}
        </ol>
        <p className="text-xs text-suave mt-5">Los nombres de los menús pueden cambiar un poco según la marca de tu teléfono.</p>
      </main>
    </div>
  )
  return (
    <div className="escena min-h-full pb-24">
      <Encabezado titulo="Guías" sub="Aprende a usar tu celular" />
      <main className="max-w-lg mx-auto px-4 py-4 space-y-2">
        {GUIAS.map((x) => (
          <Link key={x.id} to={`/guias/${x.id}`} className="vidrio rounded-xl p-4 flex items-center justify-between">
            <span>{x.titulo}</span><ChevronRight size={18} className="text-gris" />
          </Link>
        ))}
      </main>
    </div>
  )
}
