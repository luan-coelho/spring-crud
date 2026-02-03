package br.sst.auditoria.security;

import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, ContaRepository contaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        // Verifica se o usuário está banido
        if (Boolean.TRUE.equals(usuario.getBanido())) {
            throw new UsernameNotFoundException("Usuário está banido. Motivo: " +
                    (usuario.getMotivoBanimento() != null ? usuario.getMotivoBanimento() : "Não informado"));
        }

        // Busca a senha da conta de credenciais
        Optional<Conta> contaOpt = contaRepository.findByUsuarioIdAndProvedorId(usuario.getId(), "credentials");
        String senha = contaOpt.map(Conta::getSenha).orElse(null);

        return new CustomUserDetails(usuario, senha);
    }
}
