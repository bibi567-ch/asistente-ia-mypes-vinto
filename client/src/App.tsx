import { FormEvent, useMemo, useState } from 'react'
import { createProduct, listProducts, login, type Product } from './api'

type Session = { token: string; email: string }

const TOKEN_KEY = 'mypes_vinto_token'
const EMAIL_KEY = 'mypes_vinto_email'

function loadSession(): Session | null {
  const token = localStorage.getItem(TOKEN_KEY)
  const email = localStorage.getItem(EMAIL_KEY)
  if (!token || !email) return null
  return { token, email }
}

export default function App() {
  const [session, setSession] = useState<Session | null>(() => loadSession())
  const [email, setEmail] = useState('propietario@example.test')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [products, setProducts] = useState<Product[]>([])
  const [name, setName] = useState('')
  const [unit, setUnit] = useState('unidad')
  const [salePrice, setSalePrice] = useState('0.00')

  const title = useMemo(
    () => (session ? 'Inventario / catálogo' : 'Iniciar sesión'),
    [session],
  )

  async function onLogin(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = await login(email.trim(), password)
      localStorage.setItem(TOKEN_KEY, data.accessToken)
      localStorage.setItem(EMAIL_KEY, email.trim())
      setSession({ token: data.accessToken, email: email.trim() })
      await refreshProducts(data.accessToken)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error de login')
    } finally {
      setLoading(false)
    }
  }

  async function refreshProducts(token: string) {
    const data = await listProducts(token)
    const items = Array.isArray(data) ? data : data.items ?? []
    setProducts(items)
  }

  async function onCreate(e: FormEvent) {
    e.preventDefault()
    if (!session) return
    setError(null)
    setLoading(true)
    try {
      await createProduct(session.token, {
        name: name.trim(),
        unit: unit.trim() || 'unidad',
        salePrice: salePrice.trim() || '0.00',
      })
      setName('')
      await refreshProducts(session.token)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear')
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(EMAIL_KEY)
    setSession(null)
    setProducts([])
  }

  return (
    <div className="mx-auto min-h-screen max-w-lg px-4 py-8">
      <header className="mb-8">
        <p className="text-sm font-medium text-green-800">MYPES Vinto</p>
        <h1 className="text-2xl font-bold text-stone-900">{title}</h1>
        <p className="mt-1 text-sm text-stone-600">
          Panel web mínimo (React + Vite + Tailwind). El producto offline del
          comerciante sigue siendo la app Android.
        </p>
      </header>

      {error && (
        <div
          className="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
          role="alert"
        >
          {error}
        </div>
      )}

      {!session ? (
        <form
          onSubmit={onLogin}
          className="space-y-4 rounded-2xl border border-stone-200 bg-white p-6 shadow-sm"
        >
          <label className="block text-sm font-medium text-stone-700">
            Correo
            <input
              className="mt-1 w-full rounded-xl border border-stone-300 px-3 py-3 text-base outline-none focus:border-green-700 focus:ring-2 focus:ring-green-200"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="username"
            />
          </label>
          <label className="block text-sm font-medium text-stone-700">
            Contraseña
            <input
              className="mt-1 w-full rounded-xl border border-stone-300 px-3 py-3 text-base outline-none focus:border-green-700 focus:ring-2 focus:ring-green-200"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </label>
          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-xl bg-[#2E7D32] px-4 py-3 text-base font-semibold text-white disabled:opacity-60"
          >
            {loading ? 'Entrando…' : 'Entrar'}
          </button>
        </form>
      ) : (
        <div className="space-y-6">
          <div className="flex items-center justify-between rounded-2xl border border-stone-200 bg-white px-4 py-3 shadow-sm">
            <div>
              <p className="text-sm text-stone-500">Sesión</p>
              <p className="font-medium text-stone-900">{session.email}</p>
            </div>
            <button
              type="button"
              onClick={logout}
              className="rounded-xl bg-[#F44336] px-3 py-2 text-sm font-semibold text-white"
            >
              Salir
            </button>
          </div>

          <form
            onSubmit={onCreate}
            className="space-y-3 rounded-2xl border border-stone-200 bg-white p-6 shadow-sm"
          >
            <h2 className="text-lg font-semibold text-stone-900">Nuevo producto</h2>
            <input
              className="w-full rounded-xl border border-stone-300 px-3 py-3"
              placeholder="Nombre"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <div className="grid grid-cols-2 gap-3">
              <input
                className="rounded-xl border border-stone-300 px-3 py-3"
                placeholder="Unidad"
                value={unit}
                onChange={(e) => setUnit(e.target.value)}
              />
              <input
                className="rounded-xl border border-stone-300 px-3 py-3"
                placeholder="Precio"
                value={salePrice}
                onChange={(e) => setSalePrice(e.target.value)}
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-xl bg-[#2E7D32] px-4 py-3 font-semibold text-white disabled:opacity-60"
            >
              {loading ? 'Guardando…' : 'Guardar producto'}
            </button>
          </form>

          <section className="rounded-2xl border border-stone-200 bg-white p-6 shadow-sm">
            <div className="mb-3 flex items-center justify-between">
              <h2 className="text-lg font-semibold text-stone-900">Catálogo</h2>
              <button
                type="button"
                className="text-sm font-medium text-green-800"
                onClick={() => session && refreshProducts(session.token)}
              >
                Actualizar
              </button>
            </div>
            {products.length === 0 ? (
              <p className="text-sm text-stone-500">No hay productos todavía.</p>
            ) : (
              <ul className="divide-y divide-stone-100">
                {products.map((p) => (
                  <li key={p.id} className="flex justify-between py-3 text-sm">
                    <span className="font-medium text-stone-900">{p.name}</span>
                    <span className="text-stone-600">
                      {p.salePrice} / {p.unit}
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      )}
    </div>
  )
}
