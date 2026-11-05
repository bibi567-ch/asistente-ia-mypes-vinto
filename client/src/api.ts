const API_BASE = import.meta.env.VITE_API_BASE ?? ''

export type Product = {
  id: string
  name: string
  unit: string
  salePrice: string
}

export async function login(email: string, password: string) {
  const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `Login falló (${res.status})`)
  }
  return res.json() as Promise<{ accessToken: string; expiresIn: number }>
}

export async function listProducts(token: string) {
  const res = await fetch(`${API_BASE}/api/v1/products?limit=50&offset=0`, {
    headers: { Authorization: `Bearer ${token}` },
  })
  if (!res.ok) throw new Error(`No se pudo listar productos (${res.status})`)
  return res.json() as Promise<{ items?: Product[] } | Product[]>
}

export async function createProduct(
  token: string,
  body: { name: string; unit: string; salePrice: string },
) {
  const res = await fetch(`${API_BASE}/api/v1/products`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(body),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `Alta falló (${res.status})`)
  }
  return res.json()
}
