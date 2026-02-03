package br.sst.auditoria.service;

import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import br.sst.auditoria.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
            ContaRepository contaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário no sistema
     */
    @Transactional
    public Usuario registrar(String nome, String email, String cpf, String senha, String telefone) {
        // Verifica se o e-mail já está em uso
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        // Verifica se o CPF já está em uso
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Cria o usuário
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nome(nome)
                .email(email)
                .cpf(cpf)
                .telefone(telefone)
                .emailVerificado(true) // Por padrão, marca como verificado (ajustar conforme necessidade)
                .papel("user")
                .banido(false)
                .onboardingCompleto(false)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Cria a conta com a senha
        Conta conta = Conta.builder()
                .id(UUID.randomUUID().toString())
                .contaId(usuario.getId())
                .provedorId("credentials") // Provedor de credenciais locais
                .senha(passwordEncoder.encode(senha))
                .usuario(usuario)
                .build();

        contaRepository.save(conta);

        return usuario;
    }

    /**
     * Altera a senha do usuário
     */
    @Transactional
    public void alterarSenha(String usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Busca a conta de credenciais
        Conta conta = contaRepository.findByUsuarioIdAndProvedorId(usuarioId, "credentials")
                .orElseThrow(() -> new IllegalArgumentException("Conta de credenciais não encontrada"));

        // Verifica a senha atual
        if (!passwordEncoder.matches(senhaAtual, conta.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        // Atualiza a senha
        conta.setSenha(passwordEncoder.encode(novaSenha));
        contaRepository.save(conta);
    }

    /**
     * Obtém o usuário atualmente autenticado
     */
    public Optional<Usuario> getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getUsuario());
        }

        return Optional.empty();
    }

    /**
     * Verifica se o usuário está autenticado
     */
    public boolean isAutenticado() {
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
                .anyMatch(a -> a.getAuthority().equals(roleToCheck));
    }
}
