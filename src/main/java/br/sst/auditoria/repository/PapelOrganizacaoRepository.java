package br.sst.auditoria.repository;

import br.sst.auditoria.model.PapelOrganizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PapelOrganizacaoRepository extends JpaRepository<PapelOrganizacao, String> {
    
    List<PapelOrganizacao> findByOrganizacaoId(String organizacaoId);
    
    Optional<PapelOrganizacao> findByOrganizacaoIdAndPapel(String organizacaoId, String papel);
    
    boolean existsByOrganizacaoIdAndPapel(String organizacaoId, String papel);
    
    long countByOrganizacaoId(String organizacaoId);
}
