package br.sst.auditoria.repository;

import br.sst.auditoria.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, String> {

    List<Sessao> findByUsuarioId(String usuarioId);

    Optional<Sessao> findByToken(String token);

    @Query("SELECT s FROM Sessao s WHERE s.usuario.id = :usuarioId AND s.expiraEm > :agora")
    List<Sessao> findSessoesAtivasByUsuarioId(@Param("usuarioId") String usuarioId,
            @Param("agora") LocalDateTime agora);

    @Modifying
    @Query("DELETE FROM Sessao s WHERE s.expiraEm < :agora")
    void deleteSessoesExpiradas(@Param("agora") LocalDateTime agora);

    @Modifying
    @Query("DELETE FROM Sessao s WHERE s.usuario.id = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") String usuarioId);
}
