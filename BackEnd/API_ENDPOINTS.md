# Endpoints da API

Base URL local prevista:

```text
http://localhost:8080
```

## Autenticacao

As operacoes que alteram anuncios e as listagens pessoais exigem um token JWT no cabecalho:

```http
Authorization: Bearer {token}
```

O token e obtido em `POST /api/users/login`, tem duracao de uma hora e representa o usuario autenticado. O `usuarioId` enviado nessas rotas deve corresponder ao usuario do token.

## Anuncios

### Criar anuncio

```http
POST /api/anuncios
Content-Type: application/json
```

Corpo esperado:

```json
{
  "titulo": "Livro de Calculo I",
  "descricao": "Livro usado em bom estado.",
  "categoria": "LIVROS",
  "tipo": "VENDA",
  "preco": 45.0,
  "imagem": "https://exemplo.com/imagem.jpg",
  "usuarioId": 1
}
```

Resposta esperada:

```http
201 Created
Content-Type: application/json
```

```json
{
  "id": 1,
  "titulo": "Livro de Calculo I",
  "descricao": "Livro usado em bom estado.",
  "categoria": "LIVROS",
  "tipo": "VENDA",
  "status": "DISPONIVEL",
  "preco": 45.0,
  "imagem": "https://exemplo.com/imagem.jpg",
  "interessados": 0
}
```

O campo `usuarioId` e obrigatorio na criacao do anuncio. Se ele nao for informado ou nao existir, a API retorna `400 Bad Request`.

Cada usuario pode possuir no maximo 3 anuncios. Uma quarta tentativa retorna `409 Conflict`.

Todo anuncio e criado com status `DISPONIVEL`.

### Listar anuncios

```http
GET /api/anuncios?pagina=0&tamanho=12
```

A listagem publica retorna somente anuncios com status `DISPONIVEL`.

Resposta esperada:

```http
200 OK
Content-Type: application/json
```

```json
{
  "conteudo": [
    {
    "id": 1,
    "titulo": "Livro de Calculo I",
    "descricao": "Livro usado em bom estado.",
    "categoria": "LIVROS",
    "tipo": "VENDA",
    "preco": 45.0,
    "imagem": "https://exemplo.com/imagem.jpg",
    "interessados": 0
    }
  ],
  "pagina": 0,
  "tamanho": 12,
  "totalItens": 1,
  "totalPaginas": 1,
  "primeira": true,
  "ultima": true
}
```

### Filtrar anuncios

```http
GET /api/anuncios?titulo=calculo&categoria=LIVROS&categoria=ELETRONICOS&pagina=0&tamanho=12
```

Parametros opcionais:

- `titulo`: filtra anuncios cujo titulo contenha o texto informado.
- `categoria`: filtra por uma ou mais categorias. Repita o parametro para combinar categorias.
- `pagina`: indice da pagina, iniciado em `0`. O padrao e `0`.
- `tamanho`: quantidade de anuncios por pagina, entre `1` e `50`. O padrao e `12`.

Categorias aceitas:

- `LIVROS`
- `ELETRONICOS`
- `VESTUARIOS`
- `OUTROS`

Resposta esperada:

```http
200 OK
Content-Type: application/json
```

```json
{
  "conteudo": [
    {
    "id": 1,
    "titulo": "Livro de Calculo I",
    "descricao": "Livro usado em bom estado.",
    "categoria": "LIVROS",
    "tipo": "VENDA",
    "preco": 45.0,
    "imagem": "https://exemplo.com/imagem.jpg",
    "interessados": 0
    }
  ],
  "pagina": 0,
  "tamanho": 12,
  "totalItens": 1,
  "totalPaginas": 1,
  "primeira": true,
  "ultima": true
}
```

### Buscar anuncio por ID

```http
GET /api/anuncios/{id}
```

Resposta esperada quando o anuncio existe:

```http
200 OK
Content-Type: application/json
```

```json
{
  "id": 1,
  "titulo": "Livro de Calculo I",
  "descricao": "Livro usado em bom estado.",
  "categoria": "LIVROS",
  "tipo": "VENDA",
  "preco": 45.0,
  "imagem": "https://exemplo.com/imagem.jpg",
  "interessados": 0
}
```

### Demonstrar interesse

```http
POST /api/anuncios/{anuncioId}/interessados/{usuarioId}
```

Adiciona o usuario a lista de interessados do anuncio. Se ele ja estiver na lista, a operacao nao duplica o registro.

O dono do anuncio nao pode demonstrar interesse no proprio anuncio. Nesse caso, a API retorna `403 Forbidden`.

Anuncios encerrados tambem nao aceitam novos interesses e retornam `409 Conflict`.

Resposta esperada:

```http
200 OK
```

O corpo retorna o anuncio atualizado, incluindo a quantidade de interessados.

### Remover interesse

```http
DELETE /api/anuncios/{anuncioId}/interessados/{usuarioId}
```

Remove o usuario da lista de interessados e retorna o anuncio atualizado.

### Consultar interesse do usuario

```http
GET /api/anuncios/{anuncioId}/interessados/{usuarioId}
```

Resposta esperada:

```http
200 OK
Content-Type: application/json
```

```json
true
```

Resposta esperada quando o anuncio nao existe:

```http
404 Not Found
```

### Deletar anuncio

```http
DELETE /api/anuncios/{id}?usuarioId={usuarioId}
```

O `usuarioId` deve ser o dono do anuncio.

Resposta esperada quando o anuncio existe e pertence ao usuario:

```http
204 No Content
```

Resposta esperada quando o anuncio nao existe:

```http
404 Not Found
```

Resposta esperada quando o usuario nao e o dono:

```http
403 Forbidden
```

### Encerrar anuncio

```http
PATCH /api/anuncios/{id}/status
Authorization: Bearer {token}
Content-Type: application/json
```

Corpo esperado para venda:

```json
{
  "status": "VENDIDO"
}
```

Para doacao, o status esperado e `DOADO`. Apenas o dono autenticado pode encerrar o anuncio. Anuncios encerrados permanecem visiveis em `Meus Anuncios`, mas deixam de aparecer na listagem publica e nao aceitam novos interesses.

### Listar interessados de um anuncio

```http
GET /api/anuncios/{id}/interessados?usuarioId={usuarioId}
```

O `usuarioId` deve ser o dono do anuncio.

Resposta esperada:

```json
[
  {
    "id": 2,
    "login": "aluno123",
    "contato": "aluno@example.com"
  }
]
```

## Observacoes

- A API trafega dados em JSON.
- Os endpoints de anuncio usam DTOs na entrada e na saida.
- Autenticacao e autorizacao usam JWT nas operacoes protegidas.
- Criacao, delecao e encerramento de anuncios exigem usuario autenticado; apenas o dono pode deletar ou encerrar o proprio anuncio.

### Listar anuncios do usuario

```http
GET /api/users/{id}/anuncios?pagina=0&tamanho=12
```

Retorna apenas os anuncios associados ao usuario informado, no mesmo formato paginado de `GET /api/anuncios`.

### Listar anuncios interessantes do usuario

```http
GET /api/users/{id}/interessados?pagina=0&tamanho=12
```

Retorna os anuncios marcados como interessantes pelo usuario, no mesmo formato paginado de `GET /api/anuncios`.

## Usuarios

### Criar conta

```http
POST /api/users
Content-Type: application/json
```

Corpo esperado:

```json
{
  "login": "aluno123",
  "senha": "senha-do-usuario",
  "contato": "aluno@example.com"
}
```

Quando o login ainda nao estiver cadastrado, os dados sao persistidos no banco e a API retorna:

```http
201 Created
Content-Type: application/json
```

```json
{
  "id": 1,
  "login": "aluno123",
  "contato": "aluno@example.com"
}
```

Se o login ja estiver cadastrado:

```http
409 Conflict
```

### Verificar login

```http
POST /api/users/login
Content-Type: application/json
```

Corpo esperado:

```json
{
  "login": "aluno123",
  "senha": "senha-do-usuario"
}
```

Credenciais validas retornam `200 OK` com os dados publicos do usuario. Credenciais invalidas retornam `401 Unauthorized`.

Resposta de credenciais validas:

```json
{
  "id": 1,
  "login": "aluno123",
  "contato": "aluno@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Estatisticas

### Consultar estatisticas

```http
GET /api/estatisticas
```

Resposta esperada:

```json
{
  "itensAnunciados": 12,
  "alunosParticipando": 8,
  "itensDisponiveis": 7,
  "itensVendidos": 3,
  "itensDoados": 2
}
```

`itensAnunciados` representa o total historico de anuncios criados e nao diminui quando um anuncio e apagado. `itensDisponiveis`, `itensVendidos` e `itensDoados` contam, respectivamente, os anuncios com status `DISPONIVEL`, `VENDIDO` e `DOADO`.
