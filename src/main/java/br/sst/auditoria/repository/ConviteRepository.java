package br.sst.auditoria.repository;

import br.sst.auditoria.model.Convite;
import br.sst.auditoria.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConviteRepository extends JpaRepository<Convite, String> {
    
    List<Convite> findByOrganizacao(Organizacao organizacao);
    
    List<Convite> findByOrganizacaoId(String organizacaoId);
    
    List<Convite> findByEmail(String email);
    
    List<Convite> findByEmailAndStatus(String email, String status);
    
    Optional<Convite> findByOrganizacaoIdAndEmailAndStatus(String organizacaoId, String email, String status);
    
    boolean existsByOrganizacaoIdAndEmailAndStatus(String organizacaoId, String email, String status);
    
    @Query("SELECT c FROM Convite c WHERE c.email = :email AND c.status = 'pending' AND c.expiraEm > :agora")
    List<Convite> findConvitesPendentesValidos(@Param("email") String email, @Param("agora") LocalDateTime agora);
    
    @Query("SELECT c FROM Convite c WHERE c.organizacao.id = :organizacaoId AND c.status = 'pending'")
    List<Convite> findConvitesPendentesByOrganizacaoId(@Param("organizacaoId") String organizacaoId);
    
    long countByOrganizacaoIdAndStatus(String organizacaoId, String status);
}
