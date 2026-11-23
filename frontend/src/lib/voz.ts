/**
 * Reconocimiento y síntesis de voz del navegador (Web Speech API).
 * En la web cumple el papel de Vosk en la app Android. El umbral de 0,70 es el mismo
 * que CloudAsrFallback.kt usa para decidir si la transcripción es confiable.
 */
export const UMBRAL_CONFIANZA = 0.7

type Rec = {
  lang: string; interimResults: boolean; maxAlternatives: number; continuous: boolean
  start(): void; stop(): void; abort(): void
  onresult: ((e: { results: ArrayLike<ArrayLike<{ transcript: string; confidence: number }> & { isFinal: boolean }> }) => void) | null
  onerror: ((e: { error: string }) => void) | null
  onend: (() => void) | null
}
const Ctor = (): (new () => Rec) | undefined =>
  (window as unknown as { SpeechRecognition?: new () => Rec; webkitSpeechRecognition?: new () => Rec }).SpeechRecognition ??
  (window as unknown as { webkitSpeechRecognition?: new () => Rec }).webkitSpeechRecognition

export const vozDisponible = () => !!Ctor()

export interface Escucha { detener(): void }
export function escuchar(idioma: string, h: {
  parcial: (t: string) => void
  final: (t: string, confianza: number) => void
  error: (motivo: string) => void
  fin: () => void
}): Escucha | null {
  const C = Ctor()
  if (!C) { h.error('no_soportado'); return null }
  const rec = new C()
  rec.lang = idioma; rec.interimResults = true; rec.maxAlternatives = 1; rec.continuous = false
  rec.onresult = (e) => {
    const r = e.results[e.results.length - 1]
    if (r.isFinal) h.final(r[0].transcript, r[0].confidence || 0)
    else h.parcial(r[0].transcript)
  }
  rec.onerror = (e) => h.error(e.error)
  rec.onend = () => h.fin()
  try { rec.start() } catch { h.error('no_iniciado'); return null }
  return { detener: () => rec.stop() }
}

export function hablar(texto: string, activo = true, velocidad = 1) {
  if (!activo || !('speechSynthesis' in window)) return
  speechSynthesis.cancel()
  const u = new SpeechSynthesisUtterance(texto)
  u.lang = 'es-419'; u.rate = velocidad
  const es = speechSynthesis.getVoices().find((v) => v.lang.startsWith('es'))
  if (es) u.voice = es
  speechSynthesis.speak(u)
}

export const MENSAJE_ERROR_VOZ: Record<string, string> = {
  'not-allowed': 'El navegador no tiene permiso para usar el micrófono. Revisa los permisos del sitio.',
  'service-not-allowed': 'El reconocimiento de voz no está permitido en este navegador.',
  'no-speech': 'No escuché nada. Toca el micrófono y vuelve a hablar.',
  'audio-capture': 'No se encontró un micrófono.',
  network: 'El reconocimiento de voz de este navegador necesita internet. Escribe la orden o usa la app Android (Vosk funciona sin conexión).',
  no_soportado: 'Este navegador no reconoce voz. Usa Chrome en Android o escribe la orden.',
  no_iniciado: 'No se pudo iniciar el micrófono.',
  aborted: '',
}
