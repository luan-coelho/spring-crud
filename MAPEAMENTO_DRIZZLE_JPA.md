# Mapeamento Better Auth + Drizzle → JPA/Hibernate

## 📋 Resumo da Conversão

Este documento descreve o mapeamento das entidades do Better Auth e do schema Drizzle (TypeScript) para JPA/Hibernate (Java).

---

## 🔐 Entidades Better Auth

### 1. Organizacao (organization)

**Tabela:** `organizacao`

| Campo Better Auth | Campo JPA   | Tipo          | Descrição                |
| ----------------- | ----------- | ------------- | ------------------------ |
| `id`              | `id`        | String        | PK - Identificador único |
| `name`            | `nome`      | String        | Nome da organização      |
| `slug`            | `slug`      | String        | Slug único               |
| `logo`            | `logo`      | String        | URL do logo              |
| `metadata`        | `metadados` | TEXT          | Metadados adicionais     |
| `createdAt`       | `criadoEm`  | LocalDateTime | Data de criação          |

**Relacionamentos:**

- `@OneToMany` → `Membro`, `Convite`, `PapelOrganizacao`
- `@OneToMany` → `Endereco`, `Empresa`, `Unidade`, `Setor`, `Cargo`, `Funcionario`

---

### 2. Membro (member)

**Tabela:** `membro`

| Campo Better Auth | Campo JPA     | Tipo          | Descrição                       |
| ----------------- | ------------- | ------------- | ------------------------------- |
| `id`              | `id`          | String        | PK - Identificador único        |
| `userId`          | `usuario`     | Usuario       | FK - Usuário                    |
| `organizationId`  | `organizacao` | Organizacao   | FK - Organização                |
| `role`            | `papel`       | String        | Papel do usuário na organização |
| `createdAt`       | `criadoEm`    | LocalDateTime | Data de criação                 |

**Índices:**

- `membro_organizacao_id_idx` (organizacao_id)
- `membro_usuario_id_idx` (usuario_id)

---

### 3. Convite (invitation)

**Tabela:** `convite`

| Campo Better Auth | Campo JPA     | Tipo          | Descrição                 |
| ----------------- | ------------- | ------------- | ------------------------- |
| `id`              | `id`          | String        | PK - Identificador único  |
| `email`           | `email`       | String        | E-mail do convidado       |
| `inviterId`       | `convidador`  | Usuario       | FK - Usuário que convidou |
| `organizationId`  | `organizacao` | Organizacao   | FK - Organização          |
| `role`            | `papel`       | String        | Papel do convidado        |
| `status`          | `status`      | String        | Status do convite         |
| `createdAt`       | `criadoEm`    | LocalDateTime | Data de criação           |
| `expiresAt`       | `expiraEm`    | LocalDateTime | Data de expiração         |

**Índices:**

- `convite_organizacao_id_idx` (organizacao_id)
- `convite_email_idx` (email)

---

### 4. PapelOrganizacao (organizationRole) - Opcional

**Tabela:** `papel_organizacao`

| Campo Better Auth | Campo JPA      | Tipo          | Descrição                |
| ----------------- | -------------- | ------------- | ------------------------ |
| `id`              | `id`           | String (UUID) | PK - Identificador único |
| `organizationId`  | `organizacao`  | Organizacao   | FK - Organização         |
| `role`            | `papel`        | String        | Nome do papel            |
| `permission`      | `permissao`    | TEXT          | Permissões do papel      |
| `createdAt`       | `criadoEm`     | LocalDateTime | Data de criação          |
| `updatedAt`       | `atualizadoEm` | LocalDateTime | Data de atualização      |

**Índices:**

- `idx_papel_organizacao_organizacao_id` (organizacao_id)
- `idx_papel_organizacao_papel` (papel)

---

### 5. Sessao (session) - Atualizada

**Tabela:** `sessao`

| Campo Better Auth      | Campo JPA          | Tipo          | Descrição                  |
| ---------------------- | ------------------ | ------------- | -------------------------- |
| `id`                   | `id`               | String        | PK - Identificador único   |
| `token`                | `token`            | String        | Token da sessão            |
| `expiresAt`            | `expiraEm`         | LocalDateTime | Expiração                  |
| `ipAddress`            | `enderecoIp`       | String        | IP do cliente              |
| `userAgent`            | `agenteUsuario`    | String        | User Agent                 |
| `impersonatedBy`       | `personificadoPor` | String        | ID do admin personificando |
| `activeOrganizationId` | `organizacaoAtiva` | Organizacao   | **FK** - Organização ativa |
| `userId`               | `usuario`          | Usuario       | FK - Usuário               |
| `createdAt`            | `criadoEm`         | LocalDateTime | Data de criação            |
| `updatedAt`            | `atualizadoEm`     | LocalDateTime | Data de atualização        |

**Índices:**

- `idx_sessao_usuario_id` (usuario_id)
- `idx_sessao_organizacao_ativa_id` (organizacao_ativa_id)
- `idx_sessao_token` (token)

---

## 📊 Entidades de Negócio SST

Todas as entidades de negócio agora têm **relacionamento JPA** com `Organizacao`:

### Antes (String)

```java
@Column(name = "organization_id", nullable = false)
private String organizationId;
```

### Depois (JPA Relationship)

```java
@NotNull(message = "Organização é obrigatória")
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "organizacao_id", nullable = false)
private Organizacao organizacao;
```

### Entidades Atualizadas:

| Entidade      | Relacionamento com Organizacao |
| ------------- | ------------------------------ |
| `Endereco`    | `@ManyToOne → Organizacao`     |
| `Empresa`     | `@ManyToOne → Organizacao`     |
| `Unidade`     | `@ManyToOne → Organizacao`     |
| `Setor`       | `@ManyToOne → Organizacao`     |
| `Cargo`       | `@ManyToOne → Organizacao`     |
| `Funcionario` | `@ManyToOne → Organizacao`     |

---

## 🔗 Diagrama de Relacionamentos

```
┌─────────────────────────────────────────────────────────────────────┐
│                           USUARIO                                   │
│  • ID, Nome, Email, CPF                                             │
│  • Papel (user/admin)                                               │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               │ @OneToMany
               ↓
┌─────────────────────────────────────────────────────────────────────┐
│                            MEMBRO                                    │
│  • usuario_id (FK)                                                   │
│  • organizacao_id (FK)                                               │
│  • papel (owner/admin/member)                                        │
└──────────────┬──────────────────────────────────────────────────────┘
               │
               │ @ManyToOne
               ↓
┌─────────────────────────────────────────────────────────────────────┐
│                         ORGANIZACAO                                  │
│  • ID, Nome, Slug, Logo                                              │
│  • Metadados                                                         │
├─────────────────────────────────────────────────────────────────────┤
│  Relacionamentos Better Auth:                                        │
│  ├── membros: List<Membro>                                          │
│  ├── convites: List<Convite>                                        │
│  └── papeis: List<PapelOrganizacao>                                 │
├─────────────────────────────────────────────────────────────────────┤
│  Relacionamentos de Negócio:                                         │
│  ├── enderecos: List<Endereco>                                      │
│  ├── empresas: List<Empresa>                                        │
│  ├── unidades: List<Unidade>                                        │
│  ├── setores: List<Setor>                                           │
│  ├── cargos: List<Cargo>                                            │
│  └── funcionarios: List<Funcionario>                                │
└─────────────────────────────────────────────────────────────────────┘
               │
               │ @OneToMany
               ↓
┌─────────────────────────────────────────────────────────────────────┐
│                   ENTIDADES DE NEGÓCIO                               │
│                                                                      │
│  EMPRESA ─────────┐                                                  │
│    ↓              │                                                  │
│  UNIDADE ←────────┤                                                  │
│    ↓              │                                                  │
│  SETOR            │                                                  │
│    ↓              │                                                  │
│  FUNCIONARIO ←────┘                                                  │
│    ↓                                                                 │
│  CARGO ───────────┘                                                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔒 Multi-tenancy

O sistema implementa **multi-tenancy por organização**:

1. **Isolamento**: Cada organização tem seus próprios dados
2. **Sessão Ativa**: A sessão guarda qual organização está ativa
3. **Relacionamento JPA**: Integridade referencial garantida pelo banco

### Fluxo de Acesso:

```
1. Usuário faz login
2. Sistema busca membros do usuário
3. Usuário seleciona organização ativa
4. Sessão salva organizacaoAtiva
5. Todas as queries filtram por organizacao.id
```

---

## 📝 Convenções de Nomenclatura

| Aspecto     | Inglês (Better Auth) | Português (JPA) |
| ----------- | -------------------- | --------------- |
| Organização | organization         | organizacao     |
| Membro      | member               | membro          |
| Convite     | invitation           | convite         |
| Papel       | role                 | papel           |
| Permissão   | permission           | permissao       |
| Sessão      | session              | sessao          |

---

## ✅ Compilação

```bash
./gradlew clean compileJava
BUILD SUCCESSFUL
```

Todas as entidades foram compiladas com sucesso!

---

## 📁 Estrutura Final

```
src/main/java/br/sst/auditoria/model/
├── enums/
│   ├── Status.java
│   ├── StatusFuncionario.java
│   └── TipoDocumento.java
│
├── # Better Auth
├── Usuario.java
├── Sessao.java           ← Atualizado com organizacaoAtiva
├── Conta.java
├── Verificacao.java
├── Organizacao.java      ← Hub central
├── Membro.java
├── Convite.java
├── PapelOrganizacao.java ← Novo
│
├── # Negócio SST
├── Endereco.java         ← Vinculado à Organizacao
├── Empresa.java          ← Vinculado à Organizacao
├── Unidade.java          ← Vinculado à Organizacao
├── Setor.java            ← Vinculado à Organizacao
├── Cargo.java            ← Vinculado à Organizacao
└── Funcionario.java      ← Vinculado à Organizacao
```
