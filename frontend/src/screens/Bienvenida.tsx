import { useNavigate } from 'react-router-dom'
import Orbe from '../components/Orbe'
export default function Bienvenida() {
  const ir = useNavigate()
  return (
    <main className="escena min-h-full flex flex-col items-center justify-center px-6 py-10 relative">
      <div className="w-full max-w-sm flex flex-col items-center gap-7">
        <Orbe tam={120} escuchando />
        <div className="text-center">
          <h1 className="titulo text-3xl leading-tight" style={{ textShadow: '0 0 24px rgba(255,255,255,.18)' }}>Habla con tu<br />celular</h1>
          <p className="text-sm mt-3 text-suave">En castellano, quechua o quechuañol. Llama, envía mensajes, pon alarmas y aprende a usar tu teléfono.</p>
        </div>
        <div className="w-full flex flex-col gap-3">
          <button className="btn-neon h-14" onClick={() => ir('/agente')}>Empezar a hablar</button>
          <button className="btn-borde h-14" onClick={() => ir('/ingresar')}>Ya tengo cuenta, ingresar</button>
          <button className="btn-sutil h-14" onClick={() => ir('/registro')}>Crear mi cuenta</button>
        </div>
        <p className="text-xs text-suave text-center">El agente funciona sin cuenta. La cuenta sirve para respaldar tu historial en el servidor.</p>
      </div>
      <p className="absolute bottom-5 text-xs text-[#37474F]">IA Vinto · Vinto, Cochabamba</p>
    </main>
  )
}
