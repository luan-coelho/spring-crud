# UI Components Library

Biblioteca de componentes reutilizáveis baseada no design system Tailwind Admin.

## 📦 Namespace Pattern ("TagLib Fake")

Esta biblioteca utiliza o padrão de fragments parametrizados do Thymeleaf para criar componentes
reutilizáveis similares a TagLibs. Para usar os componentes, declare os namespaces no HTML:

```html
<html xmlns:th="http://www.thymeleaf.org" xmlns:ui="http://www.thymeleaf.org"></html>
```

## 🔘 Buttons (`ui/button.html`)

### Uso Básico

```html
<!-- Primary Button -->
<ui:button th:replace="~{ui/button :: primary(text='Salvar', type='submit')}" />

<!-- Secondary Button -->
<ui:button th:replace="~{ui/button :: secondary(text='Cancelar', type='button')}" />

<!-- Danger Button -->
<ui:button th:replace="~{ui/button :: danger(text='Excluir', type='button')}" />

<!-- Success Button -->
<ui:button th:replace="~{ui/button :: success(text='Confirmar', type='submit')}" />

<!-- Outline Button -->
<ui:button th:replace="~{ui/button :: outline(text='Ver mais', type='button')}" />

<!-- Ghost Button -->
<ui:button th:replace="~{ui/button :: ghost(text='Cancelar', type='button')}" />

<!-- Loading Button -->
<ui:button th:replace="~{ui/button :: loading(text='Processando...')}" />
```

### Buttons com Ícones

```html
<ui:button th:replace="~{ui/button :: withIcon(text='Salvar', type='submit', iconPosition='left')}">
  <th:block th:ref="iconContent">
    <svg>...</svg>
  </th:block>
</ui:button>
```

### Links estilizados como Buttons

```html
<!-- Link com estilo Primary -->
<ui:link th:replace="~{ui/button :: link(href='/usuarios', text='Ver Lista')}" />

<!-- Link com estilo Secondary -->
<ui:link th:replace="~{ui/button :: linkSecondary(href='@{/voltar}', text='Voltar')}" />
```

---

## ⚠️ Alerts (`ui/alert.html`)

### Tipos de Alerta

```html
<!-- Success -->
<ui:alert th:replace="~{ui/alert :: success(title='Sucesso!', message='Operação realizada.')}" />

<!-- Error -->
<ui:alert th:replace="~{ui/alert :: error(title='Erro!', message='Algo deu errado.')}" />

<!-- Warning -->
<ui:alert th:replace="~{ui/alert :: warning(title='Atenção!', message='Verifique os dados.')}" />

<!-- Info -->
<ui:alert th:replace="~{ui/alert :: info(title='Dica', message='Use atalhos do teclado.')}" />
```

### Alerta Dismissível (com botão fechar)

```html
<ui:alert
  th:replace="~{ui/alert :: dismissible(type='success', title='Salvo!', message='Registro atualizado.')}" />
```

---

## 📝 Form Components (`ui/form.html`)

### Inputs

```html
<!-- Input de Texto -->
<ui:form
  th:replace="~{ui/form :: input(id='nome', label='Nome', placeholder='Digite seu nome', required=true)}" />

<!-- Input de Email -->
<ui:form
  th:replace="~{ui/form :: email(id='email', label='E-mail', placeholder='seu@email.com', required=true)}" />

<!-- Input de Senha (com toggle de visibilidade) -->
<ui:form
  th:replace="~{ui/form :: password(id='senha', label='Senha', placeholder='••••••••', required=true)}" />

<!-- Input com Máscara (Alpine.js Mask) -->
<ui:form
  th:replace="~{ui/form :: masked(id='cpf', label='CPF', placeholder='000.000.000-00', required=true, mask='999.999.999-99')}" />

<!-- Textarea -->
<ui:form
  th:replace="~{ui/form :: textarea(id='bio', label='Biografia', placeholder='Conte sobre você...', required=false, rows=5)}" />
```

### Checkbox, Radio e Toggle

```html
<!-- Checkbox -->
<ui:form th:replace="~{ui/form :: checkbox(id='ativo', label='Usuário ativo', checked=true)}" />

<!-- Radio -->
<ui:form
  th:replace="~{ui/form :: radio(id='sexo-m', name='sexo', value='M', label='Masculino', checked=true)}" />
<ui:form
  th:replace="~{ui/form :: radio(id='sexo-f', name='sexo', value='F', label='Feminino', checked=false)}" />

<!-- Toggle/Switch -->
<ui:form
  th:replace="~{ui/form :: toggle(id='notificacoes', label='Receber notificações', checked=true)}" />
```

### Mensagens de Erro e Ajuda

```html
<!-- Mensagem de erro -->
<ui:form th:replace="~{ui/form :: error(message=${#fields.errors('nome')})}" />

<!-- Texto de ajuda -->
<ui:form th:replace="~{ui/form :: help(text='Mínimo 8 caracteres')}" />
```

---

## 📇 Cards (`ui/card.html`)

```html
<!-- Card Básico com Título -->
<ui:card
  th:replace="~{ui/card :: basic(title='Configurações', subtitle='Ajuste suas preferências')}">
  <th:block th:ref="content">
    <!-- Conteúdo do card aqui -->
  </th:block>
</ui:card>

<!-- Card Simples (sem header) -->
<ui:card th:replace="~{ui/card :: simple}">
  <th:block th:ref="content">
    <p>Conteúdo aqui...</p>
  </th:block>
</ui:card>

<!-- Card com Footer -->
<ui:card th:replace="~{ui/card :: withFooter(title='Novo Usuário')}">
  <th:block th:ref="content">
    <!-- Formulário -->
  </th:block>
  <th:block th:ref="footer">
    <button>Cancelar</button>
    <button>Salvar</button>
  </th:block>
</ui:card>

<!-- Card de Estatística -->
<ui:card
  th:replace="~{ui/card :: stat(title='Total Usuários', value='1,234', change='12%', changeType='up')}" />
```

---

## 🏷️ Badges (`ui/badge.html`)

```html
<!-- Tipos de Badge -->
<ui:badge th:replace="~{ui/badge :: success(text='Ativo')}" />
<ui:badge th:replace="~{ui/badge :: error(text='Inativo')}" />
<ui:badge th:replace="~{ui/badge :: warning(text='Pendente')}" />
<ui:badge th:replace="~{ui/badge :: info(text='Novo')}" />
<ui:badge th:replace="~{ui/badge :: primary(text='Premium')}" />
<ui:badge th:replace="~{ui/badge :: gray(text='Rascunho')}" />

<!-- Badge com Dot -->
<ui:badge th:replace="~{ui/badge :: withDot(text='Online', color='success')}" />

<!-- Badge de Contagem -->
<ui:badge th:replace="~{ui/badge :: count(value='9')}" />
```

---

## 📊 Tables (`ui/table.html`)

```html
<ui:table th:replace="~{ui/table :: wrapper}">
  <th:block th:ref="content">
    <ui:table th:replace="~{ui/table :: thead}">
      <th:block th:ref="columns">
        <ui:th th:replace="~{ui/table :: th(text='Nome', align='left')}" />
        <ui:th th:replace="~{ui/table :: th(text='Email', align='left')}" />
        <ui:th th:replace="~{ui/table :: th(text='Status', align='center')}" />
        <ui:th th:replace="~{ui/table :: th(text='Ações', align='center')}" />
      </th:block>
    </ui:table>

    <ui:table th:replace="~{ui/table :: tbody}">
      <th:block th:ref="rows">
        <tr th:each="user : ${users}">
          <ui:td th:replace="~{ui/table :: td(text=${user.nome})}" />
          <ui:td th:replace="~{ui/table :: td(text=${user.email})}" />
          <!-- ... -->
        </tr>
      </th:block>
    </ui:table>
  </th:block>
</ui:table>

<!-- Estado Vazio -->
<ui:table
  th:replace="~{ui/table :: empty(colspan=4, title='Nenhum usuário', description='Crie um novo usuário.')}" />
```

---

## 🪟 Modals (`ui/modal.html`)

### Modal Básico

```html
<ui:modal th:replace="~{ui/modal :: base(id='meuModal', title='Editar Perfil', size='md')}">
  <th:block th:ref="body">
    <!-- Conteúdo do modal -->
  </th:block>
  <th:block th:ref="footer">
    <button @click="$dispatch('close-modal', {id: 'meuModal'})">Cancelar</button>
    <button>Salvar</button>
  </th:block>
</ui:modal>

<!-- Para abrir o modal -->
<button @click="$dispatch('open-modal', {id: 'meuModal'})">Abrir Modal</button>
```

### Modal de Confirmação

```html
<ui:modal
  th:replace="~{ui/modal :: confirm(
  id='confirmDelete', 
  title='Confirmar Exclusão', 
  message='Tem certeza que deseja excluir este registro?',
  confirmText='Sim, Excluir',
  cancelText='Cancelar',
  type='danger'
)}" />

<!-- Para abrir o modal de confirmação -->
<button
  @click="$dispatch('open-confirm', {
  id: 'confirmDelete',
  onConfirm: () => document.getElementById('formDelete').submit()
})">
  Excluir
</button>
```

**Tamanhos disponíveis:** `sm`, `md` (default), `lg`, `xl`, `full`

---

## 🎨 Icons (`ui/icon.html`)

```html
<ui:icon th:replace="~{ui/icon :: edit}" />
<ui:icon th:replace="~{ui/icon :: delete}" />
<ui:icon th:replace="~{ui/icon :: view}" />
<ui:icon th:replace="~{ui/icon :: plus}" />
<ui:icon th:replace="~{ui/icon :: check}" />
<ui:icon th:replace="~{ui/icon :: close}" />
<ui:icon th:replace="~{ui/icon :: arrowLeft}" />
<ui:icon th:replace="~{ui/icon :: arrowRight}" />
<ui:icon th:replace="~{ui/icon :: user}" />
<ui:icon th:replace="~{ui/icon :: users}" />
<ui:icon th:replace="~{ui/icon :: search}" />
<ui:icon th:replace="~{ui/icon :: settings}" />
<ui:icon th:replace="~{ui/icon :: mail}" />
<ui:icon th:replace="~{ui/icon :: download}" />
<ui:icon th:replace="~{ui/icon :: upload}" />
<ui:icon th:replace="~{ui/icon :: save}" />
<ui:icon th:replace="~{ui/icon :: filter}" />
<ui:icon th:replace="~{ui/icon :: refresh}" />
```

---

## � Input Components (`ui/input.html`)

Componentes de input avançados com estados de validação, ícones e tipos especializados.

### Inputs Básicos

```html
<!-- Input de Texto -->
<ui:input
  th:replace="~{ui/input :: text(id='nome', label='Nome', placeholder='Digite seu nome', required=true)}" />

<!-- Input com Estado de Erro -->
<ui:input
  th:replace="~{ui/input :: textWithError(
  id='email', 
  label='E-mail', 
  placeholder='seu@email.com', 
  required=true, 
  value='invalido', 
  hasError=true, 
  errorMessage='E-mail inválido')}" />

<!-- Input com Estado de Sucesso -->
<ui:input
  th:replace="~{ui/input :: textWithSuccess(
  id='email', 
  label='E-mail', 
  placeholder='seu@email.com', 
  required=true, 
  value='valido@email.com', 
  successMessage='E-mail válido!')}" />

<!-- Input Desabilitado -->
<ui:input
  th:replace="~{ui/input :: textDisabled(id='id', label='ID', placeholder='', value='12345')}" />
```

### Inputs com Ícones

```html
<!-- Input com ícone à esquerda -->
<ui:input
  th:replace="~{ui/input :: withLeftIcon(
  id='email', 
  label='E-mail', 
  placeholder='seu@email.com', 
  required=true, 
  iconType='email')}" />

<!-- Tipos de ícone: email, user, search, phone, lock -->
```

### Inputs com Prefixo

```html
<!-- Input com prefixo de texto -->
<ui:input
  th:replace="~{ui/input :: withPrefix(
  id='website', 
  label='Website', 
  placeholder='exemplo.com.br', 
  required=false, 
  prefix='https://')}" />
```

### Inputs Especializados

```html
<!-- Email -->
<ui:input
  th:replace="~{ui/input :: email(id='email', label='E-mail', placeholder='seu@email.com', required=true)}" />

<!-- Senha com toggle de visibilidade -->
<ui:input
  th:replace="~{ui/input :: password(id='senha', label='Senha', placeholder='••••••••', required=true)}" />

<!-- Data -->
<ui:input
  th:replace="~{ui/input :: date(id='nascimento', label='Data de Nascimento', required=true)}" />

<!-- Hora -->
<ui:input th:replace="~{ui/input :: time(id='horario', label='Horário', required=true)}" />

<!-- Número -->
<ui:input
  th:replace="~{ui/input :: number(id='idade', label='Idade', placeholder='0', required=true, min=0, max=120, step=1)}" />

<!-- Busca -->
<ui:input th:replace="~{ui/input :: search(id='busca', placeholder='Buscar usuários...')}" />

<!-- Textarea -->
<ui:input
  th:replace="~{ui/input :: textarea(id='bio', label='Biografia', placeholder='Conte sobre você...', required=false, rows=4)}" />

<!-- Upload de Arquivo -->
<ui:input
  th:replace="~{ui/input :: file(id='avatar', label='Foto de Perfil', accept='image/*', required=false)}" />
```

---

## 🖼️ Avatar Components (`ui/avatar.html`)

Componentes de avatar em vários tamanhos com suporte a status online.

### Tamanhos

```html
<ui:avatar th:replace="~{ui/avatar :: xs(src='/img/user.jpg', alt='João')}" />
<!-- 24px -->
<ui:avatar th:replace="~{ui/avatar :: sm(src='/img/user.jpg', alt='João')}" />
<!-- 32px -->
<ui:avatar th:replace="~{ui/avatar :: md(src='/img/user.jpg', alt='João')}" />
<!-- 40px -->
<ui:avatar th:replace="~{ui/avatar :: lg(src='/img/user.jpg', alt='João')}" />
<!-- 48px -->
<ui:avatar th:replace="~{ui/avatar :: xl(src='/img/user.jpg', alt='João')}" />
<!-- 64px -->
<ui:avatar th:replace="~{ui/avatar :: xxl(src='/img/user.jpg', alt='João')}" />
<!-- 80px -->
```

### Avatar com Status

```html
<!-- Status: online, busy, away, offline -->
<ui:avatar
  th:replace="~{ui/avatar :: withStatus(src='/img/user.jpg', alt='João', status='online')}" />
<ui:avatar
  th:replace="~{ui/avatar :: withStatusLg(src='/img/user.jpg', alt='João', status='busy')}" />
```

### Avatar Placeholder (sem imagem)

```html
<!-- size: xs, sm, md, lg, xl -->
<ui:avatar th:replace="~{ui/avatar :: placeholder(initials='JS', size='md')}" />
```

### Avatar com Nome

```html
<ui:avatar
  th:replace="~{ui/avatar :: withName(src='/img/user.jpg', name='João Silva', subtitle='Administrador')}" />
<ui:avatar
  th:replace="~{ui/avatar :: withNameStatus(src='/img/user.jpg', name='João Silva', subtitle='Administrador', status='online')}" />
```

### Avatar Group (empilhado)

```html
<!-- Passa uma lista de avatars [{src, alt}] e quantidade máxima a mostrar -->
<ui:avatar th:replace="~{ui/avatar :: group(avatars=${usuarios}, maxShow=4)}" />
```

---

## 🧭 Breadcrumb Components (`ui/breadcrumb.html`)

Navegação hierárquica com vários estilos.

### Uso Básico

```html
<!-- items = [{label: 'Home', url: '/'}, {label: 'Usuários', url: '/usuarios'}, {label: 'Novo'}] -->
<!-- O último item sem url é tratado como página atual -->

<ui:breadcrumb th:replace="~{ui/breadcrumb :: default(items=${breadcrumbs})}" />
<ui:breadcrumb th:replace="~{ui/breadcrumb :: withChevron(items=${breadcrumbs})}" />
<ui:breadcrumb th:replace="~{ui/breadcrumb :: withDot(items=${breadcrumbs})}" />
```

### Breadcrumb Simples

```html
<ui:breadcrumb th:replace="~{ui/breadcrumb :: simple(home='Início', current='Novo Usuário')}" />
```

### Breadcrumb com Botão Voltar

```html
<ui:breadcrumb
  th:replace="~{ui/breadcrumb :: withBack(backUrl='/usuarios', current='Editar Usuário')}" />
```

---

## ⬇️ Dropdown Components (`ui/dropdown.html`)

Menus dropdown interativos com Alpine.js.

### Dropdown Básico

```html
<ui:dropdown th:replace="~{ui/dropdown :: basic(id='menu', label='Opções')}">
  <th:block th:ref="content">
    <ui:dropdown
      th:replace="~{ui/dropdown :: item(url='/editar', label='Editar', iconType='edit')}" />
    <ui:dropdown
      th:replace="~{ui/dropdown :: item(url='/ver', label='Visualizar', iconType='view')}" />
    <ui:dropdown th:replace="~{ui/dropdown :: divider}" />
    <ui:dropdown
      th:replace="~{ui/dropdown :: itemButton(onclick='confirmarExclusao()', label='Excluir', iconType='delete', variant='danger')}" />
  </th:block>
</ui:dropdown>
```

### Dropdown com Ícone (3 pontos)

```html
<ui:dropdown th:replace="~{ui/dropdown :: iconOnly(id='acoes')}">
  <th:block th:ref="content">
    <ui:dropdown th:replace="~{ui/dropdown :: header(title='Ações')}" />
    <ui:dropdown
      th:replace="~{ui/dropdown :: item(url='/editar', label='Editar', iconType='edit')}" />
    <!-- ... -->
  </th:block>
</ui:dropdown>
```

### Dropdown de Usuário (com avatar)

```html
<ui:dropdown
  th:replace="~{ui/dropdown :: user(avatarSrc='/img/user.jpg', name='João Silva', email='joao@email.com')}">
  <th:block th:ref="content">
    <ui:dropdown
      th:replace="~{ui/dropdown :: item(url='/perfil', label='Meu Perfil', iconType='view')}" />
    <ui:dropdown
      th:replace="~{ui/dropdown :: item(url='/config', label='Configurações', iconType='settings')}" />
    <ui:dropdown th:replace="~{ui/dropdown :: divider}" />
    <ui:dropdown
      th:replace="~{ui/dropdown :: itemButton(onclick='logout()', label='Sair', iconType='logout', variant='danger')}" />
  </th:block>
</ui:dropdown>
```

### Tipos de Ícones para Items

- `edit`, `delete`, `view`, `download`, `settings`, `logout`

---

## 📄 Pagination Components (`ui/pagination.html`)

Paginação integrada com Spring Data Page.

### Paginação Completa

```html
<!-- page = objeto Page do Spring Data, baseUrl = URL base para links -->
<ui:pagination th:replace="~{ui/pagination :: default(page=${usuarios}, baseUrl='/usuarios')}" />
```

### Paginação Simples (Anterior/Próxima)

```html
<ui:pagination th:replace="~{ui/pagination :: simple(page=${usuarios}, baseUrl='/usuarios')}" />
```

### Paginação Compacta (apenas números)

```html
<ui:pagination th:replace="~{ui/pagination :: compact(page=${usuarios}, baseUrl='/usuarios')}" />
```

### Botão "Carregar Mais"

```html
<ui:pagination th:replace="~{ui/pagination :: loadMore(page=${usuarios}, baseUrl='/usuarios')}" />
```

---

## 📊 Metric Components (`ui/metric.html`)

Cards de estatísticas e métricas.

### Metric Card Básico

```html
<ui:metric th:replace="~{ui/metric :: card(title='Total Usuários', value='1,234')}" />
```

### Metric com Variação

```html
<!-- changeType: up, down, neutral -->
<ui:metric
  th:replace="~{ui/metric :: withChange(
  title='Vendas', 
  value='R$ 12.450', 
  change='+15%', 
  changeType='up')}" />
```

### Metric com Ícone

```html
<!-- iconType: users, money, chart, shopping -->
<ui:metric
  th:replace="~{ui/metric :: withIcon(
  title='Total Usuários', 
  value='1,234', 
  iconType='users', 
  iconBgColor='bg-brand-500/10')}" />
```

### Metric Completo (Ícone + Variação)

```html
<ui:metric
  th:replace="~{ui/metric :: full(
  title='Receita Mensal', 
  value='R$ 45.231', 
  change='+12.5%', 
  changeType='up', 
  description='Comparado ao mês anterior', 
  iconType='money', 
  iconBgColor='bg-success-500/10')}" />
```

### Metric Inline

```html
<ui:metric th:replace="~{ui/metric :: inline(title='Pedidos Hoje', value='42')}" />
```

### Grid de Métricas (4 colunas)

```html
<ui:metric th:replace="~{ui/metric :: grid}">
  <th:block th:ref="content">
    <ui:metric th:replace="~{ui/metric :: card(title='Usuários', value='1,234')}" />
    <ui:metric th:replace="~{ui/metric :: card(title='Pedidos', value='567')}" />
    <ui:metric th:replace="~{ui/metric :: card(title='Receita', value='R$ 12.450')}" />
    <ui:metric th:replace="~{ui/metric :: card(title='Conversão', value='3.2%')}" />
  </th:block>
</ui:metric>
```

---

## 🗂️ Tabs Components (`ui/tabs.html`)

Abas navegáveis com Alpine.js.

### Tabs com Underline (padrão)

```html
<ui:tabs th:replace="~{ui/tabs :: default(activeTab='tab1')}">
  <th:block th:ref="tabList">
    <ui:tabs th:replace="~{ui/tabs :: tabButton(id='tab1', label='Geral')}" />
    <ui:tabs th:replace="~{ui/tabs :: tabButton(id='tab2', label='Segurança')}" />
    <ui:tabs th:replace="~{ui/tabs :: tabButton(id='tab3', label='Notificações')}" />
  </th:block>
  <th:block th:ref="tabPanels">
    <ui:tabs th:replace="~{ui/tabs :: tabPanel(id='tab1')}">
      <th:block th:ref="content">Conteúdo da aba Geral</th:block>
    </ui:tabs>
    <ui:tabs th:replace="~{ui/tabs :: tabPanel(id='tab2')}">
      <th:block th:ref="content">Conteúdo da aba Segurança</th:block>
    </ui:tabs>
    <ui:tabs th:replace="~{ui/tabs :: tabPanel(id='tab3')}">
      <th:block th:ref="content">Conteúdo da aba Notificações</th:block>
    </ui:tabs>
  </th:block>
</ui:tabs>
```

### Tabs com Estilo Pill

```html
<ui:tabs th:replace="~{ui/tabs :: pills(activeTab='tab1')}">
  <th:block th:ref="tabList">
    <ui:tabs th:replace="~{ui/tabs :: pillButton(id='tab1', label='Todos')}" />
    <ui:tabs th:replace="~{ui/tabs :: pillButton(id='tab2', label='Ativos')}" />
    <ui:tabs th:replace="~{ui/tabs :: pillButton(id='tab3', label='Inativos')}" />
  </th:block>
  <!-- ... -->
</ui:tabs>
```

### Tabs com Ícones

```html
<!-- iconType: home, user, settings, bell -->
<ui:tabs th:replace="~{ui/tabs :: tabButtonWithIcon(id='tab1', label='Home', iconType='home')}" />
```

### Tabs com Badge de Contagem

```html
<ui:tabs th:replace="~{ui/tabs :: tabButtonWithBadge(id='tab1', label='Mensagens', count=5)}" />
```

### Tabs Verticais

```html
<ui:tabs th:replace="~{ui/tabs :: vertical(activeTab='tab1')}">
  <th:block th:ref="tabList">
    <ui:tabs th:replace="~{ui/tabs :: verticalButton(id='tab1', label='Perfil')}" />
    <ui:tabs th:replace="~{ui/tabs :: verticalButton(id='tab2', label='Senha')}" />
  </th:block>
  <!-- ... -->
</ui:tabs>
```

---

## 🔽 Select Components (`ui/select.html`)

Componentes de select com estilos avançados.

### Select Básico

```html
<!-- options = [{value, label, selected}] -->
<ui:select
  th:replace="~{ui/select :: default(id='status', label='Status', required=true, options=${statusOptions})}" />
```

### Select com Placeholder

```html
<ui:select
  th:replace="~{ui/select :: withPlaceholder(
  id='pais', 
  label='País', 
  placeholder='Selecione um país', 
  required=true, 
  options=${paises})}" />
```

### Select com Erro

```html
<ui:select
  th:replace="~{ui/select :: withError(
  id='categoria', 
  label='Categoria', 
  required=true, 
  options=${categorias}, 
  errorMessage='Selecione uma categoria')}" />
```

### Select Desabilitado

```html
<ui:select th:replace="~{ui/select :: disabled(id='tipo', label='Tipo', options=${tipos})}" />
```

### Select de País (com bandeiras)

```html
<ui:select th:replace="~{ui/select :: country(id='pais', label='País', required=true)}" />
```

---

## 📁 Estrutura de Arquivos

```
templates/
└── ui/
    ├── alert.html      # Componentes de alerta
    ├── avatar.html     # Avatares e grupos de avatar
    ├── badge.html      # Badges/Tags
    ├── breadcrumb.html # Navegação breadcrumb
    ├── button.html     # Botões
    ├── card.html       # Cards/Containers
    ├── dropdown.html   # Menus dropdown
    ├── form.html       # Componentes básicos de formulário
    ├── icon.html       # Ícones SVG
    ├── input.html      # Inputs avançados
    ├── metric.html     # Cards de métricas/estatísticas
    ├── modal.html      # Modais
    ├── pagination.html # Paginação
    ├── select.html     # Selects avançados
    ├── table.html      # Tabelas
    └── tabs.html       # Abas/Tabs
```

---

## ⚡ Dependências

- **Thymeleaf 3.x** - Template engine
- **Alpine.js 3.x** - Para interatividade (modals, toggles, dropdowns, tabs, etc.)
- **Alpine.js Mask Plugin** - Para máscaras de input
- **Tailwind CSS** - Framework CSS

### Setup Alpine.js

```html
<script defer src="https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js"></script>
<script defer src="https://unpkg.com/@alpinejs/mask@3.x.x/dist/cdn.min.js"></script>
```

---

## 🎨 Cores do Design System

O design system utiliza as seguintes cores semânticas (configuradas no Tailwind):

| Nome      | Uso                                     |
| --------- | --------------------------------------- |
| `brand`   | Cor principal da marca                  |
| `success` | Ações positivas, estados de sucesso     |
| `error`   | Erros, ações destrutivas                |
| `warning` | Alertas, estados de atenção             |
| `info`    | Informações, estados neutros            |
| `gray`    | Textos, bordas, backgrounds secundários |

### Exemplo de Classes

```html
<!-- Backgrounds -->
<div class="bg-brand-500 bg-success-500 bg-error-500 bg-warning-500 bg-info-500"></div>

<!-- Textos -->
<span class="text-brand-600 text-success-600 text-error-600 text-warning-600"></span>

<!-- Bordas -->
<div class="border-brand-300 border-error-300 border-success-300"></div>

<!-- Com Dark Mode -->
<div class="bg-white dark:bg-gray-900 text-gray-800 dark:text-white"></div>
```
