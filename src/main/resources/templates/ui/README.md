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

## 📁 Estrutura de Arquivos

```
templates/
└── ui/
    ├── alert.html    # Componentes de alerta
    ├── badge.html    # Badges/Tags
    ├── button.html   # Botões
    ├── card.html     # Cards/Containers
    ├── form.html     # Inputs, Selects, Checkboxes
    ├── icon.html     # Ícones SVG
    ├── modal.html    # Modais
    └── table.html    # Tabelas
```

---

## ⚡ Dependências

- **Thymeleaf 3.x** - Template engine
- **Alpine.js 3.x** - Para interatividade (modals, toggles, etc.)
- **Alpine.js Mask Plugin** - Para máscaras de input
- **Tailwind CSS** - Framework CSS

### Setup Alpine.js

```html
<script defer src="https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js"></script>
<script defer src="https://unpkg.com/@alpinejs/mask@3.x.x/dist/cdn.min.js"></script>
```
