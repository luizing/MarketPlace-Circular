import Anuncios from './Pages/Anuncios'
import Login from './Pages/Login'
import placeholderLanding from './assets/PlaceHolder Landing.png'
import './App.css'

function App() {
  if (window.location.pathname === '/login') {
    return <Login />
  }

  return (
    <main className="landing-page" aria-label="Landing page">
      <header className="navbar">
        <a className="navbar__logo" href="#inicio" aria-label="MarketPlace-Circular">
          Logo
        </a>

        <nav className="navbar__links" aria-label="Navegacao principal">
          <a href="#sobre-nos">Sobre nos</a>
          <a href="#anuncios">Anuncios</a>
          <a href="#estatisticas">Estatisticas</a>
          <a className="navbar__login" href="/login">
            Login
          </a>
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
            <img src="/Unifor_logo.svg.webp" alt="Unifor" />
          </div>
          <div className="hero-section__logo-slot">
            <img src="/VORTEX.png" alt="Vortex" />
          </div>
          <div className="hero-section__logo-slot">
            <img src="/luizing.png" alt="Luizing" />
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
            <span>Numero de itens anunciados:</span>
            <strong>128</strong>
          </p>
          <p>
            <span>Numero de itens vendidos:</span>
            <strong>42</strong>
          </p>
          <p>
            <span>Numero de alunos participando:</span>
            <strong>76</strong>
          </p>
        </div>
      </section>
    </main>
  )
}

export default App
