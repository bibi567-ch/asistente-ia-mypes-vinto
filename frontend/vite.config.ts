import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import basicSsl from '@vitejs/plugin-basic-ssl'

// HTTPS es obligatorio para usar el micrófono y la linterna desde el celular
// (el navegador solo los permite en contextos seguros). basicSsl crea un
// certificado de desarrollo: en el celular hay que aceptar la advertencia una vez.
// El proxy /api reenvía al backend Express (puerto 3000) o al gateway (8080),
// así el celular solo habla con este servidor y no hay problemas de CORS.
const API = process.env.VITE_API_TARGET ?? 'http://localhost:3000'

export default defineConfig({
  plugins: [react(), basicSsl()],
  server: { port: 5173, proxy: { '/api': { target: API, changeOrigin: true } } },
  preview: { port: 4173, proxy: { '/api': { target: API, changeOrigin: true } } },
  test: { environment: 'node' },
})
