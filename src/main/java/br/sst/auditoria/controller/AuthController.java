package br.sst.auditoria.controller;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.dto.auth.ChangePasswordRequest;
import br.sst.auditoria.dto.auth.LoginRequest;
import br.sst.auditoria.dto.auth.RegisterRequest;
import br.sst.auditoria.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints de autenticação.
 * Toda a lógica de negócio está encapsulada no AuthService.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint de registro de novo usuário
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<AuthResponse> cadastro(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    /**
     * Endpoint para obter dados do usuário autenticado
     */
    @GetMapping("/pegar-usuario-logado")
    public ResponseEntity<AuthResponse> pegarUsuarioLogado() {
        return ResponseEntity.ok(authService.pegarUsuarioLogado());
    }

    /**
     * Endpoint para alteração de senha
     */
    @PostMapping("/alterar-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.alterarSenha(request);
    }

}
