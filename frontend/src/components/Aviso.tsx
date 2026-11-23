import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'
type T = { texto: string; tipo: 'ok' | 'error' | 'info' }
const Ctx = createContext<(texto: string, tipo?: T['tipo']) => void>(() => {})
export const useAviso = () => useContext(Ctx)
export function ProveedorAvisos({ children }: { children: ReactNode }) {
  const [aviso, setAviso] = useState<T | null>(null)
  const mostrar = useCallback((texto: string, tipo: T['tipo'] = 'info') => {
    setAviso({ texto, tipo }); window.setTimeout(() => setAviso(null), 3500)
  }, [])
  const color = aviso?.tipo === 'error' ? '#FF5252' : aviso?.tipo === 'ok' ? '#00E676' : '#2979FF'
  return (
    <Ctx.Provider value={mostrar}>
      {children}
      {aviso && (
        <div role="status" className="fixed left-1/2 -translate-x-1/2 top-4 z-50 vidrio rounded-xl px-4 py-3 text-sm max-w-[92vw]" style={{ borderColor: color }}>
          {aviso.texto}
        </div>
      )}
    </Ctx.Provider>
  )
}
