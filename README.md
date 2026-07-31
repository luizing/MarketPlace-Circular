# MarketPlace-Circular

Marketplace de economia circular para estudantes anunciarem itens para venda ou doacao.

## Diario de Bordo da IA

Decisoes relevantes assistidas por IA estao registradas em [AI_LOG.md](AI_LOG.md).

## Docker Compose

1. Crie o arquivo `.env` na raiz a partir de `.env.example` e defina `POSTGRES_PASSWORD`.
2. Confirme que `BackEnd/marktplaceCircular/.env` contem `JWT_SECRET`.
3. Execute `docker compose up --build`.

A aplicacao estara disponivel em `http://localhost:3000` e a API em `http://localhost:8080`.
