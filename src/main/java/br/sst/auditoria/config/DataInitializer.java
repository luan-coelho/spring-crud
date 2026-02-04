package br.sst.auditoria.config;

import br.sst.auditoria.model.Conta;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.ContaRepository;
import br.sst.auditoria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Componente para inicializar dados padrão no banco de dados.
 * Cria um usuário admin padrão se não existir.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
            ContaRepository contaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Verifica se já existe um usuário admin
        if (!usuarioRepository.existsByEmail("admin@gmail.com")) {
            // Cria o usuário admin
            Usuario admin = Usuario.builder()
                    .id(UUID.randomUUID().toString())
                    .nome("Administrador")
                    .email("admin@gmail.com")
                    .cpf("000.000.000-00")
                    .emailVerificado(true)
                    .papel("admin")
                    .banido(false)
                    .onboardingCompleto(true)
                    .build();

            admin = usuarioRepository.save(admin);

            // Cria a conta com senha
            Conta conta = Conta.builder()
                    .id(UUID.randomUUID().toString())
                    .contaId(admin.getId())
                    .provedorId("credentials")
                    .senha(passwordEncoder.encode("admin123")) // Senha padrão - alterar em produção!
                    .usuario(admin)
                    .build();

            contaRepository.save(conta);

            System.out.println("==============================================");
            System.out.println("Usuário admin criado com sucesso!");
            System.out.println("E-mail: admin@gmail.com");
            System.out.println("Senha: admin123");
            System.out.println("ATENÇÃO: Altere a senha em produção!");
            System.out.println("==============================================");
        }
    }
}
