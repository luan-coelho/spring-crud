package br.sst.auditoria.repository;

import br.sst.auditoria.model.Membro;
import br.sst.auditoria.model.Organizacao;
import br.sst.auditoria.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, String> {
    
    List<Membro> findByUsuario(Usuario usuario);
    
    List<Membro> findByUsuarioId(String usuarioId);
    
    List<Membro> findByOrganizacao(Organizacao organizacao);
    
    List<Membro> findByOrganizacaoId(String organizacaoId);
    
    Page<Membro> findByOrganizacaoId(String organizacaoId, Pageable pageable);
    
    Optional<Membro> findByOrganizacaoAndUsuario(Organizacao organizacao, Usuario usuario);
    
    Optional<Membro> findByOrganizacaoIdAndUsuarioId(String organizacaoId, String usuarioId);
    
    Optional<Membro> findByOrganizacaoIdAndUsuarioEmail(String organizacaoId, String email);
    
    boolean existsByOrganizacaoAndUsuario(Organizacao organizacao, Usuario usuario);
    
    boolean existsByOrganizacaoIdAndUsuarioId(String organizacaoId, String usuarioId);
    
    @Query("SELECT m FROM Membro m JOIN FETCH m.organizacao WHERE m.usuario.id = :usuarioId")
    List<Membro> findByUsuarioIdWithOrganizacao(@Param("usuarioId") String usuarioId);
    
    long countByOrganizacaoId(String organizacaoId);
}
