import { useState } from 'react'
import type { FormEvent } from 'react'
import { TOKEN_STORAGE_KEY, USER_STORAGE_KEY } from '../auth'
import { obterMensagemErro } from '../api'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

type UsuarioResposta = {
  id: number
  login: string
  contato: string
  token: string
}

function Login() {
  const [criandoConta, setCriandoConta] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState<string | null>(null)
  const [sucesso, setSucesso] = useState<string | null>(null)
  const [loginInicial, setLoginInicial] = useState('')

  const entrar = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setEnviando(true)
    setErro(null)
    setSucesso(null)

    const formData = new FormData(event.currentTarget)
    const payload = {
      login: String(formData.get('login') ?? '').trim(),
      senha: String(formData.get('senha') ?? ''),
    }

    try {
      const resposta = await fetch(`${apiBaseUrl}/api/users/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })

      if (!resposta.ok) {
        throw new Error(
          await obterMensagemErro(
            resposta,
            resposta.status === 401 ? 'Login ou senha invalidos.' : 'Nao foi possivel realizar o login.',
          ),
        )
      }

      const usuario = (await resposta.json()) as UsuarioResposta
      window.localStorage.setItem(TOKEN_STORAGE_KEY, usuario.token)
      window.localStorage.setItem(
        USER_STORAGE_KEY,
        JSON.stringify({ id: usuario.id, login: usuario.login, contato: usuario.contato }),
      )
      window.location.href = '/#anuncios'
    } catch (error) {
      setErro(error instanceof Error ? error.message : 'Nao foi possivel realizar o login.')
    } finally {
      setEnviando(false)
    }
  }

  const criarConta = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setEnviando(true)
    setErro(null)
    setSucesso(null)

    const formData = new FormData(event.currentTarget)
    const payload = {
      contato: String(formData.get('contato') ?? '').trim(),
      login: String(formData.get('login') ?? '').trim(),
      senha: String(formData.get('senha') ?? ''),
    }

    try {
      const resposta = await fetch(`${apiBaseUrl}/api/users`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      })

      if (!resposta.ok) {
        throw new Error(
          await obterMensagemErro(
            resposta,
            resposta.status === 409 ? 'Este login ja esta cadastrado.' : 'Nao foi possivel criar a conta.',
          ),
        )
      }

      setLoginInicial(payload.login)
      setCriandoConta(false)
      setSucesso('Conta criada com sucesso. Agora faca login.')
    } catch (error) {
      setErro(error instanceof Error ? error.message : 'Nao foi possivel criar a conta.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <main className="login-page" aria-label="Login">
      <section className="login-card">
        <a className="login-card__back" href="/">
          Voltar
        </a>

        {criandoConta ? (
          <>
            <h1>Criar conta</h1>
            <form className="login-card__form" onSubmit={criarConta}>
              <label>
                <span>Email para contato</span>
                <input type="email" name="contato" autoComplete="email" required />
              </label>

              <label>
                <span>Login</span>
                <input
                  type="text"
                  name="login"
                  autoComplete="username"
                  required
                />
              </label>

              <label>
                <span>Nova senha</span>
                <input
                  type="password"
                  name="senha"
                  autoComplete="new-password"
                  required
                />
              </label>

              <button type="submit" disabled={enviando}>
                {enviando ? 'Criando...' : 'Criar conta'}
              </button>
            </form>

            <button
              type="button"
              className="login-card__switch-mode"
              onClick={() => {
                setCriandoConta(false)
                setErro(null)
              }}
            >
              Ja tenho conta
            </button>
          </>
        ) : (
          <>
            <h1>Login</h1>
            <form className="login-card__form" onSubmit={entrar}>
              <label>
                <span>Login</span>
                <input
                  type="text"
                  name="login"
                  autoComplete="username"
                  defaultValue={loginInicial}
                  required
                />
              </label>

              <label>
                <span>Senha</span>
                <input type="password" name="senha" autoComplete="current-password" required />
              </label>

              <button type="submit" disabled={enviando}>
                {enviando ? 'Entrando...' : 'Entrar'}
              </button>
            </form>

            <button
              type="button"
              className="login-card__switch-mode"
              onClick={() => {
                setCriandoConta(true)
                setErro(null)
                setSucesso(null)
              }}
            >
              Ainda não possuo uma conta
            </button>
          </>
        )}

        {erro && (
          <p className="login-card__feedback login-card__feedback--error" role="alert">
            {erro}
          </p>
        )}
        {sucesso && (
          <p className="login-card__feedback login-card__feedback--success" role="status">
            {sucesso}
          </p>
        )}
      </section>
    </main>
  )
}

export default Login
