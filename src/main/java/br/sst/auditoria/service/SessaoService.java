package br.sst.auditoria.service;

import br.sst.auditoria.exception.UnauthorizedException;
import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.SessaoRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import br.sst.auditoria.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento de sessões de autenticação.
 * Implementa autenticação baseada em sessão persistida no banco (como Better Auth).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Value("${session.expiration:604800000}") // 7 days default
    private long sessionExpirationMs;

    /**
     * Cria uma nova sessão para o usuário
     */
    @Transactional
    public Sessao criarSessao(Usuario usuario, String enderecoIp, String agenteUsuario) {
        // Gera um token de sessão seguro (opaco, não JWT)
        String sessionToken = gerarTokenSeguro();

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = agora.plusSeconds(sessionExpirationMs / 1000);

        Sessao sessao = Sessao.builder()
                .id(UUID.randomUUID().toString())
                .token(sessionToken)
                .usuario(usuario)
                .expiraEm(expiracao)
                .enderecoIp(enderecoIp)
                .agenteUsuario(agenteUsuario)
                .build();

        return sessaoRepository.save(sessao);
    }

    /**
     * Valida um token de sessão e retorna a sessão se válida
     */
    @Transactional(readOnly = true)
    public Optional<Sessao> validarSessao(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        Optional<Sessao> sessaoOpt = sessaoRepository.findByToken(token);

        if (sessaoOpt.isEmpty()) {
            return Optional.empty();
        }

        Sessao sessao = sessaoOpt.get();

        // Verifica se a sessão expirou
        if (sessao.getExpiraEm().isBefore(LocalDateTime.now())) {
            log.debug("Sessão expirada para usuário: {}", sessao.getUsuario().getEmail());
            return Optional.empty();
        }

        // Verifica se o usuário está banido
        if (Boolean.TRUE.equals(sessao.getUsuario().getBanido())) {
            log.warn("Tentativa de acesso com sessão de usuário banido: {}", sessao.getUsuario().getEmail());
            return Optional.empty();
        }

        return sessaoOpt;
    }

    /**
     * Obtém a sessão atual pelo token
     */
    @Transactional(readOnly = true)
    public Sessao obterSessao(String token) {
        return validarSessao(token)
                .orElseThrow(() -> new UnauthorizedException("Sessão inválida ou expirada"));
    }

    /**
     * Renova uma sessão existente (sliding expiration)
     */
    @Transactional
    public Sessao renovarSessao(String token) {
        Sessao sessao = obterSessao(token);

        LocalDateTime novaExpiracao = LocalDateTime.now().plusSeconds(sessionExpirationMs / 1000);
        sessao.setExpiraEm(novaExpiracao);

        return sessaoRepository.save(sessao);
    }

    /**
     * Revoga uma sessão específica (logout)
     */
    @Transactional
    public void revogarSessao(String token) {
        sessaoRepository.findByToken(token)
                .ifPresent(sessao -> sessaoRepository.delete(sessao));
    }

    /**
     * Revoga todas as sessões de um usuário (logout de todos os dispositivos)
     */
    @Transactional
    public void revogarTodasSessoes(String usuarioId) {
        sessaoRepository.deleteAllByUsuarioId(usuarioId);
    }

    /**
     * Lista todas as sessões ativas de um usuário
     */
    @Transactional(readOnly = true)
    public List<Sessao> listarSessoesAtivas(String usuarioId) {
        return sessaoRepository.findSessoesAtivasByUsuarioId(usuarioId, LocalDateTime.now());
    }

    /**
     * Atualiza a organização ativa na sessão
     */
    @Transactional
    public void atualizarOrganizacaoAtiva(String token, String organizacaoId) {
        Sessao sessao = obterSessao(token);
        sessaoRepository.atualizarOrganizacaoAtiva(sessao.getId(), organizacaoId);
    }

    /**
     * Limpa a organização ativa na sessão
     */
    @Transactional
    public void limparOrganizacaoAtiva(String token) {
        Sessao sessao = obterSessao(token);
        sessaoRepository.limparOrganizacaoAtiva(sessao.getId());
    }

    /**
     * Task agendada para limpar sessões expiradas do banco
     * Executa a cada hora
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void limparSessoesExpiradas() {
        int deletedCount = 0;
        List<Sessao> sessoesExpiradas = sessaoRepository.findAll().stream()
                .filter(s -> s.getExpiraEm().isBefore(LocalDateTime.now()))
                .toList();

        for (Sessao sessao : sessoesExpiradas) {
            sessaoRepository.delete(sessao);
            deletedCount++;
        }

        if (deletedCount > 0) {
            log.info("Limpas {} sessões expiradas", deletedCount);
        }
    }

    /**
     * Gera um token de sessão seguro usando SecureRandom
     */
    private String gerarTokenSeguro() {
        byte[] randomBytes = new byte[32]; // 256 bits
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_ENCODER.encodeToString(randomBytes);
    }

    /**
     * Obtém o usuário da sessão atual pelo token
     */
    @Transactional(readOnly = true)
    public Usuario obterUsuarioPorToken(String token) {
        return obterSessao(token).getUsuario();
    }

    /**
     * Verifica se o token de sessão é válido
     */
    public boolean isTokenValido(String token) {
        return validarSessao(token).isPresent();
    }
}
