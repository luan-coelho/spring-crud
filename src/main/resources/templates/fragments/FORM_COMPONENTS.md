# Componentes de Formulário Reutilizáveis

Este documento descreve como usar os componentes de formulário criados em `fragments/components.html`.

## Componentes Disponíveis

### 1. Input de Texto (`input`)

Componente para campos de entrada de texto com validação automática, ícone de erro e suporte a máscaras.

#### Parâmetros:

- `field` (obrigatório): Nome do campo do modelo
- `label` (obrigatório): Texto do label
- `placeholder` (obrigatório): Texto do placeholder
- `required` (opcional): `true` para exibir asterisco vermelho
- `type` (opcional): Tipo do input (padrão: 'text'). Ex: 'email', 'url', 'password'
- `mask` (opcional): Máscara Alpine.js. Ex: '999.999.999-99' para CPF

#### Exemplo de Uso:

```html
<!-- Input simples -->
<div
  th:replace="~{fragments/components :: input(
  field='nome',
  label='Nome',
  placeholder='Nome completo',
  required=true
)}"></div>

<!-- Input com tipo email -->
<div
  th:replace="~{fragments/components :: input(
  field='email',
  label='E-mail',
  placeholder='seu@email.com',
  required=true,
  type='email'
)}"></div>

<!-- Input com máscara (CPF) -->
<div
  th:replace="~{fragments/components :: input(
  field='cpf',
  label='CPF',
  placeholder='000.000.000-00',
  required=true,
  mask='999.999.999-99'
)}"></div>

<!-- Input com máscara (Telefone) -->
<div
  th:replace="~{fragments/components :: input(
  field='telefone',
  label='Telefone',
  placeholder='(00) 00000-0000',
  required=false,
  mask='(99) 99999-9999'
)}"></div>

<!-- Input URL -->
<div
  th:replace="~{fragments/components :: input(
  field='imagem',
  label='URL da Imagem',
  placeholder='https://exemplo.com/imagem.jpg',
  required=false,
  type='url'
)}"></div>
```

---

### 2. Textarea (`textarea`)

Componente para campos de texto multilinha com validação automática.

#### Parâmetros:

- `field` (obrigatório): Nome do campo do modelo
- `label` (obrigatório): Texto do label
- `placeholder` (obrigatório): Texto do placeholder
- `required` (opcional): `true` para exibir asterisco vermelho
- `rows` (opcional): Número de linhas (padrão: 3)

#### Exemplo de Uso:

```html
<!-- Textarea simples -->
<div
  th:replace="~{fragments/components :: textarea(
  field='motivoBanimento',
  label='Motivo do Banimento',
  placeholder='Descreva o motivo do banimento...',
  required=false,
  rows=3
)}"></div>

<!-- Textarea com mais linhas -->
<div
  th:replace="~{fragments/components :: textarea(
  field='descricao',
  label='Descrição',
  placeholder='Digite a descrição...',
  required=true,
  rows=6
)}"></div>
```

---

### 3. Select (`select`)

Componente para campos de seleção com validação automática.

#### Parâmetros:

- `field` (obrigatório): Nome do campo do modelo
- `label` (obrigatório): Texto do label
- `required` (opcional): `true` para exibir asterisco vermelho
- `options` (obrigatório): Fragment com as opções do select

#### Exemplo de Uso:

```html
<!-- Select com opções estáticas -->
<div
  th:replace="~{fragments/components :: select(
  field='papel',
  label='Papel',
  required=true,
  options=~{::papel-options}
)}">
  <th:block th:fragment="papel-options">
    <option value="user">Usuário</option>
    <option value="admin">Administrador</option>
    <option value="moderator">Moderador</option>
  </th:block>
</div>

<!-- Select com opções dinâmicas -->
<div
  th:replace="~{fragments/components :: select(
  field='categoria',
  label='Categoria',
  required=true,
  options=~{::categoria-options}
)}">
  <th:block th:fragment="categoria-options">
    <option value="">Selecione...</option>
    <option th:each="cat : ${categorias}" th:value="${cat.id}" th:text="${cat.nome}">
      Categoria
    </option>
  </th:block>
</div>
```

---

## Recursos Automáticos

Todos os componentes incluem automaticamente:

✅ **Validação Visual**: Borda vermelha quando há erro  
✅ **Ícone de Erro**: Ícone vermelho aparece dentro do input quando há erro (apenas input de texto)  
✅ **Mensagem de Erro**: Mensagem de validação do servidor abaixo do campo  
✅ **Asterisco Obrigatório**: Asterisco vermelho quando `required=true`  
✅ **Dark Mode**: Suporte completo a tema escuro  
✅ **Acessibilidade**: Labels corretamente associados aos inputs

---

## Integração com Thymeleaf Form Binding

Os componentes funcionam automaticamente com `th:object` do Thymeleaf:

```html
<form th:object="${usuario}" method="post" novalidate>
  <!-- O campo 'nome' será automaticamente vinculado a usuario.nome -->
  <div
    th:replace="~{fragments/components :: input(
    field='nome',
    label='Nome',
    placeholder='Nome completo',
    required=true
  )}"></div>

  <!-- Outros campos... -->
</form>
```

---

## Exemplo Completo

```html
<form th:object="${usuario}" method="post" novalidate>
  <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
    <!-- Nome -->
    <div
      th:replace="~{fragments/components :: input(
      field='nome',
      label='Nome',
      placeholder='Nome completo',
      required=true
    )}"></div>

    <!-- Email -->
    <div
      th:replace="~{fragments/components :: input(
      field='email',
      label='E-mail',
      placeholder='seu@email.com',
      required=true,
      type='email'
    )}"></div>

    <!-- CPF -->
    <div
      th:replace="~{fragments/components :: input(
      field='cpf',
      label='CPF',
      placeholder='000.000.000-00',
      required=true,
      mask='999.999.999-99'
    )}"></div>

    <!-- Telefone -->
    <div
      th:replace="~{fragments/components :: input(
      field='telefone',
      label='Telefone',
      placeholder='(00) 00000-0000',
      required=false,
      mask='(99) 99999-9999'
    )}"></div>

    <!-- Papel -->
    <div
      th:replace="~{fragments/components :: select(
      field='papel',
      label='Papel',
      required=true,
      options=~{::papel-options}
    )}">
      <th:block th:fragment="papel-options">
        <option value="user">Usuário</option>
        <option value="admin">Administrador</option>
        <option value="moderator">Moderador</option>
      </th:block>
    </div>

    <!-- Descrição -->
    <div
      class="lg:col-span-2"
      th:replace="~{fragments/components :: textarea(
      field='descricao',
      label='Descrição',
      placeholder='Digite uma descrição...',
      required=false,
      rows=4
    )}"></div>
  </div>
</form>
```

---

## Notas Importantes

1. **Máscaras Alpine.js**: Para usar máscaras, certifique-se de que o Alpine.js Mask plugin está carregado na página.

2. **Validação do Servidor**: Os componentes exibem erros de validação do Spring Boot automaticamente através de `#fields.hasErrors()`.

3. **Formulário com `novalidate`**: Adicione `novalidate` ao formulário para desabilitar validações nativas do HTML5 e usar apenas as validações do servidor.

4. **Binding com `th:object`**: Os componentes usam `th:name` e `th:id`, então funcionam tanto com `th:object` quanto sem ele. Se usar `th:object`, o Spring Boot fará o binding automaticamente.
