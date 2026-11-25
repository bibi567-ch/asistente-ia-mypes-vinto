import { GoogleGenerativeAI } from '@google/generative-ai';
import { env } from '../config/env.js';

export async function generarRespuestaGemini(pregunta: string, idioma: string): Promise<string> {
  const client = new GoogleGenerativeAI(env.geminiApiKey);
  const model = client.getGenerativeModel({
    model: env.geminiModel,
    generationConfig: { maxOutputTokens: 180, temperature: 0.4 },
    systemInstruction: [
      'Eres Yapay, una guía breve para pequeñas empresas de Vinto, Bolivia.',
      'Responde en el idioma indicado por el usuario si puedes hacerlo con fiabilidad; si no, dilo brevemente y responde en español.',
      'Limita la respuesta a tres líneas. No afirmes que consultaste inventario, ventas, cuentas ni datos del negocio.',
      'No ejecutes ni confirmes operaciones comerciales. Las ventas, inventario y fiados se gestionan localmente en la app.',
      'No presentes orientación legal, contable o tributaria como asesoría profesional; recomienda verificar fuentes oficiales.'
    ].join(' ')
  }, { timeout: 15_000 });
  const result = await model.generateContent(`Idioma solicitado: ${idioma}\nPregunta: ${pregunta}`);
  const respuesta = result.response.text().trim();
  if (!respuesta) {
    throw new Error('Gemini returned an empty assistant response.');
  }
  return respuesta;
}
