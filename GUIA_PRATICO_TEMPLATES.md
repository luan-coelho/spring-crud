# Guia Prático - Templates Thymeleaf

## 🎨 Exemplos de Uso

### 1. Criando uma Nova Página com Layout

**Arquivo**: `src/main/resources/templates/views/produtos/lista.html`

```html
<!DOCTYPE html>
<html
  xmlns:th="http://www.thymeleaf.org"
  th:replace="~{layouts/layout-admin :: layout(~{::content}, 'Produtos', 'produtos')}">
  <div th:fragment="content">
    <!-- Breadcrumb -->
    <div th:replace="~{components/breadcrumb}"></div>

    <div class="container mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">Lista de Produtos</h1>

      <!-- Tabela de produtos -->
      <div th:replace="~{components/table}"></div>
    </div>
  </div>
</html>
```

**Controller**:

```java
@GetMapping("/produtos")
public String listarProdutos(Model model) {
    model.addAttribute("produtos", produtoService.findAll());
    return "views/produtos/lista";  // Novo caminho
}
```

---

### 2. Usando Componentes Reutilizáveis

#### Alert Component

```html
<!-- Incluir alerta de sucesso -->
<div
  th:if="${mensagemSucesso}"
  th:replace="~{components/alert :: success(${mensagemSucesso})}"></div>

<!-- Incluir alerta de erro -->
<div th:if="${mensagemErro}" th:replace="~{components/alert :: error(${mensagemErro})}"></div>
```

#### Card Component

```html
<div
  th:replace="~{components/card :: card(
    title='Estatísticas',
    content=~{::stats-content}
)}">
  <div th:fragment="stats-content">
    <p>
      Total de usuários:
      <span th:text="${totalUsuarios}">0</span>
    </p>
  </div>
</div>
```

#### Modal Component

```html
<div
  th:replace="~{components/modal :: modal(
    id='deleteModal',
    title='Confirmar Exclusão',
    content=~{::delete-content}
)}">
  <div th:fragment="delete-content">
    <p>Tem certeza que deseja excluir este item?</p>
  </div>
</div>
```

---

### 3. Trabalhando com Fragmentos

**Arquivo**: `src/main/resources/templates/fragments/components.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <!-- Fragment: Header de Página -->
  <div th:fragment="page-header(title, description)">
    <div class="mb-6">
      <h1 class="text-3xl font-bold" th:text="${title}">Título</h1>
      <p class="text-gray-600" th:text="${description}">Descrição</p>
    </div>
  </div>

  <!-- Fragment: Botão de Ação -->
  <a th:fragment="action-button(url, text, icon)" th:href="@{${url}}" class="btn btn-primary">
    <i th:class="'icon-' + ${icon}"></i>
    <span th:text="${text}">Ação</span>
  </a>

  <!-- Fragment: Empty State -->
  <div th:fragment="empty-state(message)">
    <div class="text-center py-12">
      <i class="icon-inbox text-6xl text-gray-400"></i>
      <p class="text-gray-500 mt-4" th:text="${message}">Nenhum registro encontrado</p>
    </div>
  </div>
</html>
```

**Uso**:

```html
<!-- Usar fragment de header -->
<div
  th:replace="~{fragments/components :: page-header(
    title='Meus Produtos',
    description='Gerencie todos os seus produtos'
)}"></div>

<!-- Usar fragment de botão -->
<div
  th:replace="~{fragments/components :: action-button(
    url='/produtos/novo',
    text='Novo Produto',
    icon='plus'
)}"></div>
```

---

### 4. Layout Base Personalizado

**Arquivo**: `src/main/resources/templates/layouts/layout-simple.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:fragment="layout(content, pageTitle)">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title th:text="${pageTitle} + ' | SST'">SST</title>
    <link rel="stylesheet" th:href="@{/css/style.build.tailwind.css}" />
  </head>
  <body>
    <main>
      <div th:replace="${content}"></div>
    </main>

    <script th:src="@{/js/app.js}"></script>
  </body>
</html>
```

**Uso em página de login**:

```html
<!DOCTYPE html>
<html
  xmlns:th="http://www.thymeleaf.org"
  th:replace="~{layouts/layout-simple :: layout(~{::content}, 'Login')}">
  <div th:fragment="content">
    <div class="login-container">
      <h1>Fazer Login</h1>
      <form th:action="@{/login}" method="post">
        <!-- Formulário de login -->
      </form>
    </div>
  </div>
</html>
```

---

### 5. Iteração e Condicionais

```html
<!-- Lista de usuários com componente table -->
<table class="w-full">
  <thead>
    <tr>
      <th>Nome</th>
      <th>Email</th>
      <th>Status</th>
      <th>Ações</th>
    </tr>
  </thead>
  <tbody>
    <tr th:each="usuario : ${usuarios}">
      <td th:text="${usuario.nome}">Nome</td>
      <td th:text="${usuario.email}">email@exemplo.com</td>
      <td>
        <!-- Badge condicional -->
        <span th:if="${usuario.ativo}" th:replace="~{components/badge :: success('Ativo')}"></span>
        <span
          th:unless="${usuario.ativo}"
          th:replace="~{components/badge :: danger('Inativo')}"></span>
      </td>
      <td>
        <a
          th:href="@{/usuarios/{id}(id=${usuario.id})}"
          th:replace="~{components/button :: icon('eye')}"></a>
        <a
          th:href="@{/usuarios/{id}/editar(id=${usuario.id})}"
          th:replace="~{components/button :: icon('edit')}"></a>
      </td>
    </tr>

    <!-- Mensagem quando vazio -->
    <tr th:if="${#lists.isEmpty(usuarios)}">
      <td colspan="4">
        <div
          th:replace="~{fragments/components :: empty-state(
                    'Nenhum usuário cadastrado'
                )}"></div>
      </td>
    </tr>
  </tbody>
</table>
```

---

### 6. Formulários com Validação

```html
<!DOCTYPE html>
<html
  xmlns:th="http://www.thymeleaf.org"
  th:replace="~{layouts/layout-admin :: layout(~{::content}, 'Novo Produto', 'produtos')}">
  <div th:fragment="content">
    <div class="container mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">Cadastrar Produto</h1>

      <form th:action="@{/produtos}" th:object="${produto}" method="post" class="space-y-4">
        <!-- Campo Nome -->
        <div
          th:replace="~{components/input :: text(
                field='nome',
                label='Nome do Produto',
                placeholder='Digite o nome'
            )}"></div>

        <!-- Campo Preço -->
        <div
          th:replace="~{components/input :: number(
                field='preco',
                label='Preço',
                placeholder='0.00'
            )}"></div>

        <!-- Campo Categoria -->
        <div
          th:replace="~{components/select :: select(
                field='categoria',
                label='Categoria',
                options=${categorias}
            )}"></div>

        <!-- Botões -->
        <div class="flex gap-2">
          <button type="submit" th:replace="~{components/button :: primary('Salvar')}"></button>
          <a th:href="@{/produtos}" th:replace="~{components/button :: secondary('Cancelar')}"></a>
        </div>
      </form>
    </div>
  </div>
</html>
```

---

### 7. Paginação

```html
<div th:if="${page.totalPages > 1}">
  <div
    th:replace="~{components/pagination :: pagination(
        page=${page.number},
        totalPages=${page.totalPages},
        url='/usuarios'
    )}"></div>
</div>
```

---

### 8. URL Building

```html
<!-- URLs simples -->
<a th:href="@{/usuarios}">Lista de Usuários</a>

<!-- URLs com parâmetros de path -->
<a th:href="@{/usuarios/{id}(id=${usuario.id})}">Detalhes</a>

<!-- URLs com query parameters -->
<a th:href="@{/usuarios(page=${page.number},size=10)}">Página</a>

<!-- URLs com múltiplos parâmetros -->
<a th:href="@{/usuarios/{id}/editar(id=${usuario.id},returnUrl='/dashboard')}">Editar</a>
```

---

### 9. Recursos Estáticos

```html
<!-- CSS -->
<link rel="stylesheet" th:href="@{/css/pages/dashboard.css}" />
<link rel="stylesheet" th:href="@{/css/components/table.css}" />

<!-- JavaScript -->
<script th:src="@{/js/utils.js}"></script>

<!-- Imagens -->
<img th:src="@{/images/logo.png}" alt="Logo" />
```

---

### 10. Mensagens Flash com RedirectAttributes

**Controller**:

```java
@PostMapping
public String create(@Valid Usuario usuario,
                     BindingResult result,
                     RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
        return "pages/usuarios/form";
    }

    usuarioService.save(usuario);
    redirectAttributes.addFlashAttribute("mensagemSucesso",
        "Usuário cadastrado com sucesso!");
    return "redirect:/usuarios";
}
```

**Template**:

```html
<!-- No topo da página -->
<div th:if="${mensagemSucesso}">
  <div th:replace="~{components/alert :: success(${mensagemSucesso})}"></div>
</div>

<div th:if="${mensagemErro}">
  <div th:replace="~{components/alert :: error(${mensagemErro})}"></div>
</div>
```

---

## 📂 Criando Novo Módulo - Checklist

- [ ] Criar pasta em `templates/views/nome-modulo/`
- [ ] Criar templates: `lista.html`, `form.html`, `visualizar.html`
- [ ] Criar Controller em `controller/NomeModuloController.java`
- [ ] Retornar templates com caminho: `views/nome-modulo/lista`
- [ ] (Opcional) Criar CSS em `static/css/pages/nome-modulo.css`
- [ ] (Opcional) Criar componentes específicos em `components/`
- [ ] Testar todas as rotas

---

## 🎯 Dicas de Performance

1. **Cache em Produção**: Sempre ative cache no `application.yaml` em produção

   ```yaml
   spring.thymeleaf.cache: true
   ```

2. **Fragmentos para Reutilização**: Use fragmentos para evitar duplicação de código

3. **Lazy Loading de Componentes**: Carregue componentes pesados apenas quando necessário

4. **Minimize Requests**: Agrupe CSS e JS quando possível

5. **Use th:replace ao invés de th:include**: Melhor performance e mais semântico

---

Desenvolvido com ❤️ pela equipe SST Auditoria
