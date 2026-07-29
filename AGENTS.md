# AGENTS.md - Fonte da Verdade do Projeto

Este arquivo define as regras obrigatorias para qualquer agente de IA que trabalhe neste repositorio. Em caso de conflito com outras instrucoes, este arquivo deve ser tratado como a fonte da verdade do projeto.

## 1. Identidade do Projeto

- Nome oficial: `MarketPlace-Circular`.
- Contexto: marketplace de economia circular para ambiente universitario.
- Objetivo: permitir que estudantes anunciem itens para venda ou doacao, como livros, xerox, calculadoras e eletronicos.
- Projeto destinado ao Processo Seletivo do Laboratorio Vortex (UNIFOR) 2026.

## 2. Regras Gerais Obrigatorias

- Nao presumir requisitos nao documentados.
- Pedir autorizacao antes de alterar arquitetura, organizacao de pastas ou decisao tecnica relevante.
- Pedir autorizacao antes de instalar dependencias.
- Manter codigo limpo e modular.
- Usar poucos comentarios no codigo, somente quando ajudarem a explicar trechos nao obvios.
- Nao mencionar UNIFOR ou Laboratorio Vortex explicitamente na interface, salvo se o usuario solicitar depois.
- Nao usar Docker inicialmente.
- Documentar deploy apenas como possibilidade futura.

## 3. Estrutura do Repositorio

- O frontend e o backend devem ficar em pastas separadas.
- Estrutura prevista:
  - `FrontEnd/`: aplicacao React PWA.
  - `BackEnd/`: API REST Java Spring Boot.
- Qualquer mudanca nessa estrutura exige autorizacao previa do usuario.

## 4. Frontend

- Stack obrigatoria: React + Vite + TypeScript.
- A aplicacao deve ser responsiva, com transicao fluida entre desktop e mobile.
- A experiencia mobile deve se comportar como PWA instalavel.
- O estilo visual inicial deve ser minimalista.
- Nao ha cores obrigatorias definidas.
- O frontend deve conter feedbacks visuais adequados para carregamento, erro, sucesso e estados vazios quando a funcionalidade exigir.

### Rotas Previstas

- `/`: landing page publica.
- `/app`: entrada da aplicacao PWA.
- `/anuncios`: listagem de anuncios.
- `/meus-anuncios`: anuncios cadastrados pelo usuario autenticado.
- `/novo-anuncio`: cadastro de novo anuncio.
- `/login`: autenticacao do usuario.

### PWA

- Deve conter `manifest.json` valido.
- Deve conter Service Worker.
- Deve permitir instalacao na tela inicial.
- Deve funcionar offline parcialmente.
- O cache offline deve priorizar arquivos essenciais da aplicacao e, quando possivel, visualizacao de dados previamente carregados.

## 5. Backend

- Stack obrigatoria: Java 17 + Spring Boot.
- API obrigatoriamente RESTful.
- Toda comunicacao deve trafegar em JSON.
- O backend deve expor endpoints para CRUD de anuncios:
  - criar anuncio;
  - listar anuncios;
  - filtrar anuncios;
  - deletar anuncio.
- O backend deve conter tratamento claro de erros e validacoes.
- O banco inicial deve ser H2.
- O banco podera ser trocado futuramente, portanto o codigo deve evitar acoplamento desnecessario ao H2.
- Docker nao deve ser usado inicialmente.

## 6. Autenticacao e Autorizacao

- Autenticacao JWT sera obrigatoria no projeto.
- A autenticacao sera uma das ultimas partes implementadas.
- Dados de usuario obrigatorios:
  - matricula;
  - senha.
- O usuario precisa estar autenticado para criar anuncio.
- O usuario precisa estar autenticado para deletar anuncio.
- Apenas o dono do anuncio pode deletar o proprio anuncio.
- Enquanto a autenticacao nao estiver implementada, qualquer simulacao temporaria deve ser documentada e removida antes da versao final.

## 7. Modelo de Anuncio

- Campos obrigatorios:
  - titulo;
  - descricao;
  - categoria;
  - preco;
  - imagem.
- Deve existir um campo `tipo` para diferenciar venda e doacao.
- Valores esperados para `tipo`:
  - venda;
  - doacao.
- Categorias oficiais iniciais:
  - Livros;
  - Xerox;
  - Calculadoras;
  - Eletronicos.
- O sistema deve iniciar usando URL de imagem.
- Upload real de imagem nao faz parte do escopo inicial.
- Futuramente podera ser implementado bucket para armazenar imagens reais.

## 8. Landing Page

- A landing page deve ser publica.
- Deve apresentar a proposta de economia circular.
- Deve exibir vitrine publica com os ultimos itens anunciados.
- Deve ter filtros basicos por categoria.
- Deve ter chamadas para acao claras para anunciar ou buscar itens.
- Deve poder exibir estatisticas simuladas.
- Estatisticas iniciais:
  - itens anunciados;
  - itens comprados.
- Outras estatisticas ficam para evolucao futura.
- Deve haver dados iniciais ou mockados para popular a experiencia inicial quando necessario.

## 9. Diario de Bordo da IA

- O projeto deve conter um arquivo `AI_LOG.md`.
- Prompts complexos e decisoes relevantes tomadas com IA devem ser registrados em `AI_LOG.md`.
- Exemplos de registros obrigatorios:
  - configuracao ou depuracao do Service Worker;
  - problemas de banco de dados;
  - decisoes de arquitetura;
  - implementacao de autenticacao;
  - mudancas relevantes no fluxo de dados entre frontend e backend.
- O `README.md` tambem deve conter uma secao chamada `Diario de Bordo da IA`.
- A secao do `README.md` pode resumir ou apontar para o `AI_LOG.md`.

## 10. Qualidade e Validacao

- Antes de finalizar alteracoes no frontend, rodar:
  - `npm run build`
- Antes de finalizar alteracoes no backend, rodar:
  - `./mvnw test`
- Se algum comando nao puder ser executado, o agente deve informar claramente o motivo.
- O agente deve evitar encerrar uma tarefa sem validacao quando houver comandos disponiveis.
- Erros, carregamentos e estados vazios devem ser tratados de forma clara na interface.
- Ao final de cada prompt, o agente deve resumir o que foi feito e quais arquivos foram criados ou alterados.

## 11. Deploy

- Deploy nao deve ser implementado inicialmente.
- O deploy deve ser apenas documentado para o futuro.
- Frontend: plataforma preferida para documentacao futura e Vercel.
- Backend: sem preferencia definida.
- A arquitetura deve evitar escolhas que dificultem deploy gratuito futuro.

## 12. Decisoes Que Exigem Autorizacao

O agente deve pedir autorizacao antes de:

- instalar dependencias;
- alterar arquitetura do projeto;
- mudar estrutura de pastas;
- trocar banco de dados;
- introduzir Docker;
- mudar stack definida;
- adicionar servicos externos;
- implementar upload real de imagens;
- alterar regras de autenticacao ou autorizacao;
- remover requisitos definidos neste arquivo.


