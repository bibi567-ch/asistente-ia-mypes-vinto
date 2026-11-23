import { describe, it, expect } from 'vitest'
import { clasificar, requiereConfirmacion } from './intentClassifier'
import { extraer } from './entityExtractor'

describe('clasificar (mismas reglas que IntentClassifier.kt)', () => {
  it.each([
    ['llama a mi hermana', 'LLAMAR_CONTACTO'],
    ['manda mensaje a Rosa que diga ya llegué', 'ENVIAR_MENSAJE'],
    ['pon una alarma a las seis', 'CREAR_ALARMA'],
    ['abre whatsapp', 'ABRIR_APP'],
    ['qué hora es', 'CONSULTAR_HORA'],
    ['prende la linterna', 'LINTERNA'],
    ['activa el wifi', 'AJUSTES_RED'],
    ['guía para compartir ubicación', 'GUIA_TELEFONO'],
    ['ayuda', 'AYUDA'],
    ['kichay whatsapp', 'ABRIR_APP'],
    ['wawqiyta llamay', 'LLAMAR_CONTACTO'],
    ['hola', 'DESCONOCIDO'],
  ])('"%s" -> %s', (t, esperado) => expect(clasificar(t)).toBe(esperado))

  it('respeta el orden de las reglas de Kotlin: "llama" gana a "abre"', () => {
    expect(clasificar('llama y abre')).toBe('LLAMAR_CONTACTO')
  })
  it('pide confirmación igual que ConfirmationPolicy.kt', () => {
    expect(requiereConfirmacion('LLAMAR_CONTACTO')).toBe(true)
    expect(requiereConfirmacion('CONSULTAR_HORA')).toBe(false)
    expect(requiereConfirmacion('GUIA_TELEFONO')).toBe(false)
  })
})

describe('extraer', () => {
  it('número con dígitos', () => expect(extraer('llama al 71234567').numeroTelefono).toBe('71234567'))
  it('número hablado', () => expect(extraer('llama al siete uno dos tres cuatro cinco seis siete').numeroTelefono).toBe('71234567'))
  it('contacto', () => expect(extraer('llama a mi hermana').contacto).toBe('mi hermana'))
  it('mensaje', () => expect(extraer('manda mensaje a Rosa que diga ya llegué').mensaje).toBe('ya llegué'))
  it('hora y media', () => { const e = extraer('pon una alarma a las seis y media'); expect([e.hora, e.minutos]).toEqual([6, 30]) })
  it('hora de la noche', () => expect(extraer('alarma a las 7 de la noche').hora).toBe(19))
  it('hora con dos puntos', () => { const e = extraer('alarma 6:45'); expect([e.hora, e.minutos]).toEqual([6, 45]) })
  it('aplicación', () => expect(extraer('abre whatsapp').nombreApp).toBe('whatsapp'))
  it('linterna apagar', () => expect(extraer('apaga la linterna').linternaEncendida).toBe(false))
})
