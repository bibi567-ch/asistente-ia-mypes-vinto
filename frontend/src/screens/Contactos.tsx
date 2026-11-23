import { useState, type FormEvent } from 'react'
import { Pencil, Phone, Trash2, UserPlus } from 'lucide-react'
import Encabezado from '../components/Encabezado'
import { contactos, type Contacto } from '../lib/datos'
import { useAviso } from '../components/Aviso'

/** CRUD de contactos frecuentes. El agente los usa para «llama a mi hermana». */
export default function Contactos() {
  const aviso = useAviso()
  const [lista, setLista] = useState(contactos.todos)
  const vacio = { nombre: '', alias: '', numero: '' }
  const [f, setF] = useState<Omit<Contacto, 'id'> & { id?: string }>(vacio)
  const refrescar = () => setLista(contactos.todos())
  const valido = f.nombre.trim() && /^\+?\d{7,15}$/.test(f.numero.replace(/\s/g, ''))
  function guardarC(e: FormEvent) {
    e.preventDefault(); if (!valido) return
    contactos.guardar({ ...f, numero: f.numero.replace(/\s/g, ''), alias: f.alias.trim().toLowerCase() })
    aviso(f.id ? 'Contacto actualizado' : 'Contacto guardado', 'ok'); setF(vacio); refrescar()
  }
  async function importar() {
    const nav = navigator as Navigator & { contacts?: { select(p: string[], o: { multiple: boolean }): Promise<Array<{ name?: string[]; tel?: string[] }>> } }
    if (!nav.contacts) { aviso('Este navegador no permite leer la agenda. Escribe el contacto.', 'info'); return }
    try {
      const [c] = await nav.contacts.select(['name', 'tel'], { multiple: false })
      if (c) setF({ nombre: c.name?.[0] ?? '', alias: '', numero: (c.tel?.[0] ?? '').replace(/[^\d+]/g, '') })
    } catch { /* cancelado */ }
  }
  return (
    <div className="escena min-h-full pb-24">
      <Encabezado titulo="Contactos frecuentes" sub="Para llamar o escribir por voz" />
      <main className="max-w-lg mx-auto px-4 py-4 space-y-5">
        <form onSubmit={guardarC} className="vidrio rounded-2xl p-4 space-y-3">
          <h2 className="titulo">{f.id ? 'Editar contacto' : 'Nuevo contacto'}</h2>
          <label><span className="etiqueta">Nombre</span><input className="campo" value={f.nombre} onChange={(e) => setF({ ...f, nombre: e.target.value })} /></label>
          <label><span className="etiqueta">Cómo lo llamas al hablar (opcional): hermana, mamá, casera…</span><input className="campo" value={f.alias} onChange={(e) => setF({ ...f, alias: e.target.value })} /></label>
          <label><span className="etiqueta">Número</span><input className="campo" inputMode="tel" value={f.numero} onChange={(e) => setF({ ...f, numero: e.target.value })} /></label>
          <div className="flex gap-2">
            <button className="btn-neon h-12 flex-1" disabled={!valido}>{f.id ? 'Guardar cambios' : 'Guardar'}</button>
            {f.id ? <button type="button" className="btn-sutil h-12 px-4" onClick={() => setF(vacio)}>Cancelar</button>
              : <button type="button" className="btn-borde h-12 px-4 flex items-center gap-2" onClick={importar}><UserPlus size={18} /> Agenda</button>}
          </div>
        </form>
        {lista.length === 0 ? <p className="text-center text-sm text-suave">Todavía no hay contactos. Agrega uno para decir «llama a mi hermana».</p> : (
          <ul className="space-y-2">
            {lista.map((c) => (
              <li key={c.id} className="vidrio rounded-xl p-3 flex items-center justify-between gap-3">
                <div className="min-w-0"><p className="font-medium truncate">{c.nombre}</p>
                  <p className="text-xs text-suave">{c.numero}{c.alias && ` · «${c.alias}»`}</p></div>
                <div className="flex gap-1 shrink-0">
                  <a href={`tel:${c.numero}`} className="p-2 text-neon" aria-label={`Llamar a ${c.nombre}`}><Phone size={18} /></a>
                  <button className="p-2 text-gris" onClick={() => setF(c)} aria-label="Editar"><Pencil size={18} /></button>
                  <button className="p-2 text-rojo" onClick={() => { contactos.borrar(c.id); refrescar() }} aria-label="Borrar"><Trash2 size={18} /></button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </main>
    </div>
  )
}
