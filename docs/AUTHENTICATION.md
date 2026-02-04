# Modelo de Autenticação - Sessão Persistida

Este projeto migrou de autenticação JWT Stateless para um modelo de autenticação baseada em sessão persistida no banco de dados, inspirado no **Better Auth**.

## Como Funciona

1.  **Login/Registro**:
    - O cliente envia credenciais.
    - O servidor valida e cria um registro na tabela `sessao` no banco de dados.
    - Um token opaco seguro (não-JWT) é gerado.
    - O servidor retorna o token no corpo da resposta e também define um cookie `HttpOnly` chamado `session_token`.

2.  **Autenticação das Requisições**:
    - O cliente faz requisições enviando o token via:
      - Cookie `session_token` (automático em browsers)
      - Header `Authorization: Bearer <token>`
    - O `SessionAuthenticationFilter` intercepta a requisição.
    - Consulta a tabela `sessao` para validar o token e verificar expiração.
    - Se válido, autentica o usuário no Spring Security.

3.  **Logout**:
    - Remove o registro da sessão do banco de dados (revogação imediata).
    - Limpa o cookie no navegador.

## Vantagens

- **Revogação Imediata**: É possível desconectar um usuário instantaneamente (banimento, troca de senha, roubo de dispositivo).
- **Controle de Sessões**: O usuário pode ver e gerenciar onde está logado (listar dispositivos).
- **Segurança**: Cookies `HttpOnly` protegem contra XSS. Tokens opacos não expõem dados internos.

## Endpoints

- `POST /api/auth/login`: Cria nova sessão.
- `POST /api/auth/cadastrar`: Cria usuário e sessão.
- `POST /api/auth/logout`: Revoga sessão atual.
- `POST /api/auth/logout-todos-dispositivos`: Revoga todas as sessões do usuário.
- `GET /api/auth/pegar-usuario-logado`: Retorna dados do usuário atual.
- `GET /api/auth/sessoes`: Lista sessões ativas do usuário.
- `DELETE /api/auth/sessoes/{id}`: Revoga uma sessão específica.

## Configuração

A configuração de validade da sessão está em `src/main/resources/application.yaml`:

```yaml
session:
  expiration: 604800000 # 7 dias (ms)
  cookie:
    max-age: 604800 # 7 dias (s)
    secure: false # true em produção (HTTPS)
    same-site: Lax
```
