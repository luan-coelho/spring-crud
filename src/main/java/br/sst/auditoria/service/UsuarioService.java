package br.sst.auditoria.service;

import br.sst.auditoria.dto.usuario.UsuarioRequest;
import br.sst.auditoria.dto.usuario.UsuarioResponse;
import br.sst.auditoria.exception.BusinessException;
import br.sst.auditoria.exception.ResourceNotFoundException;
import br.sst.auditoria.mapper.UsuarioMapper;
import br.sst.auditoria.model.Usuario;
import br.sst.auditoria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    /**
     * Lista todos os usuários
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    /**
     * Busca um usuário por ID
     */
    @Transactional(readOnly = true)
    public UsuarioResponse findById(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Cria um novo usuário
     */
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        // Valida duplicidade de email
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já está em uso");
        }

        // Valida duplicidade de CPF
        if (usuarioRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF já está em uso");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setId(UUID.randomUUID().toString());
        
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(saved);
    }

    /**
     * Atualiza um usuário existente
     */
    @Transactional
    public void update(String id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        // Valida duplicidade de email (exceto o próprio usuário)
        if (isEmailDuplicado(request.email(), id)) {
            throw new BusinessException("E-mail já está em uso");
        }

        // Valida duplicidade de CPF (exceto o próprio usuário)
        if (isCpfDuplicado(request.cpf(), id)) {
            throw new BusinessException("CPF já está em uso");
        }

        usuarioMapper.updateEntity(request, usuario);
        usuarioRepository.save(usuario);
    }

    /**
     * Atualiza parcialmente um usuário
     */
    @Transactional
    public void partialUpdate(String id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        // Valida duplicidade apenas se os campos foram fornecidos
        if (request.email() != null && isEmailDuplicado(request.email(), id)) {
            throw new BusinessException("E-mail já está em uso");
        }

        if (request.cpf() != null && isCpfDuplicado(request.cpf(), id)) {
            throw new BusinessException("CPF já está em uso");
        }

        usuarioMapper.updateEntity(request, usuario);
        usuarioRepository.save(usuario);
    }

    /**
     * Remove um usuário
     */
    @Transactional
    public void delete(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", "id", id);
        }
        
        usuarioRepository.deleteById(id);
    }

    // Métodos auxiliares privados

    private boolean isEmailDuplicado(String email, String usuarioId) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        return usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioId);
    }

    private boolean isCpfDuplicado(String cpf, String usuarioId) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCpf(cpf);
        return usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioId);
    }
}
