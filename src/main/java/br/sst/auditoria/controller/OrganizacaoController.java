package br.sst.auditoria.controller;

import br.sst.auditoria.dto.organizacao.*;
import br.sst.auditoria.security.CustomUserDetails;
import br.sst.auditoria.service.OrganizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de Organizações
 * Baseado na API do Better Auth Organization Plugin
 * 
 * Endpoints:
 * 
 * ORGANIZAÇÃO:
 * - POST   /api/organizacao              - Criar organização
 * - GET    /api/organizacao              - Listar organizações do usuário
 * - GET    /api/organizacao/verificar-slug - Verificar disponibilidade de slug
 * - POST   /api/organizacao/ativar       - Definir organização ativa
 * - GET    /api/organizacao/{id}         - Obter organização por ID
 * - GET    /api/organizacao/{id}/completa - Obter organização completa
 * - PUT    /api/organizacao/{id}         - Atualizar organização
 * - DELETE /api/organizacao/{id}         - Deletar organização
 * 
 * CONVITES:
 * - POST   /api/organizacao/{id}/convites          - Convidar membro
 * - GET    /api/organizacao/{id}/convites          - Listar convites da organização
 * - GET    /api/organizacao/meus-convites          - Listar convites do usuário
 * - GET    /api/organizacao/convites/{id}          - Obter convite
 * - POST   /api/organizacao/convites/{id}/aceitar  - Aceitar convite
 * - POST   /api/organizacao/convites/{id}/cancelar - Cancelar convite
 * - POST   /api/organizacao/convites/{id}/rejeitar - Rejeitar convite
 * 
 * MEMBROS:
 * - GET    /api/organizacao/{id}/membros                   - Listar membros
 * - POST   /api/organizacao/{id}/membros                   - Adicionar membro
 * - DELETE /api/organizacao/{id}/membros/{membroIdOuEmail} - Remover membro
 * - PUT    /api/organizacao/{id}/membros/papel             - Atualizar papel do membro
 * - GET    /api/organizacao/membro-ativo                   - Obter membro ativo
 * - GET    /api/organizacao/membro-ativo/papel             - Obter papel do membro ativo
 * - POST   /api/organizacao/{id}/sair                      - Sair da organização
 * 
 * PAPÉIS DINÂMICOS:
 * - POST   /api/organizacao/{id}/papeis              - Criar papel
 * - GET    /api/organizacao/{id}/papeis              - Listar papéis
 * - GET    /api/organizacao/{id}/papeis/{idOuNome}   - Obter papel
 * - PUT    /api/organizacao/{id}/papeis/{idOuNome}   - Atualizar papel
 * - DELETE /api/organizacao/{id}/papeis/{idOuNome}   - Deletar papel
 * 
 * PERMISSÕES:
 * - POST   /api/organizacao/verificar-permissao      - Verificar permissão
 */
@RestController
@RequestMapping("/api/organizacao")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrganizacaoController {

    private final OrganizacaoService organizacaoService;

    // ========================================================================
    // ORGANIZAÇÃO
    // ========================================================================

    /**
     * Criar uma nova organização
     */
    @PostMapping
    public ResponseEntity<OrganizacaoResponse> criarOrganizacao(
            @Valid @RequestBody CriarOrganizacaoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrganizacaoResponse response = organizacaoService.criarOrganizacao(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar organizações do usuário
     */
    @GetMapping
    public ResponseEntity<List<OrganizacaoResponse>> listarOrganizacoes(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<OrganizacaoResponse> organizacoes = organizacaoService.listarOrganizacoesDoUsuario(userDetails.getId());
        return ResponseEntity.ok(organizacoes);
    }

    /**
     * Verificar se slug está disponível
     */
    @GetMapping("/verificar-slug")
    public ResponseEntity<Map<String, Boolean>> verificarSlug(@RequestParam String slug) {
        boolean disponivel = organizacaoService.verificarSlugDisponivel(slug);
        return ResponseEntity.ok(Map.of("disponivel", disponivel));
    }

    /**
     * Definir organização ativa na sessão
     */
    @PostMapping("/ativar")
    public ResponseEntity<OrganizacaoResponse> definirOrganizacaoAtiva(
            @RequestParam(required = false) String organizacaoId,
            @RequestParam(required = false) String organizacaoSlug,
            @RequestParam String sessaoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrganizacaoResponse response = organizacaoService.definirOrganizacaoAtiva(
                organizacaoId, organizacaoSlug, sessaoId, userDetails.getId()
        );
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obter organização por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoResponse> obterOrganizacao(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrganizacaoCompletaResponse completa = organizacaoService.obterOrganizacaoCompleta(id, userDetails.getId(), 0);
        OrganizacaoResponse response = new OrganizacaoResponse(
                completa.id(), completa.nome(), completa.slug(), 
                completa.logo(), completa.metadados(), completa.criadoEm()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Obter organização completa com membros e convites
     */
    @GetMapping("/{id}/completa")
    public ResponseEntity<OrganizacaoCompletaResponse> obterOrganizacaoCompleta(
            @PathVariable String id,
            @RequestParam(required = false) Integer limiteMembros,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrganizacaoCompletaResponse response = organizacaoService.obterOrganizacaoCompleta(
                id, userDetails.getId(), limiteMembros
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar organização
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizacaoResponse> atualizarOrganizacao(
            @PathVariable String id,
            @Valid @RequestBody AtualizarOrganizacaoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrganizacaoResponse response = organizacaoService.atualizarOrganizacao(id, request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletar organização
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrganizacao(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.deletarOrganizacao(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // CONVITES
    // ========================================================================

    /**
     * Convidar membro para a organização
     */
    @PostMapping("/{id}/convites")
    public ResponseEntity<ConviteResponse> convidarMembro(
            @PathVariable String id,
            @Valid @RequestBody ConvidarMembroRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ConviteResponse response = organizacaoService.convidarMembro(id, request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar convites de uma organização
     */
    @GetMapping("/{id}/convites")
    public ResponseEntity<List<ConviteResponse>> listarConvites(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ConviteResponse> convites = organizacaoService.listarConvites(id, userDetails.getId());
        return ResponseEntity.ok(convites);
    }

    /**
     * Listar convites do usuário (convites recebidos)
     */
    @GetMapping("/meus-convites")
    public ResponseEntity<List<ConviteResponse>> listarMeusConvites(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ConviteResponse> convites = organizacaoService.listarConvitesDoUsuario(userDetails.getEmail());
        return ResponseEntity.ok(convites);
    }

    /**
     * Obter convite por ID
     */
    @GetMapping("/convites/{id}")
    public ResponseEntity<ConviteResponse> obterConvite(@PathVariable String id) {
        ConviteResponse response = organizacaoService.obterConvite(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Aceitar convite
     */
    @PostMapping("/convites/{id}/aceitar")
    public ResponseEntity<MembroResponse> aceitarConvite(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MembroResponse response = organizacaoService.aceitarConvite(id, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancelar convite (pelo admin/owner)
     */
    @PostMapping("/convites/{id}/cancelar")
    public ResponseEntity<Void> cancelarConvite(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.cancelarConvite(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Rejeitar convite (pelo convidado)
     */
    @PostMapping("/convites/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarConvite(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.rejeitarConvite(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // MEMBROS
    // ========================================================================

    /**
     * Listar membros de uma organização
     */
    @GetMapping("/{id}/membros")
    public ResponseEntity<Page<MembroResponse>> listarMembros(
            @PathVariable String id,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Page<MembroResponse> membros = organizacaoService.listarMembros(id, userDetails.getId(), pageable);
        return ResponseEntity.ok(membros);
    }

    /**
     * Adicionar membro diretamente (sem convite)
     */
    @PostMapping("/{id}/membros")
    public ResponseEntity<MembroResponse> adicionarMembro(
            @PathVariable String id,
            @Valid @RequestBody AdicionarMembroRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MembroResponse response = organizacaoService.adicionarMembro(id, request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remover membro
     */
    @DeleteMapping("/{id}/membros/{membroIdOuEmail}")
    public ResponseEntity<Void> removerMembro(
            @PathVariable String id,
            @PathVariable String membroIdOuEmail,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.removerMembro(id, membroIdOuEmail, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualizar papel do membro
     */
    @PutMapping("/{id}/membros/papel")
    public ResponseEntity<MembroResponse> atualizarPapelMembro(
            @PathVariable String id,
            @Valid @RequestBody AtualizarPapelMembroRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MembroResponse response = organizacaoService.atualizarPapelMembro(id, request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Obter membro ativo (do usuário logado na organização ativa)
     */
    @GetMapping("/membro-ativo")
    public ResponseEntity<MembroResponse> obterMembroAtivo(
            @RequestParam String organizacaoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MembroResponse response = organizacaoService.obterMembroAtivo(organizacaoId, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Obter papel do membro ativo
     */
    @GetMapping("/membro-ativo/papel")
    public ResponseEntity<Map<String, String>> obterPapelMembroAtivo(
            @RequestParam String organizacaoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String papel = organizacaoService.obterPapelMembroAtivo(organizacaoId, userDetails.getId());
        return ResponseEntity.ok(Map.of("papel", papel));
    }

    /**
     * Sair da organização
     */
    @PostMapping("/{id}/sair")
    public ResponseEntity<Void> sairDaOrganizacao(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.sairDaOrganizacao(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // PAPÉIS DINÂMICOS
    // ========================================================================

    /**
     * Criar papel de organização
     */
    @PostMapping("/{id}/papeis")
    public ResponseEntity<PapelOrganizacaoResponse> criarPapel(
            @PathVariable String id,
            @Valid @RequestBody CriarPapelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PapelOrganizacaoResponse response = organizacaoService.criarPapel(id, request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar papéis de uma organização
     */
    @GetMapping("/{id}/papeis")
    public ResponseEntity<List<PapelOrganizacaoResponse>> listarPapeis(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<PapelOrganizacaoResponse> papeis = organizacaoService.listarPapeis(id, userDetails.getId());
        return ResponseEntity.ok(papeis);
    }

    /**
     * Obter papel específico
     */
    @GetMapping("/{id}/papeis/{papelIdOuNome}")
    public ResponseEntity<PapelOrganizacaoResponse> obterPapel(
            @PathVariable String id,
            @PathVariable String papelIdOuNome,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PapelOrganizacaoResponse response = organizacaoService.obterPapel(id, papelIdOuNome, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar papel
     */
    @PutMapping("/{id}/papeis/{papelIdOuNome}")
    public ResponseEntity<PapelOrganizacaoResponse> atualizarPapel(
            @PathVariable String id,
            @PathVariable String papelIdOuNome,
            @Valid @RequestBody AtualizarPapelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Criar request com o ID/nome do path se não fornecido no body
        AtualizarPapelRequest requestAtualizado = new AtualizarPapelRequest(
                request.papelNome() != null ? request.papelNome() : papelIdOuNome,
                request.papelId() != null ? request.papelId() : papelIdOuNome,
                request.organizacaoId() != null ? request.organizacaoId() : id,
                request.novoNome(),
                request.permissao()
        );
        PapelOrganizacaoResponse response = organizacaoService.atualizarPapel(id, requestAtualizado, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletar papel
     */
    @DeleteMapping("/{id}/papeis/{papelIdOuNome}")
    public ResponseEntity<Void> deletarPapel(
            @PathVariable String id,
            @PathVariable String papelIdOuNome,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        organizacaoService.deletarPapel(id, papelIdOuNome, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // PERMISSÕES
    // ========================================================================

    /**
     * Verificar se usuário tem permissão específica
     */
    @PostMapping("/verificar-permissao")
    public ResponseEntity<Map<String, Boolean>> verificarPermissao(
            @RequestParam String organizacaoId,
            @RequestParam String recurso,
            @RequestParam String acao,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean temPermissao = organizacaoService.verificarPermissao(
                organizacaoId, userDetails.getId(), recurso, acao
        );
        return ResponseEntity.ok(Map.of("temPermissao", temPermissao));
    }
}
