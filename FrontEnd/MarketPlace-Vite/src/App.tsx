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

      <section id="inicio" className="landing-section" aria-label="Inicio" />
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
