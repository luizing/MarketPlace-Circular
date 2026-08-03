const CACHE_NAME = 'marketplace-circular-v5'
const APP_SHELL = [
  '/',
  '/index.html',
  '/icon-192.png',
  '/icon-512.png',
  '/favicon.svg',
  '/manifest.json',
]

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(APP_SHELL)),
  )
  self.skipWaiting()
})

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) =>
      Promise.all(
        cacheNames
          .filter((cacheName) => cacheName !== CACHE_NAME)
          .map((cacheName) => caches.delete(cacheName)),
      ),
    ),
  )
  self.clients.claim()
})

self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') {
    return
  }

  const requestUrl = new URL(event.request.url)

  if (requestUrl.pathname.startsWith('/api/')) {
    const atualizacaoForcada = requestUrl.searchParams.has('__atualizar')
    const urlParaCache = new URL(requestUrl)
    urlParaCache.searchParams.delete('__atualizar')
    const requisicaoParaCache = new Request(urlParaCache.toString(), {
      method: event.request.method,
    })

    if (atualizacaoForcada) {
      event.respondWith(
        fetch(event.request)
          .then((response) => {
            const responseCopy = response.clone()
            caches.open(CACHE_NAME).then((cache) => cache.put(requisicaoParaCache, responseCopy))
            return response
          })
          .catch(() =>
            caches.match(requisicaoParaCache).then(
              (cachedResponse) =>
                cachedResponse ?? new Response('Recurso indisponivel offline.', { status: 503 }),
            ),
          ),
      )
      return
    }

    event.respondWith(
      caches.match(requisicaoParaCache).then((cachedResponse) => {
        const respostaDaRede = fetch(event.request)
        .then((response) => {
          const responseCopy = response.clone()
          caches.open(CACHE_NAME).then((cache) => cache.put(requisicaoParaCache, responseCopy))
          if (cachedResponse) {
            notificarAtualizacaoDaApi(event.request.url)
          }
          return response
        })
        .catch(() =>
          caches.match(requisicaoParaCache).then(
            (cachedResponse) =>
              cachedResponse ?? new Response('Recurso indisponivel offline.', { status: 503 }),
          ),
        )

        event.waitUntil(respostaDaRede.then(() => undefined))
        return cachedResponse ?? respostaDaRede
      }),
    )
    return
  }

  if (event.request.mode === 'navigate') {
    event.respondWith(
      fetch(event.request)
        .then((response) => {
          const responseCopy = response.clone()
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, responseCopy))
          return response
        })
        .catch(() =>
          caches.match(event.request).then(
            (cachedResponse) =>
              cachedResponse ??
              caches
                .match('/index.html')
                .then(
                  (appShell) =>
                    appShell ?? new Response('Aplicacao indisponivel offline.', { status: 503 }),
                ),
          ),
        ),
    )
    return
  }

  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      const networkResponse = fetch(event.request)
        .then((response) => {
          const responseCopy = response.clone()
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, responseCopy))
          return response
        })
        .catch(
          () => cachedResponse ?? new Response('Recurso indisponivel offline.', { status: 503 }),
        )

      return cachedResponse ?? networkResponse
    }),
  )
})

async function notificarAtualizacaoDaApi(url) {
  const clientes = await self.clients.matchAll({ type: 'window', includeUncontrolled: true })

  clientes.forEach((cliente) => {
    cliente.postMessage({ type: 'api-atualizada', url })
  })
}
