package br.sst.auditoria.service;

import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        // Gera ID automático se for novo usuário
        if (usuario.getId() == null || usuario.getId().isEmpty()) {
            usuario.setId(UUID.randomUUID().toString());
        }

        // Valida duplicidade de email
        if (isEmailDuplicado(usuario.getEmail(), usuario.getId())) {
            throw new IllegalArgumentException("E-mail já está em uso");
        }

        // Valida duplicidade de CPF
        if (isCpfDuplicado(usuario.getCpf(), usuario.getId())) {
            throw new IllegalArgumentException("CPF já está em uso");
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(String id) {
        usuarioRepository.deleteById(id);
    }

    private boolean isEmailDuplicado(String email, String usuarioId) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        return usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioId);
    }

    private boolean isCpfDuplicado(String cpf, String usuarioId) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCpf(cpf);
        return usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioId);
    }
}
