type ApiErro = {
  mensagem?: string
}

export async function obterMensagemErro(resposta: Response, mensagemPadrao: string) {
  try {
    const corpo = (await resposta.json()) as ApiErro
    return corpo.mensagem?.trim() || mensagemPadrao
  } catch {
    return mensagemPadrao
  }
}
