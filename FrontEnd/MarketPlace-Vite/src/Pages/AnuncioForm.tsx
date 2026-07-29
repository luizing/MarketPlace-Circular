type AnuncioFormProps = {
  onClose: () => void
}

const categorias = ['Livros', 'Xerox', 'Calculadoras', 'Eletronicos']

function AnuncioForm({ onClose }: AnuncioFormProps) {
  return (
    <div className="ad-form-overlay" role="presentation">
      <section className="ad-form-card" aria-label="Formulario de anuncio">
        <div className="ad-form-card__header">
          <h2>Anunciar produto</h2>
          <button type="button" onClick={onClose} aria-label="Fechar formulario">
            Fechar
          </button>
        </div>

        <form
          className="ad-form-card__form"
          onSubmit={(event) => {
            event.preventDefault()
            onClose()
          }}
        >
          <label>
            <span>Titulo</span>
            <input type="text" name="titulo" required />
          </label>

          <label>
            <span>Descricao</span>
            <textarea name="descricao" rows={4} required />
          </label>

          <label>
            <span>Categoria</span>
            <select name="categoria" required defaultValue="">
              <option value="" disabled>
                Selecione uma categoria
              </option>
              {categorias.map((categoria) => (
                <option key={categoria} value={categoria}>
                  {categoria}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Tipo</span>
            <select name="tipo" required defaultValue="venda">
              <option value="venda">Venda</option>
              <option value="doacao">Doacao</option>
            </select>
          </label>

          <label>
            <span>Preco</span>
            <input type="number" name="preco" min="0" step="0.01" />
          </label>

          <label>
            <span>URL da imagem</span>
            <input type="url" name="imagem" required />
          </label>

          <button type="submit">Salvar anuncio</button>
        </form>
      </section>
    </div>
  )
}

export default AnuncioForm
