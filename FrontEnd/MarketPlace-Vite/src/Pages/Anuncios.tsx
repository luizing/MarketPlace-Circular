import { useMemo, useState } from 'react'

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
  },
  {
    id: 2,
    nome: 'Xerox de Estruturas de Dados',
    descricao: 'Material impresso com resumos e listas de exercicios.',
    categoria: 'Xerox',
    tipo: 'doacao',
    imagem: 'https://picsum.photos/seed/xerox-dados/640/480',
  },
  {
    id: 3,
    nome: 'Calculadora Cientifica',
    descricao: 'Calculadora funcional para provas e disciplinas de exatas.',
    categoria: 'Calculadoras',
    tipo: 'venda',
    valor: 60,
    imagem: 'https://picsum.photos/seed/calculadora/640/480',
  },
  {
    id: 4,
    nome: 'Kit Arduino Basico',
    descricao: 'Componentes eletronicos para prototipos e projetos de laboratorio.',
    categoria: 'Eletronicos',
    tipo: 'venda',
    valor: 85,
    imagem: 'https://picsum.photos/seed/arduino-kit/640/480',
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

  return (
    <section id="anuncios" className="ads-section" aria-label="Anuncios">
      <div className="ads-section__inner">
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
            <article className="product-card" key={produto.id}>
              <img src={produto.imagem} alt={produto.nome} />
              <div className="product-card__body">
                <h3>{produto.nome}</h3>
                <p>{produto.descricao}</p>
                <span className="product-card__tag">{formatarValor(produto)}</span>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  )
}

export default Anuncios
