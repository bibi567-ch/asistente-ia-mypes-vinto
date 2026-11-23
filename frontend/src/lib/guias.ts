/**
 * Las ocho guías paso a paso del alcance del perfil. Los pasos son generales de Android;
 * los nombres de los menús pueden cambiar un poco según la marca del teléfono.
 * Textos en quechua: pendientes de revisión por el hablante del equipo (no se inventan aquí).
 */
export interface Guia { id: string; titulo: string; claves: string[]; pasos: string[] }

export const GUIAS: Guia[] = [
  { id: 'letra', titulo: 'Aumentar el tamaño de la letra', claves: ['letra', 'grande', 'tamaño', 'fuente'],
    pasos: ['Abre Ajustes (el ícono de engranaje).', 'Entra en Pantalla.', 'Toca Tamaño de fuente o Tamaño y estilo de fuente.', 'Mueve la barra hacia la derecha hasta que leas cómodo.', 'Vuelve atrás: el cambio se guarda solo.'] },
  { id: 'ubicacion', titulo: 'Compartir mi ubicación', claves: ['ubicación', 'ubicacion', 'dónde estoy', 'donde estoy', 'mapa'],
    pasos: ['Abre WhatsApp y entra al chat de la persona.', 'Toca el clip o el signo + junto al cuadro de texto.', 'Elige Ubicación.', 'Toca Enviar tu ubicación actual.', 'Si pide permiso de ubicación, toca Permitir.'] },
  { id: 'foto', titulo: 'Enviar una foto', claves: ['foto', 'imagen', 'galería', 'galeria'],
    pasos: ['Abre WhatsApp y entra al chat.', 'Toca el clip o el signo +.', 'Elige Galería (o Cámara para tomar una nueva).', 'Toca la foto que quieres enviar.', 'Toca el botón verde de enviar.'] },
  { id: 'wifi', titulo: 'Conectarme a una red wifi', claves: ['wifi', 'wi-fi', 'red', 'internet'],
    pasos: ['Desliza el dedo desde arriba de la pantalla hacia abajo.', 'Mantén presionado el ícono de Wi-Fi.', 'Toca el nombre de la red.', 'Escribe la contraseña con cuidado (mayúsculas y minúsculas cuentan).', 'Toca Conectar.'] },
  { id: 'datos', titulo: 'Activar los datos móviles', claves: ['datos', 'megas', 'saldo'],
    pasos: ['Desliza el dedo desde arriba de la pantalla hacia abajo.', 'Busca el ícono de Datos móviles (dos flechas).', 'Tócalo para que quede encendido (de color).', 'Si no aparece, entra en Ajustes > Conexiones o Red > Datos móviles.'] },
  { id: 'contacto', titulo: 'Guardar un contacto', claves: ['contacto', 'guardar número', 'guardar numero', 'agenda'],
    pasos: ['Abre la aplicación Teléfono o Contactos.', 'Toca el signo + o Crear contacto.', 'Escribe el nombre y el número.', 'Toca Guardar.'] },
  { id: 'espacio', titulo: 'Liberar espacio de almacenamiento', claves: ['espacio', 'memoria', 'almacenamiento', 'lleno'],
    pasos: ['Abre Ajustes.', 'Entra en Almacenamiento o Mantenimiento del dispositivo.', 'Toca Liberar espacio o revisa Archivos grandes.', 'Borra videos y archivos que ya no necesites (revisa antes de borrar).', 'En WhatsApp: Ajustes > Almacenamiento y datos > Administrar almacenamiento.'] },
  { id: 'volumen', titulo: 'Subir o bajar el volumen', claves: ['volumen', 'sonido', 'no se escucha', 'fuerte'],
    pasos: ['Usa los botones del costado del teléfono: el de arriba sube y el de abajo baja.', 'Para más opciones, toca los tres puntos que aparecen junto a la barra de volumen.', 'Ahí puedes subir por separado el volumen de llamadas, música y notificaciones.'] },
]

export const buscarGuia = (texto: string) => {
  const t = texto.toLowerCase()
  return GUIAS.find((g) => g.claves.some((c) => t.includes(c)))
}
