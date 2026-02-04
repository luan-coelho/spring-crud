# API de Organizações - SST Auditoria

## 📋 Visão Geral

Esta API implementa o gerenciamento de organizações baseado no **Better Auth Organization Plugin**, permitindo:

- Gerenciamento multi-tenant de organizações
- Sistema de membros com papéis (owner, admin, member)
- Convites por e-mail
- Papéis dinâmicos com permissões customizadas
- Controle de acesso (RBAC)

---

## 🔐 Autenticação

Todos os endpoints requerem autenticação via JWT Bearer Token:

```http
Authorization: Bearer <token>
```

---

## 📖 Endpoints

### Organização

#### Criar Organização

```http
POST /api/organizacao
```

**Request Body:**

```json
{
  "nome": "Minha Empresa",
  "slug": "minha-empresa",
  "logo": "https://exemplo.com/logo.png",
  "metadados": "{\"plano\": \"pro\"}"
}
```

**Response:** `201 Created`

```json
{
  "id": "org-uuid",
  "nome": "Minha Empresa",
  "slug": "minha-empresa",
  "logo": "https://exemplo.com/logo.png",
  "metadados": "{\"plano\": \"pro\"}",
  "criadoEm": "2024-02-03T22:00:00"
}
```

---

#### Listar Organizações do Usuário

```http
GET /api/organizacao
```

**Response:** `200 OK`

```json
[
  {
    "id": "org-uuid",
    "nome": "Minha Empresa",
    "slug": "minha-empresa",
    "logo": "https://exemplo.com/logo.png",
    "metadados": null,
    "criadoEm": "2024-02-03T22:00:00"
  }
]
```

---

#### Verificar Disponibilidade de Slug

```http
GET /api/organizacao/verificar-slug?slug=minha-empresa
```

**Response:** `200 OK`

```json
{
  "disponivel": true
}
```

---

#### Definir Organização Ativa

```http
POST /api/organizacao/ativar?organizacaoId=org-uuid&sessaoId=sessao-uuid
```

Ou por slug:

```http
POST /api/organizacao/ativar?organizacaoSlug=minha-empresa&sessaoId=sessao-uuid
```

Para limpar organização ativa:

```http
POST /api/organizacao/ativar?sessaoId=sessao-uuid
```

**Response:** `200 OK` ou `204 No Content`

---

#### Obter Organização

```http
GET /api/organizacao/{id}
```

**Response:** `200 OK`

```json
{
  "id": "org-uuid",
  "nome": "Minha Empresa",
  "slug": "minha-empresa",
  "logo": "https://exemplo.com/logo.png",
  "metadados": null,
  "criadoEm": "2024-02-03T22:00:00"
}
```

---

#### Obter Organização Completa

```http
GET /api/organizacao/{id}/completa?limiteMembros=50
```

**Response:** `200 OK`

```json
{
  "id": "org-uuid",
  "nome": "Minha Empresa",
  "slug": "minha-empresa",
  "logo": null,
  "metadados": null,
  "criadoEm": "2024-02-03T22:00:00",
  "membros": [
    {
      "id": "membro-uuid",
      "usuarioId": "user-uuid",
      "usuarioNome": "João Silva",
      "usuarioEmail": "joao@exemplo.com",
      "organizacaoId": "org-uuid",
      "papel": "owner",
      "criadoEm": "2024-02-03T22:00:00"
    }
  ],
  "convites": []
}
```

---

#### Atualizar Organização

```http
PUT /api/organizacao/{id}
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "nome": "Novo Nome",
  "slug": "novo-slug",
  "logo": "https://novo-logo.com/logo.png"
}
```

**Response:** `200 OK`

---

#### Deletar Organização

```http
DELETE /api/organizacao/{id}
```

**Permissão:** `owner`

**Response:** `204 No Content`

---

### Convites

#### Convidar Membro

```http
POST /api/organizacao/{id}/convites
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "email": "novo.membro@exemplo.com",
  "papel": "member",
  "reenviar": false
}
```

**Response:** `201 Created`

```json
{
  "id": "convite-uuid",
  "email": "novo.membro@exemplo.com",
  "convidadorId": "user-uuid",
  "convidadorNome": "João Silva",
  "organizacaoId": "org-uuid",
  "organizacaoNome": "Minha Empresa",
  "papel": "member",
  "status": "pending",
  "criadoEm": "2024-02-03T22:00:00",
  "expiraEm": "2024-02-05T22:00:00"
}
```

---

#### Listar Convites da Organização

```http
GET /api/organizacao/{id}/convites
```

**Response:** `200 OK`

```json
[
  {
    "id": "convite-uuid",
    "email": "novo.membro@exemplo.com",
    "papel": "member",
    "status": "pending",
    ...
  }
]
```

---

#### Listar Meus Convites (Recebidos)

```http
GET /api/organizacao/meus-convites
```

**Response:** `200 OK`

```json
[
  {
    "id": "convite-uuid",
    "organizacaoNome": "Empresa ABC",
    "papel": "member",
    "status": "pending",
    ...
  }
]
```

---

#### Obter Convite

```http
GET /api/organizacao/convites/{id}
```

**Response:** `200 OK`

---

#### Aceitar Convite

```http
POST /api/organizacao/convites/{id}/aceitar
```

**Response:** `200 OK`

```json
{
  "id": "membro-uuid",
  "usuarioId": "user-uuid",
  "usuarioNome": "Maria Santos",
  "usuarioEmail": "maria@exemplo.com",
  "organizacaoId": "org-uuid",
  "papel": "member",
  "criadoEm": "2024-02-03T22:00:00"
}
```

---

#### Cancelar Convite

```http
POST /api/organizacao/convites/{id}/cancelar
```

**Permissão:** `owner` ou `admin`

**Response:** `204 No Content`

---

#### Rejeitar Convite

```http
POST /api/organizacao/convites/{id}/rejeitar
```

**Response:** `204 No Content`

---

### Membros

#### Listar Membros

```http
GET /api/organizacao/{id}/membros?page=0&size=20
```

**Response:** `200 OK` (paginado)

```json
{
  "content": [
    {
      "id": "membro-uuid",
      "usuarioId": "user-uuid",
      "usuarioNome": "João Silva",
      "usuarioEmail": "joao@exemplo.com",
      "organizacaoId": "org-uuid",
      "papel": "owner",
      "criadoEm": "2024-02-03T22:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  ...
}
```

---

#### Adicionar Membro (Direto)

```http
POST /api/organizacao/{id}/membros
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "usuarioId": "user-uuid",
  "papel": "member"
}
```

**Response:** `201 Created`

---

#### Remover Membro

```http
DELETE /api/organizacao/{id}/membros/{membroIdOuEmail}
```

**Permissão:** `owner` ou `admin`

**Response:** `204 No Content`

---

#### Atualizar Papel do Membro

```http
PUT /api/organizacao/{id}/membros/papel
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "membroId": "membro-uuid",
  "papel": "admin"
}
```

**Response:** `200 OK`

---

#### Obter Membro Ativo

```http
GET /api/organizacao/membro-ativo?organizacaoId=org-uuid
```

**Response:** `200 OK`

```json
{
  "id": "membro-uuid",
  "usuarioId": "user-uuid",
  "usuarioNome": "João Silva",
  "usuarioEmail": "joao@exemplo.com",
  "organizacaoId": "org-uuid",
  "papel": "owner",
  "criadoEm": "2024-02-03T22:00:00"
}
```

---

#### Obter Papel do Membro Ativo

```http
GET /api/organizacao/membro-ativo/papel?organizacaoId=org-uuid
```

**Response:** `200 OK`

```json
{
  "papel": "owner"
}
```

---

#### Sair da Organização

```http
POST /api/organizacao/{id}/sair
```

**Response:** `204 No Content`

> **Nota:** O owner não pode sair. Deve transferir a propriedade primeiro.

---

### Papéis Dinâmicos

#### Criar Papel

```http
POST /api/organizacao/{id}/papeis
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "papel": "gerente",
  "permissao": "{\"projeto\": [\"criar\", \"editar\"], \"funcionario\": [\"visualizar\"]}"
}
```

**Response:** `201 Created`

```json
{
  "id": "papel-uuid",
  "organizacaoId": "org-uuid",
  "papel": "gerente",
  "permissao": "{\"projeto\": [\"criar\", \"editar\"], \"funcionario\": [\"visualizar\"]}",
  "criadoEm": "2024-02-03T22:00:00",
  "atualizadoEm": "2024-02-03T22:00:00"
}
```

---

#### Listar Papéis

```http
GET /api/organizacao/{id}/papeis
```

**Response:** `200 OK`

```json
[
  {
    "id": "papel-uuid",
    "organizacaoId": "org-uuid",
    "papel": "gerente",
    "permissao": "...",
    "criadoEm": "2024-02-03T22:00:00",
    "atualizadoEm": "2024-02-03T22:00:00"
  }
]
```

---

#### Obter Papel

```http
GET /api/organizacao/{id}/papeis/{papelIdOuNome}
```

**Response:** `200 OK`

---

#### Atualizar Papel

```http
PUT /api/organizacao/{id}/papeis/{papelIdOuNome}
```

**Permissão:** `owner` ou `admin`

**Request Body:**

```json
{
  "novoNome": "supervisor",
  "permissao": "{\"projeto\": [\"criar\", \"editar\", \"deletar\"]}"
}
```

**Response:** `200 OK`

---

#### Deletar Papel

```http
DELETE /api/organizacao/{id}/papeis/{papelIdOuNome}
```

**Permissão:** `owner` ou `admin`

**Response:** `204 No Content`

---

### Permissões

#### Verificar Permissão

```http
POST /api/organizacao/verificar-permissao?organizacaoId=org-uuid&recurso=projeto&acao=criar
```

**Response:** `200 OK`

```json
{
  "temPermissao": true
}
```

---

## 🔒 Sistema de Papéis

### Papéis Padrão

| Papel    | Descrição                   | Permissões                                       |
| -------- | --------------------------- | ------------------------------------------------ |
| `owner`  | Proprietário da organização | Todas as permissões                              |
| `admin`  | Administrador               | Todas exceto deletar organização e alterar owner |
| `member` | Membro comum                | Apenas leitura                                   |

### Papéis Dinâmicos

Você pode criar papéis customizados com permissões específicas:

```json
{
  "papel": "gerente-rh",
  "permissao": {
    "funcionario": ["criar", "editar", "visualizar"],
    "cargo": ["criar", "editar", "visualizar"],
    "setor": ["visualizar"]
  }
}
```

---

## ⚠️ Códigos de Erro

| Código | Descrição                                         |
| ------ | ------------------------------------------------- |
| `400`  | Requisição inválida (validação falhou)            |
| `401`  | Não autenticado                                   |
| `403`  | Sem permissão para a ação                         |
| `404`  | Recurso não encontrado                            |
| `409`  | Conflito (slug duplicado, membro já existe, etc.) |

---

## 📁 Estrutura de Arquivos

```
src/main/java/br/sst/auditoria/
├── controller/
│   └── OrganizacaoController.java
├── dto/
│   └── organizacao/
│       ├── CriarOrganizacaoRequest.java
│       ├── AtualizarOrganizacaoRequest.java
│       ├── OrganizacaoResponse.java
│       ├── OrganizacaoCompletaResponse.java
│       ├── ConvidarMembroRequest.java
│       ├── ConviteResponse.java
│       ├── AdicionarMembroRequest.java
│       ├── AtualizarPapelMembroRequest.java
│       ├── MembroResponse.java
│       ├── CriarPapelRequest.java
│       ├── AtualizarPapelRequest.java
│       └── PapelOrganizacaoResponse.java
├── model/
│   ├── Organizacao.java
│   ├── Membro.java
│   ├── Convite.java
│   └── PapelOrganizacao.java
├── repository/
│   ├── OrganizacaoRepository.java
│   ├── MembroRepository.java
│   ├── ConviteRepository.java
│   └── PapelOrganizacaoRepository.java
└── service/
    └── OrganizacaoService.java
```

---

## 🎯 Comparação com Better Auth

| Better Auth                          | Spring Boot                                    | Método              |
| ------------------------------------ | ---------------------------------------------- | ------------------- |
| `organization.create()`              | `POST /api/organizacao`                        | Criar organização   |
| `organization.checkSlug()`           | `GET /api/organizacao/verificar-slug`          | Verificar slug      |
| `organization.list()`                | `GET /api/organizacao`                         | Listar organizações |
| `organization.setActive()`           | `POST /api/organizacao/ativar`                 | Definir ativa       |
| `organization.getFullOrganization()` | `GET /api/organizacao/{id}/completa`           | Obter completa      |
| `organization.update()`              | `PUT /api/organizacao/{id}`                    | Atualizar           |
| `organization.delete()`              | `DELETE /api/organizacao/{id}`                 | Deletar             |
| `organization.inviteMember()`        | `POST /api/organizacao/{id}/convites`          | Convidar            |
| `organization.acceptInvitation()`    | `POST /api/organizacao/convites/{id}/aceitar`  | Aceitar             |
| `organization.cancelInvitation()`    | `POST /api/organizacao/convites/{id}/cancelar` | Cancelar            |
| `organization.rejectInvitation()`    | `POST /api/organizacao/convites/{id}/rejeitar` | Rejeitar            |
| `organization.listMembers()`         | `GET /api/organizacao/{id}/membros`            | Listar membros      |
| `organization.addMember()`           | `POST /api/organizacao/{id}/membros`           | Adicionar           |
| `organization.removeMember()`        | `DELETE /api/organizacao/{id}/membros/{id}`    | Remover             |
| `organization.updateMemberRole()`    | `PUT /api/organizacao/{id}/membros/papel`      | Atualizar papel     |
| `organization.getActiveMember()`     | `GET /api/organizacao/membro-ativo`            | Membro ativo        |
| `organization.leave()`               | `POST /api/organizacao/{id}/sair`              | Sair                |
| `organization.createRole()`          | `POST /api/organizacao/{id}/papeis`            | Criar papel         |
| `organization.deleteRole()`          | `DELETE /api/organizacao/{id}/papeis/{id}`     | Deletar papel       |
| `organization.listRoles()`           | `GET /api/organizacao/{id}/papeis`             | Listar papéis       |
| `organization.hasPermission()`       | `POST /api/organizacao/verificar-permissao`    | Verificar permissão |
