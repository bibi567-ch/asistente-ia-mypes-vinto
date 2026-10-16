export const tiposTransaccion = ['VENTA', 'COMPRA', 'FIADO', 'COBRO'] as const;
export type TipoTransaccion = (typeof tiposTransaccion)[number];

export const formasPago = ['CONTADO', 'FIADO'] as const;
export type FormaPago = (typeof formasPago)[number];

export function esFormaPago(value: unknown): value is FormaPago {
  return value === 'CONTADO' || value === 'FIADO';
}

export interface TransaccionEntrante {
  operationId: string;
  tipo: TipoTransaccion;
  payload: {
    ventaId: string;
    productoId: string;
    cantidad: number;
    precioUnitario: number;
    total: number;
    formaPago: FormaPago;
    clienteFiado?: string | null;
    creadoEn: number;
  };
  schemaVersion: number;
}

export interface SyncBatchRequest {
  transacciones: TransaccionEntrante[];
}

export interface ResultadoPorOperacion {
  operationId: string;
  estado: 'ACEPTADA' | 'DUPLICADA' | 'CONFLICTO' | 'RECHAZADA';
  mensaje?: string;
}

export interface SyncBatchResponse {
  procesadas: number;
  resultados: ResultadoPorOperacion[];
}
