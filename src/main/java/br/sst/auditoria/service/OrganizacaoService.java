package br.sst.auditoria.service;

import br.sst.auditoria.dto.organizacao.*;
import br.sst.auditoria.exception.BusinessException;
import br.sst.auditoria.exception.ResourceNotFoundException;
import br.sst.auditoria.exception.UnauthorizedException;
import br.sst.auditoria.model.*;
import br.sst.auditoria.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service para gerenciamento de Organizações
 * Baseado na API do Better Auth Organization Plugin
 */
import br.sst.auditoria.model.enums.Papel;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizacaoService {

    private final OrganizacaoRepository organizacaoRepository;
    private final MembroRepository membroRepository;
    private final ConviteRepository conviteRepository;
    private final PapelOrganizacaoRepository papelOrganizacaoRepository;
    private final SessaoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Papéis padrão
    private static final String PAPEL_OWNER = Papel.PROPRIETARIO.name();
    private static final String PAPEL_ADMIN = Papel.ADMINISTRADOR.name();
    private static final String PAPEL_MEMBER = Papel.MEMBRO.name();

    // Status de convite
    private static final String CONVITE_PENDENTE = "PENDENTE";
    private static final String CONVITE_ACEITO = "ACEITO";
    private static final String CONVITE_REJEITADO = "REJEITADO";
    private static final String CONVITE_CANCELADO = "CANCELADO";

    // Configurações padrão
    private static final int CONVITE_EXPIRACAO_HORAS = 48;
    private static final int LIMITE_MEMBROS = 100;

    // ========================================================================
    // ORGANIZAÇÃO
    // ========================================================================

    /**
     * Criar uma nova organização
     * POST /api/organizacao
     */
    public OrganizacaoResponse criarOrganizacao(CriarOrganizacaoRequest request, String usuarioId) {
        // Validar se slug já existe
        if (organizacaoRepository.existsBySlug(request.slug())) {
            throw new BusinessException("Slug já está em uso");
        }

        // Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Criar organização
        Organizacao organizacao = Organizacao.builder()
                .id(UUID.randomUUID().toString())
                .nome(request.nome())
                .slug(request.slug())
                .logo(request.logo())
                .metadados(request.metadados())
                .build();

        organizacao = organizacaoRepository.save(organizacao);

        // Criar membro owner
        Membro membro = Membro.builder()
                .id(UUID.randomUUID().toString())
                .organizacao(organizacao)
                .usuario(usuario)
                .papel(PAPEL_OWNER)
                .build();

        membroRepository.save(membro);

        return OrganizacaoResponse.fromEntity(organizacao);
    }

    /**
     * Verificar se slug está disponível
     * GET /api/organizacao/verificar-slug
     */
    @Transactional(readOnly = true)
    public boolean verificarSlugDisponivel(String slug) {
        return !organizacaoRepository.existsBySlug(slug);
    }

    /**
     * Listar organizações do usuário
     * GET /api/organizacao
     */
    @Transactional(readOnly = true)
    public List<OrganizacaoResponse> listarOrganizacoesDoUsuario(String usuarioId) {
        List<Membro> membros = membroRepository.findByUsuarioIdWithOrganizacao(usuarioId);
        return membros.stream()
                .map(m -> OrganizacaoResponse.fromEntity(m.getOrganizacao()))
                .toList();
    }

    /**
     * Definir organização ativa na sessão
     * POST /api/organizacao/ativar
     */
    public OrganizacaoResponse definirOrganizacaoAtiva(String organizacaoId, String organizacaoSlug, String sessaoId, String usuarioId) {
        Organizacao organizacao;

        if (organizacaoId != null) {
            organizacao = organizacaoRepository.findById(organizacaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", organizacaoId));
        } else if (organizacaoSlug != null) {
            organizacao = organizacaoRepository.findBySlug(organizacaoSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Organização", "slug", organizacaoSlug));
        } else {
            // Limpar organização ativa
            sessaoRepository.limparOrganizacaoAtiva(sessaoId);
            return null;
        }

        // Verificar se usuário é membro
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacao.getId(), usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        sessaoRepository.atualizarOrganizacaoAtiva(sessaoId, organizacao.getId());
        return OrganizacaoResponse.fromEntity(organizacao);
    }

    /**
     * Obter organização completa com membros e convites
     * GET /api/organizacao/{id}/completa
     */
    @Transactional(readOnly = true)
    public OrganizacaoCompletaResponse obterOrganizacaoCompleta(String organizacaoId, String usuarioId, Integer limiteMembros) {
        Organizacao organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", organizacaoId));

        // Verificar se usuário é membro
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        int limite = limiteMembros != null ? limiteMembros : LIMITE_MEMBROS;
        List<Membro> membros = membroRepository.findByOrganizacaoId(organizacaoId);
        List<Convite> convites = conviteRepository.findConvitesPendentesByOrganizacaoId(organizacaoId);

        List<MembroResponse> membrosResponse = membros.stream()
                .limit(limite)
                .map(MembroResponse::fromEntity)
                .toList();

        List<ConviteResponse> convitesResponse = convites.stream()
                .map(ConviteResponse::fromEntity)
                .toList();

        return OrganizacaoCompletaResponse.fromEntity(organizacao, membrosResponse, convitesResponse);
    }

    /**
     * Atualizar organização
     * PUT /api/organizacao/{id}
     */
    public OrganizacaoResponse atualizarOrganizacao(String organizacaoId, AtualizarOrganizacaoRequest request, String usuarioId) {
        Organizacao organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", organizacaoId));

        // Verificar permissão (owner ou admin)
        verificarPermissao(organizacaoId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        // Validar slug se estiver sendo alterado
        if (request.slug() != null && !request.slug().equals(organizacao.getSlug())) {
            if (organizacaoRepository.existsBySlug(request.slug())) {
                throw new BusinessException("Slug já está em uso");
            }
            organizacao.setSlug(request.slug());
        }

        if (request.nome() != null) {
            organizacao.setNome(request.nome());
        }
        if (request.logo() != null) {
            organizacao.setLogo(request.logo());
        }
        if (request.metadados() != null) {
            organizacao.setMetadados(request.metadados());
        }

        organizacao = organizacaoRepository.save(organizacao);
        return OrganizacaoResponse.fromEntity(organizacao);
    }

    /**
     * Deletar organização
     * DELETE /api/organizacao/{id}
     */
    public void deletarOrganizacao(String organizacaoId, String usuarioId) {
        Organizacao organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", organizacaoId));

        // Apenas owner pode deletar
        verificarPermissao(organizacaoId, usuarioId, PAPEL_OWNER);

        organizacaoRepository.delete(organizacao);
    }

    // ========================================================================
    // CONVITES
    // ========================================================================

    /**
     * Convidar membro para a organização
     * POST /api/organizacao/{id}/convites
     */
    public ConviteResponse convidarMembro(String organizacaoId, ConvidarMembroRequest request, String convidadorId) {
        Organizacao organizacao = organizacaoRepository.findById(organizacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", organizacaoId));

        // Verificar permissão
        verificarPermissao(organizacaoId, convidadorId, PAPEL_OWNER, PAPEL_ADMIN);

        Usuario convidador = usuarioRepository.findById(convidadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", convidadorId));

        // Verificar se já é membro
        if (membroRepository.findByOrganizacaoIdAndUsuarioEmail(organizacaoId, request.email()).isPresent()) {
            throw new BusinessException("Usuário já é membro desta organização");
        }

        // Verificar se já existe convite pendente
        var conviteExistente = conviteRepository.findByOrganizacaoIdAndEmailAndStatus(
                organizacaoId, request.email(), CONVITE_PENDENTE);

        if (conviteExistente.isPresent()) {
            if (Boolean.TRUE.equals(request.reenviar())) {
                // Atualizar expiração
                Convite convite = conviteExistente.get();
                convite.setExpiraEm(LocalDateTime.now().plusHours(CONVITE_EXPIRACAO_HORAS));
                convite = conviteRepository.save(convite);
                return ConviteResponse.fromEntity(convite);
            } else {
                throw new BusinessException("Já existe um convite pendente para este e-mail");
            }
        }

        // Criar novo convite
        Convite convite = Convite.builder()
                .id(UUID.randomUUID().toString())
                .email(request.email())
                .organizacao(organizacao)
                .convidador(convidador)
                .papel(request.papel())
                .status(CONVITE_PENDENTE)
                .expiraEm(LocalDateTime.now().plusHours(CONVITE_EXPIRACAO_HORAS))
                .build();

        convite = conviteRepository.save(convite);
        return ConviteResponse.fromEntity(convite);
    }

    /**
     * Aceitar convite
     * POST /api/organizacao/convites/{id}/aceitar
     */
    public MembroResponse aceitarConvite(String conviteId, String usuarioId) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite", "id", conviteId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Verificar se o e-mail corresponde
        if (!convite.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            throw new UnauthorizedException("Este convite não pertence a você");
        }

        // Verificar status
        if (!CONVITE_PENDENTE.equals(convite.getStatus())) {
            throw new BusinessException("Convite não está mais pendente");
        }

        // Verificar expiração
        if (convite.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Convite expirado");
        }

        // Verificar se já é membro
        if (membroRepository.existsByOrganizacaoIdAndUsuarioId(convite.getOrganizacao().getId(), usuarioId)) {
            throw new BusinessException("Você já é membro desta organização");
        }

        // Criar membro
        Membro membro = Membro.builder()
                .id(UUID.randomUUID().toString())
                .organizacao(convite.getOrganizacao())
                .usuario(usuario)
                .papel(convite.getPapel())
                .build();

        membro = membroRepository.save(membro);

        // Atualizar status do convite
        convite.setStatus(CONVITE_ACEITO);
        conviteRepository.save(convite);

        return MembroResponse.fromEntity(membro);
    }

    /**
     * Cancelar convite (pelo convidador/admin)
     * POST /api/organizacao/convites/{id}/cancelar
     */
    public void cancelarConvite(String conviteId, String usuarioId) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite", "id", conviteId));

        // Verificar permissão
        verificarPermissao(convite.getOrganizacao().getId(), usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        if (!CONVITE_PENDENTE.equals(convite.getStatus())) {
            throw new BusinessException("Convite não está mais pendente");
        }

        convite.setStatus(CONVITE_CANCELADO);
        conviteRepository.save(convite);
    }

    /**
     * Rejeitar convite (pelo convidado)
     * POST /api/organizacao/convites/{id}/rejeitar
     */
    public void rejeitarConvite(String conviteId, String usuarioId) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite", "id", conviteId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Verificar se o e-mail corresponde
        if (!convite.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            throw new UnauthorizedException("Este convite não pertence a você");
        }

        if (!CONVITE_PENDENTE.equals(convite.getStatus())) {
            throw new BusinessException("Convite não está mais pendente");
        }

        convite.setStatus(CONVITE_REJEITADO);
        conviteRepository.save(convite);
    }

    /**
     * Obter convite por ID
     * GET /api/organizacao/convites/{id}
     */
    @Transactional(readOnly = true)
    public ConviteResponse obterConvite(String conviteId) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Convite", "id", conviteId));
        return ConviteResponse.fromEntity(convite);
    }

    /**
     * Listar convites de uma organização
     * GET /api/organizacao/{id}/convites
     */
    @Transactional(readOnly = true)
    public List<ConviteResponse> listarConvites(String organizacaoId, String usuarioId) {
        // Verificar se é membro
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        return conviteRepository.findByOrganizacaoId(organizacaoId).stream()
                .map(ConviteResponse::fromEntity)
                .toList();
    }

    /**
     * Listar convites do usuário
     * GET /api/organizacao/meus-convites
     */
    @Transactional(readOnly = true)
    public List<ConviteResponse> listarConvitesDoUsuario(String email) {
        return conviteRepository.findConvitesPendentesValidos(email, LocalDateTime.now()).stream()
                .map(ConviteResponse::fromEntity)
                .toList();
    }

    // ========================================================================
    // MEMBROS
    // ========================================================================

    /**
     * Listar membros de uma organização
     * GET /api/organizacao/{id}/membros
     */
    @Transactional(readOnly = true)
    public Page<MembroResponse> listarMembros(String organizacaoId, String usuarioId, Pageable pageable) {
        // Verificar se é membro
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        return membroRepository.findByOrganizacaoId(organizacaoId, pageable)
                .map(MembroResponse::fromEntity);
    }

    /**
     * Remover membro
     * DELETE /api/organizacao/{orgId}/membros/{membroIdOuEmail}
     */
    public void removerMembro(String organizacaoId, String membroIdOuEmail, String usuarioId) {
        // Verificar permissão
        verificarPermissao(organizacaoId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        Membro membro;

        // Tentar encontrar por ID primeiro, depois por email
        var membroPorId = membroRepository.findById(membroIdOuEmail);
        if (membroPorId.isPresent() && membroPorId.get().getOrganizacao().getId().equals(organizacaoId)) {
            membro = membroPorId.get();
        } else {
            membro = membroRepository.findByOrganizacaoIdAndUsuarioEmail(organizacaoId, membroIdOuEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Membro", "id/email", membroIdOuEmail));
        }

        // Não pode remover o owner
        if (PAPEL_OWNER.equals(membro.getPapel())) {
            throw new BusinessException("Não é possível remover o proprietário da organização");
        }

        membroRepository.delete(membro);
    }

    /**
     * Atualizar papel do membro
     * PUT /api/organizacao/{orgId}/membros/{membroId}/papel
     */
    public MembroResponse atualizarPapelMembro(String organizacaoId, AtualizarPapelMembroRequest request, String usuarioId) {
        String orgId = request.organizacaoId() != null ? request.organizacaoId() : organizacaoId;

        // Verificar permissão
        verificarPermissao(orgId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        Membro membro = membroRepository.findById(request.membroId())
                .orElseThrow(() -> new ResourceNotFoundException("Membro", "id", request.membroId()));

        // Verificar se pertence à organização
        if (!membro.getOrganizacao().getId().equals(orgId)) {
            throw new BusinessException("Membro não pertence a esta organização");
        }

        // Não pode alterar papel do owner
        if (PAPEL_OWNER.equals(membro.getPapel())) {
            throw new BusinessException("Não é possível alterar o papel do proprietário");
        }

        // Apenas owner pode promover para owner
        if (PAPEL_OWNER.equals(request.papel())) {
            verificarPermissao(orgId, usuarioId, PAPEL_OWNER);
        }

        membro.setPapel(request.papel());
        membro = membroRepository.save(membro);

        return MembroResponse.fromEntity(membro);
    }

    /**
     * Obter membro ativo (do usuário logado na organização ativa)
     * GET /api/organizacao/membro-ativo
     */
    @Transactional(readOnly = true)
    public MembroResponse obterMembroAtivo(String organizacaoId, String usuarioId) {
        Membro membro = membroRepository.findByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro", "usuario", usuarioId));
        return MembroResponse.fromEntity(membro);
    }

    /**
     * Obter papel do membro ativo
     * GET /api/organizacao/membro-ativo/papel
     */
    @Transactional(readOnly = true)
    public String obterPapelMembroAtivo(String organizacaoId, String usuarioId) {
        Membro membro = membroRepository.findByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro", "usuario", usuarioId));
        return membro.getPapel();
    }

    /**
     * Adicionar membro diretamente (sem convite - apenas server-side)
     * POST /api/organizacao/{id}/membros
     */
    public MembroResponse adicionarMembro(String organizacaoId, AdicionarMembroRequest request, String adminUsuarioId) {
        String orgId = request.organizacaoId() != null ? request.organizacaoId() : organizacaoId;

        // Verificar permissão (server-side, admin ou owner)
        verificarPermissao(orgId, adminUsuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        Organizacao organizacao = organizacaoRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", orgId));

        String usuarioId = request.usuarioId() != null ? request.usuarioId() : adminUsuarioId;
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Verificar se já é membro
        if (membroRepository.existsByOrganizacaoIdAndUsuarioId(orgId, usuarioId)) {
            throw new BusinessException("Usuário já é membro desta organização");
        }

        Membro membro = Membro.builder()
                .id(UUID.randomUUID().toString())
                .organizacao(organizacao)
                .usuario(usuario)
                .papel(request.papel())
                .build();

        membro = membroRepository.save(membro);
        return MembroResponse.fromEntity(membro);
    }

    /**
     * Sair da organização
     * POST /api/organizacao/{id}/sair
     */
    public void sairDaOrganizacao(String organizacaoId, String usuarioId) {
        Membro membro = membroRepository.findByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro", "usuario", usuarioId));

        // Owner não pode sair
        if (PAPEL_OWNER.equals(membro.getPapel())) {
            throw new BusinessException("O proprietário não pode sair da organização. Transfira a propriedade primeiro.");
        }

        membroRepository.delete(membro);
    }

    // ========================================================================
    // PAPÉIS DINÂMICOS
    // ========================================================================

    /**
     * Criar papel de organização
     * POST /api/organizacao/{id}/papeis
     */
    public PapelOrganizacaoResponse criarPapel(String organizacaoId, CriarPapelRequest request, String usuarioId) {
        String orgId = request.organizacaoId() != null ? request.organizacaoId() : organizacaoId;

        // Verificar permissão
        verificarPermissao(orgId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        Organizacao organizacao = organizacaoRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organização", "id", orgId));

        // Verificar se papel já existe
        if (papelOrganizacaoRepository.existsByOrganizacaoIdAndPapel(orgId, request.papel())) {
            throw new BusinessException("Papel já existe nesta organização");
        }

        PapelOrganizacao papel = PapelOrganizacao.builder()
                .organizacao(organizacao)
                .papel(request.papel())
                .permissao(request.permissao())
                .build();

        papel = papelOrganizacaoRepository.save(papel);
        return PapelOrganizacaoResponse.fromEntity(papel);
    }

    /**
     * Deletar papel de organização
     * DELETE /api/organizacao/{orgId}/papeis/{papelIdOuNome}
     */
    public void deletarPapel(String organizacaoId, String papelIdOuNome, String usuarioId) {
        // Verificar permissão
        verificarPermissao(organizacaoId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        PapelOrganizacao papel;

        // Tentar encontrar por ID primeiro, depois por nome
        var papelPorId = papelOrganizacaoRepository.findById(papelIdOuNome);
        if (papelPorId.isPresent() && papelPorId.get().getOrganizacao().getId().equals(organizacaoId)) {
            papel = papelPorId.get();
        } else {
            papel = papelOrganizacaoRepository.findByOrganizacaoIdAndPapel(organizacaoId, papelIdOuNome)
                    .orElseThrow(() -> new ResourceNotFoundException("Papel", "id/nome", papelIdOuNome));
        }

        papelOrganizacaoRepository.delete(papel);
    }

    /**
     * Listar papéis de uma organização
     * GET /api/organizacao/{id}/papeis
     */
    @Transactional(readOnly = true)
    public List<PapelOrganizacaoResponse> listarPapeis(String organizacaoId, String usuarioId) {
        // Verificar permissão (membro pode ver)
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        return papelOrganizacaoRepository.findByOrganizacaoId(organizacaoId).stream()
                .map(PapelOrganizacaoResponse::fromEntity)
                .toList();
    }

    /**
     * Obter papel específico
     * GET /api/organizacao/{orgId}/papeis/{papelIdOuNome}
     */
    @Transactional(readOnly = true)
    public PapelOrganizacaoResponse obterPapel(String organizacaoId, String papelIdOuNome, String usuarioId) {
        // Verificar permissão
        if (!membroRepository.existsByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)) {
            throw new UnauthorizedException("Usuário não é membro desta organização");
        }

        PapelOrganizacao papel;

        // Tentar encontrar por ID primeiro, depois por nome
        var papelPorId = papelOrganizacaoRepository.findById(papelIdOuNome);
        if (papelPorId.isPresent() && papelPorId.get().getOrganizacao().getId().equals(organizacaoId)) {
            papel = papelPorId.get();
        } else {
            papel = papelOrganizacaoRepository.findByOrganizacaoIdAndPapel(organizacaoId, papelIdOuNome)
                    .orElseThrow(() -> new ResourceNotFoundException("Papel", "id/nome", papelIdOuNome));
        }

        return PapelOrganizacaoResponse.fromEntity(papel);
    }

    /**
     * Atualizar papel
     * PUT /api/organizacao/{orgId}/papeis/{papelIdOuNome}
     */
    public PapelOrganizacaoResponse atualizarPapel(String organizacaoId, AtualizarPapelRequest request, String usuarioId) {
        String orgId = request.organizacaoId() != null ? request.organizacaoId() : organizacaoId;

        // Verificar permissão
        verificarPermissao(orgId, usuarioId, PAPEL_OWNER, PAPEL_ADMIN);

        PapelOrganizacao papel;
        String identificador = request.papelId() != null ? request.papelId() : request.papelNome();

        // Tentar encontrar por ID primeiro, depois por nome
        var papelPorId = papelOrganizacaoRepository.findById(identificador);
        if (papelPorId.isPresent() && papelPorId.get().getOrganizacao().getId().equals(orgId)) {
            papel = papelPorId.get();
        } else {
            papel = papelOrganizacaoRepository.findByOrganizacaoIdAndPapel(orgId, identificador)
                    .orElseThrow(() -> new ResourceNotFoundException("Papel", "id/nome", identificador));
        }

        if (request.novoNome() != null) {
            // Verificar se novo nome já existe
            if (papelOrganizacaoRepository.existsByOrganizacaoIdAndPapel(orgId, request.novoNome())) {
                throw new BusinessException("Papel com este nome já existe");
            }
            papel.setPapel(request.novoNome());
        }

        if (request.permissao() != null) {
            papel.setPermissao(request.permissao());
        }

        papel = papelOrganizacaoRepository.save(papel);
        return PapelOrganizacaoResponse.fromEntity(papel);
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    /**
     * Verificar se usuário tem um dos papéis especificados na organização
     */
    private void verificarPermissao(String organizacaoId, String usuarioId, String... papeisPermitidos) {
        Membro membro = membroRepository.findByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não é membro desta organização"));

        boolean temPermissao = false;
        for (String papel : papeisPermitidos) {
            if (papel.equals(membro.getPapel())) {
                temPermissao = true;
                break;
            }
        }

        if (!temPermissao) {
            throw new UnauthorizedException("Você não tem permissão para realizar esta ação");
        }
    }

    /**
     * Verificar se usuário tem permissão específica
     * POST /api/organizacao/verificar-permissao
     */
    @Transactional(readOnly = true)
    public boolean verificarPermissao(String organizacaoId, String usuarioId, String recurso, String acao) {
        var membroOpt = membroRepository.findByOrganizacaoIdAndUsuarioId(organizacaoId, usuarioId);
        if (membroOpt.isEmpty()) {
            return false;
        }

        Membro membro = membroOpt.get();
        String papel = membro.getPapel();

        // Owner tem todas as permissões
        if (PAPEL_OWNER.equals(papel)) {
            return true;
        }

        // Admin tem quase todas as permissões
        if (PAPEL_ADMIN.equals(papel)) {
            // Admin não pode deletar organização ou alterar owner
            if ("organization".equals(recurso) && "delete".equals(acao)) {
                return false;
            }
            return true;
        }

        // Member tem permissões limitadas (apenas leitura)
        if (PAPEL_MEMBER.equals(papel)) {
            return "read".equals(acao);
        }

        // Verificar papéis dinâmicos
        var papelDinamico = papelOrganizacaoRepository.findByOrganizacaoIdAndPapel(organizacaoId, papel);
        if (papelDinamico.isPresent()) {
            String permissoes = papelDinamico.get().getPermissao();
            // Formato esperado: {"recurso": ["acao1", "acao2"]}
            // Implementação simplificada - pode ser expandida conforme necessário
            return permissoes.contains(recurso) && permissoes.contains(acao);
        }

        return false;
    }
}
