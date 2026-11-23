/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        fondo: '#0A0A0A', panel: '#18181B', borde: '#27272A', suave: '#90A4AE', gris: '#A1A1AA',
        neon: '#00E676', azul: '#2979FF', ambar: '#FFAB00', rojo: '#FF5252',
      },
      fontFamily: { sans: ['Inter', 'system-ui', 'sans-serif'], titulo: ['Montserrat', 'Inter', 'sans-serif'] },
    },
  },
  plugins: [],
}
