import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { ApiError, registrar, type Registro as Datos } from '../lib/api'
import { useAviso } from '../components/Aviso'

/** Campos obligatorios de POST /api/v1/auth/register (auth.routes.ts). */
export default function Registro() {
  const ir = useNavigate(); const aviso = useAviso()
  const [d, setD] = useState<Datos>({ nombre: '', carnet: '', celular: '', email: '', password: '' })
  const [error, setError] = useState(''); const [cargando, setCargando] = useState(false)
  const cambiar = (k: keyof Datos) => (e: React.ChangeEvent<HTMLInputElement>) => setD({ ...d, [k]: e.target.value })
  const valido = d.nombre.trim() && d.carnet.trim() && d.celular.trim() && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(d.email) && d.password.length >= 12
  async function enviar(e: FormEvent) {
    e.preventDefault(); setError(''); setCargando(true)
    try { await registrar({ ...d, email: d.email.trim() }); aviso('Cuenta creada', 'ok'); ir('/agente') }
    catch (err) {
      const a = err as ApiError
      setError(a.status === 409 ? 'Ya existe una cuenta con ese correo.' : a.status === 0 ? 'No hay conexión con el servidor.' : a.message)
    } finally { setCargando(false) }
  }
  const campo = (k: keyof Datos, t: string, tipo = 'text', ac = 'off') => (
    <label key={k}><span className="etiqueta">{t}</span>
      <input className="campo" type={tipo} autoComplete={ac} value={d[k]} onChange={cambiar(k)} required /></label>
  )
  return (
    <main className="escena min-h-full px-5 py-6">
      <div className="max-w-sm mx-auto">
        <button onClick={() => ir(-1)} className="text-gris flex items-center gap-1 text-sm"><ArrowLeft size={18} /> Volver</button>
        <h1 className="titulo text-2xl mt-4">Crear mi cuenta</h1>
        <p className="text-sm text-suave mt-1 mb-5">Sirve para respaldar tu historial en el servidor.</p>
        <form onSubmit={enviar} className="flex flex-col gap-4" noValidate>
          {campo('nombre', 'Nombre completo', 'text', 'name')}
          {campo('carnet', 'Carnet de identidad')}
          {campo('celular', 'Celular', 'tel', 'tel')}
          {campo('email', 'Correo', 'email', 'email')}
          {campo('password', 'Contraseña (mínimo 12 caracteres)', 'password', 'new-password')}
          {d.password && d.password.length < 12 && <p className="text-xs text-ambar">Faltan {12 - d.password.length} caracteres.</p>}
          {error && <p role="alert" className="text-sm text-rojo">{error}</p>}
          <button className="btn-neon h-14" disabled={cargando || !valido}>{cargando ? 'Creando…' : 'Crear cuenta'}</button>
        </form>
      </div>
    </main>
  )
}
