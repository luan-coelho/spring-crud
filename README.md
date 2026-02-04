# SST Auditoria - API REST

API REST desenvolvida com Spring Boot 4.0 para gerenciamento de usuários e autenticação.

## 🚀 Tecnologias

- **Java 25**
- **Spring Boot 4.0.2**
- **Spring Security** com JWT
- **Spring Data JPA**
- **PostgreSQL**
- **MapStruct** para mapeamento de DTOs
- **Lombok** para redução de boilerplate
- **Bean Validation** para validações

## 📋 Pré-requisitos

- Java 25
- PostgreSQL 12+
- Gradle 9.3+

## ⚙️ Configuração

### Banco de Dados

Configure o PostgreSQL no arquivo `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sst-spring
    username: postgres
    password: postgres
```

### JWT

A chave secreta JWT está configurada em `application.yaml`. **IMPORTANTE**: Altere para produção!

```yaml
jwt:
  secret: sst-auditoria-jwt-secret-key-deve-ter-pelo-menos-256-bits-para-ser-seguro
  expiration: 86400000 # 24 horas
```

### CORS

Por padrão, a API aceita requisições de:

- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)

Configure em `application.yaml` conforme necessário.

## 🏗️ Arquitetura

### Camadas

```
┌─────────────────┐
│   Controllers   │  ← Thin controllers (apenas delegação)
├─────────────────┤
│    Services     │  ← Lógica de negócio e transações
├─────────────────┤
│   Repositories  │  ← Acesso a dados
├─────────────────┤
│    Entities     │  ← Modelos de domínio
└─────────────────┘
```

### DTOs com Records

Todos os DTOs são **Java Records** imutáveis:

- **Request**: Entrada de dados (validações com Bean Validation)
- **Response**: Saída de dados (mapeados com MapStruct)

### Padrão de Respostas

#### Sucesso

- **GET/POST com dados**: `200 OK` ou `201 Created` + body JSON
- **PUT/PATCH/DELETE**: `204 No Content` (sem body)

#### Erro

Todos os erros retornam `ErrorResponse`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descrição do erro",
  "path": "/api/auth/login",
  "timestamp": "2026-02-03T21:55:00",
  "fieldErrors": {
    "email": "O e-mail é obrigatório"
  }
}
```

## 📡 Endpoints

### Autenticação

#### POST `/api/auth/login`

Login de usuário.

**Request:**

```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Response:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "id": "uuid",
  "nome": "Nome do Usuário",
  "email": "usuario@example.com",
  "papel": "user",
  "imagem": null
}
```

#### POST `/api/auth/register`

Registro de novo usuário.

**Request:**

```json
{
  "nome": "Nome Completo",
  "email": "novo@example.com",
  "cpf": "12345678900",
  "senha": "senha123",
  "confirmarSenha": "senha123",
  "telefone": "11999999999"
}
```

**Response:** `201 Created` + AuthResponse

#### GET `/api/auth/me`

Dados do usuário autenticado.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK` + AuthResponse (sem token)

#### POST `/api/auth/change-password`

Alteração de senha.

**Headers:** `Authorization: Bearer {token}`

**Request:**

```json
{
  "senhaAtual": "senha123",
  "novaSenha": "novaSenha456",
  "confirmarNovaSenha": "novaSenha456"
}
```

**Response:** `204 No Content`

### Usuários

Todos os endpoints requerem autenticação (`Authorization: Bearer {token}`).

#### GET `/api/usuarios`

Lista todos os usuários (apenas ADMIN).

**Response:** `200 OK`

```json
[
  {
    "id": "uuid",
    "nome": "Nome",
    "email": "email@example.com",
    "cpf": "12345678900",
    "telefone": "11999999999",
    "emailVerificado": true,
    "imagem": null,
    "onboardingCompleto": false,
    "papel": "user",
    "banido": false,
    "motivoBanimento": null,
    "banimentoExpiraEm": null,
    "criadoEm": "2026-02-03T20:00:00",
    "atualizadoEm": "2026-02-03T20:00:00"
  }
]
```

#### GET `/api/usuarios/{id}`

Busca usuário por ID.

**Response:** `200 OK` + UsuarioResponse

#### POST `/api/usuarios`

Cria novo usuário (apenas ADMIN).

**Request:**

```json
{
  "nome": "Nome Completo",
  "email": "usuario@example.com",
  "cpf": "12345678900",
  "telefone": "11999999999",
  "papel": "user"
}
```

**Response:** `201 Created` + UsuarioResponse

#### PUT `/api/usuarios/{id}`

Atualiza usuário completo.

**Request:** UsuarioRequest (todos os campos obrigatórios)

**Response:** `204 No Content`

#### PATCH `/api/usuarios/{id}`

Atualiza usuário parcialmente.

**Request:** UsuarioRequest (campos opcionais)

**Response:** `204 No Content`

#### DELETE `/api/usuarios/{id}`

Remove usuário (apenas ADMIN).

**Response:** `204 No Content`

## 🔒 Segurança

### JWT

- Token válido por 24 horas
- Algoritmo: HS256
- Claims: email, userId, nome, papel

### Roles

- `ROLE_USER`: Usuário padrão
- `ROLE_ADMIN`: Administrador

### CORS

Configurado para aceitar requisições de frontends React/Vite em desenvolvimento.

## 🧪 Executar

```bash
# Compilar
./gradlew build

# Executar
./gradlew bootRun

# Executar em modo dev (com hot reload)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

A API estará disponível em `http://localhost:8080`

## 📝 Boas Práticas Implementadas

✅ **Separation of Concerns**: Controllers thin, lógica no Service  
✅ **DTOs com Records**: Imutabilidade e menos boilerplate  
✅ **MapStruct**: Mapeamento automático e type-safe  
✅ **Tratamento centralizado de exceções**: GlobalExceptionHandler  
✅ **Validações com Bean Validation**: Declarativas nos DTOs  
✅ **Transações**: `@Transactional` nos services  
✅ **Segurança stateless**: JWT sem sessão  
✅ **RESTful**: Verbos HTTP corretos e status codes apropriados

## 🔄 Migração do Thymeleaf

Este projeto foi migrado de uma aplicação fullstack com Thymeleaf para uma API REST pura:

- ❌ Removido: Thymeleaf, HTMX, templates, static files
- ✅ Adicionado: JWT, DTOs Records, MapStruct, CORS
- ✅ Refatorado: Controllers thin, Services com lógica de negócio
- ✅ Padronizado: Respostas JSON, tratamento de erros

## 📚 Próximos Passos

- [ ] Adicionar Swagger/OpenAPI
- [ ] Implementar refresh tokens
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar paginação nas listagens
- [ ] Adicionar logs estruturados
- [ ] Configurar profiles (dev, prod)
- [ ] Implementar rate limiting
- [ ] Adicionar health checks e métricas

## 📄 Licença

Este projeto é privado e proprietário.
