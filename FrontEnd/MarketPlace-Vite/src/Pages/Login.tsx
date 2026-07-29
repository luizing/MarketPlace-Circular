function Login() {
  return (
    <main className="login-page" aria-label="Login">
      <section className="login-card">
        <a className="login-card__back" href="/">
          Voltar
        </a>
        <h1>Login</h1>
        <form className="login-card__form">
          <label>
            <span>Login</span>
            <input type="text" name="login" autoComplete="username" />
          </label>

          <label>
            <span>Senha</span>
            <input type="password" name="senha" autoComplete="current-password" />
          </label>

          <button type="submit">Entrar</button>
        </form>

        <a className="login-card__create-account" href="#">
          Criar conta
        </a>
      </section>
    </main>
  )
}

export default Login
