import { useEffect, useState } from 'react'
import { Wifi, WifiOff } from 'lucide-react'
import { sesionActual } from '../lib/api'
export function useEnLinea() {
  const [en, setEn] = useState(navigator.onLine)
  useEffect(() => {
    const a = () => setEn(true), b = () => setEn(false)
    window.addEventListener('online', a); window.addEventListener('offline', b)
    return () => { window.removeEventListener('online', a); window.removeEventListener('offline', b) }
  }, [])
  return en
}
export default function Encabezado({ titulo, sub }: { titulo: string; sub?: string }) {
  const enLinea = useEnLinea()
  const s = sesionActual()
  return (
    <header className="sticky top-0 z-30 vidrio border-x-0 border-t-0 px-4 py-3 flex items-center justify-between">
      <div>
        <h1 className="titulo text-lg leading-tight">{titulo}</h1>
        {sub && <p className="text-xs text-suave">{sub}</p>}
      </div>
      <div className="flex items-center gap-2 text-xs">
        <span className={`flex items-center gap-1 px-2 py-1 rounded-full ${enLinea ? 'text-neon bg-neon/10' : 'text-ambar bg-ambar/10'}`}>
          {enLinea ? <Wifi size={14} /> : <WifiOff size={14} />} {enLinea ? 'En línea' : 'Sin conexión'}
        </span>
        <span className="text-gris hidden sm:inline">{s ? s.email : 'Sin cuenta'}</span>
      </div>
    </header>
  )
}
