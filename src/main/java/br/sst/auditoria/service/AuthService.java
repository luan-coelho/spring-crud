package br.sst.auditoria.service;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.dto.auth.ChangePasswordRequest;
import br.sst.auditoria.dto.auth.LoginRequest;
import br.sst.auditoria.dto.auth.RegisterRequest;
import br.sst.auditoria.exception.BusinessException;
import br.sst.auditoria.exception.ResourceNotFoundException;
import br.sst.auditoria.exception.UnauthorizedException;
import br.sst.auditoria.mapper.AuthMapper;
import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import br.sst.auditoria.security.CustomUserDetails;
import br.sst.auditoria.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthMapper authMapper;

    /**
     * Realiza login do usuário
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authMapper.toResponse(jwt, userDetails);
    }

    /**
     * Registra um novo usuário e retorna o token de autenticação
     */
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
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
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nome(request.nome())
                .email(request.email())
                .cpf(request.cpf())
                .telefone(request.telefone())
                .emailVerificado(true)
                .papel("user")
                .banido(false)
                .onboardingCompleto(false)
                .build();

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

        // Autentica o usuário recém-criado
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()));

        String jwt = jwtUtils.generateJwtToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authMapper.toResponse(jwt, userDetails);
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

        return new AuthResponse(
                null,
                null,
                userDetails.getId(),
                userDetails.getNome(),
                userDetails.getEmail(),
                userDetails.getPapel(),
                userDetails.getImagem()
        );
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
    
}
