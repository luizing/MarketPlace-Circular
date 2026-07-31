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
