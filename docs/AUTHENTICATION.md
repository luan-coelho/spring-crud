# Modelo de Autenticação - Sessão Persistida e Guia de Integração

Este projeto utiliza um modelo de autenticação baseada em sessão persistida no banco de dados (PostgreSQL), robusto e seguro, inspirado no funcionamento do **Better Auth**.

Ao contrário do JWT Stateless tradicional, as sessões são armazenadas no banco, permitindo controle total sobre o ciclo de vida do acesso (revogação imediata, listagem de dispositivos, etc.).

---

## 🚀 Como Funciona

### 1. Login e Criação de Sessão

1.  O cliente envia credenciais (`email`, `senha`) para `/api/auth/login`.
2.  O servidor valida as credenciais.
3.  O servidor cria um registro na tabela `sessao` no banco de dados.
4.  Um token opaco seguro (não-JWT) é gerado (ex: `a8f93...`).
5.  O servidor retorna o token de duas formas:
    - **Cookie HttpOnly**: `session_token=...` (Para Web/Browsers)
    - **Body JSON**: `{ token: "..." }` (Para Mobile/Outros clientes)

### 2. Autenticação das Requisições

O cliente deve enviar o token em cada requisição protegida. O backend aceita dois métodos de transporte:

- **Método Recomendado (Web): Cookie Automático**
  - O navegador envia o cookie `session_token` automaticamente se o CORS estiver configurado corretamente (`Access-Control-Allow-Credentials: true`).
  - O frontend não precisa manipular o token manualmente.

- **Método Alternativo (Mobile/Server-to-Server): Header Authorization**
  - Header: `Authorization: Bearer <seu_token>`
  - Útil para aplicações mobile (React Native, Flutter) onde cookies não são gerenciados nativamente.

---

## 🛠️ Guia de Integração Frontend

### Configuração de Cliente HTTP (Axios)

Para que os cookies funcionem corretamente entre domínios diferentes (ex: Frontend em `localhost:3000` e Backend em `localhost:8080`), você **DEVE** habilitar as credenciais no seu cliente HTTP.

```javascript
import axios from "axios";

// Crie uma instância global do Axios
const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // IMPORTANTE: Permite o envio e recebimento de cookies HttpOnly
});

export default api;
```

### Exemplo de Login (React)

```javascript
const handleLogin = async (email, senha) => {
  try {
    // O backend define o cookie automaticamente na resposta
    await api.post("/api/auth/login", { email, senha });

    // Redireciona ou atualiza estado global
    console.log("Login realizado com sucesso!");
    window.location.href = "/dashboard";
  } catch (error) {
    console.error("Erro no login", error);
  }
};
```

### Exemplo de Fetch (Nativo)

Se preferir usar `fetch`, adicione `credentials: 'include'`:

```javascript
fetch("http://localhost:8080/api/auth/pegar-usuario-logado", {
  method: "GET",
  credentials: "include", // IMPORTANTE
  headers: {
    "Content-Type": "application/json",
  },
});
```

### Tratamento de Sessão Expirada (401)

Como as sessões expiram ou podem ser revogadas, você deve interceptar erros 401 e redirecionar para o login.

```javascript
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Sessão inválida ou expirada
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);
```

### Next.js (Server Components / Middleware)

Em ambientes Next.js modernos (App Router), os cookies HttpOnly são acessíveis no servidor.

1.  **Middleware**: Você pode verificar a existência do cookie `session_token`. Note que, como o token é opaco, você não pode validar a assinatura localmente (como em JWT). Para validação real, faça uma chamada leve ao backend ou deixe passar e trate o 401 na página.
2.  **Server Actions**: Ao fazer fetch dentro de Server Actions ou Server Components, lembre-se de repassar os cookies.

```javascript
// Exemplo em Server Component / Action
import { cookies } from "next/headers";

async function getData() {
  const cookieStore = cookies();
  const sessionToken = cookieStore.get("session_token")?.value;

  const res = await fetch("http://localhost:8080/api/recurso", {
    headers: {
      Cookie: `session_token=${sessionToken}`,
    },
  });
  // ...
}
```

---

## 📱 Guia de Integração Mobile (React Native / Flutter)

Apps mobile geralmente não persistem cookies de sessão automaticamente entre reinicializações da mesma forma que navegadores.

1.  **Login**: Faça o POST em `/login`.
2.  **Armazenamento**: Pegue o `token` do corpo da resposta JSON e salve em armazenamento seguro (Ex: `SecureStore` ou `AsyncStorage` encrypted).
    ```json
    {
      "token": "a8f93j...",
      "user": { ... }
    }
    ```
3.  **Uso**: Envie o token no header manualmente.
    ```javascript
    api.get("/rota-protegida", {
      headers: {
        Authorization: `Bearer ${tokenArmazenado}`,
      },
    });
    ```

---

## 📡 Endpoints Disponíveis

| Verbo    | Rota                                  | Descrição                                                                              |
| :------- | :------------------------------------ | :------------------------------------------------------------------------------------- |
| `POST`   | `/api/auth/cadastrar`                 | Cria usuário e retorna sessão (Cookie + JSON).                                         |
| `POST`   | `/api/auth/login`                     | Autentica e retorna sessão (Cookie + JSON).                                            |
| `POST`   | `/api/auth/logout`                    | Revoga a sessão atual e limpa o cookie.                                                |
| `POST`   | `/api/auth/logout-todos-dispositivos` | Revoga **todas** as sessões do usuário.                                                |
| `GET`    | `/api/auth/pegar-usuario-logado`      | Retorna dados do usuário autenticado.                                                  |
| `GET`    | `/api/auth/sessoes`                   | Lista todos os dispositivos/sessões ativas. Retorna dados como IP, navegador e status. |
| `DELETE` | `/api/auth/sessoes/{id}`              | Revoga (desloga) uma sessão específica pelo ID.                                        |
| `POST`   | `/api/auth/alterar-senha`             | Altera a senha do usuário.                                                             |

## ⚙️ Configuração (Backend)

Sua configuração de sessão está em `src/main/resources/application.yaml`:

```yaml
session:
  expiration: 604800000 # Tempo de vida da sessão (ms) - Default: 7 dias
  cookie:
    max-age: 604800 # Tempo de vida do cookie (s)
    secure: false # Use TRUE em produção (HTTPS obrigatório)
    same-site: Lax # Controle de envio cross-site
```
