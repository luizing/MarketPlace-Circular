# Deploy do Backend no Render

O backend e implantado como um **Web Service** Docker.

## Configuracao do servico

- Root Directory: `BackEnd/marktplaceCircular`
- Runtime: `Docker`
- Dockerfile Path: `Dockerfile`
- Health Check Path: `/api/estatisticas`
- Regiao: a mesma do PostgreSQL

## Variaveis de ambiente

Configure estes valores no painel do Render:

```text
SPRING_PROFILES_ACTIVE=docker
POSTGRES_JDBC_URL=jdbc:postgresql://HOST_INTERNO:5432/NOME_DO_BANCO
POSTGRES_USERNAME=USUARIO_DO_BANCO
POSTGRES_PASSWORD=SENHA_DO_BANCO
JWT_SECRET=CHAVE_BASE64_COM_PELO_MENOS_32_BYTES
```

Obtenha host, usuario, senha e nome do banco na opcao **Connect** do PostgreSQL no Render. Use os dados da conexao **Internal**. A URL fornecida pelo Render inicia com `postgresql://`; para o Spring Boot, substitua esse prefixo por `jdbc:postgresql://`.

O arquivo `.env` local nao e enviado para o Docker no Render. Os segredos devem ser definidos somente no painel da plataforma.
