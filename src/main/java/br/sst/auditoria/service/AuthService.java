package br.sst.auditoria.service;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.dto.auth.ChangePasswordRequest;
import br.sst.auditoria.dto.auth.LoginRequest;
import br.sst.auditoria.dto.auth.RegisterRequest;
import br.sst.auditoria.exception.BusinessException;
import br.sst.auditoria.exception.ResourceNotFoundException;
import br.sst.auditoria.exception.UnauthorizedException;
import br.sst.auditoria.mapper.AuthMapper;
import br.sst.auditoria.mapper.UsuarioMapper;
import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Sessao;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import br.sst.auditoria.security.CustomUserDetails;
import br.sst.auditoria.security.session.SessionAuthenticationToken;
import br.sst.auditoria.security.session.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Serviço de autenticação com sessões persistidas no banco de dados.
 * Implementa modelo similar ao Better Auth.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SessaoService sessaoService;
    private final AuthMapper authMapper;
    private final UsuarioMapper usuarioMapper;

    /**
     * Realiza login do usuário e cria uma sessão persistida no banco
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // Autentica o usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        // Cria a sessão no banco de dados
        String enderecoIp = extrairEnderecoIp(httpRequest);
        String agenteUsuario = httpRequest.getHeader("User-Agent");

        Sessao sessao = sessaoService.criarSessao(usuario, enderecoIp, agenteUsuario);

        // Define o contexto de autenticação com a sessão
        SessionAuthenticationToken sessionAuth = new SessionAuthenticationToken(
                userDetails, sessao, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(sessionAuth);

        return authMapper.toResponse(sessao.getToken(), userDetails);
    }

    /**
     * Realiza login sem HttpServletRequest (para compatibilidade)
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        Sessao sessao = sessaoService.criarSessao(usuario, null, null);

        SessionAuthenticationToken sessionAuth = new SessionAuthenticationToken(
                userDetails, sessao, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(sessionAuth);

        return authMapper.toResponse(sessao.getToken(), userDetails);
    }

    /**
     * Registra um novo usuário e cria uma sessão
     */
    @Transactional
    public AuthResponse registrar(RegisterRequest request, HttpServletRequest httpRequest) {
        // Validação de confirmação de senha
        if (!request.senha().equals(request.confirmarSenha())) {
            throw new BusinessException("As senhas não coincidem");
        }

        // Verifica se o e-mail já está em uso
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        // Verifica se o CPF já está em uso
        if (usuarioRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Cria o usuário
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setId(UUID.randomUUID().toString());
        usuario = usuarioRepository.save(usuario);

        // Cria a conta com a senha
        Conta conta = Conta.builder()
                .id(UUID.randomUUID().toString())
                .contaId(usuario.getId())
                .provedorId("credentials")
                .senha(passwordEncoder.encode(request.senha()))
                .usuario(usuario)
                .build();

        contaRepository.save(conta);

        // Busca a senha criptografada para o UserDetails
        CustomUserDetails userDetails = new CustomUserDetails(usuario, conta.getSenha());

        // Cria a sessão no banco de dados
        String enderecoIp = httpRequest != null ? extrairEnderecoIp(httpRequest) : null;
        String agenteUsuario = httpRequest != null ? httpRequest.getHeader("User-Agent") : null;

        Sessao sessao = sessaoService.criarSessao(usuario, enderecoIp, agenteUsuario);

        return authMapper.toResponse(sessao.getToken(), userDetails);
    }

    /**
     * Registra um novo usuário sem HttpServletRequest (para compatibilidade)
     */
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        return registrar(request, null);
    }

    /**
     * Realiza logout - revoga a sessão atual
     */
    @Transactional
    public void logout() {
        SessionUtils.getSessionToken().ifPresent(sessaoService::revogarSessao);
        SecurityContextHolder.clearContext();
    }

    /**
     * Realiza logout de todos os dispositivos
     */
    @Transactional
    public void logoutTodosDispositivos() {
        SessionUtils.getUsuarioId().ifPresent(sessaoService::revogarTodasSessoes);
        SecurityContextHolder.clearContext();
    }

    /**
     * Lista todas as sessões ativas do usuário atual
     */
    @Transactional(readOnly = true)
    public List<Sessao> listarMinhasSessoes() {
        String usuarioId = SessionUtils.getUsuarioId()
                .orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));
        return sessaoService.listarSessoesAtivas(usuarioId);
    }

    /**
     * Revoga uma sessão específica do usuário
     */
    @Transactional
    public void revogarSessao(String sessaoId) {
        String usuarioId = SessionUtils.getUsuarioId()
                .orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));

        // Busca a sessão e verifica se pertence ao usuário
        List<Sessao> minhasSessoes = sessaoService.listarSessoesAtivas(usuarioId);
        Sessao sessaoParaRevogar = minhasSessoes.stream()
                .filter(s -> s.getId().equals(sessaoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sessão", "id", sessaoId));

        sessaoService.revogarSessao(sessaoParaRevogar.getToken());
    }

    /**
     * Obtém informações do usuário autenticado
     */
    @Transactional(readOnly = true)
    public AuthResponse pegarUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("Usuário não autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Inclui o token da sessão se disponível
        String sessionToken = null;
        if (authentication instanceof SessionAuthenticationToken sessionAuth) {
            sessionToken = sessionAuth.getSessionToken();
        }

        return sessionToken != null
                ? authMapper.toResponse(sessionToken, userDetails)
                : authMapper.toResponse(userDetails);
    }

    /**
     * Altera a senha do usuário autenticado
     */
    @Transactional
    public void alterarSenha(ChangePasswordRequest request) {
        // Validação de confirmação de senha
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new BusinessException("As senhas não coincidem");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String usuarioId = userDetails.getId();

        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Busca a conta de credenciais
        Conta conta = contaRepository.findByUsuarioIdAndProvedorId(usuarioId, "credentials")
                .orElseThrow(() -> new ResourceNotFoundException("Conta de credenciais não encontrada"));

        // Verifica a senha atual
        if (!passwordEncoder.matches(request.senhaAtual(), conta.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        // Atualiza a senha
        conta.setSenha(passwordEncoder.encode(request.novaSenha()));
        contaRepository.save(conta);

        // Opcionalmente, revoga todas as outras sessões após alteração de senha
        // para forçar reautenticação em outros dispositivos
        // sessaoService.revogarTodasSessoes(usuarioId);
    }

    /**
     * Verifica se o usuário está autenticado
     */
    public boolean estaAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Verifica se o usuário tem determinado papel
     */
    public boolean temPapel(String papel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String role = papel.toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        final String roleToCheck = role;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), roleToCheck));
    }

    /**
     * Extrai o endereço IP real do cliente, considerando proxies
     */
    private String extrairEnderecoIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Se houver múltiplos IPs (via proxies), pegar o primeiro
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
