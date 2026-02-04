package br.sst.auditoria.controller;

import br.sst.auditoria.dto.usuario.UsuarioRequest;
import br.sst.auditoria.dto.usuario.UsuarioResponse;
import br.sst.auditoria.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de gerenciamento de usuários.
 * Toda a lógica de negócio está encapsulada no UsuarioService.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista todos os usuários (apenas admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> list() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Busca um usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> show(@PathVariable String id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    /**
     * Cria um novo usuário (apenas admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    /**
     * Atualiza um usuário existente
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String id, @Valid @RequestBody UsuarioRequest request) {
        usuarioService.update(id, request);
    }

    /**
     * Atualiza parcialmente um usuário
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void partialUpdate(@PathVariable String id, @RequestBody UsuarioRequest request) {
        usuarioService.partialUpdate(id, request);
    }

    /**
     * Remove um usuário (apenas admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        usuarioService.delete(id);
    }
}
