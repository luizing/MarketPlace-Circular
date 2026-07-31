import { useEffect, useState } from 'react'
import AnuncioForm from './AnuncioForm'

type Categoria = 'Livros' | 'Eletronicos' | 'Vestuarios' | 'Outros'
type TipoAnuncio = 'venda' | 'doacao'
type ApiCategoria = 'LIVROS' | 'ELETRONICOS' | 'VESTUARIOS' | 'OUTROS'
type ApiTipoAnuncio = 'VENDA' | 'DOACAO'
type FiltroUsuario = 'todos' | 'meus' | 'interessantes'

type ApiPaginaAnuncios = {
  conteudo: ApiAnuncio[]
  pagina: number
  tamanho: number
  totalItens: number
  totalPaginas: number
  primeira: boolean
  ultima: boolean
}

type ApiAnuncio = {
  id: number
  titulo: string
  descricao: string
  categoria: ApiCategoria
  tipo: ApiTipoAnuncio
  preco: number
  imagem: string
  usuarioId?: number
  interessados: number
}

type Produto = {
  id: number
  nome: string
  descricao: string
  categoria: Categoria
  tipo: TipoAnuncio
  valor?: number
  imagem: string
  usuarioId?: number
  interessados: number
}

type Interessado = {
  id: number
  login: string
  contato: string
}

const categorias: Categoria[] = ['Livros', 'Eletronicos', 'Vestuarios', 'Outros']
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
const TOKEN_STORAGE_KEY = 'marketplace-circular-token'

function obterCabecalhosAutenticados(): Record<string, string> {
  const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)

  return token ? { Authorization: `Bearer ${token}` } : {}
}

function obterColunasAnuncios() {
  if (window.innerWidth <= 720) {
    return 1
  }

  if (window.innerWidth <= 900) {
    return 2
  }

  return 4
}

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
    ELETRONICOS: 'Eletronicos',
    VESTUARIOS: 'Vestuarios',
    OUTROS: 'Outros',
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
    usuarioId: anuncio.usuarioId,
    interessados: anuncio.interessados,
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

function obterEndpointAnuncios(filtro: FiltroUsuario, usuarioId: number | null) {
  if (usuarioId === null || filtro === 'todos') {
    return `${apiBaseUrl}/api/anuncios`
  }

  const recurso = filtro === 'meus' ? 'anuncios' : 'interessados'
  return `${apiBaseUrl}/api/users/${usuarioId}/${recurso}`
}

function mapearCategoriaParaApi(categoria: Categoria): ApiCategoria {
  const categoriasMapeadas: Record<Categoria, ApiCategoria> = {
    Livros: 'LIVROS',
    Eletronicos: 'ELETRONICOS',
    Vestuarios: 'VESTUARIOS',
    Outros: 'OUTROS',
  }

  return categoriasMapeadas[categoria]
}

function Anuncios() {
  const [produtos, setProdutos] = useState<Produto[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  const [termoBusca, setTermoBusca] = useState('')
  const [categoriasSelecionadas, setCategoriasSelecionadas] = useState<Categoria[]>([])
  const [filtroUsuario, setFiltroUsuario] = useState<FiltroUsuario>('todos')
  const [colunasAnuncios, setColunasAnuncios] = useState(obterColunasAnuncios)
  const [paginaAtual, setPaginaAtual] = useState(0)
  const [totalPaginas, setTotalPaginas] = useState(0)
  const [totalItens, setTotalItens] = useState(0)
  const [atualizacao, setAtualizacao] = useState(0)
  const [formularioAberto, setFormularioAberto] = useState(false)
  const [produtoSelecionado, setProdutoSelecionado] = useState<Produto | null>(null)
  const [interessesDoUsuario, setInteressesDoUsuario] = useState<Record<number, boolean>>({})
  const [carregandoInteresse, setCarregandoInteresse] = useState(false)
  const [erroInteresse, setErroInteresse] = useState<string | null>(null)
  const [anuncioInteressados, setAnuncioInteressados] = useState<Produto | null>(null)
  const [interessados, setInteressados] = useState<Interessado[]>([])
  const [carregandoInteressados, setCarregandoInteressados] = useState(false)
  const [erroInteressados, setErroInteressados] = useState<string | null>(null)
  const [apagandoId, setApagandoId] = useState<number | null>(null)

  const usuarioLogadoId = obterUsuarioLogadoId()
  const anunciosPorPagina = colunasAnuncios * 3

  useEffect(() => {
    function atualizarColunas() {
      setColunasAnuncios(obterColunasAnuncios())
    }

    window.addEventListener('resize', atualizarColunas)
    return () => window.removeEventListener('resize', atualizarColunas)
  }, [])

  useEffect(() => {
    async function carregarAnuncios() {
      try {
        setCarregando(true)
        setErro(null)

        const parametros = new URLSearchParams({
          pagina: String(paginaAtual),
          tamanho: String(anunciosPorPagina),
        })

        const titulo = termoBusca.trim()

        if (titulo) {
          parametros.set('titulo', titulo)
        }

        categoriasSelecionadas.forEach((categoria) => {
          parametros.append('categoria', mapearCategoriaParaApi(categoria))
        })

        const endpoint = obterEndpointAnuncios(filtroUsuario, usuarioLogadoId)
        const resposta = await fetch(`${endpoint}?${parametros}`, {
          headers: filtroUsuario === 'todos' ? {} : obterCabecalhosAutenticados(),
        })

        if (!resposta.ok) {
          throw new Error('Nao foi possivel carregar os anuncios.')
        }

        const anuncios = (await resposta.json()) as ApiPaginaAnuncios
        const produtosMapeados = anuncios.conteudo.map(mapearAnuncio)

        setProdutos(produtosMapeados)
        setTotalPaginas(anuncios.totalPaginas)
        setTotalItens(anuncios.totalItens)
      } catch {
        setErro('Nao foi possivel carregar os anuncios no momento.')
      } finally {
        setCarregando(false)
      }
    }

    void carregarAnuncios()
  }, [
    anunciosPorPagina,
    atualizacao,
    categoriasSelecionadas,
    filtroUsuario,
    paginaAtual,
    termoBusca,
    usuarioLogadoId,
  ])

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
          { headers: obterCabecalhosAutenticados() },
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

  useEffect(() => {
    setPaginaAtual(0)
  }, [termoBusca, categoriasSelecionadas, filtroUsuario, colunasAnuncios])

  useEffect(() => {
    setPaginaAtual((pagina) => Math.min(pagina, Math.max(totalPaginas - 1, 0)))
  }, [totalPaginas])

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
        { method: metodo, headers: obterCabecalhosAutenticados() },
      )

      if (!resposta.ok) {
        throw new Error('Nao foi possivel atualizar seu interesse.')
      }

      const anuncioAtualizado = (await resposta.json()) as ApiAnuncio
      setProdutos((produtosAtuais) =>
        filtroUsuario === 'interessantes' && estaInteressado
          ? produtosAtuais.filter((produto) => produto.id !== produtoId)
          : produtosAtuais.map((produto) =>
              produto.id === produtoId
                ? { ...produto, interessados: anuncioAtualizado.interessados }
                : produto,
            ),
      )
      if (filtroUsuario === 'interessantes' && estaInteressado) {
        setProdutoSelecionado(null)
      }
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

  async function apagarAnuncio(produtoId: number) {
    if (usuarioLogadoId === null) {
      return
    }

    setApagandoId(produtoId)
    setErro(null)

    try {
      const resposta = await fetch(
        `${apiBaseUrl}/api/anuncios/${produtoId}?usuarioId=${usuarioLogadoId}`,
        { method: 'DELETE', headers: obterCabecalhosAutenticados() },
      )

      if (!resposta.ok) {
        throw new Error('Nao foi possivel apagar o anuncio.')
      }

      setProdutos((produtosAtuais) =>
        produtosAtuais.filter((produto) => produto.id !== produtoId),
      )
      if (produtoSelecionado?.id === produtoId) {
        setProdutoSelecionado(null)
      }
    } catch {
      setErro('Nao foi possivel apagar o anuncio no momento.')
    } finally {
      setApagandoId(null)
    }
  }

  async function mostrarInteressados(produto: Produto) {
    setAnuncioInteressados(produto)
    setInteressados([])
    setErroInteressados(null)
    setCarregandoInteressados(true)

    try {
      const resposta = await fetch(
        `${apiBaseUrl}/api/anuncios/${produto.id}/interessados?usuarioId=${usuarioLogadoId}`,
        { headers: obterCabecalhosAutenticados() },
      )

      if (!resposta.ok) {
        throw new Error('Nao foi possivel carregar os interessados.')
      }

      setInteressados((await resposta.json()) as Interessado[])
    } catch {
      setErroInteressados('Nao foi possivel carregar os interessados no momento.')
    } finally {
      setCarregandoInteressados(false)
    }
  }

  function mudarPagina(pagina: number) {
    setPaginaAtual(pagina)
    document.getElementById('anuncios')?.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    })
  }

  function abrirFormularioAnuncio() {
    if (usuarioLogadoId === null) {
      window.location.href = '/login'
      return
    }

    setFormularioAberto(true)
  }

  const catalogoVazio =
    filtroUsuario === 'todos' &&
    termoBusca.trim() === '' &&
    categoriasSelecionadas.length === 0 &&
    totalItens === 0

  return (
    <section id="anuncios" className="ads-section" aria-label="Anuncios">
      <div className="ads-section__inner">
        <div className="ads-section__header">
          <button
            type="button"
            onClick={abrirFormularioAnuncio}
          >
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

          {usuarioLogadoId !== null && (
            <div className="ads-section__user-filters" aria-label="Filtros pessoais">
              <button
                type="button"
                aria-pressed={filtroUsuario === 'interessantes'}
                onClick={() =>
                  setFiltroUsuario((filtro) =>
                    filtro === 'interessantes' ? 'todos' : 'interessantes',
                  )
                }
              >
                Interessantes
              </button>
              <button
                type="button"
                aria-pressed={filtroUsuario === 'meus'}
                onClick={() =>
                  setFiltroUsuario((filtro) => (filtro === 'meus' ? 'todos' : 'meus'))
                }
              >
                Meus Anuncios
              </button>
            </div>
          )}
        </div>

        {carregando && <p className="ads-section__status">Carregando anuncios...</p>}

        {erro && <p className="ads-section__status">{erro}</p>}

        {!carregando && !erro && catalogoVazio && (
          <div className="ads-section__empty-state">
            <button type="button" onClick={abrirFormularioAnuncio}>
              seja o primeiro a anunciar!
            </button>
          </div>
        )}

        {!carregando && !erro && produtos.length === 0 && !catalogoVazio && (
          <p className="ads-section__status">Nenhum anuncio encontrado.</p>
        )}

        {!carregando && !erro && produtos.length > 0 && (
          <>
            <div className="ads-section__grid" aria-live="polite">
            {produtos.map((produto) => (
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
            <nav className="ads-pagination" aria-label="Paginação de anuncios">
              <button
                type="button"
                disabled={paginaAtual === 0}
                onClick={() => mudarPagina(paginaAtual - 1)}
              >
                Retornar
              </button>
              <button
                type="button"
                disabled={paginaAtual >= totalPaginas - 1}
                onClick={() => mudarPagina(paginaAtual + 1)}
              >
                Avancar
              </button>
            </nav>
          </>
        )}
      </div>

      {formularioAberto && (
        <AnuncioForm
          onClose={() => setFormularioAberto(false)}
          onCreated={(anuncio) => {
            void anuncio
            setPaginaAtual(0)
            setAtualizacao((versao) => versao + 1)
          }}
        />
      )}

      {anuncioInteressados && (
        <div
          className="product-modal-overlay interested-modal-overlay"
          role="presentation"
          onClick={(event) => {
            if (event.target === event.currentTarget) {
              setAnuncioInteressados(null)
            }
          }}
        >
          <section
            className="product-modal interested-modal"
            aria-label="Usuarios interessados"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="product-modal__header">
              <h2>Interessados em {anuncioInteressados.nome}</h2>
              <button
                type="button"
                onClick={() => setAnuncioInteressados(null)}
                aria-label="Fechar lista de interessados"
              >
                Fechar
              </button>
            </div>

            {carregandoInteressados && <p>Carregando interessados...</p>}
            {erroInteressados && <p className="product-modal__interest-error">{erroInteressados}</p>}
            {!carregandoInteressados && !erroInteressados && interessados.length === 0 && (
              <p>Nenhum usuario demonstrou interesse ainda.</p>
            )}
            {!carregandoInteressados && !erroInteressados && interessados.length > 0 && (
              <ul className="interested-modal__list">
                {interessados.map((interessado) => (
                  <li key={interessado.id}>
                    <span className="interested-modal__email-icon" aria-hidden="true">
                      ✉
                    </span>
                    <span>{interessado.contato}</span>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      )}

      {produtoSelecionado && (
        <div
          className="product-modal-overlay"
          role="presentation"
          onClick={(event) => {
            if (event.target === event.currentTarget) {
              setProdutoSelecionado(null)
            }
          }}
        >
          <section
            className="product-modal"
            aria-label="Detalhes do produto"
            onClick={(event) => event.stopPropagation()}
          >
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
              {usuarioLogadoId !== null &&
              produtoSelecionado.usuarioId === usuarioLogadoId ? (
                <>
                  <p className="product-modal__interest-count">
                    {produtoSelecionado.interessados} usuario(s) marcaram interesse
                  </p>
                  <div className="product-card__actions">
                    <button
                      type="button"
                      onClick={() => void mostrarInteressados(produtoSelecionado)}
                    >
                      mostrar interessados
                    </button>
                    <button
                      type="button"
                      className="product-card__delete"
                      disabled={apagandoId === produtoSelecionado.id}
                      onClick={() => void apagarAnuncio(produtoSelecionado.id)}
                    >
                      {apagandoId === produtoSelecionado.id
                        ? 'Apagando...'
                        : 'Apagar anuncio'}
                    </button>
                  </div>
                </>
              ) : (
                <>
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
                </>
              )}
            </div>
          </section>
        </div>
      )}
    </section>
  )
}

export default Anuncios
