import { useEffect } from 'react'
import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { ProveedorAvisos, useAviso } from './components/Aviso'
import Navegacion from './components/Navegacion'
import Bienvenida from './screens/Bienvenida'
import Ingresar from './screens/Ingresar'
import Registro from './screens/Registro'
import Agente from './screens/Agente'
import Contactos from './screens/Contactos'
import Guias from './screens/Guias'
import Historial from './screens/Historial'
import Ajustes from './screens/Ajustes'
import { recordatorios, preferencias } from './lib/datos'
import { hablar } from './lib/voz'

/** Revisa cada 20 s si toca algún recordatorio (mientras la app está abierta). */
function Recordatorios() {
  const aviso = useAviso()
  useEffect(() => {
    const id = window.setInterval(() => {
      const d = new Date()
      for (const r of recordatorios.todos()) {
        if (r.hora === d.getHours() && r.minutos === d.getMinutes()) {
          recordatorios.borrar(r.id)
          const t = `Recordatorio de las ${r.hora}:${String(r.minutos).padStart(2, '0')}`
          aviso(t, 'info'); hablar(t, true, preferencias.leer().velocidad)
          navigator.vibrate?.([300, 150, 300])
          if ('Notification' in window && Notification.permission === 'granted') new Notification('IA Vinto', { body: r.texto })
        }
      }
    }, 20_000)
    return () => window.clearInterval(id)
  }, [aviso])
  return null
}

export default function App() {
  const { pathname } = useLocation()
  const conNav = !['/', '/ingresar', '/registro'].includes(pathname)
  return (
    <ProveedorAvisos>
      <Recordatorios />
      <Routes>
        <Route path="/" element={<Bienvenida />} />
        <Route path="/ingresar" element={<Ingresar />} />
        <Route path="/registro" element={<Registro />} />
        <Route path="/agente" element={<Agente />} />
        <Route path="/contactos" element={<Contactos />} />
        <Route path="/guias" element={<Guias />} />
        <Route path="/guias/:id" element={<Guias />} />
        <Route path="/historial" element={<Historial />} />
        <Route path="/ajustes" element={<Ajustes />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      {conNav && <Navegacion />}
    </ProveedorAvisos>
  )
}
