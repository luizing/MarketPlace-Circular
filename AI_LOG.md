# Diario de Bordo da IA

## 2026-07-31 - Autenticacao JWT

- Decisao: implementar autenticacao stateless com Spring Security e JJWT.
- O token usa o `login` como sujeito e expira em uma hora.
- O segredo e lido da variavel de ambiente `JWT_SECRET`; ele nao deve ser versionado.
- Senhas novas sao armazenadas com BCrypt. Registros legados com senha em texto puro sao convertidos para BCrypt no primeiro login valido.
- Operacoes autenticadas validam que o `usuarioId` solicitado corresponde ao sujeito do token.

## 2026-07-31 - Execucao com Docker Compose

- Decisao: adicionar containers para frontend, backend e PostgreSQL para desenvolvimento local integrado.
- O frontend usa Nginx como servidor estatico e proxy para `/api`, evitando CORS durante a execucao via Compose.
- Credenciais do PostgreSQL e a chave JWT permanecem em arquivos `.env` locais ignorados pelo Git.

## 2026-07-31 - Preparacao para deploy no Render

- O backend passou a usar `PORT`, com fallback local para `8080`, permitindo que o Web Service do Render defina a porta publica.
- O perfil `docker` recebe a conexao PostgreSQL e os segredos por variaveis de ambiente; arquivos `.env` locais nao entram na imagem Docker.

## 2026-08-03 - Validacao e testes de seguranca

- Validacoes de anuncios e usuarios foram centralizadas no backend sem introduzir novas dependencias.
- Titulos, descricoes e URLs de imagem possuem limites; imagens aceitam apenas URLs HTTP ou HTTPS validas.
- Dados invalidos retornam `400` em JSON com uma mensagem clara.
- Foram adicionados testes para a configuracao de CORS, assinatura JWT, permissao de propriedade e validacao dos dados de anuncio.

## 2026-08-03 - Sincronizacao e feedback de erros

- A vitrine de anuncios atualiza em segundo plano sem remover os itens ja visiveis ou exibir indicador de sincronizacao.
- A API retorna erros em JSON para limite de anuncios, credenciais invalidas, falta de autenticacao e tentativas sem permissao.
- O frontend interpreta essas mensagens ao criar contas e anuncios.
- A exclusao de um anuncio exige confirmacao explicita em um dialogo antes da requisicao de remocao.

## 2026-08-03 - Ciclo de vida dos anuncios

- Anuncios possuem os status `DISPONIVEL`, `VENDIDO` e `DOADO`; novos anuncios iniciam como disponiveis.
- A listagem publica e a estatistica de itens disponiveis consideram apenas anuncios ativos, enquanto `Meus Anuncios` preserva o historico.
- O encerramento usa `PATCH /api/anuncios/{id}/status`, identifica o proprietario pelo JWT e aceita apenas `VENDIDO` para vendas ou `DOADO` para doacoes.
- Anuncios encerrados nao aceitam novos interesses.

## 2026-08-03 - Estatisticas por status

- O endpoint de estatisticas passou a expor contagens separadas para anuncios disponiveis, vendidos e doados.
- A landing page exibe as tres contagens, alem do total historico de anuncios e de usuarios cadastrados.
- As estatisticas usam cache em memoria e o cache do Service Worker antes da revalidacao periodica em segundo plano.
