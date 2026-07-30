# Endpoints da API

Base URL local prevista:

```text
http://localhost:8080
```

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
  "imagem": "https://exemplo.com/imagem.jpg"
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
  "preco": 45.0,
  "imagem": "https://exemplo.com/imagem.jpg",
  "interessados": 0
}
```

### Listar anuncios

```http
GET /api/anuncios
```

Resposta esperada:

```http
200 OK
Content-Type: application/json
```

```json
[
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
]
```

### Filtrar anuncios

```http
GET /api/anuncios?titulo=calculo&categoria=LIVROS
```

Parametros opcionais:

- `titulo`: filtra anuncios cujo titulo contenha o texto informado.
- `categoria`: filtra pela categoria exata.

Categorias aceitas:

- `LIVROS`
- `XEROX`
- `CALCULADORAS`
- `ELETRONICOS`

Resposta esperada:

```http
200 OK
Content-Type: application/json
```

```json
[
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
]
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
DELETE /api/anuncios/{id}
```

Resposta esperada quando o anuncio existe:

```http
204 No Content
```

Resposta esperada quando o anuncio nao existe:

```http
404 Not Found
```

## Observacoes

- A API trafega dados em JSON.
- Os endpoints de anuncio usam DTOs na entrada e na saida.
- Autenticacao e autorizacao ainda nao foram implementadas nesta etapa.
- Quando a autenticacao for implementada, criacao e delecao de anuncios deverao exigir usuario autenticado.

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
