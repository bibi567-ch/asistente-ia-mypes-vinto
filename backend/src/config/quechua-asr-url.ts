export function isAllowedQuechuaAsrUrl(url: URL): boolean {
  return url.protocol === 'https:' ||
    (url.protocol === 'http:' && url.hostname.endsWith('.railway.internal'));
}
