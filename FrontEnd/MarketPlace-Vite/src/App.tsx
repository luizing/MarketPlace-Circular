import { useEffect, useRef, useState } from 'react'
import Anuncios from './Pages/Anuncios'
import Login from './Pages/Login'
import {
  encerrarSessao,
  obterDuracaoRestanteToken,
  sessaoEstaValida,
} from './auth'
import placeholderLanding from './assets/Landing pic.png'
import './App.css'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

type Estatisticas = {
  itensAnunciados: number
  alunosParticipando: number
  itensDisponiveis: number
}

function App() {
  const [estaLogado, setEstaLogado] = useState(
    () => sessaoEstaValida(),
  )
  const [estatisticas, setEstatisticas] = useState<Estatisticas | null>(null)
  const [erroEstatisticas, setErroEstatisticas] = useState(false)
  const atualizacaoEstatisticasPendente = useRef(false)
  const [versaoAtualizacaoEstatisticas, setVersaoAtualizacaoEstatisticas] = useState(0)

  const sair = () => {
    encerrarSessao()
    setEstaLogado(false)
  }

  useEffect(() => {
    if (!estaLogado) {
      return
    }

    const duracaoRestante = obterDuracaoRestanteToken()

    if (duracaoRestante === 0) {
      sair()
      window.location.href = '/login'
      return
    }

    const temporizador = window.setTimeout(() => {
      sair()
      window.location.href = '/login'
    }, duracaoRestante)

    return () => window.clearTimeout(temporizador)
  }, [estaLogado])

  useEffect(() => {
    async function carregarEstatisticas() {
      try {
        const url = new URL(`${apiBaseUrl}/api/estatisticas`, window.location.origin)

        const atualizarDoServidor = atualizacaoEstatisticasPendente.current
        atualizacaoEstatisticasPendente.current = false

        if (atualizarDoServidor) {
          url.searchParams.set('__atualizar', '1')
        }

        const resposta = await fetch(url)

        if (!resposta.ok) {
          throw new Error('Nao foi possivel carregar as estatisticas.')
        }

        setEstatisticas((await resposta.json()) as Estatisticas)
      } catch {
        setErroEstatisticas(true)
      }
    }

    void carregarEstatisticas()
  }, [versaoAtualizacaoEstatisticas])

  useEffect(() => {
    function atualizarQuandoServidorResponder(event: MessageEvent) {
      if (event.data?.type !== 'api-atualizada') {
        return
      }

      const url = new URL(event.data.url)

      if (url.pathname === '/api/estatisticas') {
        atualizacaoEstatisticasPendente.current = true
        setVersaoAtualizacaoEstatisticas((versao) => versao + 1)
      }
    }

    navigator.serviceWorker?.addEventListener('message', atualizarQuandoServidorResponder)
    return () => {
      navigator.serviceWorker?.removeEventListener('message', atualizarQuandoServidorResponder)
    }
  }, [])

  if (window.location.pathname === '/login') {
    return <Login />
  }

  return (
    <main className="landing-page" aria-label="Landing page">
      <header className="navbar">
        <a className="navbar__logo" href="#inicio" aria-label="MarketPlace-Circular">
          MarketPlace Vortex
        </a>

        <nav className="navbar__links" aria-label="Navegacao principal">
          <a href="#sobre-nos">Sobre nos</a>
          <a href="#anuncios">Anuncios</a>
          <a href="#estatisticas">Estatisticas</a>
          {estaLogado ? (
            <button className="navbar__login navbar__logout" type="button" onClick={sair}>
              Sair
            </button>
          ) : (
            <a className="navbar__login" href="/login">
              Login
            </a>
          )}
        </nav>
      </header>

      <section id="inicio" className="hero-section" aria-label="Inicio">
        <div className="hero-section__content">
          <div className="hero-section__text">
            <h1>Encontre e anuncie itens universitários</h1>
            <p>
              Esse projeto é destinado a ajudar alunos a encontrar itens necessários
              para sua formação academica com outros alunos, evitando a necessidade da compra
              de novos produtos, reduzindo o gasto e a produção de lixo.
            </p>
            <a className="hero-section__button" href="#anuncios">
              Participe Agora!
            </a>
          </div>

          <img
            className="hero-section__image"
            src={placeholderLanding}
            alt="Imagem placeholder da landing page"
          />
        </div>

        <footer className="hero-section__footer" aria-label="Logos parceiras">
          <div className="hero-section__logo-slot">
            <a href='https://unifor.br/'>
            <img src="/Unifor_logo.svg.webp" alt="Unifor" />
            </a>
          </div>
          <div className="hero-section__logo-slot">
            <a href='https://vortex.unifor.br/'>
            <img src="/VORTEX.png" alt="Vortex" />
            </a>
          </div>
          <div className="hero-section__logo-slot">
            <a href='https://github.com/luizing'>
            <img src="/luizing.png" alt="Luizing" />
            </a>
          </div>
        </footer>
      </section>

      <section id="sobre-nos" className="about-section" aria-label="Sobre nos">
        <article className="about-card">
          <img
            className="about-card__image"
            src="https://avatars.githubusercontent.com/u/83519960?v=4"
            alt="Luiz Eduardo Camurca"
          />
          <h3>Luiz Eduardo Camurça</h3>
          <p>
            Estudante de Ciência da Computação na Universidade de Fortaleza. Atua principalmente em projetos web fullstack. Tem esperiência no desenvolvimento de sistemas privados para empresas.
          </p>
          <div className="about-card__links" aria-label="Links de contato">
            <a href="mail.to:luizeduardo2099@edu.unifor.br">Email</a>
            <a href="https://github.com/luizing">GitHub</a>
            <a href="https://www.linkedin.com/in/luizeduardocamurca/">LinkedIn</a>
          </div>
        </article>
      </section>
      <Anuncios />
      <section
        id="estatisticas"
        className="stats-section"
        aria-label="Estatisticas"
      >
        <div className="stats-section__content">
          <p>
            <span>Número de itens anunciados:</span>
            <strong>{estatisticas?.itensAnunciados ?? '-'}</strong>
          </p>
          <p>
            <span>Número de itens disponiveis:</span>
            <strong>{estatisticas?.itensDisponiveis ?? '-'}</strong>
          </p>
          <p>
            <span>Número de colegas participando:</span>
            <strong>{estatisticas?.alunosParticipando ?? '-'}</strong>
          </p>
        </div>
        {erroEstatisticas && (
          <p className="stats-section__error" role="alert">
            Nao foi possivel carregar as estatisticas no momento.
          </p>
        )}
      </section>
    </main>
  )
}

export default App
