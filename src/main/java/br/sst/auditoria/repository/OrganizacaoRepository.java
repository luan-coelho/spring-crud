package br.sst.auditoria.repository;

import br.sst.auditoria.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, String> {
    
    Optional<Organizacao> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
}
