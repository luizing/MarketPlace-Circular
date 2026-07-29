import { useMemo, useState } from 'react'
import AnuncioForm from './AnuncioForm'

type Categoria = 'Livros' | 'Xerox' | 'Calculadoras' | 'Eletronicos'
type TipoAnuncio = 'venda' | 'doacao'

type Produto = {
  id: number
  nome: string
  descricao: string
  categoria: Categoria
  tipo: TipoAnuncio
  valor?: number
  imagem: string
  interessados: number
}

const categorias: Categoria[] = ['Livros', 'Xerox', 'Calculadoras', 'Eletronicos']

const produtosMockados: Produto[] = [
  {
    id: 1,
    nome: 'Livro de Calculo I',
    descricao: 'Livro usado, com poucas marcacoes e em bom estado para estudo.',
    categoria: 'Livros',
    tipo: 'venda',
    valor: 45,
    imagem: 'https://picsum.photos/seed/livro-calculo/640/480',
    interessados: 7,
  },
  {
    id: 2,
    nome: 'Xerox de Estruturas de Dados',
    descricao: 'Material impresso com resumos e listas de exercicios.',
    categoria: 'Xerox',
    tipo: 'doacao',
    imagem: 'https://picsum.photos/seed/xerox-dados/640/480',
    interessados: 12,
  },
  {
    id: 3,
    nome: 'Calculadora Cientifica',
    descricao: 'Calculadora funcional para provas e disciplinas de exatas.',
    categoria: 'Calculadoras',
    tipo: 'venda',
    valor: 60,
    imagem: 'https://picsum.photos/seed/calculadora/640/480',
    interessados: 4,
  },
  {
    id: 4,
    nome: 'Kit Arduino Basico',
    descricao: 'Componentes eletronicos para prototipos e projetos de laboratorio.',
    categoria: 'Eletronicos',
    tipo: 'venda',
    valor: 85,
    imagem: 'https://picsum.photos/seed/arduino-kit/640/480',
    interessados: 9,
  },
]

function formatarValor(produto: Produto) {
  if (produto.tipo === 'doacao') {
    return 'Doacao'
  }

  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(produto.valor ?? 0)
}

function Anuncios() {
  const [termoBusca, setTermoBusca] = useState('')
  const [categoriasSelecionadas, setCategoriasSelecionadas] = useState<Categoria[]>([])
  const [formularioAberto, setFormularioAberto] = useState(false)
  const [produtoSelecionado, setProdutoSelecionado] = useState<Produto | null>(null)
  const [interessadosPorProduto, setInteressadosPorProduto] = useState(() =>
    Object.fromEntries(
      produtosMockados.map((produto) => [produto.id, produto.interessados]),
    ) as Record<number, number>,
  )

  const produtosFiltrados = useMemo(() => {
    const termoNormalizado = termoBusca.trim().toLowerCase()

    return produtosMockados.filter((produto) => {
      const correspondeAoNome = produto.nome.toLowerCase().includes(termoNormalizado)
      const correspondeACategoria =
        categoriasSelecionadas.length === 0 ||
        categoriasSelecionadas.includes(produto.categoria)

      return correspondeAoNome && correspondeACategoria
    })
  }, [termoBusca, categoriasSelecionadas])

  function alternarCategoria(categoria: Categoria) {
    setCategoriasSelecionadas((selecionadas) =>
      selecionadas.includes(categoria)
        ? selecionadas.filter((item) => item !== categoria)
        : [...selecionadas, categoria],
    )
  }

  function registrarInteresse(produtoId: number) {
    setInteressadosPorProduto((interessadosAtuais) => ({
      ...interessadosAtuais,
      [produtoId]: interessadosAtuais[produtoId] + 1,
    }))
  }

  return (
    <section id="anuncios" className="ads-section" aria-label="Anuncios">
      <div className="ads-section__inner">
        <div className="ads-section__header">
          <button type="button" onClick={() => setFormularioAberto(true)}>
            Anunciar produto
          </button>
        </div>

        <div className="ads-section__controls">
          <label className="ads-section__search">
            <span>Pesquisar por nome</span>
            <input
              type="search"
              value={termoBusca}
              onChange={(event) => setTermoBusca(event.target.value)}
              placeholder="Nome do produto"
            />
          </label>

          <fieldset className="ads-section__categories">
            <legend>Categorias</legend>
            <div className="ads-section__category-list">
              {categorias.map((categoria) => (
                <label key={categoria}>
                  <input
                    type="checkbox"
                    checked={categoriasSelecionadas.includes(categoria)}
                    onChange={() => alternarCategoria(categoria)}
                  />
                  <span>{categoria}</span>
                </label>
              ))}
            </div>
          </fieldset>
        </div>

        <div className="ads-section__grid" aria-live="polite">
          {produtosFiltrados.map((produto) => (
            <button
              type="button"
              className="product-card"
              key={produto.id}
              onClick={() => setProdutoSelecionado(produto)}
            >
              <img src={produto.imagem} alt={produto.nome} />
              <div className="product-card__body">
                <h3>{produto.nome}</h3>
                <p>{produto.descricao}</p>
                <span className="product-card__tag">{formatarValor(produto)}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      {formularioAberto && <AnuncioForm onClose={() => setFormularioAberto(false)} />}

      {produtoSelecionado && (
        <div className="product-modal-overlay" role="presentation">
          <section className="product-modal" aria-label="Detalhes do produto">
            <div className="product-modal__header">
              <h2>{produtoSelecionado.nome}</h2>
              <button
                type="button"
                onClick={() => setProdutoSelecionado(null)}
                aria-label="Fechar detalhes do produto"
              >
                Fechar
              </button>
            </div>

            <img src={produtoSelecionado.imagem} alt={produtoSelecionado.nome} />

            <div className="product-modal__content">
              <span className="product-modal__category">
                {produtoSelecionado.categoria}
              </span>
              <p>{produtoSelecionado.descricao}</p>
              <strong>{formatarValor(produtoSelecionado)}</strong>
              <p className="product-modal__interest-count">
                Usuarios interessados: {interessadosPorProduto[produtoSelecionado.id]}
              </p>
              <button
                type="button"
                className="product-modal__interest-button"
                onClick={() => registrarInteresse(produtoSelecionado.id)}
              >
                me interessei
              </button>
            </div>
          </section>
        </div>
      )}
    </section>
  )
}

export default Anuncios
