package br.sst.auditoria.repository;

import br.sst.auditoria.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, String> {

    List<Conta> findByUsuarioId(String usuarioId);

    @Query("SELECT c FROM Conta c WHERE c.usuario.id = :usuarioId AND c.provedorId = :provedorId")
    Optional<Conta> findByUsuarioIdAndProvedorId(@Param("usuarioId") String usuarioId,
            @Param("provedorId") String provedorId);

    Optional<Conta> findByContaIdAndProvedorId(String contaId, String provedorId);

    boolean existsByContaIdAndProvedorId(String contaId, String provedorId);
}
