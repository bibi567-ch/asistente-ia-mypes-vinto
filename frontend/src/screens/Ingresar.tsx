import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import Orbe from '../components/Orbe'
import { ApiError, ingresar } from '../lib/api'
import { useAviso } from '../components/Aviso'

export default function Ingresar() {
  const ir = useNavigate(); const aviso = useAviso()
  const [email, setEmail] = useState(''); const [password, setPassword] = useState('')
  const [error, setError] = useState(''); const [cargando, setCargando] = useState(false)
  async function enviar(e: FormEvent) {
    e.preventDefault(); setError(''); setCargando(true)
    try { await ingresar(email.trim(), password); aviso('Sesión iniciada', 'ok'); ir('/agente') }
    catch (err) {
      const a = err as ApiError
      setError(a.status === 401 ? 'Correo o contraseña incorrectos.' : a.status === 0 ? 'No hay conexión con el servidor. Puedes usar el agente sin cuenta.' : a.message)
    } finally { setCargando(false) }
  }
  return (
    <main className="escena min-h-full px-5 py-6">
      <div className="max-w-sm mx-auto">
        <button onClick={() => ir(-1)} className="text-gris flex items-center gap-1 text-sm" aria-label="Volver"><ArrowLeft size={18} /> Volver</button>
        <div className="flex flex-col items-center text-center mt-4 mb-6">
          <Orbe tam={72} />
          <h1 className="titulo text-2xl mt-4">Ingresa a tu cuenta</h1>
          <p className="text-sm text-suave mt-1">Con el correo y la contraseña que registraste</p>
        </div>
        <form onSubmit={enviar} className="flex flex-col gap-4" noValidate>
          <label><span className="etiqueta">Correo</span>
            <input className="campo" type="email" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
          <label><span className="etiqueta">Contraseña</span>
            <input className="campo" type="password" autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} required /></label>
          {error && <p role="alert" className="text-sm text-rojo">{error}</p>}
          <button className="btn-neon h-14" disabled={cargando || !email || !password}>{cargando ? 'Ingresando…' : 'Ingresar'}</button>
        </form>
        <p className="text-sm text-center text-gris mt-6">¿No tienes cuenta? <Link className="text-neon" to="/registro">Créala aquí</Link></p>
      </div>
    </main>
  )
}
