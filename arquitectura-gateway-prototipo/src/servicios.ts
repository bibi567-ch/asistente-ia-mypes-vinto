/**
 * Levanta los cuatro procesos en una sola terminal y los deja corriendo:
 * gateway (:8080), autenticación (:3001), sincronización (:4000) e IA simulada (:5000).
 * Además abre el panel de arquitectura en vivo en http://localhost:8090.
 * Uso: npm run servicios  (Ctrl + C para detener). Sirve para la demo desde el celular.
 */
import { config } from './config';
import { createGateway } from './gateway';
import { createAuthService } from './authMock';
import { createSyncService } from './syncMock';
import { createIaService } from './iaMock';
import { iniciarPanel } from './panel';

createAuthService().listen(config.authPort, () => console.log(`[auth] escuchando en :${config.authPort}`));
createSyncService().listen(config.syncPort, () => console.log(`[sync] escuchando en :${config.syncPort}`));
createIaService('normal').listen(config.iaPort, () => console.log(`[ia] escuchando en :${config.iaPort} (modo normal)`));
createGateway(config).app.listen(config.gatewayPort, () => console.log(`[gateway] escuchando en :${config.gatewayPort}`));
iniciarPanel();
