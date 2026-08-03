export const USER_STORAGE_KEY = 'marketplace-circular-user'
export const TOKEN_STORAGE_KEY = 'marketplace-circular-token'

type TokenPayload = {
  exp?: number
}

function obterPayloadToken(token: string): TokenPayload | null {
  try {
    const partePayload = token.split('.')[1]

    if (!partePayload) {
      return null
    }

    const base64 = partePayload.replace(/-/g, '+').replace(/_/g, '/')
    const preenchimento = '='.repeat((4 - (base64.length % 4)) % 4)
    return JSON.parse(window.atob(`${base64}${preenchimento}`)) as TokenPayload
  } catch {
    return null
  }
}

export function obterDuracaoRestanteToken() {
  const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)
  const payload = token ? obterPayloadToken(token) : null

  if (!payload || typeof payload.exp !== 'number') {
    return 0
  }

  return Math.max(payload.exp * 1000 - Date.now(), 0)
}

export function sessaoEstaValida() {
  return obterDuracaoRestanteToken() > 0
}

export function obterCabecalhosAutenticados(): Record<string, string> {
  const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)

  return sessaoEstaValida() && token ? { Authorization: `Bearer ${token}` } : {}
}

export function encerrarSessao() {
  window.localStorage.removeItem(TOKEN_STORAGE_KEY)
  window.localStorage.removeItem(USER_STORAGE_KEY)
}

export function redirecionarParaLogin() {
  encerrarSessao()

  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

export function respostaIndicaSessaoInvalida(status: number) {
  if (status !== 401 && status !== 403) {
    return false
  }

  redirecionarParaLogin()
  return true
}
