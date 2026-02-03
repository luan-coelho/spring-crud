# Estrutura de Templates - SST Auditoria

## 📁 Organização de Pastas

Esta aplicação segue as **melhores práticas** do Thymeleaf com Spring Boot, organizando templates, componentes e recursos estáticos de forma modular e escalável.

## 🏗️ Estrutura de Diretórios

```
src/main/resources/
├── templates/                      # Templates Thymeleaf (classpath:/templates/)
│   ├── layouts/                    # Layouts base da aplicação
│   │   └── layout-admin.html      # Layout principal com header, sidebar, footer
│   │
│   ├── views/                      # Views organizadas por módulo/funcionalidade
│   │   ├── dashboard.html         # Página do Dashboard
│   │   └── usuarios/              # Módulo Usuários
│   │       ├── form.html          # Formulário de criação/edição
│   │       ├── lista.html         # Listagem de usuários
│   │       └── visualizar.html    # Visualização de detalhes
│   │
│   ├── components/                 # Componentes UI reutilizáveis
│   │   ├── alert.html             # Alertas e notificações
│   │   ├── avatar.html            # Componente de avatar
│   │   ├── badge.html             # Badges e tags
│   │   ├── breadcrumb.html        # Breadcrumb de navegação
│   │   ├── button.html            # Botões
│   │   ├── card.html              # Cards
│   │   ├── dropdown.html          # Dropdowns
│   │   ├── form.html              # Componentes de formulário
│   │   ├── icon.html              # Ícones
│   │   ├── input.html             # Inputs customizados
│   │   ├── metric.html            # Métricas e estatísticas
│   │   ├── modal.html             # Modais
│   │   ├── pagination.html        # Paginação
│   │   ├── select.html            # Selects customizados
│   │   ├── table.html             # Tabelas
│   │   └── tabs.html              # Abas/Tabs
│   │
│   └── fragments/                  # Fragmentos Thymeleaf (th:fragment)
│       └── components.html        # Coleção de fragmentos comuns
│
└── static/                         # Recursos estáticos
    ├── css/
    │   ├── components/            # Estilos dos componentes
    │   ├── pages/                 # Estilos específicos de páginas
    │   │   └── main.css           # Estilos principais
    │   ├── style.tailwind.css     # Tailwind CSS base
    │   └── style.build.tailwind.css # Tailwind CSS compilado
    ├── js/                        # JavaScript
    └── images/                    # Imagens

```

## 🎯 Convenções e Boas Práticas

### 1. **Layouts** (`layouts/`)

- Contém templates base que definem a estrutura comum das páginas
- Usa `th:fragment` para definir áreas substituíveis
- Exemplo: `layout-admin.html` define o layout geral com header, sidebar, footer

### 2. **Views** (`views/`)

- Organização por **módulo de negócio** ou **funcionalidade**
- Cada módulo em sua própria pasta
- Nomes descritivos: `lista.html`, `form.html`, `visualizar.html`
- **Referência nos Controllers**: `views/usuarios/lista`

### 3. **Components** (`components/`)

- Componentes UI **reutilizáveis** e independentes
- Cada componente em seu próprio arquivo
- Podem ser incluídos com `th:replace` ou `th:insert`
- Exemplo: `<div th:replace="~{components/alert}"></div>`

### 4. **Fragments** (`fragments/`)

- Pequenos fragmentos Thymeleaf marcados com `th:fragment`
- Usados para partes de código que se repetem
- Exemplo: `<div th:fragment="header">...</div>`

### 5. **CSS** (`static/css/`)

- **`components/`**: Estilos isolados por componente
- **`pages/`**: Estilos específicos de páginas/módulos
- Mantém CSS organizado e facilita manutenção

## 🔧 Configuração Spring Boot

No `application.yaml`:

```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8
    cache: false # false em desenvolvimento, true em produção
    check-template-location: true
```

## 💡 Como Usar nos Controllers

### Exemplo 1: Retornar uma página

```java
@GetMapping
public String list(Model model) {
    return "views/usuarios/lista";  // src/main/resources/templates/views/usuarios/lista.html
}
```

### Exemplo 2: Usar layout

```html
<!DOCTYPE html>
<html
  xmlns:th="http://www.thymeleaf.org"
  th:replace="~{layouts/layout-admin :: layout(~{::content}, 'Usuários', 'usuarios')}">
  <div th:fragment="content">
    <!-- Conteúdo da página aqui -->
  </div>
</html>
```

### Exemplo 3: Incluir componente

```html
<div th:replace="~{components/alert}"></div>
```

## 📋 Vantagens desta Estrutura

✅ **Modularidade**: Cada componente em seu lugar, fácil de encontrar  
✅ **Escalabilidade**: Fácil adicionar novos módulos sem bagunça  
✅ **Manutenibilidade**: Código organizado = manutenção simplificada  
✅ **Reutilização**: Componentes podem ser usados em várias páginas  
✅ **Padrão de Mercado**: Segue convenções estabelecidas pela comunidade  
✅ **Separação de Responsabilidades**: Layout, páginas e componentes separados

## 🚀 Adicionando Novo Módulo

1. Crie pasta em `views/`: `views/novo-modulo/`
2. Adicione templates: `lista.html`, `form.html`, etc.
3. Controller retorna: `"views/novo-modulo/lista"`
4. CSS específico em: `static/css/pages/novo-modulo.css`

## 📚 Referências

- [Spring Boot - Thymeleaf](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.template-engines)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.structuring-your-code)

---

**Última atualização**: 03/02/2026  
**Mantido por**: Equipe SST Auditoria
