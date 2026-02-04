package br.sst.auditoria.controller;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.dto.auth.ChangePasswordRequest;
import br.sst.auditoria.dto.auth.LoginRequest;
import br.sst.auditoria.dto.auth.RegisterRequest;
import br.sst.auditoria.dto.auth.SessaoResponse;
import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de autenticação.
 * Implementa autenticação baseada em sessão persistida no banco de dados (modelo Better Auth).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${session.cookie.max-age:604800}") // 7 days in seconds
    private int cookieMaxAge;

    @Value("${session.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${session.cookie.same-site:Lax}")
    private String cookieSameSite;

    /**
     * Endpoint de login - cria sessão no banco e retorna token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse httpResponse) {
        AuthResponse response = authService.login(request, httpRequest);

        // Adiciona o cookie de sessão para suporte a autenticação via cookie
        adicionarCookieSessao(httpResponse, response.token());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de registro de novo usuário - cria usuário, conta e sessão
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<AuthResponse> cadastro(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletRequest httpRequest,
                                                  HttpServletResponse httpResponse) {
        AuthResponse response = authService.registrar(request, httpRequest);

        // Adiciona o cookie de sessão
        adicionarCookieSessao(httpResponse, response.token());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint de logout - revoga a sessão atual
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse httpResponse) {
        authService.logout();

        // Remove o cookie de sessão
        removerCookieSessao(httpResponse);
    }

    /**
     * Endpoint para logout de todos os dispositivos
     */
    @PostMapping("/logout-todos-dispositivos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutTodosDispositivos(HttpServletResponse httpResponse) {
        authService.logoutTodosDispositivos();

        // Remove o cookie de sessão local
        removerCookieSessao(httpResponse);
    }

    /**
     * Endpoint para obter dados do usuário autenticado
     */
    @GetMapping("/pegar-usuario-logado")
    public ResponseEntity<AuthResponse> pegarUsuarioLogado() {
        return ResponseEntity.ok(authService.pegarUsuarioLogado());
    }

    /**
     * Endpoint para listar todas as sessões ativas do usuário
     */
    @GetMapping("/sessoes")
    public ResponseEntity<List<SessaoResponse>> listarSessoes() {
        List<Sessao> sessoes = authService.listarMinhasSessoes();
        List<SessaoResponse> response = sessoes.stream()
                .map(SessaoResponse::fromSessao)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para revogar uma sessão específica
     */
    @DeleteMapping("/sessoes/{sessaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revogarSessao(@PathVariable String sessaoId) {
        authService.revogarSessao(sessaoId);
    }

    /**
     * Endpoint para alteração de senha
     */
    @PostMapping("/alterar-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.alterarSenha(request);
    }

    /**
     * Adiciona o cookie de sessão na resposta
     */
    private void adicionarCookieSessao(HttpServletResponse response, String sessionToken) {
        Cookie cookie = new Cookie("session_token", sessionToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        // SameSite é configurado via header pois Cookie não suporta diretamente
        response.addCookie(cookie);
        
        // Adiciona SameSite via header
        String cookieHeader = String.format(
            "session_token=%s; HttpOnly; Path=/; Max-Age=%d; SameSite=%s%s",
            sessionToken, cookieMaxAge, cookieSameSite, cookieSecure ? "; Secure" : ""
        );
        response.addHeader("Set-Cookie", cookieHeader);
    }

    /**
     * Remove o cookie de sessão da resposta
     */
    private void removerCookieSessao(HttpServletResponse response) {
        Cookie cookie = new Cookie("session_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
