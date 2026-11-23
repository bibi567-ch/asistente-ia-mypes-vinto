import { NavLink } from 'react-router-dom'
import { BookOpen, History, Mic, Settings, Users } from 'lucide-react'
const ITEMS = [
  { a: '/agente', t: 'Agente', I: Mic }, { a: '/contactos', t: 'Contactos', I: Users },
  { a: '/guias', t: 'Guías', I: BookOpen }, { a: '/historial', t: 'Historial', I: History }, { a: '/ajustes', t: 'Ajustes', I: Settings },
]
export default function Navegacion() {
  return (
    <nav className="fixed bottom-0 inset-x-0 z-40 vidrio border-x-0 border-b-0" style={{ paddingBottom: 'env(safe-area-inset-bottom)' }} aria-label="Navegación principal">
      <ul className="max-w-lg mx-auto grid grid-cols-5">
        {ITEMS.map(({ a, t, I }) => (
          <li key={a}>
            <NavLink to={a} className={({ isActive }) => `flex flex-col items-center gap-1 py-2.5 text-[11px] ${isActive ? 'text-neon' : 'text-gris'}`}>
              <I size={22} aria-hidden /> {t}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  )
}
