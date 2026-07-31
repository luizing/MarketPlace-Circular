# Diario de Bordo da IA

## 2026-07-31 - Autenticacao JWT

- Decisao: implementar autenticacao stateless com Spring Security e JJWT.
- O token usa o `login` como sujeito e expira em uma hora.
- O segredo e lido da variavel de ambiente `JWT_SECRET`; ele nao deve ser versionado.
- Senhas novas sao armazenadas com BCrypt. Registros legados com senha em texto puro sao convertidos para BCrypt no primeiro login valido.
- Operacoes autenticadas validam que o `usuarioId` solicitado corresponde ao sujeito do token.
