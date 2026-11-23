import { createHash } from 'node:crypto';
import { createReadStream } from 'node:fs';
import { access, stat } from 'node:fs/promises';
import { constants } from 'node:fs';
import { Router } from 'express';
import { env } from '../config/env.js';

export const appUpdatesRouter = Router();

appUpdatesRouter.get('/latest', async (_request, response) => {
  const configuration = await updateConfiguration();
  if (!configuration) {
    response.status(404).json({
      error: { code: 'update_not_available', message: 'No Android update is configured.' }
    });
    return;
  }

  response.json({
    versionCode: configuration.versionCode,
    versionName: configuration.versionName,
    url: `${configuration.baseUrl}/api/v1/app-updates/apk`,
    sha256: configuration.sha256,
    size: configuration.size
  });
});

appUpdatesRouter.get('/apk', async (_request, response) => {
  const configuration = await updateConfiguration();
  if (!configuration) {
    response.status(404).json({
      error: { code: 'update_not_available', message: 'No Android update is configured.' }
    });
    return;
  }

  response.type('application/vnd.android.package-archive');
  response.setHeader('content-length', configuration.size);
  response.setHeader('content-disposition', 'attachment; filename="yapay-update.apk"');
  createReadStream(configuration.path).pipe(response);
});

async function updateConfiguration(): Promise<{
  path: string;
  versionCode: number;
  versionName: string;
  baseUrl: string;
  sha256: string;
  size: number;
} | null> {
  if (
    env.androidUpdateVersionCode < 1
    || !env.androidUpdateVersionName
    || !env.androidUpdateApkPath
    || !env.androidUpdateBaseUrl
  ) return null;

  try {
    await access(env.androidUpdateApkPath, constants.R_OK);
    const file = await stat(env.androidUpdateApkPath);
    if (!file.isFile() || file.size < 1) return null;

    const hash = createHash('sha256');
    for await (const chunk of createReadStream(env.androidUpdateApkPath)) hash.update(chunk);
    return {
      path: env.androidUpdateApkPath,
      versionCode: env.androidUpdateVersionCode,
      versionName: env.androidUpdateVersionName,
      baseUrl: env.androidUpdateBaseUrl.replace(/\/$/, ''),
      sha256: hash.digest('hex'),
      size: file.size
    };
  } catch {
    return null;
  }
}
