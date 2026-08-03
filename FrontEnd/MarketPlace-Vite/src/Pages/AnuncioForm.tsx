import { useState } from 'react'
import {
  obterCabecalhosAutenticados,
  redirecionarParaLogin,
  respostaIndicaSessaoInvalida,
  sessaoEstaValida,
} from '../auth'

type ApiCategoria = 'LIVROS' | 'ELETRONICOS' | 'VESTUARIOS' | 'OUTROS'
type ApiTipoAnuncio = 'VENDA' | 'DOACAO'

type AnuncioCriado = {
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

type AnuncioFormProps = {
  onClose: () => void
  onCreated: (anuncio: AnuncioCriado) => void
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

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

const categorias: Array<{ label: string; value: ApiCategoria }> = [
  { label: 'Livros', value: 'LIVROS' },
  { label: 'Eletronicos', value: 'ELETRONICOS' },
  { label: 'Vestuarios', value: 'VESTUARIOS' },
  { label: 'Outros', value: 'OUTROS' },
]

function obterTexto(formData: FormData, campo: string) {
  return String(formData.get(campo) ?? '').trim()
}

function AnuncioForm({ onClose, onCreated }: AnuncioFormProps) {
  const [enviando, setEnviando] = useState(false)
  const [erro, setErro] = useState<string | null>(null)

  async function criarAnuncio(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!sessaoEstaValida()) {
      redirecionarParaLogin()
      return
    }

    setEnviando(true)
    setErro(null)

    const formData = new FormData(event.currentTarget)
    const precoInformado = Number(formData.get('preco') ?? 0)
    const tipo = obterTexto(formData, 'tipo') as ApiTipoAnuncio

    const payload = {
      titulo: obterTexto(formData, 'titulo'),
      descricao: obterTexto(formData, 'descricao'),
      categoria: obterTexto(formData, 'categoria') as ApiCategoria,
      tipo,
      preco: tipo === 'DOACAO' ? 0 : precoInformado,
      imagem: obterTexto(formData, 'imagem'),
      usuarioId: obterUsuarioLogadoId(),
    }

    try {
      const resposta = await fetch(`${apiBaseUrl}/api/anuncios`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...obterCabecalhosAutenticados(),
        },
        body: JSON.stringify(payload),
      })

      if (!resposta.ok) {
        if (respostaIndicaSessaoInvalida(resposta.status)) {
          return
        }

        throw new Error(
          resposta.status === 409
            ? 'Cada usuario pode criar no maximo 3 anuncios.'
            : 'Nao foi possivel criar o anuncio.',
        )
      }

      const anuncioCriado = (await resposta.json()) as AnuncioCriado
      onCreated(anuncioCriado)
      onClose()
    } catch (error) {
      setErro(
        error instanceof Error
          ? error.message
          : 'Nao foi possivel salvar o anuncio no momento.',
      )
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="ad-form-overlay" role="presentation" onClick={onClose}>
      <section
        className="ad-form-card"
        aria-label="Formulario de anuncio"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="ad-form-card__header">
          <h2>Anunciar produto</h2>
          <button type="button" onClick={onClose} aria-label="Fechar formulario">
            Fechar
          </button>
        </div>

        <form
          className="ad-form-card__form"
          onSubmit={criarAnuncio}
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
                <option key={categoria.value} value={categoria.value}>
                  {categoria.label}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Tipo</span>
            <select name="tipo" required defaultValue="VENDA">
              <option value="VENDA">Venda</option>
              <option value="DOACAO">Doacao</option>
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

          {erro && <p className="ad-form-card__error">{erro}</p>}

          <button type="submit" disabled={enviando}>
            {enviando ? 'Salvando...' : 'Salvar anuncio'}
          </button>
        </form>
      </section>
    </div>
  )
}

export default AnuncioForm
