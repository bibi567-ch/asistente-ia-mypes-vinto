/** Orbe del asistente (inspirado en el AIOrb del Figma). Con `escuchando` muestra anillos de voz. */
export default function Orbe({ tam = 120, escuchando = false }: { tam?: number; escuchando?: boolean }) {
  return (
    <div className="relative flotar" style={{ width: tam, height: tam }} aria-hidden>
      {escuchando && (<><span className="anillo" /><span className="anillo d2" /><span className="anillo d3" /></>)}
      <div className="absolute inset-0 rounded-full" style={{ background: 'radial-gradient(circle at 35% 30%, #7CFFC4, #00E676 35%, #00897B 70%, #003D2B)', boxShadow: '0 0 40px rgba(0,230,118,.45), inset 0 0 30px rgba(0,0,0,.35)' }} />
      <div className="absolute rounded-full" style={{ inset: tam * 0.18, background: 'radial-gradient(circle at 40% 35%, rgba(255,255,255,.55), transparent 60%)' }} />
    </div>
  )
}
