import './App.css'

function App() {
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
          <a className="navbar__login" href="#login">
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

          <div className="hero-section__image" role="img" aria-label="Imagem placeholder" />
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
      <section id="sobre-nos" className="landing-section" aria-label="Sobre nos" />
      <section id="anuncios" className="landing-section" aria-label="Anuncios" />
      <section
        id="estatisticas"
        className="landing-section"
        aria-label="Estatisticas"
      />
      <section id="login" className="landing-section" aria-label="Login" />
    </main>
  )
}

export default App
