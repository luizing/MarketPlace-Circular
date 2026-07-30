import { useEffect, useMemo, useState } from 'react'
import AnuncioForm from './AnuncioForm'

type Categoria = 'Livros' | 'Xerox' | 'Calculadoras' | 'Eletronicos'
type TipoAnuncio = 'venda' | 'doacao'
type ApiCategoria = 'LIVROS' | 'XEROX' | 'CALCULADORAS' | 'ELETRONICOS'
type ApiTipoAnuncio = 'VENDA' | 'DOACAO'

type ApiAnuncio = {
  id: number
  titulo: string
  descricao: string
  categoria: ApiCategoria
  tipo: ApiTipoAnuncio
  preco: number
  imagem: string
  interessados?: number
}

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
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

function formatarValor(produto: Produto) {
  if (produto.tipo === 'doacao') {
    return 'Doacao'
  }

  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(produto.valor ?? 0)
}

function mapearCategoria(categoria: ApiCategoria): Categoria {
  const categoriasMapeadas: Record<ApiCategoria, Categoria> = {
    LIVROS: 'Livros',
    XEROX: 'Xerox',
    CALCULADORAS: 'Calculadoras',
    ELETRONICOS: 'Eletronicos',
  }

  return categoriasMapeadas[categoria]
}

function mapearAnuncio(anuncio: ApiAnuncio): Produto {
  return {
    id: anuncio.id,
    nome: anuncio.titulo,
    descricao: anuncio.descricao,
    categoria: mapearCategoria(anuncio.categoria),
    tipo: anuncio.tipo === 'DOACAO' ? 'doacao' : 'venda',
    valor: anuncio.preco,
    imagem: anuncio.imagem,
    interessados: anuncio.interessados ?? 0,
  }
}

function obterUsuarioLogadoId() {
  const usuarioSalvo = window.localStorage.getItem('marketplace-circular-user')

  if (!usuarioSalvo) {
    return null
  }

  try {
    const usuario = JSON.parse(usuarioSalvo) as { id?: number }
    return typeof usuario.id === 'number' ? usuario.id : null
  } catch {
    return null
  }
}

function Anuncios() {
  const [produtos, setProdutos] = useState<Produto[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  const [termoBusca, setTermoBusca] = useState('')
  const [categoriasSelecionadas, setCategoriasSelecionadas] = useState<Categoria[]>([])
  const [formularioAberto, setFormularioAberto] = useState(false)
  const [produtoSelecionado, setProdutoSelecionado] = useState<Produto | null>(null)
  const [interessadosPorProduto, setInteressadosPorProduto] = useState<
    Record<number, number>
  >({})
  const [interessesDoUsuario, setInteressesDoUsuario] = useState<Record<number, boolean>>({})
  const [carregandoInteresse, setCarregandoInteresse] = useState(false)
  const [erroInteresse, setErroInteresse] = useState<string | null>(null)

  useEffect(() => {
    async function carregarAnuncios() {
      try {
        setCarregando(true)
        setErro(null)

        const resposta = await fetch(`${apiBaseUrl}/api/anuncios`)

        if (!resposta.ok) {
          throw new Error('Nao foi possivel carregar os anuncios.')
        }

        const anuncios = (await resposta.json()) as ApiAnuncio[]
        const produtosMapeados = anuncios.map(mapearAnuncio)

        setProdutos(produtosMapeados)
        setInteressadosPorProduto((interessadosAtuais) => ({
          ...Object.fromEntries(
            produtosMapeados.map((produto) => [produto.id, produto.interessados]),
          ),
          ...interessadosAtuais,
        }))
      } catch {
        setErro('Nao foi possivel carregar os anuncios no momento.')
      } finally {
        setCarregando(false)
      }
    }

    void carregarAnuncios()
  }, [])

  useEffect(() => {
    if (!produtoSelecionado) {
      return
    }

    const anuncioSelecionado = produtoSelecionado

    const usuarioId = obterUsuarioLogadoId()

    if (usuarioId === null) {
      setInteressesDoUsuario((interessesAtuais) => ({
        ...interessesAtuais,
        [anuncioSelecionado.id]: false,
      }))
      return
    }

    let cancelado = false

    async function carregarInteresse() {
      setCarregandoInteresse(true)
      setErroInteresse(null)

      try {
        const resposta = await fetch(
          `${apiBaseUrl}/api/anuncios/${anuncioSelecionado.id}/interessados/${usuarioId}`,
        )

        if (!resposta.ok) {
          throw new Error('Nao foi possivel verificar seu interesse.')
        }

        const interessado = (await resposta.json()) as boolean

        if (!cancelado) {
          setInteressesDoUsuario((interessesAtuais) => ({
            ...interessesAtuais,
            [anuncioSelecionado.id]: interessado,
          }))
        }
      } catch {
        if (!cancelado) {
          setErroInteresse('Nao foi possivel verificar seu interesse no momento.')
        }
      } finally {
        if (!cancelado) {
          setCarregandoInteresse(false)
        }
      }
    }

    void carregarInteresse()

    return () => {
      cancelado = true
    }
  }, [produtoSelecionado])

  const produtosFiltrados = useMemo(() => {
    const termoNormalizado = termoBusca.trim().toLowerCase()

    return produtos.filter((produto) => {
      const correspondeAoNome = produto.nome.toLowerCase().includes(termoNormalizado)
      const correspondeACategoria =
        categoriasSelecionadas.length === 0 ||
        categoriasSelecionadas.includes(produto.categoria)

      return correspondeAoNome && correspondeACategoria
    })
  }, [produtos, termoBusca, categoriasSelecionadas])

  function alternarCategoria(categoria: Categoria) {
    setCategoriasSelecionadas((selecionadas) =>
      selecionadas.includes(categoria)
        ? selecionadas.filter((item) => item !== categoria)
        : [...selecionadas, categoria],
    )
  }

  async function alternarInteresse(produtoId: number) {
    const usuarioId = obterUsuarioLogadoId()

    if (usuarioId === null) {
      setErroInteresse('Entre na sua conta para demonstrar interesse.')
      return
    }

    const estaInteressado = interessesDoUsuario[produtoId] ?? false
    const metodo = estaInteressado ? 'DELETE' : 'POST'

    setCarregandoInteresse(true)
    setErroInteresse(null)

    try {
      const resposta = await fetch(
        `${apiBaseUrl}/api/anuncios/${produtoId}/interessados/${usuarioId}`,
        { method: metodo },
      )

      if (!resposta.ok) {
        throw new Error('Nao foi possivel atualizar seu interesse.')
      }

      const anuncioAtualizado = (await resposta.json()) as ApiAnuncio
      setInteressadosPorProduto((interessadosAtuais) => ({
        ...interessadosAtuais,
        [produtoId]: anuncioAtualizado.interessados ?? interessadosAtuais[produtoId] ?? 0,
      }))
      setInteressesDoUsuario((interessesAtuais) => ({
        ...interessesAtuais,
        [produtoId]: !estaInteressado,
      }))
    } catch {
      setErroInteresse('Nao foi possivel atualizar seu interesse no momento.')
    } finally {
      setCarregandoInteresse(false)
    }
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

        {carregando && <p className="ads-section__status">Carregando anuncios...</p>}

        {erro && <p className="ads-section__status">{erro}</p>}

        {!carregando && !erro && produtosFiltrados.length === 0 && (
          <p className="ads-section__status">Nenhum anuncio encontrado.</p>
        )}

        {!carregando && !erro && produtosFiltrados.length > 0 && (
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
        )}
      </div>

      {formularioAberto && (
        <AnuncioForm
          onClose={() => setFormularioAberto(false)}
          onCreated={(anuncio) => {
            const produtoCriado = mapearAnuncio(anuncio)

            setProdutos((produtosAtuais) => [produtoCriado, ...produtosAtuais])
            setInteressadosPorProduto((interessadosAtuais) => ({
              ...interessadosAtuais,
              [produtoCriado.id]: produtoCriado.interessados,
            }))
          }}
        />
      )}

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
                Usuarios interessados:{' '}
                {interessadosPorProduto[produtoSelecionado.id] ?? 0}
              </p>
              {erroInteresse && (
                <p className="product-modal__interest-error" role="alert">
                  {erroInteresse}
                </p>
              )}
              <button
                type="button"
                className="product-modal__interest-button"
                disabled={carregandoInteresse}
                onClick={() => void alternarInteresse(produtoSelecionado.id)}
              >
                {interessesDoUsuario[produtoSelecionado.id]
                  ? 'desinteressei'
                  : 'me interessei'}
              </button>
            </div>
          </section>
        </div>
      )}
    </section>
  )
}

export default Anuncios
